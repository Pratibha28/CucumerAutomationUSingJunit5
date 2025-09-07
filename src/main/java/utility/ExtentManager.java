package utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public final class ExtentManager {

	private static volatile ExtentReports extent;
	
	private ExtentManager(){}
	
	
	public static ExtentReports getInstance() {
		
		if(extent==null) {
			
			Path out= Paths.get("target", "extent-report");
			try {
			Files.createDirectories(out);
			}catch(Exception ignored){}
			
			ExtentSparkReporter spark = new ExtentSparkReporter((out.resolve("index.html")).toString());
			spark.config().setDocumentTitle("Automation Test Report");
			spark.config().setReportName("Regression Report");
			ExtentReports e= new ExtentReports();
			e.attachReporter(spark);
			 // global system info (will be visible on report)
			e.setSystemInfo("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
			e.setSystemInfo("User", System.getProperty("user.name"));
			extent = e;
			
			 // flush at JVM shutdown to guarantee write even in parallel runs
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    synchronized (ExtentManager.class) {
                        if (extent != null) extent.flush();
                    }
                } catch (Throwable t) { /* ignore */ }
            }));
		}
		 return extent;
	}
    
	
	
	   // optional helper to flush anytime (synchronized)
    public static void flush() {
        synchronized (ExtentManager.class) {
            if (extent != null) extent.flush();
        }
    }
}
