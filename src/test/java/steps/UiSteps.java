package steps;

import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UiSteps {

    private WebDriver driver() { return Hooks.driver; }

    @Given("abro la página de SauceDemo")
    public void abroLaPaginaSauceDemo() {
        driver().get("https://www.saucedemo.com/");
    }

    @When("inicio sesión con usuario {string} y contraseña {string}")
    public void inicioSesion(String user, String pass) {
        driver().findElement(By.id("user-name")).clear();
        driver().findElement(By.id("user-name")).sendKeys(user);
        driver().findElement(By.id("password")).clear();
        driver().findElement(By.id("password")).sendKeys(pass);
        driver().findElement(By.id("login-button")).click();
    }

    @Then("debo ver el inventario")
    public void deboVerInventario() {
        Assert.assertTrue("No se cargó el inventario",
                driver().getCurrentUrl().contains("inventory"));
    }

    @Then("debe mostrarse un mensaje de error de login")
    public void debeMostrarseError() {
        WebElement e = driver().findElement(By.cssSelector("[data-test='error']"));
        Assert.assertTrue("No se encontró mensaje de error", e.isDisplayed());
    }

    @When("agrego al carrito el producto {string}")
    public void agregoProducto(String nombre) {
        String id = "add-to-cart-" + nombre.toLowerCase().replace(" ", "-");
        driver().findElement(By.id(id)).click();
    }

    @Then("el badge del carrito debe mostrar {int}")
    public void validarBadgeCarrito(int cantidad) {
        WebElement badge = driver().findElement(By.cssSelector(".shopping_cart_badge"));
        Assert.assertEquals(String.valueOf(cantidad), badge.getText());
    }

    @When("remuevo del carrito el producto {string}")
    public void remuevoProducto(String nombre) {
        String id = "remove-" + nombre.toLowerCase().replace(" ", "-");
        driver().findElement(By.id(id)).click();
    }
}