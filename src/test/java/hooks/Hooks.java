package hooks;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import utils.ScreenshotUtil;
import utils.VideoRecorderUtil;

public class Hooks {

    public static WebDriver driver;
    private boolean shouldRecord = true; // control por -Dvideo=false

    private String scenarioNameSafe(Scenario scenario) {
        return scenario.getName()
                .replaceAll("[^a-zA-Z0-9-_\\. ]", "_")
                .replaceAll("\\s+", "_");
    }

    @Before("@ui")
    public void setUp(Scenario scenario) {
        // lee banderas: -Dbrowser=chrome  /  -Dvideo=false
        String browser = System.getProperty("browser", "firefox").toLowerCase();
        String videoFlag = System.getProperty("video", "true");
        shouldRecord = !"false".equalsIgnoreCase(videoFlag);

        if ("chrome".equals(browser)) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        }

        driver.manage().window().maximize();

        if (shouldRecord) {
            VideoRecorderUtil.startRecording(scenarioNameSafe(scenario));
        }
    }

    @AfterStep
    public void takeScreenshotEveryStep(Scenario scenario) {
        try {
            // Adjunta al reporte y guarda el PNG en docs/screenshots
            ScreenshotUtil.captureAttachAndSave(scenario, driver);
        } catch (Exception e) {
            System.err.println("No se pudo capturar screenshot por paso: " + e.getMessage());
        }
    }

    @After("@ui")
    public void tearDown(Scenario scenario) {
        if (driver != null) {
            try {
                // Adjunta un screenshot final y lo guarda en docs/screenshots
                ScreenshotUtil.captureAttachAndSave(scenario, driver);
            } catch (WebDriverException e) {
                System.err.println("No se pudo tomar/adjuntar screenshot final: " + e.getMessage());
            }

            try {
                if (shouldRecord) {
                    VideoRecorderUtil.stopRecording();
                }
            } catch (RuntimeException e) {
                System.err.println("No se pudo detener la grabación: " + e.getMessage());
            }

            driver.quit();
        }
    }
}
