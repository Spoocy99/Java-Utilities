package dev.spoocy.utils.common.scheduler.task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ScheduledTask<V> implements Task<V>, Callable<V>, Future<V> {

    public static <V> ScheduledTask<V> create() {
        return new ScheduledTask<>();
    }

    private final List<TaskResult<V>> results;
    private final CompletableFuture<V> future;
    private Throwable exceptionWhileExecution;

	private ScheduledTask() {
		this.future = new CompletableFuture<>();
        this.results = new CopyOnWriteArrayList<>();
	}

    @Override
    public boolean isDone() {
        return this.future.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return this.future.get();
    }

    @Override
    public V get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.future.get(timeout, unit);
    }

    @Override
    public boolean isCancelled() {
        return this.future.isCancelled();
    }

    @Override
    public boolean isFailed() {
        return this.exceptionWhileExecution != null;
    }

    @Override
    public Task<V> onSuccess(@NotNull Runnable runnable) {
        return onSuccess((task, value) -> runnable.run());
    }

    @Override
    public Task<V> onSuccess(@NotNull Consumer<V> consumer) {
        return addResult(new TaskResult<>() {

            @Override
            public void onSuccess(@NotNull Task<V> task, V value) {
                consumer.accept(value);
            }

        });
    }

    @Override
    public Task<V> onSuccess(@NotNull BiConsumer<? super Task<V>, V> consumer) {
        return addResult(new TaskResult<>() {

            @Override
            public void onSuccess(@NotNull Task<V> task, V value) {
                consumer.accept(task, value);
            }

        });
    }

    @Override
    public Task<V> onCancelled(@NotNull Runnable runnable) {
        return onCancelled(task -> runnable.run());
    }

    @Override
    public Task<V> onCancelled(@NotNull Consumer<? super Task<V>> consumer) {
		return addResult(new TaskResult<>() {

            @Override
            public void onCancelled(@NotNull Task<V> task) {
                consumer.accept(task);
            }

        });
	}

    @Override
    public Task<V> onException(@NotNull Runnable runnable) {
        return onException((task, throwable) -> runnable.run());
    }

    @Override
    public Task<V> onException(@NotNull Consumer<? super Throwable> consumer) {
        return addResult(new TaskResult<>() {

            @Override
            public void onException(@NotNull Task<V> task, Throwable throwable) {
                consumer.accept(throwable);
            }

        });
    }

    @Override
    public Task<V> onException(@NotNull BiConsumer<? super Task<V>, ? super Throwable> consumer) {
        return addResult(new TaskResult<>() {

            @Override
            public void onException(@NotNull Task<V> task, Throwable throwable) {
                consumer.accept(task, throwable);
            }

        });
    }

    @Override
    public void complete(@Nullable V value) {
        future.complete(value);

		if (isCancelled()) {
            results.forEach(result -> result.onCancelled(this));
            return;
		}

        results.forEach(result -> result.onSuccess(this, value));
    }

    @Override
    public void fail(@NotNull Throwable throwable) {
        this.exceptionWhileExecution = throwable;
		this.future.completeExceptionally(throwable);
		results.forEach(result -> result.onException(this, throwable));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (isCancelled()) {
			return false;
		}

		if (this.future.cancel(mayInterruptIfRunning)) {
            results.forEach(result -> result.onCancelled(this));
			return true;
		}

		return false;
    }

    public ScheduledTask<V> clearResults() {
        this.results.clear();
        return this;
    }

    public ScheduledTask<V> addResult(@NotNull TaskResult<V> result) {
        check(result);
		results.add(result);
        return this;
    }

    private void check(@NotNull TaskResult<V> result) {
        if (!future.isDone()) return;

        if (isCancelled()) {
            result.onCancelled(this);
            return;
        }

        if (isFailed()) {
            result.onException(this, exceptionWhileExecution);
            return;
        }

        V value = future.getNow(null);
        result.onSuccess(this, value);
    }

    @Override
    public V call() {
		if (isDone()) {
			return this.future.getNow(null);
		}
        throw new UnsupportedOperationException("ScheduledTask is not done yet!");
    }
}
