package stepdefinitions;

import io.cucumber.java.*;
import utility.ExtentManager;
import utility.ExtentTestManager;
import utility.ScreenshotUtil;
import utility.FailureStore;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Set;

public class Hooks {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(Hooks.class);

    @Before
    public void beforeScenario(Scenario scenario) {
        // ensure webdriver (ThreadLocal) is created
        BaseTest.createDriverIfNeeded();

        // Put context for log pattern
        String browser = System.getProperty("browser", BaseTest.getProps().getProperty("browser", "chrome"));
        String env = System.getProperty("env", BaseTest.getProps().getProperty("env", "local"));
        ThreadContext.put("env", env);
        ThreadContext.put("browser", browser);
        ThreadContext.put("scenario", scenario.getName());
        ThreadContext.put("tags", scenario.getSourceTagNames().toString());

        // Title and description
        String title = scenario.getName();
        String description = findDescriptionFromTags(scenario);
        if (description == null || description.isBlank()) {
            description = "No description provided";
        }

        // Create the ExtentTest
        ExtentTest test = ExtentTestManager.createTest(title, description);
        test.assignCategory("Browser: " + browser);
        ExtentManager.getInstance().setSystemInfo("Browser", browser);
        test.assignCategory(scenario.getSourceTagNames().toString());

        logger.info("=== START SCENARIO: {} ===", scenario.getName());
        logger.info("Tags: {}, Browser: {}, Env: {}", scenario.getSourceTagNames(), browser, ThreadContext.get("env"));
    }

    private static String findDescriptionFromTags(Scenario scenario) {
        for (String t : scenario.getSourceTagNames()) {
            if (t.startsWith("@desc_")) {
                return t.substring("@desc_".length()).replace('_', ' ');
            }
        }
        return null;
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            test.info("Step: " + scenario.getName());
        }
        logger.debug("Step executed (scenario status so far: failed? {})", scenario.isFailed());
    }

    @After
    public void afterScenario(Scenario scenario) {
        ExtentTest test = ExtentTestManager.getTest();

        try {
            if (test == null) return;

            if (scenario.isFailed()) {
                // 1) Try to get Throwable via Scenario.getError() reflectively
                Throwable scenarioError = null;
                try {
                    Method m = scenario.getClass().getMethod("getError");
                    Object err = m.invoke(scenario);
                    if (err instanceof Throwable) scenarioError = (Throwable) err;
                    if (scenarioError != null) {
                        logger.debug("Obtained Throwable from Scenario.getError()");
                    }
                } catch (NoSuchMethodException nsme) {
                    // method not present - ignore
                } catch (Exception reflectEx) {
                    logger.debug("Reflection to get scenario error failed: {}", reflectEx.getMessage());
                }

                // 2) Fallback to FailureStore (thread-safe map) if still null
                if (scenarioError == null) {
                    String scenarioName = scenario.getName();
                    // show current keys for diagnostics
                    try {
                        Set<String> keys = FailureStore.currentKeys();
                        logger.debug("FailureStore keys snapshot: {}", keys);
                    } catch (Exception e) {
                        logger.debug("Unable to get FailureStore keys snapshot: {}", e.getMessage());
                    }

                    // candidate keys to try
                    String key1 = scenarioName;
                    String key2 = scenarioName.replaceAll("\\(.*\\)$", "").trim(); // normalized (strip params)
                    String[] candidates = new String[] { key1, key2 };

                    for (String k : candidates) {
                        if (k == null) continue;
                        try {
                            Throwable t = FailureStore.getAndRemove(k);
                            logger.debug("Tried FailureStore.getAndRemove('{}') -> {}", k, (t == null ? "null" : "FOUND"));
                            if (t != null) {
                                scenarioError = t;
                                break;
                            }
                        } catch (Exception e) {
                            logger.debug("FailureStore lookup for key '{}' threw: {}", k, e.getMessage());
                        }
                    }
                }

                // 3) Build stacktrace string if throwable available
                String stackTraceString = null;
                if (scenarioError != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    scenarioError.printStackTrace(pw);
                    pw.flush();
                    stackTraceString = sw.toString();
                }

                // 4) Log using logger.error(msg, throwable) so file contains stacktrace
                if (scenarioError != null) {
                    logger.error("SCENARIO FAILED: {} - tags: {}", scenario.getName(), scenario.getSourceTagNames(), scenarioError);
                } else {
                    logger.error("SCENARIO FAILED: {} - tags: {}", scenario.getName(), scenario.getSourceTagNames());
                }

                // 5) Attach to Extent: mark fail and attach throwable / stacktrace
                test.fail("Scenario failed: " + scenario.getName());
                if (scenarioError != null) {
                    try {
                        test.fail(scenarioError); // Extent may accept Throwable
                    } catch (Exception e) {
                        test.fail("Exception: " + scenarioError.toString());
                    }
                    if (stackTraceString != null && !stackTraceString.isBlank()) {
                        try {
                            test.fail(MarkupHelper.createCodeBlock(stackTraceString));
                        } catch (Exception e) {
                            test.fail("Stacktrace: " + scenarioError.toString());
                        }
                    }
                }

                // 6) Screenshot via utility
                try {
                    String screenshotPath = ScreenshotUtil.captureScreenshot();
                    test.fail("Screenshot on failure", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                } catch (Exception e) {
                    test.warning("Unable to capture screenshot: " + e.getMessage());
                    logger.warn("Screenshot capture failed: {}", e.getMessage());
                }

            } else {
                test.pass("Scenario passed: " + scenario.getName());
                logger.info("SCENARIO PASSED: {}", scenario.getName());
            }

            test.assignCategory(scenario.getSourceTagNames().toString());

        } finally {
            ExtentTestManager.removeTest();
            BaseTest.quitDriver();
            ExtentManager.flush();

            // clear ThreadContext entries used in log pattern
            ThreadContext.remove("env");
            ThreadContext.remove("browser");
            ThreadContext.remove("scenario");
            ThreadContext.remove("tags");
        }
    }
}
