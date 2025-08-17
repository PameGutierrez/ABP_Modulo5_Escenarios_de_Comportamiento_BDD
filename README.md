[Ver reporte (GitHub Pages)](https://pamegutierrez.github.io/ABP_Modulo5_Escenarios_de_Comportamiento_BDD/)

# BDD – Cucumber + Selenium (Firefox/Chrome) – Java 23 – VS Code – SauceDemo
UI real con **Selenium** en **Firefox** o **Chrome** y **grabación de video** (FFmpeg) + **capturas automáticas** por escenario.

## ✅ Requisitos
- **Java 23 (JDK)** y **Maven 3.8+**
- **Firefox** y/o **Chrome** instalados
- **VS Code** (Extension Pack for Java)
- **FFmpeg** instalado y disponible en `PATH` para la grabación de video
  - Windows: https://ffmpeg.org → agregar `ffmpeg.exe` al PATH
  - macOS: `brew install ffmpeg`
  - Linux: `sudo apt-get install ffmpeg` (u otro gestor)

## ▶️ Ejecutar (por defecto Firefox)
```bash
mvn test
```
### Elegir navegador (Chrome)
```bash
mvn -Dbrowser=chrome test
```
### Filtrar por tags
```bash
mvn -Dcucumber.filter.tags="@ui" test
mvn -Dcucumber.filter.tags="@Login" test
mvn -Dcucumber.filter.tags="@Carrito" test
```

## 🎥 Grabación de video por escenario
- Cada escenario `@ui` inicia y detiene una grabación vía **FFmpeg** automáticamente.
- Los MP4 se guardan en: `evidencias/videos/<Escenario>.mp4`.
- Si FFmpeg no está presente, se omite la grabación sin fallar la prueba.

## 🖼️ Capturas automáticas
- Al finalizar cada escenario `@ui`, se guarda un PNG en `evidencias/capturas/` y se adjunta al reporte de Cucumber.

## 📁 Estructura
- `src/test/resources/features/` → `login_saucedemo.feature`, `cart_saucedemo.feature`
- `src/test/java/steps/UiSteps.java` → Pasos UI (login, carrito)
- `src/test/java/hooks/Hooks.java` → Selección de navegador y hooks @Before/@After
- `src/test/java/utils/ScreenshotUtil.java` → Guardado de capturas
- `src/test/java/utils/VideoRecorderUtil.java` → Grabación con FFmpeg
- `src/test/java/runner/TestRunner.java` → Runner JUnit4
- `evidencias/capturas/` y `evidencias/videos/` → Evidencias

## 🔧 Notas VS Code (Java 23)
- Si tienes múltiples JDK, configura **Java 23** en `Java: Configure Java Runtime`.
- Puedes crear una configuración de ejecución para `runner.TestRunner` o correr por Maven.

## 🔐 Credenciales SauceDemo
- Usuario: `standard_user`
- Password: `secret_sauce`

## 📊 Reportes
- HTML: `target/cucumber-report.html`
- JSON: `target/cucumber.json`
