import dev.spoocy.utils.common.Version.Version;
import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class VersionTest {

    @Test
    public void shouldParseFullVersion() {
        String version = "1.20.4-snapshot+data123";

        Version parsed = Version.parse(version);
        assert parsed.getMajor() == 1;
        assert parsed.getMinor() == 20;
        assert parsed.getBuild() == 4;
        assert Objects.equals(parsed.getPreReleaseIdentifier(), "snapshot");
        assert Objects.equals(parsed.getBuildMetaData(), "data123");
    }

    @Test
    public void shouldParseUnfinishedVersion() {
        String version = "1.20";

        Version parsed = Version.parse(version);
        assert parsed.getMajor() == 1;
        assert parsed.getMinor() == 20;
        assert parsed.getBuild() == 0;
    }

    @Test
    public void shouldParseShortVersion() {
        String version = "1.20.4";

        Version parsed = Version.parse(version);
        assert parsed.getMajor() == 1;
        assert parsed.getMinor() == 20;
        assert parsed.getBuild() == 4;
    }

    @Test
    public void shouldParseShortVersionWithMetadataOnly() {
        String version = "1.20.4+data123";

        Version parsed = Version.parse(version);
        assert parsed.getMajor() == 1;
        assert parsed.getMinor() == 20;
        assert parsed.getBuild() == 4;
        assert Objects.equals(parsed.getBuildMetaData(), "data123");
    }

    @Test
    public void shouldParseShortVersionWithPreReleaseOnly() {
        String version = "1.20.4-snapshot";

        Version parsed = Version.parse(version);
        assert parsed.getMajor() == 1;
        assert parsed.getMinor() == 20;
        assert parsed.getBuild() == 4;
        assert Objects.equals(parsed.getPreReleaseIdentifier(), "snapshot");
    }


}
