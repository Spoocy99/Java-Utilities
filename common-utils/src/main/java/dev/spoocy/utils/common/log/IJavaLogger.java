package dev.spoocy.utils.common.log;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface IJavaLogger extends ILogger {

    LogLevel getTranslatedLevel(LogLevel level);

}
