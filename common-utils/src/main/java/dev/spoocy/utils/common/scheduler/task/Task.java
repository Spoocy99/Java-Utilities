package dev.spoocy.utils.common.scheduler.task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Task<V> {

    /**
     * Returns whether the task has been completed.
     *
     * @return {@code true} if the task has been completed, {@code false} otherwise
     */
    boolean isDone();

    /**
     * Returns whether the task has been cancelled.
     *
     * @return {@code true} if the task has been cancelled, {@code false} otherwise
     */
    boolean isCancelled();

    /**
     * Returns whether the task has failed.
     *
     * @return {@code true} if the task has failed, {@code false} otherwise
     */
    boolean isFailed();

    /**
     * Executes the given runnable when the task is successfully completed.
     *
     * @param runnable the runnable to execute
     *
     * @return this task for chaining
     */
    Task<V> onSuccess(Runnable runnable);

    /**
     * Executes the given consumer when the task is successfully completed.
     *
     * @param consumer the consumer to execute with the task and the value
     *
     * @return this task for chaining
     */
    Task<V> onSuccess(@NotNull Consumer<V> consumer);

    /**
     * Executes the given consumer when the task is successfully completed.
     *
     * @param consumer the consumer to execute with the task and the value
     *
     * @return this task for chaining
     */
    Task<V> onSuccess(@NotNull BiConsumer<? super Task<V>, V> consumer);

    /**
     * Executes the given runnable when the task fails to complete.
     *
     * @param runnable the runnable to execute
     *
     * @return this task for chaining
     */
    Task<V> onCancelled(Runnable runnable);

    /**
     * Executes the given consumer when the task is cancelled.
     *
     * @param consumer the consumer to execute with the task
     *
     * @return this task for chaining
     */
	Task<V> onCancelled(@NotNull Consumer<? super Task<V>> consumer);

    /**
     * Executes the given runnable when the task is cancelled.
     *
     * @param runnable the runnable to execute
     *
     * @return this task for chaining
     */
    Task<V> onException(Runnable runnable);

    /**
     * Executes the given consumer when the task fails to complete.
     *
     * @param consumer the consumer to execute with the exception
     *
     * @return this task for chaining
     */
    Task<V> onException(@NotNull Consumer<? super Throwable> consumer);

    /**
     * Executes the given consumer when the task fails to complete.
     *
     * @param consumer the consumer to execute with the task and the exception
     *
     * @return this task for chaining
     */
    Task<V> onException(@NotNull BiConsumer<? super Task<V>, ? super Throwable> consumer);

    /**
     * Completes the task with the given value.
     *
     * @param value the value to complete the task with
     */
    void complete(@Nullable V value);

    /**
     * Fails the task with the given exception.
     *
     * @param throwable the exception to fail the task with
     */
    void fail(@NotNull Throwable throwable);

    /**
     * Cancels the task.
     *
     * @param mayInterruptIfRunning whether the task should be interrupted if it is running
     *
     * @return {@code true} if the task is now cancelled, {@code false} otherwise
     */
    boolean cancel(boolean mayInterruptIfRunning);

}
