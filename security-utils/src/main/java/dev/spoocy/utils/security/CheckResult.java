package dev.spoocy.utils.security;

import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public enum CheckResult {
    SKIPPED(-1),
    PASSED(10),
    WARNING(50),
    ERROR(90),
    KILL_PROGRAM(99);


    private final int severity;

    CheckResult(int severity) {
        this.severity = severity;
    }

    public int getSeverity() {
        return this.severity;
    }

    public boolean isMoreSevereThan(@NotNull CheckResult other) {
        return this.severity > other.severity;
    }

    public boolean isLessSevereThan(@NotNull CheckResult other) {
        return this.severity < other.severity;
    }

    public boolean isAtLeastAsSevereAs(@NotNull CheckResult other) {
        return this.severity >= other.severity;
    }

}
