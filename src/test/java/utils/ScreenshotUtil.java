package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtil {
    private static final String OUTPUT_DIR = "evidencias/capturas";

    private static String sanitize(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        n = n.replaceAll("[^a-zA-Z0-9-_\. ]", "_");
        n = n.replaceAll("\\s+", "_");
        return n;
    }

    public static void saveToFile(byte[] data, String scenarioName) {
        try {
            Path outDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outDir)) Files.createDirectories(outDir);
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String base = sanitize(scenarioName);
            File out = outDir.resolve(base + "-" + ts + ".png").toFile();
            try (FileOutputStream fos = new FileOutputStream(out)) {
                fos.write(data);
            }
            System.out.println("Screenshot guardado en: " + out.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("No se pudo guardar el screenshot: " + e.getMessage());
        }
    }
}