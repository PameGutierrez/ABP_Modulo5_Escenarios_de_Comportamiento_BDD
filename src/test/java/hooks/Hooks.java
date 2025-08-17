package hooks;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;  
import io.github.bonigarcia.wdm.WebDriverManager;
import utils.ScreenshotUtil;
import utils.VideoRecorderUtil;

public class Hooks {
    public static WebDriver driver;

    private String scenarioNameSafe(Scenario scenario) {
        return scenario.getName()
            .replaceAll("[^a-zA-Z0-9-_\\. ]", "_")
            .replaceAll("\\s+", "_");
    }

    @Before("@ui")
    public void setUp(Scenario scenario) {
        String browser = System.getProperty("browser", "firefox").toLowerCase();

        if ("chrome".equals(browser)) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        }

        driver.manage().window().maximize();
        VideoRecorderUtil.startRecording(scenarioNameSafe(scenario));
    }

    @After("@ui")
    public void tearDown(Scenario scenario) {
        if (driver != null) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "final-screenshot");
                ScreenshotUtil.saveToFile(screenshot, scenario.getName());
            } catch (ClassCastException | WebDriverException e) { 
                System.err.println("No se pudo tomar/adjuntar screenshot: " + e.getMessage());
            }

            try {
                VideoRecorderUtil.stopRecording();
            } catch (RuntimeException e) {
                System.err.println("No se pudo detener la grabación: " + e.getMessage());
            }

            driver.quit();
        }
    }
}
