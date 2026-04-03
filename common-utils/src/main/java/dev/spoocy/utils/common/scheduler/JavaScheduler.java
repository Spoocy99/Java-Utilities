package dev.spoocy.utils.common.scheduler;

import dev.spoocy.utils.common.scheduler.task.CompletableTask;
import dev.spoocy.utils.common.scheduler.task.Task;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class JavaScheduler implements Scheduler, AutoCloseable {

    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;
    private volatile boolean shutdown = false;

    public JavaScheduler(int corePoolSize) {
        this(Executors.newCachedThreadPool(), Executors.newScheduledThreadPool(corePoolSize));
    }

    public JavaScheduler(ExecutorService executorService, ScheduledExecutorService scheduledExecutorService) {
        this.executorService = executorService;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public void executeSync(@NotNull Runnable runnable) {
        runnable.run();
    }

    @Override
    public void executeAsync(@NotNull Runnable runnable) {
        checkShutdown();
        executorService.execute(runnable);
    }

    @Override
    public <V> Task<V> runSync(@NotNull Runnable runnable) {
        return callSync(() -> {
            runnable.run();
            return null;
        });
    }

    @Override
    public <V> Task<V> runAsync(@NotNull Runnable runnable) {
        return callAsync(() -> {
            runnable.run();
            return null;
        });
    }

    @Override
    public <V> Task<V> runDelayed(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit) {
        return callAsyncDelayed(() -> {
            runnable.run();
            return null;
        }, delay, unit);
    }

    @Override
    public <V> Task<V> runSyncCallable(@NotNull Callable<V> callable) {
        return callSync(callable);
    }

    @Override
    public <V> Task<V> runAsyncCallable(@NotNull Callable<V> callable) {
        return callAsync(callable);
    }

    @Override
    public <V> Task<V> runDelayedCallable(@NotNull Callable<V> callable, long delay, @NotNull TimeUnit unit) {
        return callAsyncDelayed(callable, delay, unit);
    }

    @Override
    public <V> Task<V> runSyncSupplier(@NotNull Supplier<V> supplier) {
        return callSync(supplier::get);
    }

    @Override
    public <V> Task<V> runAsyncSupplier(@NotNull Supplier<V> supplier) {
        return callAsync(supplier::get);
    }

    @Override
    public <V> Task<V> runDelayedSupplier(@NotNull Supplier<V> supplier, long delay, @NotNull TimeUnit unit) {
        return callAsyncDelayed(supplier::get, delay, unit);
    }

    private <V> Task<V> callSync(@NotNull Callable<V> callable) {
        CompletableTask<V> task = CompletableTask.empty();

        try {
            task.complete(callable.call());
        } catch (Throwable ex) {
            task.fail(ex);
        }

        return task;
    }

    private <V> Task<V> callAsync(@NotNull Callable<V> callable) {
        checkShutdown();
        CompletableTask<V> task = CompletableTask.empty();

        executorService.execute(() -> {
            try {
                task.complete(callable.call());
            } catch (Throwable ex) {
                task.fail(ex);
            }
        });

        return task;
    }

    private <V> Task<V> callAsyncDelayed(@NotNull Callable<V> callable, long delay, TimeUnit unit) {
        checkShutdown();
        CompletableTask<V> task = CompletableTask.empty();

        scheduledExecutorService.schedule(() -> {
            try {
                task.complete(callable.call());
            } catch (Throwable ex) {
                task.fail(ex);
            }
        }, delay, unit);

        return task;
    }

    private void checkShutdown() {
        if (shutdown) {
            throw new IllegalStateException("JavaScheduler has been shut down");
        }
    }

    @Override
    public void close() throws Exception {
        if (shutdown) {
            return;
        }

        shutdown = true;

        // Shutdown executor service
        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        // Shutdown scheduled executor service
        scheduledExecutorService.shutdown();
        if (!scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS)) {
            scheduledExecutorService.shutdownNow();
        }
    }

    /**
     * Shuts down the scheduler immediately without waiting for termination.
     * Useful when graceful shutdown is not required.
     */
    public void shutdown() {
        if (shutdown) {
            return;
        }

        shutdown = true;
        executorService.shutdownNow();
        scheduledExecutorService.shutdownNow();
    }
}
