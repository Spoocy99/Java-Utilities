package dev.spoocy.utils.common.Version;

import dev.spoocy.utils.common.text.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SimpleVersion implements Version {

    private final int major;
    private final int minor;
    private final int build;

    @Nullable
    private final String preReleaseIdentifier;
    @Nullable
    private final String buildMetaData;

    public SimpleVersion(int major, int minor, int build) {
        this(major, minor, build, null, null);
    }

    public SimpleVersion(int major, int minor, int build, @Nullable String preReleaseIdentifier, @Nullable String buildMetaData) {
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.preReleaseIdentifier = preReleaseIdentifier;
        this.buildMetaData = buildMetaData;
    }

    @Override
    public int getMajor() {
        return this.major;
    }

    @Override
    public int getMinor() {
        return this.minor;
    }

    @Override
    public int getBuild() {
        return this.build;
    }

    @Override
    public boolean isPreRelease() {
        return !StringUtils.isNullOrEmpty(this.preReleaseIdentifier);
    }

    @Override
    public @Nullable String getPreReleaseIdentifier() {
        return this.preReleaseIdentifier;
    }

    @Override
    public @Nullable String getBuildMetaData() {
        return this.buildMetaData;
    }


    @Override
    public String toString() {
        return formatFull();
    }
}
