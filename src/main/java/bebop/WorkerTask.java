/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop;

import static java.util.concurrent.TimeUnit.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import kiss.I;
import kiss.Interceptor;

/**
 * <p>
 * Cancelable task which is executing in worker thread.
 * </p>
 * 
 * @version 2011/12/23 13:44:05
 */
class WorkerTask extends Interceptor<InWorkerThread> implements Runnable {

    /** The task store. */
    static final ThreadLocal<WorkerTask> store = new ThreadLocal();

    /** The thread pool. */
    private static final Workers workers = new Workers();

    /** The mapping between invocation (caller instance and method) and task. */
    private static final Map<WorkerTask, WorkerTask> tasks = new ConcurrentHashMap();

    /** The task state. */
    private static final int Intialized = 0;

    /** The task state. */
    private static final int WorkerTaskStarted = 1;

    /** The task state. */
    private static final int WorkerTaskDone = 1 << 1;

    /** The task state. */
    private static final int UITaskStarted = 1 << 2;

    /** The task state. */
    private static final int UITaskDone = 1 << 3;

    /** The task state. */
    private static final int Completed = 1 << 4;

    /** The task state. */
    private static final int Canceled = 1 << 5;

    /** The current state. */
    private volatile int state = Intialized;

    /** The controllable UI task queue. */
    private ConcurrentLinkedQueue<UITask> events;

    /** The sequencial following tasks. */
    private ConcurrentLinkedQueue<WorkerTask> followings;

    /** The actual worker task parameters. */
    private Object[] params;

    /** The worker thread. */
    private Thread worker;

    /**
     * @see ezbean.Interceptor#invoke(java.lang.Object[])
     */
    @Override
    protected Object invoke(Object... params) {
        // Store method parameter.
        this.params = params;

        // Search previous worker task associated with this invocation point.
        WorkerTask task = tasks.get(this);

        if (task != null) {
            switch (annotation.execute()) {
            case Sequence:
                // register as next task
                if (task.followings == null) {
                    task.followings = new ConcurrentLinkedQueue();
                }
                task.followings.add(this);

                // API definition
                return null;

            case Single:
                task.cancel();
                break;
            }
        }

        // Invoke this worker task.
        invoke();

        // API definition
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (state == Intialized) {
            // In worker thread

            // state : start worker task
            state |= WorkerTaskStarted;

            try {
                super.invoke(params);
            } catch (Throwable e) {
                if (e instanceof InterruptedException) {

                } else {
                    throw I.quiet(e);
                }
            } finally {
                // state : done worker task
                state |= WorkerTaskDone;

                // check task completion
                if ((state & UITaskStarted) == 0 || (state & UITaskDone) != 0) {
                    complete();
                }
            }
        } else {
            // In UI thread
            UITask task = events.poll();

            if (task != null) {
                task.run();
            }

            if (events.isEmpty()) {
                // state : done UI task
                state |= UITaskDone;

                // check task completion
                if ((state & WorkerTaskDone) != 0) {
                    complete();
                }
            } else {
                // continue to execute remaining UI tasks
                Application.display.asyncExec(this);
            }
        }
    }

    /**
     * <p>
     * Associate this worker task with the specified UI task.
     * </p>
     * 
     * @param task UI task.
     */
    void associateUITask(UITask task) {
        if ((state & Canceled) == 0 && (state & Completed) == 0) {
            if (events == null) {
                // state : start UI task
                state |= UITaskStarted;

                // initialize task queue
                events = new ConcurrentLinkedQueue();
            } else {
                // state : reset UI task
                state &= ~UITaskDone;
            }

            // Chech whether this worker task has some associated UI tasks.
            // Don't use size() instead of isEmpty() because linked queue's size() is too slow.
            boolean inactive = events.isEmpty();

            // register
            events.add(task);

            if (inactive) {
                Application.display.asyncExec(this);
            }
        }
    }

    /**
     * <p>
     * Invoke actual task.
     * </p>
     */
    private void invoke() {
        if (Application.display.getThread() != Thread.currentThread()) {
            super.invoke(params);
        } else {
            // register invocation point
            tasks.put(this, this);

            // execute in worker thread
            workers.execute(this);
        }
    }

    /**
     * 
     */
    private void cancel() {
        if ((state & Canceled) == 0) {
            // state : cancel worker task
            state |= Canceled;

            // Task is not reusable but thread is reusable. So if some sequencial tasks which has
            // same invocation on same thread invokes Thread#interrupt, it will may suspend the next
            // task unexpectedly.
            if ((state & WorkerTaskStarted) != 0 && (state & WorkerTaskDone) == 0) {
                // interrupt worker thread
                worker.interrupt();
            }

            // Cancel all UI tasks associated with this worker task.
            events.clear();
        }
    }

    /**
     * <p>
     * Complete this task.
     * </p>
     */
    private void complete() {
        if ((state & Completed) == 0) {
            // state : complete worker task and associated UI tasks
            state |= Completed;

            // unregister invocation point
            tasks.remove(this);

            // check following tasks
            if (followings != null) {
                WorkerTask next = followings.poll();

                if (next != null) {
                    // take over the followings
                    next.followings = followings;

                    // invoke next task
                    next.invoke();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return that.hashCode() + annotation.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WorkerTask) {
            WorkerTask interceptor = (WorkerTask) obj;

            return that == interceptor.that && annotation == interceptor.annotation;
        } else {
            return false;
        }
    }

    /**
     * @version 2011/12/21 19:23:39
     */
    private static class Workers extends ThreadPoolExecutor {

        /**
         * 
         */
        private Workers() {
            super(4, 16, 30, SECONDS, new SynchronousQueue(), new Factory(), new AbortPolicy());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void beforeExecute(Thread thread, Runnable runnable) {
            WorkerTask task = (WorkerTask) runnable;
            task.worker = thread;

            store.set(task);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void afterExecute(Runnable runnable, Throwable throwable) {
            store.set(null);
        }

    }

    /**
     * @version 2011/12/21 19:31:49
     */
    private static class Factory implements ThreadFactory {

        /**
         * {@inheritDoc}
         */
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "UI Friendly Worker Task Thread");
            thread.setDaemon(true);

            return thread;
        }
    }
}
