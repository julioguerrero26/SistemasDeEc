# Calculadora de Sistemas de Ecuaciones 2x2 

Un programa interactivo en consola desarrollado en **Java** para resolver sistemas de ecuaciones lineales y no lineales de 2x2. Este proyecto fue creado como parte de la materia de **Métodos Numéricos** de la carrera de Ingeniería en Sistemas Computacionales en el Tec de Poza Rica.

##  Características

* **Interfaz en consola a color:** Utiliza códigos de escape ANSI para una experiencia de usuario más amigable y visual.
* **Análisis de ecuaciones mediante Regex:** Capacidad para interpretar ecuaciones introducidas como texto libre (ej. `2x^2 + 3y = 5`).
* **Validación de consistencia:** Verifica que las variables y potencias coincidan en ambas ecuaciones antes de procesarlas.
* **Múltiples métodos de resolución:** 
  * Sustitución
  * Igualación
* **Flujo interactivo:** Permite al usuario confirmar si el sistema fue interpretado correctamente antes de proceder a calcular la solución.

## ️ Tecnologías utilizadas

* **Lenguaje:** Java (Standard Edition)
* **Librerías nativas:** `java.util.Scanner`, `java.util.regex.Matcher`, `java.util.regex.Pattern`

##  Requisitos previos

Para compilar y ejecutar este proyecto, necesitas tener instalado:
* [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/) versión 8 o superior.

##  Explicación Matemática y Algorítmica

El programa resuelve un sistema de ecuaciones lineales de la forma:
$$a_1x + b_1y = c_1$$
$$a_2x + b_2y = c_2$$

Aunque la interfaz ofrece los métodos de "Sustitución" e "Igualación", a nivel algorítmico el código optimiza la resolución apoyándose en la **Regla de Cramer** para calcular el determinante principal del sistema y evitar divisiones por cero iterativas o manejo complejo de fracciones simbólicas.

### 1. Cálculo del Determinante Principal ($\Delta$)
Antes de proceder con cualquier método, el algoritmo calcula el determinante del sistema ($\Delta$) usando los coeficientes de las variables:
$$\Delta = a_1b_2 - a_2b_1$$

Si $\Delta = 0$, el programa determina automáticamente que el sistema no tiene una solución única (es dependiente o inconsistente) y detiene el cálculo.

### 2. Lógica del Método "Sustitución" (Implementación Directa / Cramer)
En esta rama del código, el algoritmo resuelve directamente ambas variables utilizando las determinantes de $x$ ($\Delta x$) y $y$ ($\Delta y$), divididas por el determinante principal:

* Para la variable $x$:
  $$x = \frac{c_1b_2 - c_2b_1}{\Delta}$$

* Para la variable $y$:
  $$y = \frac{a_1c_2 - a_2c_1}{\Delta}$$

### 3. Lógica del Método "Igualación" (Implementación Híbrida)
En este enfoque, el algoritmo primero calcula el valor exacto de la variable $y$ utilizando el mismo principio de determinantes que el método anterior:
$$y = \frac{a_1c_2 - a_2c_1}{\Delta}$$

Una vez obtenido el valor numérico de $y$, el algoritmo evalúa cuál de las dos ecuaciones originales tiene un coeficiente diferente de cero para $x$ (evaluando primero $a_1$), y sustituye el valor de $y$ para despejar $x$:
$$x = \frac{c_1 - b_1y}{a_1} \quad \text{(si } a_1 \neq 0\text{)}$$
$$x = \frac{c_2 - b_2y}{a_2} \quad \text{(si } a_1 = 0\text{)}$$

##  Instalación y Uso

1. **Clona este repositorio** o descarga el archivo `Main.java`.
   ```bash
   git clone https://github.com/julioguerrero26/SistemasDeEc.git