/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

module dev.spoocy.utils.config {
    requires static lombok;

    requires org.jetbrains.annotations;
    requires dev.spoocy.utils.common;
    requires org.json;
    requires dev.spoocy.utils.reflection;

    exports dev.spoocy.utils.config;
    exports dev.spoocy.utils.config.documents;
    exports dev.spoocy.utils.config.misc;
    exports dev.spoocy.utils.config.serializer;
    exports dev.spoocy.utils.config.serializer.impl;
}