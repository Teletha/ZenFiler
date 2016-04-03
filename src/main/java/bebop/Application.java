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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.CopyOnWriteArrayList;

import kiss.I;
import kiss.Manageable;
import kiss.Preference;
import kiss.model.ClassUtil;

import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Display;

import toybox.filesystem.FSPath;

/**
 * <p>
 * This is your application's configuration too.
 * </p>
 * 
 * @version 2012/03/02 22:21:01
 */
@Manageable(lifestyle = Preference.class)
public abstract class Application {

    // initialization
    static {
        I.load(ClassUtil.getArchive(Application.class));

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                e.printStackTrace();
            }
        });
    }

    /** The root display. */
    public static final Display display = Display.getDefault();

    /** The application context directory. */
    public final FSPath contextDirectory;

    /** The configuration. */
    protected Class<? extends ApplicationWindow> initialApplicationWindow;

    /** The active windows. */
    private CopyOnWriteArrayList<Class> windows = new CopyOnWriteArrayList();

    /** The active windows. */
    private final CopyOnWriteArrayList<Class<? extends ApplicationWindow>> actives = new CopyOnWriteArrayList();

    /**
     * <p>
     * Create your application.
     * </p>
     */
    protected Application() {
        contextDirectory = FSPath.locate(I.locate("").toAbsolutePath());
    }

    /**
     * Get the windows property of this {@link Application}.
     * 
     * @return The windows property.
     */
    CopyOnWriteArrayList<Class> getWindows() {
        return windows;
    }

    /**
     * Set the windows property of this {@link Application}.
     * 
     * @param windows The windows value to set.
     */
    void setWindows(CopyOnWriteArrayList<Class> windows) {
        this.windows = windows;
    }

    /**
     * <p>
     * Activate application window.
     * </p>
     * 
     * @param windowClass
     * @return
     */
    public void openWindow(Class modelClass) {

    }

    /**
     * <p>
     * Close your application safely.
     * </p>
     */
    public void close() {

    }

    /**
     * <p>
     * Close the specified application window safely.
     * </p>
     */
    public void close(Class<? extends ApplicationWindow> windowClass) {
        if (actives.remove(windowClass)) {
            // ApplicationWindow window = I.make(windowClass); // This is singleton!
        }
    }

    /**
     * <p>
     * Activate application window.
     * </p>
     * 
     * @param windowClass
     * @return
     */
    public ApplicationWindow open(Class<? extends ApplicationWindow> windowClass) {
        ApplicationWindow window = I.make(windowClass);

        // check duplication
        if (!actives.addIfAbsent(windowClass)) {
            window.active();

            return window; // This is singleton!
        }

        // Assign closing ability.
        window.shell.addShellListener(new Deactivator(windowClass));

        // open window
        window.open();

        // API definition
        return window;
    }

    /**
     * <p>
     * Launch your application gracefully.
     * </p>
     * 
     * @param applicationClass
     */
    public static void activate(Class<? extends Application> applicationClass, String... params) {
        activate(applicationClass, null, params);
    }

    /**
     * <p>
     * Launch your application gracefully.
     * </p>
     * 
     * @param applicationClass
     */
    public static void activate(Class<? extends Application> applicationClass, ActivationPolicy policy, String... params) {
        try {
            new Activator(applicationClass, policy, params);
        } catch (Throwable e) {
            // handle appication error
            Path path = ClassUtil.getArchive(applicationClass);

            if (Files.isDirectory(path)) {
                e.printStackTrace(System.out);
            } else {
                try {
                    PrintWriter writer = new PrintWriter(Files.newBufferedWriter(I.locate("error.log"), I.$encoding));
                    e.printStackTrace(writer);
                    writer.close();
                } catch (IOException io) {
                    System.out.println(io);
                }
            }
        }
    }

    /**
     * @version 2012/03/02 22:57:22
     */
    protected static enum ActivationPolicy {

        /**
         * Continue to process the earliest application. The subsequent applications will not start
         * up.
         */
        Earliest,

        /**
         * Application has multiple processes.
         */
        Multiple,

        /**
         * Terminate the prior applications immediately, then the latest application will start up.
         */
        Latest;
    }

    /**
     * <p>
     * Application Activation Manager.
     * </p>
     * 
     * @version 2012/01/05 17:27:40
     */
    private static class Activator {

        /** The current running application. */
        private Application application;

        /** The termination flag. */
        private volatile boolean terminate = false;

        /**
         * <p>
         * Hide constructor.
         * </p>
         * 
         * @param applicationClass
         * @param policy
         */
        private Activator(Class<? extends Application> applicationClass, ActivationPolicy policy, String[] params) {
            if (applicationClass == null) {
                throw new NullPointerException("No application!");
            }

            if (policy == null) {
                policy = ActivationPolicy.Earliest;
            }

            // =====================================================================
            // Validation Phase
            // =====================================================================
            if (policy != ActivationPolicy.Multiple) {
                try {
                    // create application specified directory for lock
                    Path root = I.locate(System.getProperty("java.io.tmpdir")).resolve(applicationClass.getName());

                    if (Files.notExists(root)) {
                        Files.createDirectory(root);
                    }

                    // try to retrieve lock to validate
                    @SuppressWarnings("resource")
                    FileChannel channel = new RandomAccessFile(root.resolve("lock").toFile(), "rw").getChannel();

                    @SuppressWarnings("resource")
                    FileLock lock = channel.tryLock();

                    if (lock == null) {
                        // another application is activated
                        if (policy == ActivationPolicy.Earliest) {
                            // make the window active
                            touch(root.resolve("active"));

                            return;
                        } else {
                            // close the window
                            touch(root.resolve("close"));

                            // wait for shutdown previous application
                            channel.lock();
                        }
                    }

                    // observe lock directory for next application
                    I.observe(root)
                            .to(e -> {
                                if (e.kind() == StandardWatchEventKinds.ENTRY_CREATE || e.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                                    notify(e.context());
                                }
                            });
                } catch (Exception e) {
                    throw I.quiet(e);
                }
            }

            // =====================================================================
            // Launching Phase
            // =====================================================================
            // load application library
            I.load(ClassUtil.getArchive(applicationClass));

            // setup application
            application = I.make(applicationClass);

            // Create UI and Visualize actual widgets.
            application.open(application.initialApplicationWindow);

            while (application.actives.size() != 0 && !terminate) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }

            // Clean up resources.
            display.dispose();
        }

        /**
         * <p>
         * Implements the same behaviour as the "touch" utility on Unix. It creates a new file with
         * size 0 or, if the file exists already, it is opened and closed without modifying it, but
         * updating the file date and time.
         * </p>
         * 
         * @param path
         */
        private void touch(Path path) throws IOException {
            if (Files.exists(path)) {
                Files.setLastModifiedTime(path, FileTime.fromMillis(System.currentTimeMillis()));
            } else {
                Files.createFile(path);
            }
        }

        /**
         * <p>
         * Notify modification.
         * </p>
         * 
         * @param path
         */
        private void notify(Path path) {
            String message = path.getFileName().toString();

            if (message.equals("active")) {
                // active window
                I.make(application.actives.get(0)).active();
            } else if (message.equals("close") && Files.exists(path)) {
                // close window
                terminate = true;

                // wake up if ui thread is sleeping
                display.wake();
            }
        }
    }

    /**
     * @version 2012/03/02 15:17:34
     */
    private class Deactivator implements ShellListener {

        /** The window model type. */
        private final Class<? extends ApplicationWindow> type;

        /**
         * @param type
         */
        private Deactivator(Class<? extends ApplicationWindow> type) {
            this.type = type;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void shellActivated(ShellEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void shellClosed(ShellEvent event) {
            close(type);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void shellDeactivated(ShellEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void shellDeiconified(ShellEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void shellIconified(ShellEvent event) {
        }
    }
}
