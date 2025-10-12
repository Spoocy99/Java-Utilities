package dev.spoocy.utils.common.log.factory;

import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.log.logger.JavaLogger;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class JavaLoggerFactory extends DefaultLoggerFactory {

    @Override
    protected ILogger createLogger(String name) {
        return new JavaLogger(name);
    }

}
