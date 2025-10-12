package dev.spoocy.utils.common.log.factory;

import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.log.ILoggerFactory;
import dev.spoocy.utils.common.log.LogLevel;
import dev.spoocy.utils.common.log.logger.Slf4jLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class Slf4jLoggerFactory implements ILoggerFactory {

    protected final ConcurrentHashMap<String, ILogger> loggers = new ConcurrentHashMap<>();

    public Slf4jLoggerFactory() { }

    @Override
    public @NotNull ILogger getOrCreateLogger(@Nullable String name) {
        return loggers.computeIfAbsent(name, log -> new Slf4jLogger(
                LoggerFactory.getLogger(check(name))
        ));
    }

    @Override
    public ILoggerFactory setLevel(@NotNull LogLevel level) {
        // not possible to set Level at Runtime
        return this;
    }

    private String check(String caller) {
        return caller != null ? caller : "Logger";
    }

}
