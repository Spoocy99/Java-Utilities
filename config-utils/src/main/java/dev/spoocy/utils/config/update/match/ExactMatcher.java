package dev.spoocy.utils.config.update.match;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.update.VersionMatcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ExactMatcher implements VersionMatcher {

    private final Version version;

    public ExactMatcher(@NotNull Version version) {
        this.version = version;
    }

    @Override
    public boolean matches(@NotNull Version version) {
        return this.version.compareTo(version) == 0;
    }

    @Override
    public boolean isExact() {
        return true;
    }

    @Override
    public @NotNull String describe() {
        return this.version.formatFull();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ExactMatcher)) return false;
        ExactMatcher other = (ExactMatcher) object;
        return VersionMatcher.equalsVersion(this.version, other.version);
    }

    @Override
    public int hashCode() {
        return this.version.formatFull().hashCode();
    }

}
