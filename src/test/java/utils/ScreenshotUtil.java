package utils;

import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtil {

    // Guarda en docs/screenshots (carpeta que publica GitHub Pages)
    private static final Path BASE_DIR
            = Paths.get(System.getProperty("user.dir"))
                    .resolve("docs")
                    .resolve("screenshots");

    private static String sanitize(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^a-zA-Z0-9-_\\. ]", "_")
                .replaceAll("\\s+", "_");
        return (n == null || n.isBlank()) ? "screenshot" : n;
    }

    /**
     * Guarda un PNG en docs/screenshots con nombre basado en el escenario
     */
    public static void saveToFile(byte[] data, String scenarioName) {
        try {
            if (!Files.exists(BASE_DIR)) {
                Files.createDirectories(BASE_DIR);
            }

            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String base = sanitize(scenarioName);
            File out = BASE_DIR.resolve(base + "-" + ts + ".png").toFile();

            try (FileOutputStream fos = new FileOutputStream(out)) {
                fos.write(data);
            }
            System.out.println("Screenshot guardado en: " + out.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("No se pudo guardar el screenshot: " + e.getMessage());
        }
    }

    /**
     * Toma la captura desde el WebDriver, la ADJUNTA al reporte y además la
     * guarda en docs/screenshots
     */
    public static void captureAttachAndSave(Scenario scenario, WebDriver driver) {
        if (driver == null) {
            return;
        }
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String name = sanitize(scenario != null ? scenario.getName() : "screenshot") + "_" + ts;

            // 1) Adjunta a Cucumber/Extent
            if (scenario != null) {
                scenario.attach(png, "image/png", name);
            }

            // 2) Guarda archivo físico en docs/screenshots
            saveToFile(png, name);

        } catch (Exception e) {
            System.err.println("No se pudo capturar/adjuntar/guardar screenshot: " + e.getMessage());
        }
    }
}
