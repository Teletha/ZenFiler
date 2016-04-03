/*
 * Copyright (C) 2011 Nameless Production Committee.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bebop;

import kiss.Interceptor;

/**
 * <p>
 * Cancelable task which is executing in UI thread.
 * </p>
 * 
 * @version 2011/12/23 13:43:51
 */
class UITask extends Interceptor<InUIThread> implements Runnable {

    /** The actual parameters. */
    private Object[] params;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object invoke(final Object... params) {
        if (Application.display.getThread() == Thread.currentThread()) {
            try {
                super.invoke(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // store actual method parameter
            this.params = params;

            // associate this UI task with the worker task
            WorkerTask task = WorkerTask.store.get();

            if (task == null) {
                // invoked in non-worker thread

                // execute in UI thread
                Application.display.asyncExec(this);
            } else {
                // invoked in worker thread

                // execute in UI thread
                task.associateUITask(this);
            }
        }

        // API definition
        return null;
    }

    /**
     * <p>
     * Invoke actual task.
     * </p>
     * {@inheritDoc}
     */
    @Override
    public void run() {
        super.invoke(params);
    }
}
