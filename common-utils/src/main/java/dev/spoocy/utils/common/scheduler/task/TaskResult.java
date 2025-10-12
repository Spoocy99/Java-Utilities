package dev.spoocy.utils.common.scheduler.task;

import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface TaskResult<V> {

    /**
     * This method is called when the task is successfully completed.
     *
     * @param task the task that was completed
     * @param value the value that was returned by the task
     */
    default void onSuccess(@NotNull Task<V> task, V value) { }

    /**
     * This method is called when the task fails to complete.
     *
     * @param task the task that failed
     * @param throwable the exception that was thrown
     */
    default void onException(@NotNull Task<V> task, Throwable throwable) { }

    /**
     * This method is called when the task is cancelled.
     *
     * @param task the task that was cancelled
     */
    default void onCancelled(@NotNull Task<V> task) { }
}
