package dev.spoocy.utils.common.log;

import dev.spoocy.utils.common.log.factory.ConsoleLoggerFactory;
import dev.spoocy.utils.common.log.factory.JavaLoggerFactory;
import dev.spoocy.utils.common.log.factory.Slf4jLoggerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public final class FactoryHolder {

    private static ILoggerFactory factory;

    public static void setFactory(@NotNull ILoggerFactory factory) {
        FactoryHolder.factory = factory;
        ILogger.forThisClass().info("Using Logger Factory: " + factory.getClass().getSimpleName());
    }

    public static ILoggerFactory getFactory() {
        if (factory == null) {
            findFactory();
        }
        return factory;
    }

    public static ILogger getOrCreateLogger(@Nullable String name) {
        return getFactory().getOrCreateLogger(name);
    }

    public static void setLevel(@NotNull LogLevel level) {
        getFactory().setLevel(level);
    }

    private static void findFactory() {

        try {
            Class.forName("org.slf4j.Logger");
            setFactory(new Slf4jLoggerFactory());
            return;
        } catch (Exception ignored) { }

        try {
            Class.forName("java.util.logging.Logger");
            setFactory(new JavaLoggerFactory());
            return;
        } catch (Exception ignored) { }

        setFactory(new ConsoleLoggerFactory());
    }

    private FactoryHolder() { }

}
