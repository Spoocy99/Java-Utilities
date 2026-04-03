/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

module dev.spoocy.utils.config {
    requires static org.jetbrains.annotations;
    requires dev.spoocy.utils.common;
    requires dev.spoocy.utils.reflection;
    requires static org.json;
    requires static org.yaml.snakeyaml;

    exports dev.spoocy.utils.config;
    exports dev.spoocy.utils.config.types;
    exports dev.spoocy.utils.config.serializer;
    exports dev.spoocy.utils.config.serializer.impl;
    exports dev.spoocy.utils.config.components;
    exports dev.spoocy.utils.config.io;
    exports dev.spoocy.utils.config.loader;
    exports dev.spoocy.utils.config.update;
    exports dev.spoocy.utils.config.update.base;
    exports dev.spoocy.utils.config.update.migrations;
    exports dev.spoocy.utils.config.update.match;
    exports dev.spoocy.utils.config.representer;
    exports dev.spoocy.utils.config.constructor;
}