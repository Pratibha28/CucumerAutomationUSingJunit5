package utility;

import base.BaseTest;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ScreenshotUtil {

    /**
     * Capture screenshot and return relative path used in Extent report
     * e.g. "screenshots/<uuid>.png"
     */
    public static String captureScreenshot() throws Exception {
        File src = ((TakesScreenshot) BaseTest.getDriver()).getScreenshotAs(OutputType.FILE);
        Path outDir = Paths.get("target", "extent-report", "screenshots");
        outDir.toFile().mkdirs();
        String fileName = UUID.randomUUID().toString() + ".png";
        File dest = outDir.resolve(fileName).toFile();
        FileUtils.copyFile(src, dest);
        return "screenshots/" + fileName;
    }
}
