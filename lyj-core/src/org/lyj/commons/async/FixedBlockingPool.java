package org.lyj.commons.async;

import org.lyj.commons.Delegates;

import java.util.concurrent.*;

/**
 * Execute threads in a fixed pool with a bounded queue to avoid memory consumption.
 * One queue is full, the queue is blocked until next action.
 */
public class FixedBlockingPool {

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private int _capacity;
    private int _core_pool_size;
    private int _maximum_pool_size;
    private int _keep_alive_time;
    private TimeUnit _keep_alive_time_unit;
    private int _thread_priority;

    private ExecutorService __executor;
    private ExecutorMonitor _executor_monitor;
    private Delegates.Callback<ExecutorMonitor> _monitor_callback;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public FixedBlockingPool() {
        _capacity = 100; // starts with 100 threads in queue

        _core_pool_size = 5;
        _maximum_pool_size = 10;
        _keep_alive_time = 30;
        _keep_alive_time_unit = TimeUnit.MINUTES;
        _thread_priority = Thread.MIN_PRIORITY;
    }

    // --------------------------------------------------------------------
    //               p r o p e r t i e s
    // --------------------------------------------------------------------

    /**
     * Queue capacity. Once queue limit is reached, other threads are blocked until queue has idle threads for the job.
     *
     * @param value capacity of the blocking queue.
     * @return
     */
    public FixedBlockingPool capacity(final int value) {
        _capacity = value;
        return this;
    }

    /**
     * The number of threads to keep in the pool, even
     * if they are idle.
     *
     * @param value
     * @return
     */
    public FixedBlockingPool corePoolSize(final int value) {
        _core_pool_size = value;
        return this;
    }

    /**
     * The maximum number of threads to allow in the  pool
     *
     * @param value
     * @return
     */
    public FixedBlockingPool maximumPoolSize(final int value) {
        _maximum_pool_size = value;
        return this;
    }

