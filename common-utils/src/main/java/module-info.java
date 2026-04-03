/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

module dev.spoocy.utils.common {
    requires static org.jetbrains.annotations;
    requires static org.slf4j;
    requires java.logging;

    exports dev.spoocy.utils.common.cache;
    exports dev.spoocy.utils.common.collections;
    exports dev.spoocy.utils.common.exceptions;
    exports dev.spoocy.utils.common.log;
    exports dev.spoocy.utils.common.log.factory;
    exports dev.spoocy.utils.common.log.logger;
    exports dev.spoocy.utils.common.log.report;
    exports dev.spoocy.utils.common.misc;
    exports dev.spoocy.utils.common.scheduler.task;
    exports dev.spoocy.utils.common.scheduler;
    exports dev.spoocy.utils.common.text;
    exports dev.spoocy.utils.common.tuple;
    exports dev.spoocy.utils.common.version;
}