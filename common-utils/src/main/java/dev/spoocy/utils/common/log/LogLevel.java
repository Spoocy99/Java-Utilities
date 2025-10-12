package dev.spoocy.utils.common.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Getter
@AllArgsConstructor
public enum LogLevel {
    ALL(0, "ALL", Level.ALL),
    TRACE(10, "TRACE", Level.FINER),
    DEBUG(30, "DEBUG", Level.FINE),
    INFO(50, "INFO", Level.INFO),
    WARN(70, "WARN", Level.WARNING),
    ERROR(100, "ERROR", Level.SEVERE),
    OFF(Integer.MAX_VALUE, "OFF", Level.OFF);

    public static final LogLevel DEFAULT_LEVEL = LogLevel.INFO;
    private final int level;
    private final String name;
    private final Level javaLevel;

    public boolean covers(@NotNull LogLevel level) {
        return this.getLevel() <= level.getLevel();
    }

    public boolean covers(int level) {
        return this.getLevel() <= level;
    }

    public static LogLevel byLevel(int level) {
        for (LogLevel logLevel : values()) {
            if (logLevel.getLevel() == level) {
                return logLevel;
            }
        }
        return DEFAULT_LEVEL;
    }

    public static LogLevel byName(@NotNull String name) {
        for (LogLevel logLevel : values()) {
            if (logLevel.getName().equalsIgnoreCase(name)) {
                return logLevel;
            }
        }
        return DEFAULT_LEVEL;
    }

    public static LogLevel byJavaLevel(@NotNull Level level) {
        for (LogLevel logLevel : values()) {
            if (logLevel.getJavaLevel().equals(level)) {
                return logLevel;
            }
        }
        return DEFAULT_LEVEL;
    }

    public static LogLevel getDefaultLevel() {
        return DEFAULT_LEVEL;
    }

}
