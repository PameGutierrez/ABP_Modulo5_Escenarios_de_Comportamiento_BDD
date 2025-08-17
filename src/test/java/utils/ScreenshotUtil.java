package utils;

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

    // Base absoluta = carpeta del proyecto (donde está tu pom.xml)
    private static final Path BASE_DIR =
            Paths.get(System.getProperty("user.dir"))        // raíz del proyecto
                 .resolve("evidencias")
                 .resolve("capturas");

    private static String sanitize(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
            .replaceAll("[^a-zA-Z0-9-_\\. ]", "_")
            .replaceAll("\\s+", "_");
        return n;
    }

    public static void saveToFile(byte[] data, String scenarioName) {
        try {
            if (!Files.exists(BASE_DIR)) Files.createDirectories(BASE_DIR);

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
}
