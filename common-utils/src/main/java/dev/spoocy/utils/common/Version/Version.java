package dev.spoocy.utils.common.Version;

import com.google.common.collect.ComparisonChain;
import dev.spoocy.utils.common.text.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Version extends Comparable<Version>, Serializable {

    /**
     * @return The major version
     */
    int getMajor();

    /**
     * @return The minor version
     */
    int getMinor();

    /**
     * @return The build version
     */
    int getBuild();

    /**
     * @return True if this version is a snapshot version, false otherwise
     */
    boolean isPreRelease();

    /**
     * @return The pre-release identifier, e.g. "alpha", "beta", "snapshot", etc.
     */
    @Nullable
    String getPreReleaseIdentifier();

    /**
     * @return The build metadata, e.g. "20231001", "build-1234", etc.
     */
    @Nullable
    String getBuildMetaData();

    /**
     * Determines if this version is newer than the given version
     *
     * @param version The version to compare
     *
     * @return True if this version is newer than the given version
     */
    default boolean isNewerThan(@NotNull Version version) {
        return this.compareTo(version) > 0;
    }

    /**
     * Determines if this version is older than the given version
     *
     * @param version The version to compare
     *
     * @return True if this version is older than the given version
     */
    default boolean isOlderThan(@NotNull Version version) {
        return this.compareTo(version) < 0;
    }

    /**
     * Determines if this version is equal to the given version
     *
     * @param version The version to compare
     *
     * @return True if this version is equal to the given version
     */
    default boolean equals(@NotNull Version version) {
        return this.compareTo(version) == 0;
    }

    /**
     * Determines if this version is at least the given version
     *
     * @param version The version to compare
     *
     * @return True if this version is at least the given version
     */
    default boolean isAtLeast(@NotNull Version version) {
        return this.compareTo(version) >= 0;
    }

    /**
     * Determines if this version is at most the given version
     *
     * @param version The version to compare
     *
     * @return True if this version is at most the given version
     */
    default boolean isAtMost(@NotNull Version version) {
        return this.compareTo(version) <= 0;
    }

    /**
     * Determines if two versions are part of the same major version.
     *
     * @param version The version to compare
     * @return True if the versions are part of the same major version
     */
    default boolean isSameMajor(@NotNull Version version) {
        return this.getMajor() == version.getMajor();
    }

    /**
     * Determines if two versions are part of the same minor version.
     *
     * @param version The version to compare
     * @return True if the versions are part of the same minor version
     */
    default boolean isSameMinor(@NotNull Version version) {
        return isSameMajor(version) && this.getMinor() == version.getMinor();
    }

    /**
     * Determines the difference between the major versions.
     *
     * @param version The version to compare
     * @return The difference between the major versions
     */
    default int getMajorDifference(@NotNull Version version) {
        return Math.abs(this.getMajor() - version.getMajor());
    }

    /**
     * Determines the difference between the minor versions.
     *
     * @param version The version to compare
     * @return The difference between the minor versions
     */
    default int getMinorDifference(@NotNull Version version) {
        return Math.abs(this.getMinor() - version.getMinor());
    }

    /**
     * Determines the difference between the build versions.
     *
     * @param version The version to compare
     * @return The difference between the build versions
     */
    default int getBuildDifference(@NotNull Version version) {
        return Math.abs(this.getBuild() - version.getBuild());
    }

    /**
     * @return The formatted version
     */
    default String format() {
        return String.format("%s.%s.%s", this.getMajor(), this.getMinor(), this.getBuild());
    }

    default String formatFull() {
        StringBuilder sb = new StringBuilder(format());
        if (!StringUtils.isNullOrEmpty(this.getPreReleaseIdentifier())) {
            sb.append("-").append(this.getPreReleaseIdentifier());
        }
        if (!StringUtils.isNullOrEmpty(this.getBuildMetaData())) {
            sb.append("+").append(this.getBuildMetaData());
        }
        return sb.toString();
    }

    @Override
    default int compareTo(@NotNull Version o) {
        return ComparisonChain.start()
                .compare(this.getMajor(), o.getMajor())
                .compare(this.getMinor(), o.getMinor())
                .compare(this.getBuild(), o.getBuild())
                .result();
    }

    int hashCode();

    static Version parse(@NotNull String version) {
        // major.minor.build[-prerelease][+metadata]
        Pattern pattern = Pattern.compile("^(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:-([a-zA-Z0-9.-]+))?(?:\\+([a-zA-Z0-9.-]+))?$");
        Matcher matcher = pattern.matcher(version);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Version does not match Pattern 'major.minor.build[-prerelease][+metadata]': " + version);
        }

        int major = Integer.parseInt(matcher.group(1));
        int minor = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
        int build = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
        String preRelease = matcher.group(4);
        String buildMetaData = matcher.group(5);

        return new SimpleVersion(major, minor, build, preRelease, buildMetaData);
    }

}
