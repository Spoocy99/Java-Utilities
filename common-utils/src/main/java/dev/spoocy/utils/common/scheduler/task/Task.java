package dev.spoocy.utils.common.scheduler.task;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents a unit of work that can be executed, tracked, and monitored for its completion status.
 * This interface provides methods to check the task's state (e.g., completed, cancelled, or failed)
 * and to attach callback mechanisms for responding to various outcomes of the task execution.
 *
 * @param <V> the type of the result produced by the task
 *
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
    Task<V> onSuccess(@NotNull Runnable runnable);

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
    Task<V> onCancelled(@NotNull Runnable runnable);

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
    Task<V> onException(@NotNull Runnable runnable);

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

}
