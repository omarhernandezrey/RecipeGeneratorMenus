# Recipe Generator — Generador de Menús Semanales

---

## CARÁTULA

**Título de la Aplicación:**
# Recipe Generator — Generador de Menús Semanales

**Materia:** Herramientas de Programación Móvil I

**Institución:** Politécnico Grancolombiano — Bogotá, Colombia

**Programa:** Ingeniería de Sistemas

**Integrantes:**
- Omar Hernández Rey — Cód. 100349113
- Julian David Ortiz Bedoya
- Yonatan Ferney Fernández
- Juan David Rivera Casallas

**Docente:** Jose Ricardo Casallas Triana

**Grupo:** B03

**Fecha:** Marzo 2026

---

## TABLA DE CONTENIDO

1. [Título de la Aplicación](#1-título-de-la-aplicación)
2. [Descripción del Proyecto](#2-descripción-del-proyecto) *(F1-02)*
3. [Objetivo General](#3-objetivo-general) *(F1-03)*
4. [Objetivos Específicos](#4-objetivos-específicos) *(F1-04)*
5. [Requerimientos Funcionales](#5-requerimientos-funcionales) *(F1-05)*
6. [Requerimientos No Funcionales](#6-requerimientos-no-funcionales) *(F1-06)*
7. [Diagrama de Casos de Uso](#7-diagrama-de-casos-de-uso) *(F1-07)*
8. [Diagrama de Clases](#8-diagrama-de-clases) *(F1-08)*
9. [Diagrama de Secuencia](#9-diagrama-de-secuencia) *(F1-09)*
10. [Wireframes / Mockups](#10-wireframes--mockups) *(F1-10)*
11. [Referencias](#referencias)

---

## 1. Título de la Aplicación

**Recipe Generator — Generador de Menús Semanales**

La aplicación recibe el nombre **Recipe Generator**, cuya traducción al español es
**Generador de Menús Semanales**. El título refleja con precisión el propósito central
del sistema: generar, visualizar y gestionar menús de comida para cada día de la semana,
facilitando al usuario la planificación de su alimentación de forma semanal.

El nombre combina dos elementos:

- **Recipe Generator** (inglés técnico): alineado con las convenciones del ecosistema
  Android y del entorno académico de desarrollo de software, donde los nombres de
  proyectos y paquetes se escriben en inglés.
- **Generador de Menús Semanales** (español descriptivo): indica con claridad la
  funcionalidad principal para el usuario hispanohablante, que es el público objetivo
  de la aplicación.

### Identificación técnica del proyecto

| Atributo          | Valor                                      |
|-------------------|--------------------------------------------|
| Nombre completo   | Recipe Generator — Generador de Menús Semanales |
| Nombre técnico    | RecipeGenerator                            |
| Package ID        | `com.example.recipe_generator`             |
| Plataforma        | Android (API 24 — Android 7.0 Nougat en adelante) |
| UI Framework      | Jetpack Compose + Material Design 3        |
| Lenguaje          | Kotlin 2.2.10                              |
| Arquitectura      | MVVM + Clean Architecture (3 capas)        |
| Base de datos     | Room Database (SQLite local)               |
| IDE               | Android Studio Panda 2025.3.2              |

---

---

## 2. Descripción del Proyecto

### 2.1 Propósito

**Recipe Generator — Generador de Menús Semanales** es una aplicación móvil nativa para
Android desarrollada en Kotlin con Jetpack Compose y Material Design 3. Su propósito
principal es ayudar a las personas a planificar su alimentación semanal de forma
organizada, práctica y saludable, proporcionando un conjunto de recetas clasificadas
por día de la semana y por tipo de comida (desayuno, almuerzo y cena).

La aplicación busca resolver un problema cotidiano: la dificultad que enfrentan muchas
personas para decidir qué comer cada día, evitar la repetición de platos, y mantener
una dieta equilibrada sin invertir tiempo excesivo en planificación. A través de una
interfaz intuitiva y visualmente atractiva, el usuario puede explorar recetas, guardar
sus favoritas, generar menús personalizados según sus preferencias y configurar la
aplicación a su gusto.

Adicionalmente, el proyecto tiene un propósito académico: demostrar la aplicación de
conceptos fundamentales del desarrollo de aplicaciones móviles en Android, cubriendo
los lineamientos de formación LF1 a LF8 del módulo **Herramientas de Programación
Móvil I** del Politécnico Grancolombiano, con énfasis en arquitectura MVVM + Clean
Architecture, Jetpack Compose, Room Database y navegación entre pantallas.

### 2.2 Público Objetivo

La aplicación está dirigida a los siguientes perfiles de usuario:

| Perfil | Descripción |
|---|---|
| **Personas activas** | Adultos de 18 a 45 años que buscan comer saludable pero disponen de poco tiempo para planificar su menú diario. |
| **Familias** | Hogares que necesitan organizar las comidas de la semana para toda la familia, con control de ingredientes y porciones. |
| **Estudiantes** | Jóvenes universitarios que viven solos y requieren orientación para preparar comidas variadas y económicas. |
| **Deportistas** | Personas que siguen regímenes alimenticios específicos (alta proteína, baja en carbohidratos, etc.) y necesitan filtrar recetas por criterios nutricionales. |
| **Usuarios con dietas especiales** | Personas vegetarianas, veganas o con restricciones alimentarias que desean filtrar recetas según su tipo de dieta. |

El idioma principal de la interfaz es el **español**, orientado al mercado latinoamericano
y en especial al usuario colombiano, aunque la arquitectura permite agregar soporte
multiidioma (español, inglés, portugués) a través de la pantalla de Ajustes.

El dispositivo objetivo principal es el **Samsung SM-A528B** (Samsung Galaxy A52s 5G),
con Android 7.0 Nougat (API 24) como versión mínima soportada, lo que garantiza
compatibilidad con aproximadamente el **95 % de los dispositivos Android activos** en
el mercado.

### 2.3 Alcance Funcional

La versión 1.0 de **Recipe Generator** contempla las siguientes funcionalidades:

#### Funcionalidades incluidas (en alcance)

| # | Módulo | Descripción |
|---|---|---|
| 1 | **Menú Semanal** | Visualización de recetas organizadas por día (Lunes–Domingo) y tipo de comida (Desayuno, Almuerzo, Cena). |
| 2 | **Detalle de Receta** | Pantalla con imagen hero, información nutricional (calorías, proteínas, carbos, grasas), ingredientes y pasos de preparación. |
| 3 | **Favoritos** | Marcado de recetas favoritas con persistencia local (Room Database). Pantalla con búsqueda por texto y filtro por categoría. |
| 4 | **Generador de Menú** | Generación automática de menús según filtros: dificultad (Slider), tipo de comida (Dropdown) y tipo de dieta (Chips). |
| 5 | **Ajustes** | Configuración de tema claro/oscuro (Switch), idioma (RadioButton group), porciones (Spinner/Dropdown) y dietas (Checkbox). |
| 6 | **Menú lateral** | Panel izquierdo con accesos directos: Perfil, Fotos, Video, Navegador web y pantalla de Controles. |
| 7 | **Perfil de usuario** | Pantalla con imagen local e información del perfil (sin autenticación en v1.0). |
| 8 | **Galería de fotos** | LazyColumn de imágenes locales con descripción al seleccionar. |
| 9 | **Reproductor de video** | Pantalla con `VideoView` (AndroidView) para reproducción de video local con `MediaController`. |
| 10 | **Navegador web** | Pantalla con `WebView` (AndroidView), campo de URL y barra de progreso. |
| 11 | **Controles LF8** | Pantalla de demostración con todos los controles requeridos: Button, IconButton, Checkbox, RadioButton, Switch, Dropdown (Spinner), LazyColumn (ListView). |
| 12 | **Widget de escritorio** | Widget nativo Android (AppWidgetProvider) que muestra la receta del día con botón para abrir la app. |

#### Funcionalidades fuera de alcance (v1.0)

- Autenticación de usuarios (login / registro).
- Sincronización con backend en la nube o servidor externo.
- Compra de ingredientes en línea o integración con supermercados.
- Notificaciones push programadas.
- Cámara para fotografiar platos propios.
- Reconocimiento de ingredientes mediante inteligencia artificial.

### 2.4 Stack Tecnológico

El proyecto se desarrolla con la siguiente pila tecnológica, aprobada por el docente:

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material Design 3 |
| Arquitectura | MVVM + Clean Architecture (Presentation / Domain / Data) |
| Navegación | Navigation Component — `navController.navigate()` |
| Base de datos | Room Database (SQLite local) |
| Persistencia liviana | DataStore Preferences |
| Estado | `StateFlow` + `collectAsStateWithLifecycle()` |
| Concurrencia | Kotlin Coroutines + Flow (`viewModelScope`) |
| Build | Gradle Kotlin DSL + Version Catalog (`libs.versions.toml`) |
| SDK mínimo | API 24 — Android 7.0 Nougat |
| SDK objetivo | API 35 — Android 15 |
| IDE | Android Studio Panda 2025.3.2 |

*Las secciones siguientes se completan en las tareas F1-03 a F1-10.*

---

## Referencias

Google LLC. (2024). *Jetpack Compose — Android Developers*.
https://developer.android.com/jetpack/compose

Google LLC. (2024). *Material Design 3 for Android*.
https://m3.material.io/develop/android/jetpack-compose

Google LLC. (2024). *Guide to app architecture — Android Developers*.
https://developer.android.com/topic/architecture

Politécnico Grancolombiano. (2026). *Plan Maestro Recipe Generator v3.0 —
Herramientas de Programación Móvil I*. Bogotá, Colombia.
