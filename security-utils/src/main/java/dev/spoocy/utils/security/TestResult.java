package dev.spoocy.utils.security;

import dev.spoocy.utils.common.text.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class TestResult {

    public static final TestResult ALL_PASSED = new TestResult(CheckResult.PASSED, "Security Tests passed.");
    public static final TestResult SKIPPED = new TestResult(CheckResult.SKIPPED, "Test skipped.");

    private final CheckResult result;
    private final String[] messages;

    public TestResult(
            @NotNull CheckResult result,
            @Nullable String... messages
    ) {
        this.result = result;

        this.messages = (messages == null || messages.length == 0) ? new String[] {"NONE"} : Arrays.copyOf(messages, messages.length);
        for (int i = 0; i < this.messages.length; i++) {
            if (StringUtils.isNullOrEmpty(this.messages[i])) {
                this.messages[i] = "NONE";
            }
        }
    }

    @NotNull
    public String[] getMessages() {
        return messages;
    }

    @NotNull
    public CheckResult getResult() {
        return result;
    }
}
