package dev.spoocy.utils.common.log.logger;

import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.log.LogLevel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.spi.LocationAwareLogger;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class Slf4jLogger implements ILogger {

    private static final String FQCN = Slf4jLogger.class.getName();
    private final Logger logger;
    private final boolean isLocationAware;

    public Slf4jLogger(@NotNull Logger logger) {
        this.logger = logger;
        this.isLocationAware = logger instanceof LocationAwareLogger;
    }

    @Override
    public String getName() {
        return this.logger.getName();
    }

    @Override
    public LogLevel getLevel() {
        if (logger.isTraceEnabled()) {
            return LogLevel.TRACE;
        }
        if (logger.isDebugEnabled()) {
            return LogLevel.DEBUG;
        }
        if (logger.isInfoEnabled()) {
            return LogLevel.INFO;
        }
        if (logger.isWarnEnabled()) {
            return LogLevel.WARN;
        }
        if (logger.isErrorEnabled()) {
            return LogLevel.ERROR;
        }
        return LogLevel.DEFAULT_LEVEL;
    }

    @Override
    public ILogger setLevel(@NotNull LogLevel level) {
        // not possible to set Level at Runtime
        return this;
    }

    @Override
    public void log(@NotNull LogLevel level, @NotNull String message, Object... args) {
        if(!this.getLevel().covers(level)) return;

        switch (level) {
            case ALL:
            case TRACE:

                if (this.isLocationAware) {
                    ((LocationAwareLogger) this.logger).log(null, FQCN, LocationAwareLogger.TRACE_INT, message, args, null);
                } else {
                    this.logger.trace(message, args);
                }

                break;
            case DEBUG:


                if (this.isLocationAware) {
                    ((LocationAwareLogger) this.logger).log(null, FQCN, LocationAwareLogger.DEBUG_INT, message, args, null);
                } else {
                    this.logger.debug(message, args);
                }

                break;
            case INFO:

                if (this.isLocationAware) {
                    ((LocationAwareLogger) this.logger).log(null, FQCN, LocationAwareLogger.INFO_INT, message, args, null);
                } else {
                    this.logger.info(message, args);
                }


                break;
            case WARN:

                if (this.isLocationAware) {
                    ((LocationAwareLogger) this.logger).log(null, FQCN, LocationAwareLogger.WARN_INT, message, args, null);
                } else {
                    this.logger.warn(message, args);
                }

                break;
            case ERROR:

                if (this.isLocationAware) {
                    ((LocationAwareLogger) this.logger).log(null, FQCN, LocationAwareLogger.ERROR_INT, message, args, null);
                } else {
                    this.logger.error(message, args);
                }

                break;
        }
    }
}
