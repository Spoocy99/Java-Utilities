package dev.spoocy.utils.common.log;

import dev.spoocy.utils.common.log.logger.JavaLogger;
import dev.spoocy.utils.common.log.logger.Slf4jLogger;
import dev.spoocy.utils.common.misc.ClassFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ILogger {

    String getName();

    LogLevel getLevel();

    ILogger setLevel(@NotNull LogLevel level);

    void log(@NotNull LogLevel level, @NotNull String message, Object... args);

    default void trace(@NotNull String message, Object... args) {
        log(LogLevel.TRACE, format(message, args), args);
    }

    default void debug(@NotNull String message, Object... args) {
        log(LogLevel.DEBUG, format(message, args), args);
    }

    default void info(@NotNull String message, Object... args) {
        log(LogLevel.INFO, format(message, args), args);
    }

    default void warn(@NotNull String message, Object... args) {
        log(LogLevel.WARN, format(message, args), args);
    }

    default void error(@NotNull String message, Object... args) {
        log(LogLevel.ERROR, format(message, args), args);
    }

    default boolean isLogLevelCovered(@NotNull LogLevel level) {
        return getLevel().covers(level);
    }

    @NotNull
    static ILogger forName(@Nullable String name) {
        return FactoryHolder.getOrCreateLogger(name);
    }

    @NotNull
    static ILogger forClass(@Nullable Class<?> clazz) {
        return forName(clazz == null ? null : clazz.getSimpleName());
    }

    @NotNull
    static ILogger forClass(@Nullable Object object) {
        return forName(object == null ? null : object.getClass().getSimpleName());
    }

    @NotNull
    static ILogger forThisClass() {
        return forName(ClassFinder.callingClassName());
    }

    @NotNull
    static ILogger anonymous() {
        return forName("anonymous");
    }

    @NotNull
    static ILogger global() {
        return forName("Logger");
    }

    @NotNull
    static ILogger java(@NotNull String name) {
        return new JavaLogger(java.util.logging.Logger.getLogger(name));
    }

    @NotNull
    static ILogger forJava(@NotNull java.util.logging.Logger logger) {
        return new JavaLogger(logger);
    }

    @NotNull
    static ILogger forSlf4j(@NotNull String name) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(name));
    }

    @NotNull
    static ILogger forSlf4j(@NotNull org.slf4j.Logger logger) {
        return new Slf4jLogger(logger);
    }

    static String format(@NotNull String message, @Nullable Object... args) {
        if (args == null) {
            return message;
        }

        for (Object arg : args) {
            if (arg instanceof Throwable) continue;

            try {
                message = message.replaceFirst("\\{}",
                        Matcher.quoteReplacement(arg != null ? arg.toString() : "null"));

            } catch (Throwable e) {
                ILogger.forThisClass().error("Error while formatting log message: " + message, e);
                message = message.replaceFirst("\\{}", "error (null)");
            }

        }

        return message;
    }

}
