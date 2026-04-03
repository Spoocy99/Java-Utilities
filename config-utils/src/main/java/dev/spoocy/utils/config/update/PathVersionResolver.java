package dev.spoocy.utils.config.update;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.ConfigSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class PathVersionResolver implements VersionResolver {

    @NotNull
    private final String versionPath;

    @Nullable
    private final Version fallbackVersion;

    public PathVersionResolver(@NotNull String versionPath, @Nullable Version fallbackVersion) {
        this.versionPath = versionPath;
        this.fallbackVersion = fallbackVersion;
    }

    @Override
    @NotNull
    public Version resolve(@NotNull ConfigSection config) {
        Version resolved = config.getVersion(this.versionPath, null);
        if (resolved != null) {
            return resolved;
        }

        if (this.fallbackVersion != null) {
            return this.fallbackVersion;
        }

        throw new IllegalStateException("Version not found at path '" + this.versionPath + "' and no fallback version provided");
    }

    @Override
    public void apply(@NotNull ConfigSection config, @NotNull Version version) {
        config.set(this.versionPath, Args.notNull(version, "version")
                .formatFull());
    }
}
