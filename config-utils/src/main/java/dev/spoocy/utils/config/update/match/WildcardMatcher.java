package dev.spoocy.utils.config.update.match;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.update.VersionMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class WildcardMatcher implements VersionMatcher {

        private final int major;
        @Nullable
        private final Integer minor;
        @Nullable
        private final Integer build;
        private final String pattern;

        public WildcardMatcher(int major, @Nullable Integer minor, @Nullable Integer build, @NotNull String pattern) {
            this.major = major;
            this.minor = minor;
            this.build = build;
            this.pattern = pattern;
        }

        @Override
        public boolean matches(@NotNull Version version) {
            if (version.getMajor() != this.major) {
                return false;
            }

            if (this.minor != null && version.getMinor() != this.minor) {
                return false;
            }

            return this.build == null || version.getBuild() == this.build;
        }

        @Override
        public boolean isExact() {
            return false;
        }

        @Override
        public @NotNull String describe() {
            return this.pattern;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof WildcardMatcher)) return false;
            WildcardMatcher other = (WildcardMatcher) object;
            return this.major == other.major
                    && Objects.equals(this.minor, other.minor)
                    && Objects.equals(this.build, other.build);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.major, this.minor, this.build);
        }
    }
