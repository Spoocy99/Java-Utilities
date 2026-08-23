package dev.spoocy.utils.config.update;

import dev.spoocy.utils.config.ConfigSection;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Applies configuration migrations from one schema version to the next.
 * Implementations are responsible for finding and applying the appropriate migration sequence.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public interface ConfigUpdater {

    /**
     * Convenience factory method to create a new ConfigUpdaterChain builder.
     *
     * @return a new builder for ConfigUpdaterChain
     */
    static ConfigUpdaterChain.Builder chain() {
        return ConfigUpdaterChain.builder();
    }

    /**
     * Retrieves a collection of all possible configuration migrations that can be applied.
     *
     * @return an immutable collection of available migrations (never null)
     */
    @NotNull
    Collection<ConfigMigration> getPossibleMigrations();

    /**
     * Executes the update process on the provided configuration section.
     * Applies migrations in sequence until the configuration is at the latest version.
     *
     * @param config the configuration section to update (not null)
     *
     * @return the number of migrations applied during this run
     */
    int run(@NotNull ConfigSection config);

}

