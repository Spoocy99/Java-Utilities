package dev.spoocy.utils.security;

import dev.spoocy.utils.common.collections.SortedArray;
import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import dev.spoocy.utils.security.report.SecurityReport;
import dev.spoocy.utils.security.report.TestContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnegative;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SecurityManager {

    private final Collection<Test> registeredTests = new SortedArray<>();

    private final Report globalReport;

    public SecurityManager() {
        this.globalReport = new Report(null);
        this.registerTests(this);
    }

    @NotNull
    public SecurityReport getGlobalReport() {
        return this.globalReport;
    }

    public <T> void registerTests(@NotNull T instance) {
        registerTests(instance.getClass(), instance);
    }

    public void registerTests(@NotNull Class<?> clazz) {
        registerTests(clazz, null);
    }

    private <T> void registerTests(@NotNull Class<?> clazz, @Nullable T instance) {

        Set<MethodAccessor> methods = Reflection.builder()
                .forClass(clazz)
                .privateMembers()
                .buildAccess()
                .methodsWithAnnotation(SecurityTest.class);

        for(MethodAccessor method : methods) {
            SecurityTest annotation = method.getMethod().getAnnotation(SecurityTest.class);

            if(method.isStatic() && instance != null) {
                continue;
            }

            Test test = new Test(
                    method,
                    annotation.value(),
                    annotation.stage(),
                    annotation.priority(),
                    method.isStatic() ? null : instance,
                    annotation.resultOnException()
            );

            this.registeredTests.add(test);
        }

    }

    public SecurityReport runTests(@NotNull SecurityTest.Stage stage) {

        Report report = new Report(stage);

        for(Test test : this.registeredTests) {

            if (!test.appliesToStage(stage)) continue;

            TestResult result = test.run();
            TestContext context = new TestContext(test, stage, result);

            report.addTestResult(context);
            this.globalReport.addTestResult(context);
        }

        return report;
    }

    @SecurityTest(
            value = "Self Test",
            priority = 0,
            stage = {SecurityTest.Stage.INIT, SecurityTest.Stage.FINISHED_LOADING, SecurityTest.Stage.READY, SecurityTest.Stage.SHUTDOWN},
            resultOnException = CheckResult.KILL_PROGRAM
    )
    public TestResult selfTest() {
        return new TestResult(CheckResult.PASSED, "Security Instance is OK.");
    }

    public static class Test implements Comparable<Test> {

        private final MethodAccessor test;
        private final String name;
        private final SecurityTest.Stage[] stage;
        private final int priority;
        private final @Nullable Object instance;
        private final CheckResult resultOnException;

        protected Test(
                @NotNull MethodAccessor test,
                @NotNull String name,
                @NotNull SecurityTest.Stage[] stage,
                @Nonnegative int priority,
                @Nullable Object instance,
                @NotNull CheckResult resultOnException
        ) {

            if(!test.isStatic() && instance == null) {
                throw new IllegalArgumentException("Instance cannot be null for non-static test methods.");
            }

            if(test.getMethod().getReturnType() != TestResult.class) {
                                throw new IllegalArgumentException("Test method must return TestResult.");
            }

            this.test = test;
            this.name = name;
            this.stage = stage;
            this.priority = priority;
            this.instance = instance;
            this.resultOnException = resultOnException;
        }

        public String name() {
            return this.name;
        }

        public boolean appliesToStage(@NotNull SecurityTest.Stage stage) {
            for(SecurityTest.Stage s : this.stage) {
                if(s == stage) return true;
            }
            return false;
        }

        public TestResult run() {

            TestResult result;

            try {
                result = (TestResult) this.test.invoke(instance);
            } catch (Exception e) {
                result = new TestResult(this.resultOnException, "Could not complete test: " + e.getMessage());
            }

            return result;
        }

        @Override
        public int compareTo(@NotNull SecurityManager.Test o) {
            return Integer.compare(this.priority, o.priority);
        }
    }

    private static class Report implements SecurityReport {

        private final String name;
        private final List<TestContext> results;
        private TestResult mostSevereTest = TestResult.ALL_PASSED;

        public Report(@Nullable SecurityTest.Stage stage) {
            this.name = ">> Security Report - " + (stage != null ? stage.name() : "Global Executed") + " <<";
            this.results = new LinkedList<>();
        }

        public void addTestResult(@NotNull TestContext context) {
            this.results.add(context);
            if (context.getResult().isMoreSevereThan(this.mostSevereTest.getResult())) {
                this.mostSevereTest = context.getTest();
            }
        }

        @Override
        public List<TestContext> getTests() {
            return this.results;
        }

        @Override
        public TestResult getFinalTest() {
            return this.mostSevereTest;
        }

        @Override
        public void process(@NotNull Consumer<TestContext> consumer) {
            for(TestContext context : this.results) {
                consumer.accept(context);
            }
        }

        @Override
        public String generateReport() {
            StringBuilder builder = new StringBuilder(this.name).append("\n ");

            for(TestContext context : this.results) {
                boolean detailed = context.getResult() != CheckResult.PASSED;
                builder
                        .append("\n ")
                        .append("** ")
                        .append(context.format(detailed))
                        .append("\n ");
            }

            return builder.toString();
        }

        @Override
        public String toString() {
            return "SecurityReport{" + name + "}";
        }
    }

}
