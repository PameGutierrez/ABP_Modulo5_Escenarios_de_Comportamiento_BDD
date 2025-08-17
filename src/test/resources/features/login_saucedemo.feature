@ui @Login @SmokeTest
Feature: Login en SauceDemo (HU-001)
  Como usuario
  Quiero iniciar sesión
  Para ver el inventario

  Scenario: Login exitoso con credenciales válidas
    Given abro la página de SauceDemo
    When inicio sesión con usuario "standard_user" y contraseña "secret_sauce"
    Then debo ver el inventario

  Scenario Outline: Login fallido con credenciales inválidas
    Given abro la página de SauceDemo
    When inicio sesión con usuario "<user>" y contraseña "<pass>"
    Then debe mostrarse un mensaje de error de login

    Examples:
      | user             | pass         |
      | locked_out_user  | secret_sauce |
      | user_invalido    | secret_sauce |
      | standard_user    | pass_erronea |