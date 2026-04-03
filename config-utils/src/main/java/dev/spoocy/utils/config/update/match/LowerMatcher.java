package dev.spoocy.utils.config.update.match;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.update.VersionMatcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class LowerMatcher implements VersionMatcher {

    private final Version version;

    public LowerMatcher(@NotNull Version version) {
        this.version = version;
    }

    @Override
    public boolean matches(@NotNull Version version) {
        return version.compareTo(this.version) < 0;
    }

    @Override
    public boolean isExact() {
        return false;
    }

    @Override
    public @NotNull String describe() {
        return "greater than " + version;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof LowerMatcher)) return false;
        LowerMatcher other = (LowerMatcher) object;
        return VersionMatcher.equalsVersion(this.version, other.version);
    }

    @Override
    public int hashCode() {
        return this.version.formatFull().hashCode();
    }
}
