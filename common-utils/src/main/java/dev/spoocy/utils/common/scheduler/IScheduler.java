package dev.spoocy.utils.common.scheduler;

import dev.spoocy.utils.common.scheduler.task.Task;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface IScheduler {

    /**
     * Executes the given runnable synchronously.
     *
     * @param runnable the runnable to execute
     */
    void executeSync(@NotNull Runnable runnable);

    /**
     * Executes the given runnable asynchronously.
     *
     * @param runnable the runnable to execute
     */
    void executeAsync(@NotNull Runnable runnable);

    /**
     * Schedules a runnable to be executed synchronously.
     *
     * @param runnable the runnable to execute
     * @param <V> the type of the value returned by the runnable
     * @return the task representing the runnable
     */
    <V> Task<V> runSync(@NotNull Runnable runnable);

    /**
     * Schedules a runnable to be executed asynchronously.
     *
     * @param runnable the runnable to execute
     * @param <V> the type of the value returned by the runnable
     * @return the task representing the runnable
     */
    <V> Task<V> runAsync(@NotNull Runnable runnable);

    /**
     * Schedules a runnable to be executed asynchronously after a delay.
     *
     * @param runnable the runnable to execute
     * @param delay the delay before execution
     * @param unit the unit of the delay
     * @param <V> the type of the value returned by the runnable
     * @return the task representing the runnable
     */
    <V> Task<V> runDelayed(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit);

    /**
     * Schedules a callable to be executed synchronously.
     *
     * @param callable the callable to execute
     * @param <V> the type of the value returned by the callable
     * @return the task representing the callable
     */
    <V> Task<V> runSyncCallable(@NotNull Callable<V> callable);

    /**
     * Schedules a callable to be executed asynchronously.
     *
     * @param callable the callable to execute
     * @param <V> the type of the value returned by the callable
     * @return the task representing the callable
     */
    <V> Task<V> runAsyncCallable(@NotNull Callable<V> callable);

    /**
     * Schedules a callable to be executed asynchronously after a delay.
     *
     * @param callable the callable to execute
     * @param delay the delay before execution
     * @param unit the unit of the delay
     * @param <V> the type of the value returned by the callable
     * @return the task representing the callable
     */
    <V> Task<V> runDelayedCallable(@NotNull Callable<V> callable, long delay, @NotNull TimeUnit unit);

    /**
     * Schedules a supplier to be executed synchronously.
     *
     * @param supplier the supplier to execute
     * @param <V> the type of the value returned by the supplier
     * @return the task representing the supplier
     */
    <V> Task<V> runSyncSupplier(@NotNull Supplier<V> supplier);

    /**
     * Schedules a supplier to be executed asynchronously.
     *
     * @param supplier the supplier to execute
     * @param <V> the type of the value returned by the supplier
     * @return the task representing the supplier
     */
    <V> Task<V> runAsyncSupplier(@NotNull Supplier<V> supplier);

    /**
     * Schedules a supplier to be executed asynchronously after a delay.
     *
     * @param supplier the supplier to execute
     * @param delay the delay before execution
     * @param unit the unit of the delay
     * @param <V> the type of the value returned by the supplier
     * @return the task representing the supplier
     */
    <V> Task<V> runDelayedSupplier(@NotNull Supplier<V> supplier, long delay, @NotNull TimeUnit unit);

}
