/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

module dev.spoocy.utils.bukkit {
    requires static org.jetbrains.annotations;
    requires dev.spoocy.utils.common;
    requires dev.spoocy.utils.config;
    requires org.bukkit;
    requires authlib;
    requires com.google.gson;

    exports dev.spoocy.utils.bukkit.compatibility;
    exports dev.spoocy.utils.bukkit.misc;
    exports dev.spoocy.utils.bukkit.serializers;
    exports dev.spoocy.utils.bukkit.utils;
}