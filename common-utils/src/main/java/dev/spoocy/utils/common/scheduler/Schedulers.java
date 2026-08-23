package dev.spoocy.utils.common.scheduler;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Factory and helper methods for creating scheduler instances.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public final class Schedulers {

    private Schedulers() {
        throw new UnsupportedOperationException("This utility class cannot be instantiated");
    }

    @NotNull
    public static Scheduler javaScheduler(int corePoolSize) {
        return new JavaScheduler(corePoolSize);
    }

    @NotNull
    public static Scheduler javaScheduler(@NotNull ExecutorService executorService,
                                          @NotNull ScheduledExecutorService scheduledExecutorService) {
        return new JavaScheduler(executorService, scheduledExecutorService);
    }

    @NotNull
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        return Executors.newScheduledThreadPool(corePoolSize);
    }
}

