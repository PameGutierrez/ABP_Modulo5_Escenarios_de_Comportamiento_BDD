@ui @Carrito @Regression
Feature: Gestión de carrito en SauceDemo (HU-020)
  Como usuario autenticado
  Quiero agregar y remover productos
  Para gestionar mi compra

  Background:
    Given abro la página de SauceDemo
    When inicio sesión con usuario "standard_user" y contraseña "secret_sauce"
    Then debo ver el inventario

  Scenario: Agregar un producto al carrito
    When agrego al carrito el producto "Sauce Labs Backpack"
    Then el badge del carrito debe mostrar 1

  Scenario: Agregar y remover un producto
    When agrego al carrito el producto "Sauce Labs Backpack"
    And remuevo del carrito el producto "Sauce Labs Backpack"
    Then el badge del carrito debe mostrar 0