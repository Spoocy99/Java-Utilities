package dev.spoocy.utils.config;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.loader.ConfigLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SafeResourceResolver extends BaseResourceResolver {

    public SafeResourceResolver(
            @Nullable ClassLoader classLoader,
            @NotNull ConfigLoader<?, ?>... loader
    ) {
        super(classLoader, loader);
    }

    @Override
    public @NotNull Config loadConfig(@NotNull Resource resource, @NotNull Constructor constructor) throws IOException {
        Args.notNull(resource, "resource");
        Args.notNull(constructor, "constructor");

        ConfigLoader<? extends Config, ?> loader = resolveLoader(resource);
        if (loader == null) {
            throw new IOException("No config loader for resource " + resource);
        }

        if(!resource.exists()) {
            return loader.createEmpty();
        }

        return loader.load(resource, constructor);
    }
}
