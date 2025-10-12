package dev.spoocy.utils.common.log.logger;

import dev.spoocy.utils.common.log.IJavaLogger;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.log.LogLevel;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class JavaLogger implements IJavaLogger {

    private final java.util.logging.Logger logger;

    public JavaLogger(@NotNull String name) {
        this.logger = java.util.logging.Logger.getLogger(name);
    }

    public JavaLogger(@NotNull java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public String getName() {
        return this.logger.getName();
    }

    @Override
    public LogLevel getLevel() {
        return LogLevel.byJavaLevel(this.logger.getLevel());
    }

    @Override
    public ILogger setLevel(@NotNull LogLevel level) {
        this.logger.setLevel(level.getJavaLevel());
        return this;
    }

    @Override
    public void log(@NotNull LogLevel level, @NotNull String message, Object... args) {
        Throwable throwable = null;
        Level l = getTranslatedLevel(level).getJavaLevel();

		for (Object arg : args) {
			if (arg instanceof Throwable) {
                throwable = (Throwable) arg;
                break;
            }
		}

        if (throwable != null) {
            logger.log(l, message, throwable);
            return;
        }

        logger.log(l, message);
    }

    @Override
    public LogLevel getTranslatedLevel(LogLevel level) {
        // Prevents logging below INFO level if the current level is above INFO
        if(!LogLevel.INFO.covers(level) && getLevel().covers(level)) return LogLevel.INFO;
        return level;
    }
}
