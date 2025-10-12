package dev.spoocy.utils.common.log.factory;

import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.log.logger.ConsoleLogger;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ConsoleLoggerFactory extends DefaultLoggerFactory {

    @Override
    protected ILogger createLogger(String name) {
        return new ConsoleLogger(name);
    }

}