    /**
     * When the number of threads is greater than
     * the core, this is the maximum time that excess idle threads
     * will wait for new tasks before terminating.
     *
     * @param value
     * @param unit
     * @return
     */
    public FixedBlockingPool keepAliveTime(final int value, final TimeUnit unit) {
        _keep_alive_time = value;
        _keep_alive_time_unit = unit;
        return this;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public FixedBlockingPool monitor(final Delegates.Callback<ExecutorMonitor> callback) {
        _monitor_callback = callback;
        return this;
    }

    public FixedBlockingPool start(final Delegates.Handler callback) {
        if (null != callback) {
            this.run(callback);
        }
        return this;
    }

    public <T> Future<T> submit(final Callable<T> callable) {
        if (null != callable) {
            return this.run(callable);
        }
        return null;
    }

    public Future<?> submit(final Runnable runnable) {
        if (null != runnable) {
            return this.run(runnable);
        }
        return null;
    }

    public FixedBlockingPool stop() {
        return this.stop(true);
    }

    public FixedBlockingPool stop(final boolean interruptIfRunning) {
        if (null != __executor) {
            if (interruptIfRunning) {
                __executor.shutdownNow();
            } else {
                __executor.shutdown();
            }
            if (__executor instanceof ThreadPoolExecutor) {
                ((ThreadPoolExecutor) __executor).purge();
            }
        }
        if (null != _executor_monitor) {
            _executor_monitor.stop();
        }
        return this;
    }

    public void join(final long timeout) throws InterruptedException {
        if (null != __executor && !__executor.isTerminated()) {
            __executor.awaitTermination(timeout, TimeUnit.MILLISECONDS);
        }
        if (null != _executor_monitor) {
            _executor_monitor.stop();
        }
    }

    public boolean isTerminated() {
        if (null != __executor) {
            return __executor.isTerminated();
        }
        return true;
    }

    public int getActiveCount() {
        if (__executor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor) __executor).getActiveCount();
        }
        return 0;
    }

    public long getCompletedCount() {
        if (__executor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor) __executor).getCompletedTaskCount();
        }
        return 0;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private ExecutorService executor() {
        if (null == __executor) {
            final BlockingQueue<Runnable> linkedBlockingDeque = new LinkedBlockingDeque<>(_capacity);
            ThreadFactory threadFactory = Executors.defaultThreadFactory();
            __executor = new ThreadPoolExecutor(_core_pool_size, _maximum_pool_size,
                    _keep_alive_time, _keep_alive_time_unit,
                    linkedBlockingDeque,
                    threadFactory,
                    new ThreadPoolExecutor.CallerRunsPolicy());
        }
        return __executor;
    }

    private Future<?> run(final Delegates.Handler callback) {
        final Future<?> future = this.executor().submit(new WorkerThread(_thread_priority, callback));
        if (null != _monitor_callback) {
            _executor_monitor = new ExecutorMonitor(this.executor(), _monitor_callback);
            _executor_monitor.run();
        }
        return future;
    }

    private <T> Future<T> run(final Callable<T> task) {
        final Future<T> future = this.executor().submit(task);
        if (null != _monitor_callback) {
            _executor_monitor = new ExecutorMonitor(this.executor(), _monitor_callback);
            _executor_monitor.run();
        }
        return future;
    }

    private Future<?> run(final Runnable task) {
        final Future<?> future = this.executor().submit(task);
        if (null != _monitor_callback) {
            _executor_monitor = new ExecutorMonitor(this.executor(), _monitor_callback);
            _executor_monitor.run();
        }
        return future;
    }

    // --------------------------------------------------------------------
    //               s t a t i c
    // --------------------------------------------------------------------

    public static FixedBlockingPool create() {
        return new FixedBlockingPool();
    }

    // --------------------------------------------------------------------
    //               E M B E D D E D
    // --------------------------------------------------------------------

    // --------------------------------------------------------------------
    //               ExecutorMonitor
    // --------------------------------------------------------------------

    public static class WorkerThread extends Thread {

        private final Delegates.Handler _callback;

        public WorkerThread(final int priority,
                            final Delegates.Handler callback) {
            super.setPriority(priority);
            super.setName("fixed-" + super.getName());
            _callback = callback;
        }

        @Override
        public void run() {
            this.processCommand();
            //System.out.println(Thread.currentThread().getName());
        }

        private void processCommand() {
            try {
                Delegates.invoke(_callback);
            } catch (Throwable t) {

            }
        }

    }

    // --------------------------------------------------------------------
    //               ExecutorMonitor
    // --------------------------------------------------------------------

    public static class ExecutorMonitor {

        private ThreadPoolExecutor _executor;
        private Delegates.Callback<ExecutorMonitor> _callback;
        private boolean _stopped;

        private ExecutorMonitor(final ExecutorService executor, final Delegates.Callback<ExecutorMonitor> callback) {
            if (executor instanceof ThreadPoolExecutor) {
                _executor = (ThreadPoolExecutor) executor;
            }
            _callback = callback;
            _stopped = false;
        }

        private void run() {
            if (null == _callback || null == _executor) {
                return;
            }
            new Thread(() -> {
                try {
                    while (!_stopped) {
                        Delegates.invoke(_callback, this);
                        Thread.sleep(3000);
                    }
                } catch (Throwable ignored) {
                    _stopped = true;
                }
            }).start();
        }

        private void stop() {
            _stopped = true;
        }

        public int activeCount() {
            if (null != _executor) {
                return _executor.getActiveCount();
            }
            return 0;
        }

        public long completedCount() {
            if (null != _executor) {
                return _executor.getCompletedTaskCount();
            }
            return 0;
        }

        public long taskCount() {
            if (null != _executor) {
                return _executor.getTaskCount();
            }
            return 0;
        }

        public int poolSize() {
            if (null != _executor) {
                return _executor.getPoolSize();
            }
            return 0;
        }

        public int corePoolSize() {
            if (null != _executor) {
                return _executor.getCorePoolSize();
            }
            return 0;
        }

        public int largestPoolSize() {
            if (null != _executor) {
                return _executor.getLargestPoolSize();
            }
            return 0;
        }

        public int maximumPoolSize() {
            if (null != _executor) {
                return _executor.getMaximumPoolSize();
            }
            return 0;
        }

    }

}
