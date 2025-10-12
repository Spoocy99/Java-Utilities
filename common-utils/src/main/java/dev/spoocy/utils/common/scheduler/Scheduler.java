package dev.spoocy.utils.common.scheduler;

import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.scheduler.task.Task;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class Scheduler {

    private static IScheduler scheduler;
    private static final ILogger logger = ILogger.forThisClass();

    private static IScheduler getScheduler() {
        if (scheduler == null) {
            logger.debug("Scheduler is not set, using JavaScheduler.");
            setScheduler(new JavaScheduler());
        }
        return scheduler;
    }

    public static void setScheduler(IScheduler scheduler) {
        Scheduler.scheduler = scheduler;
    }

    public static void executeSync(Runnable runnable) {
        getScheduler().executeSync(runnable);
    }

    public static void executeAsync(Runnable runnable) {
        getScheduler().executeAsync(runnable);
    }

    public static <V> Task<V> runSync(Runnable runnable) {
        return getScheduler().runSync(runnable);
    }

    public static <V> Task<V> runAsync(Runnable runnable) {
        return getScheduler().runAsync(runnable);
    }

    public static <V> Task<V> runDelayed(Runnable runnable, long delay, TimeUnit unit) {
        return getScheduler().runDelayed(runnable, delay, unit);
    }

    public static <V> Task<V> runSyncCallable(Callable<V> callable) {
        return getScheduler().runSyncCallable(callable);
    }

    public static <V> Task<V> runAsyncCallable(Callable<V> callable) {
        return getScheduler().runAsyncCallable(callable);
    }

    public static <V> Task<V> runDelayedCallable(Callable<V> callable, long delay, TimeUnit unit) {
        return getScheduler().runDelayedCallable(callable, delay, unit);
    }

    public static <V> Task<V> callSyncSupplier(Supplier<V> supplier) {
        return getScheduler().runSyncSupplier(supplier);
    }

    public static <V> Task<V> callAsyncSupplier(Supplier<V> supplier) {
        return getScheduler().runAsyncSupplier(supplier);
    }

    public static <V> Task<V> callDelayedSupplier(Supplier<V> supplier, long delay, TimeUnit unit) {
        return getScheduler().runDelayedSupplier(supplier, delay, unit);
    }

    @NotNull
    public static  ScheduledExecutorService newScheduledThreadPool(int i) {
        return Executors.newScheduledThreadPool(i);
    }
}
