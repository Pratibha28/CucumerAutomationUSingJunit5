package utility;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public final class ExtentTestManager {
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    private ExtentTestManager() {}

    public static ExtentTest createTest(String name) {
        return createTest(name, null);
    }

    public static ExtentTest createTest(String name, String description) {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test;
        if (description == null || description.isBlank()) {
            test = extent.createTest(name);
        } else {
            test = extent.createTest(name, description);
        }
        testThreadLocal.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public static void removeTest() {
        testThreadLocal.remove();
    }
}
