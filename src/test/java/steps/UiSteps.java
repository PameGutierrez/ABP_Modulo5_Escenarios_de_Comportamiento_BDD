package steps;

import java.time.Duration;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UiSteps {

    private WebDriver driver() { return Hooks.driver; }
    private WebDriverWait webWait() { return new WebDriverWait(driver(), Duration.ofSeconds(10)); }

    private final By badgeBy = By.cssSelector(".shopping_cart_badge");

    private String slug(String nombre) {
        // ids en SauceDemo siguen este patrón: "sauce-labs-backpack"
        return nombre.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    @Given("abro la página de SauceDemo")
    public void abroLaPaginaSauceDemo() {
        driver().get("https://www.saucedemo.com/");
        webWait().until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
    }

    @When("inicio sesión con usuario {string} y contraseña {string}")
    public void inicioSesion(String user, String pass) {
        webWait().until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).clear();
        driver().findElement(By.id("user-name")).sendKeys(user);
        driver().findElement(By.id("password")).clear();
        driver().findElement(By.id("password")).sendKeys(pass);
        driver().findElement(By.id("login-button")).click();
    }

    @Then("debo ver el inventario")
    public void deboVerInventario() {
        webWait().until(ExpectedConditions.urlContains("inventory"));
        webWait().until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_list")));
        Assert.assertTrue(driver().getCurrentUrl().contains("inventory"));
    }

    @Then("debe mostrarse un mensaje de error de login")
    public void debeMostrarseError() {
        WebElement e = webWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='error']"))
        );
        Assert.assertTrue("No se encontró mensaje de error", e.isDisplayed());
    }

    @When("agrego al carrito el producto {string}")
    public void agregoProducto(String nombre) {
        String id = "add-to-cart-" + slug(nombre);
        WebElement addBtn = webWait().until(
                ExpectedConditions.elementToBeClickable(By.id(id))
        );
        addBtn.click();
        // Espera a que aparezca el badge
        webWait().until(d -> !d.findElements(badgeBy).isEmpty());
    }

    @When("remuevo del carrito el producto {string}")
    public void remuevoProducto(String nombre) {
        String id = "remove-" + slug(nombre);
        WebElement removeBtn = webWait().until(
                ExpectedConditions.elementToBeClickable(By.id(id))
        );
        removeBtn.click();
        // No forzamos espera: si queda en 0, el badge debería desaparecer
    }

    @Then("el badge del carrito debe mostrar {int}")
    public void validarBadgeCarrito(int cantidad) {
        if (cantidad == 0) {
            // Cuando el carrito queda en 0, el badge desaparece
            boolean gone = webWait().until(d -> d.findElements(badgeBy).isEmpty());
            Assert.assertTrue("Se esperaba que el badge no estuviera presente (0 items)", gone);
        } else {
            WebElement badge = webWait().until(ExpectedConditions.visibilityOfElementLocated(badgeBy));
            webWait().until(ExpectedConditions.textToBePresentInElement(badge, String.valueOf(cantidad)));
            Assert.assertEquals(String.valueOf(cantidad), badge.getText());
        }
    }
}

