package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Graba la pantalla usando FFmpeg ejecutado como proceso externo.
 * Requiere FFmpeg instalado y disponible en el PATH del sistema.
 * - Windows: usa gdigrab (pantalla completa)
 * - Linux: usa x11grab (:0.0) por defecto
 * - macOS: usa avfoundation (dispositivo '1'); puede requerir ajustes con 'ffmpeg -f avfoundation -list_devices true -i ""'
 */
public class VideoRecorderUtil {
    private static Process process;
    private static String currentFilePath;

    public static void startRecording(String scenarioSafeName) {
        if (process != null && process.isAlive()) {
            stopRecording();
        }
        String os = System.getProperty("os.name").toLowerCase();
        Path outDir = Paths.get("evidencias", "videos");
        try {
            if (!Files.exists(outDir)) Files.createDirectories(outDir);
        } catch (IOException e) {
            System.err.println("No se pudo crear carpeta de videos: " + e.getMessage());
        }
        currentFilePath = outDir.resolve(scenarioSafeName + ".mp4").toString();

        try {
            ProcessBuilder pb;
            if (os.contains("win")) {
                // Windows: captura pantalla completa con gdigrab
                pb = new ProcessBuilder("ffmpeg", "-y", "-f", "gdigrab", "-framerate", "15", "-i", "desktop",
                        "-pix_fmt", "yuv420p", currentFilePath);
            } else if (os.contains("mac")) {
                // macOS: avfoundation (puede requerir cambiar el índice del dispositivo de pantalla)
                pb = new ProcessBuilder("ffmpeg", "-y", "-f", "avfoundation", "-framerate", "15", "-i", "1",
                        "-pix_fmt", "yuv420p", currentFilePath);
            } else {
                // Linux: X11 por defecto DISPLAY :0.0
                String display = System.getenv().getOrDefault("DISPLAY", ":0.0");
                pb = new ProcessBuilder("ffmpeg", "-y", "-f", "x11grab", "-framerate", "15", "-i", display,
                        "-pix_fmt", "yuv420p", currentFilePath);
            }
            pb.redirectErrorStream(true);
            process = pb.start();
            System.out.println("Grabación iniciada → " + currentFilePath);
        } catch (IOException e) {
            System.err.println("FFmpeg no disponible o error al iniciar grabación: " + e.getMessage());
        }
    }

    public static void stopRecording() {
        if (process != null) {
            process.destroy();
            try { process.waitFor(); } catch (InterruptedException ignored) {}
            System.out.println("Grabación detenida → " + currentFilePath);
            process = null;
        }
    }

    public static String getCurrentFilePath() {
        return currentFilePath;
    }
}