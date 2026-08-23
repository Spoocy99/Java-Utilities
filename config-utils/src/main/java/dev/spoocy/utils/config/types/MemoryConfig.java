package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.AbstractConfig;
import dev.spoocy.utils.config.io.WriteableResource;
import dev.spoocy.utils.config.nodes.ScalarNode;
import dev.spoocy.utils.config.representer.Representer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class MemoryConfig extends AbstractConfig {

    private final ConfigSettings settings;

    public MemoryConfig() {
        super();
        this.settings = new ConfigSettings(this);
    }

    @Override
    public @NotNull ConfigSettings settings() {
        return this.settings;
    }

    @Override
    public @NotNull String saveToString(@NotNull Representer representer) {
        throw new UnsupportedOperationException("Saving to string is not supported for MemoryConfig");
    }

    @Override
    public void save(@NotNull WriteableResource file, @NotNull Representer representer) throws IOException {
        throw new UnsupportedOperationException("Saving to string is not supported for MemoryConfig");
    }

    @Override
    protected @Nullable Object unpackScalar(@NotNull ScalarNode node) {
        return node.getData();
    }
}
