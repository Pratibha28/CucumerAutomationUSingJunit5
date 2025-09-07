package listener;

import utility.FailureStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

public class JUnitLoggerListener implements TestExecutionListener {
    private static final Logger logger = LogManager.getLogger(JUnitLoggerListener.class);

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult result) {
        if (!testIdentifier.isTest()) return;

        result.getThrowable().ifPresent(error -> {
            // store several variants to increase chance of match in Hooks
            try {
                String uid = testIdentifier.getUniqueId();
                if (uid != null) FailureStore.put(uid, error);
            } catch (Exception ignored) {}

            try {
                String name = testIdentifier.getDisplayName();
                if (name != null) FailureStore.put(name, error);
            } catch (Exception ignored) {}

            try {
                // normalized version: remove parameter markers and line breaks (common differences)
                String norm = testIdentifier.getDisplayName().replaceAll("\\(.*\\)$", "").trim();
                if (norm != null && !norm.isBlank()) FailureStore.put(norm, error);
            } catch (Exception ignored) {}

            logger.error("❌ Test failed (listener): displayName='{}' uniqueId='{}' — stored keys={}",
                         testIdentifier.getDisplayName(),
                         testIdentifier.getUniqueId(),
                         FailureStore.currentKeys(),
                         error);
        });
    }
}
