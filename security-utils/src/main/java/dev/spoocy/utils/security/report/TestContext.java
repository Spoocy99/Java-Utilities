package dev.spoocy.utils.security.report;

import dev.spoocy.utils.security.CheckResult;
import dev.spoocy.utils.security.SecurityTest;
import dev.spoocy.utils.security.SecurityManager;
import dev.spoocy.utils.security.TestResult;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class TestContext {

    private final SecurityManager.Test test;
    private final SecurityTest.Stage runAtStage;
    private final TestResult result;

    public TestContext(
            @NotNull SecurityManager.Test test,
            @NotNull SecurityTest.Stage runAtStage,
            @NotNull TestResult result
    ) {
        this.test = test;
        this.runAtStage = runAtStage;
        this.result = result;
    }

    @NotNull
    public String getName() {
        return this.test.name();
    }

    @NotNull
    public SecurityTest.Stage getStage() {
        return this.runAtStage;
    }

    @NotNull
    public TestResult getTest() {
        return this.result;
    }

    @NotNull
    public CheckResult getResult() {
        return this.result.getResult();
    }

    @NotNull
    public String[] getDetails() {
        return this.result.getMessages();
    }

    public String format(boolean detailed) {
        if (!detailed) {
            return toString();
        }

        String[] details = getDetails();

        StringBuilder builder = new StringBuilder(toString());

//        if(details.length == 1) {
//            builder.append(" (")
//                     .append(details[0])
//                     .append(")")
//            ;
//            return builder.toString();
//        }

        for (String detail : details) {
            builder.append("\n - ").append(detail);
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return "['" + test.name() + "'@" + runAtStage.name() + "] >> " + result.getResult();
    }
}
