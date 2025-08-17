package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

import utils.ScreenshotUtil;
import utils.VideoRecorderUtil;

public class Hooks {
    public static WebDriver driver;

    private String scenarioNameSafe(Scenario scenario) {
        return scenario.getName().replaceAll("[^a-zA-Z0-9-_\. ]","_").replaceAll("\\s+","_");
    }

    @Before("@ui")
    public void setUp(Scenario scenario) {
        String browser = System.getProperty("browser", "firefox").toLowerCase();
        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            default:
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
        }
        driver.manage().window().maximize();

        // Inicia grabación de video (si ffmpeg está instalado)
        VideoRecorderUtil.startRecording(scenarioNameSafe(scenario));
    }

    @After("@ui")
    public void tearDown(Scenario scenario) {
        if (driver != null) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "final-screenshot");
                ScreenshotUtil.saveToFile(screenshot, scenario.getName());
            } catch (Exception ignored) {}

            try {
                VideoRecorderUtil.stopRecording();
            } catch (Exception e) {
                System.err.println("No se pudo detener la grabación: " + e.getMessage());
            }

            driver.quit();
        }
    }
}