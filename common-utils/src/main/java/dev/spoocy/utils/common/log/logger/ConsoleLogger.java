package dev.spoocy.utils.common.log.logger;

import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.log.LogLevel;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ConsoleLogger implements ILogger {

    protected final String name;
    protected LogLevel level;

    public ConsoleLogger(@NotNull String name) {
        this.name = name;
        this.level = LogLevel.DEFAULT_LEVEL;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public LogLevel getLevel() {
        return this.level;
    }

    @Override
    public ILogger setLevel(@NotNull LogLevel level) {
        this.level = level;
        return this;
    }

    @Override
    public void log(@NotNull LogLevel level, @NotNull String message, Object... args) {
        if(!isLogLevelCovered(level)) return;
        System.err.println(formatLoggerMessage(level, message));

        for(Object arg : args) {
            if(arg instanceof Throwable) ((Throwable) arg).printStackTrace();
        }
    }

    private String formatLoggerMessage(@NotNull LogLevel level, @NotNull String message) {
		String time = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		String name = Thread.currentThread().getName();
		return String.format("[%s: %s/%s] %s: %s", time, name, level.getName(), this.name, message);
    }
}
