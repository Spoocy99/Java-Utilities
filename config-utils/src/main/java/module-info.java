/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

module dev.spoocy.utils.config {
    requires org.jetbrains.annotations;
    requires org.json;
    requires dev.spoocy.utils.common;
    requires dev.spoocy.utils.reflection;

    exports dev.spoocy.utils.config;
    exports dev.spoocy.utils.config.types;
    exports dev.spoocy.utils.config.misc;
    exports dev.spoocy.utils.config.serializer;
    exports dev.spoocy.utils.config.serializer.impl;
    exports dev.spoocy.utils.config.components;
}