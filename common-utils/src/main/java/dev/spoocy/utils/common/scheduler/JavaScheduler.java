package dev.spoocy.utils.common.scheduler;

import dev.spoocy.utils.common.scheduler.task.ScheduledTask;
import dev.spoocy.utils.common.scheduler.task.Task;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class JavaScheduler implements IScheduler {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);

    @Override
    public void executeSync(@NotNull Runnable runnable) {
        runnable.run();
    }

    @Override
    public void executeAsync(@NotNull Runnable runnable) {
        EXECUTOR_SERVICE.execute(runnable);
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
        Task<V> task = ScheduledTask.create();

        try {
            task.complete(callable.call());
        } catch (Throwable ex) {
            task.fail(ex);
        }

        return task;
    }

    private <V> Task<V> callAsync(@NotNull Callable<V> callable) {
        Task<V> task = ScheduledTask.create();

		EXECUTOR_SERVICE.execute( ()-> {
                try {
                    task.complete(callable.call());
                } catch (Throwable ex) {
                    task.fail(ex);
                }
            });

		return task;
    }

    private <V> Task<V> callAsyncDelayed(@NotNull Callable<V> callable, long delay, TimeUnit unit) {
        Task<V> task = ScheduledTask.create();

        SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
            try {
                task.complete(callable.call());
            } catch (Throwable ex) {
                task.fail(ex);
            }
        }, delay, unit);

        return task;
    }

}
