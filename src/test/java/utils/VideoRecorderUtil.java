package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Graba la pantalla con FFmpeg (debe estar en PATH).
 * Guarda los MP4 en <proyecto>/evidencias/videos/
 * - Inicio: crea carpeta y lanza ffmpeg
 * - Stop: envía 'q' por stdin para cerrar el contenedor MP4 correctamente
 * - Drena stdout/err para evitar bloqueos del proceso
 */
public class VideoRecorderUtil {
    private static Process process;
    private static String currentFilePath;
    private static Thread gobblerThread;

    private static final Path VIDEOS_DIR =
            Paths.get(System.getProperty("user.dir"))
                 .resolve("evidencias")
                 .resolve("videos");

    public static void startRecording(String scenarioSafeName) {
        stopIfRunning(); // por seguridad
        try {
            if (!Files.exists(VIDEOS_DIR)) Files.createDirectories(VIDEOS_DIR);
        } catch (IOException e) {
            System.err.println("No se pudo crear carpeta de videos: " + e.getMessage());
        }

        currentFilePath = VIDEOS_DIR.resolve(scenarioSafeName + ".mp4").toString();

        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;

            if (os.contains("win")) {
                // Windows: captura escritorio completo con gdigrab
                pb = new ProcessBuilder("ffmpeg", "-y",
                        "-f", "gdigrab",
                        "-framerate", "15",
                        "-i", "desktop",
                        "-pix_fmt", "yuv420p",
                        currentFilePath);
            } else if (os.contains("mac")) {
                // macOS: ajustar el índice del dispositivo si es necesario
                pb = new ProcessBuilder("ffmpeg", "-y",
                        "-f", "avfoundation",
                        "-framerate", "15",
                        "-i", "1",
                        "-pix_fmt", "yuv420p",
                        currentFilePath);
            } else {
                // Linux: DISPLAY por defecto
                String display = System.getenv().getOrDefault("DISPLAY", ":0.0");
                pb = new ProcessBuilder("ffmpeg", "-y",
                        "-f", "x11grab",
                        "-framerate", "15",
                        "-i", display,
                        "-pix_fmt", "yuv420p",
                        currentFilePath);
            }

            // Unifica stdout/err y drénalo para evitar que se llene el buffer
            pb.redirectErrorStream(true);
            process = pb.start();

            // Thread para drenar la salida de ffmpeg (evita bloqueos por buffer lleno)
            gobblerThread = new Thread(() -> {
                try (InputStream is = process.getInputStream()) {
                    byte[] buf = new byte[4096];
                    while (process.isAlive()) {
                        if (is.read(buf) < 0) break;
                    }
                } catch (IOException ignored) {}
            }, "ffmpeg-gobbler");
            gobblerThread.setDaemon(true);
            gobblerThread.start();

            System.out.println("Grabación iniciada → " + currentFilePath);
        } catch (IOException e) {
            System.err.println("FFmpeg no disponible o error al iniciar grabación: " + e.getMessage());
        }
    }

    public static void stopRecording() {
        if (process == null) return;
        try {
            // Enviar 'q' por stdin: cierre limpio del MP4
            try (OutputStream os = process.getOutputStream()) {
                os.write('q');
                os.write('\n');
                os.flush();
            }
            // Espera a que termine
            process.waitFor();

            if (gobblerThread != null) {
                try { gobblerThread.join(1000); } catch (InterruptedException ignored) {}
                gobblerThread = null;
            }

            System.out.println("Grabación detenida → " + currentFilePath);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al detener la grabación: " + e.getMessage());
            process.destroyForcibly();
        } finally {
            process = null;
        }
    }

    private static void stopIfRunning() {
        if (process != null && process.isAlive()) {
            stopRecording();
        }
    }

    public static String getCurrentFilePath() {
        return currentFilePath;
    }
}
