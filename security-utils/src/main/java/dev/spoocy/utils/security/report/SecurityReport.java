package dev.spoocy.utils.security.report;

import dev.spoocy.utils.common.log.report.LoggingReport;
import dev.spoocy.utils.security.CheckResult;
import dev.spoocy.utils.security.TestResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface SecurityReport extends LoggingReport {

    List<TestContext> getTests();

    TestResult getFinalTest();

    void process(@NotNull Consumer<TestContext> consumer);

    default boolean shouldWarn() {
        return getFinalTest().getResult().isAtLeastAsSevereAs(CheckResult.WARNING);
    }

}
