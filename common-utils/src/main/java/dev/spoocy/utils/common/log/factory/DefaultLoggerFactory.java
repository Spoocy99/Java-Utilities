package dev.spoocy.utils.common.log.factory;

import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.log.ILoggerFactory;
import dev.spoocy.utils.common.log.LogLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class DefaultLoggerFactory implements ILoggerFactory {

    private LogLevel level;
    private final ConcurrentHashMap<String, ILogger> loggers = new ConcurrentHashMap<>();

    public DefaultLoggerFactory() {
        this.level = LogLevel.DEFAULT_LEVEL;
    }

    @Override
    public @NotNull ILogger getOrCreateLogger(@Nullable String name) {
        name = check(name);
        ILogger logger = loggers.get(name);

        if (logger == null) {
            logger = createLogger(name);
            logger.setLevel(this.level);
            loggers.put(name, logger);
        }

        return logger;
    }

    @Override
    public ILoggerFactory setLevel(@NotNull LogLevel level) {
        this.level = level;
        this.loggers.values().forEach(logger -> logger.setLevel(level));
        return this;
    }

    private String check(String caller) {
        return caller != null ? caller : "anonymous";
    }

    protected abstract ILogger createLogger(String name);
}
