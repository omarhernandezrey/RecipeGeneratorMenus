<div align="center">

# Recipe Generator — Generador de Menús Semanales

&nbsp;

**Omar Hernández Rey** — Cód. 100349113  
**Julian David Ortiz Bedoya**  
**Yonatan Ferney Fernández**  
**Juan David Rivera Casallas**

&nbsp;

Facultad de Ingeniería y Ciencias Básicas  
Programa de Ingeniería de Sistemas  
Politécnico Grancolombiano — Bogotá, Colombia

&nbsp;

Herramientas de Programación Móvil I — Grupo B03

Docente: Jose Ricardo Casallas Triana

&nbsp;

Marzo de 2026

</div>

---

## Resumen

*Recipe Generator — Generador de Menús Semanales* es una aplicación móvil nativa para
Android, desarrollada en Kotlin con Jetpack Compose y Material Design 3, cuyo propósito
es facilitar la planificación alimenticia semanal del usuario a través de una interfaz
intuitiva, completamente local y sin dependencia de servicios externos. El presente
documento corresponde a la **Entrega 1** del proyecto integrador del módulo
*Herramientas de Programación Móvil I* del Politécnico Grancolombiano (Grupo B03) y
describe el diseño y la planificación del sistema antes de su implementación. Se incluye
la identificación del proyecto, la descripción del público objetivo y el alcance
funcional, el objetivo general y cinco objetivos específicos medibles alineados con los
lineamientos LF1–LF8 del módulo, doce requerimientos funcionales y veinte requerimientos
no funcionales clasificados según la norma ISO/IEC 25010 (International Organization for
Standardization, 2011), así como los modelos UML de casos de uso, clases y secuencia, y
los wireframes de las tres superficies principales de la interfaz: pantalla principal
(*MainScreen*), pantalla de detalle de receta (*RecipeDetailScreen*) y panel lateral de
filtros (*LeftMenuPanel*). La arquitectura adoptada sigue el patrón MVVM + Clean
Architecture en tres capas (Presentation, Domain, Data), con Room Database como fuente
de verdad local, DataStore Preferences para la persistencia de configuración y
Navigation Component para la navegación entre pantallas (Google LLC, 2024a, 2024c).

**Palabras clave:** aplicación móvil Android, Jetpack Compose, MVVM, Clean Architecture,
Room Database, planificación alimenticia, menú semanal, Material Design 3.

---

## TABLA DE CONTENIDO

> **Nota sobre formato APA 7:** Este documento sigue las pautas de la 7.ª edición del
> *Publication Manual of the American Psychological Association* para trabajos de
> estudiantes. Las referencias utilizan sangría francesa (representada con espacios
> en este formato Markdown). La numeración de página figura en la esquina superior
> derecha al exportar a PDF. Los encabezados de nivel 1 están centrados en negrita;
> los de nivel 2 son alineados a la izquierda en negrita; los de nivel 3 son alineados
> a la izquierda en negrita cursiva, conforme a la Tabla 2.3 del manual APA 7.

&nbsp;

- [Resumen](#resumen)
1. [Título de la Aplicación](#1-título-de-la-aplicación)
2. [Descripción del Proyecto](#2-descripción-del-proyecto)
3. [Objetivo General](#3-objetivo-general)
4. [Objetivos Específicos](#4-objetivos-específicos)
5. [Requerimientos Funcionales](#5-requerimientos-funcionales)
6. [Requerimientos No Funcionales](#6-requerimientos-no-funcionales)
7. [Diagrama de Casos de Uso](#7-diagrama-de-casos-de-uso)
8. [Diagrama de Clases](#8-diagrama-de-clases)
9. [Diagrama de Secuencia](#9-diagrama-de-secuencia)
10. [Wireframes / Mockups](#10-wireframes--mockups)
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

*Tabla 1*

*Identificación técnica del proyecto Recipe Generator*

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

*Tabla 2*

*Perfiles de usuario objetivo de la aplicación*

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

*Tabla 3*

*Módulos funcionales incluidos en el alcance de la versión 1.0*

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

*Tabla 4*

*Stack tecnológico del proyecto*

El proyecto se desarrolla con la siguiente pila tecnológica, aprobada por el docente.
Jetpack Compose es la solución oficial de Google para la construcción de interfaces
declarativas en Android (Google LLC, 2024a); Material Design 3 provee el sistema de
diseño visual adoptado en esta arquitectura (Google LLC, 2024b); Room Database actúa
como capa de abstracción sobre SQLite para la persistencia local (Google LLC, 2024d);
Navigation Component gestiona las transiciones entre destinos mediante `NavHost`
(Google LLC, 2024e); y Kotlin 2.2.10 es el lenguaje de programación principal
(JetBrains, 2024):

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material Design 3 |
| Arquitectura | MVVM + Clean Architecture (Presentation / Domain / Data) |
| Navegación | Navigation Component — `NavHostFragment` + `main_nav_graph.xml` |
| Base de datos | Room Database (SQLite local) |
| Persistencia liviana | DataStore Preferences |
| Estado | `StateFlow` + `collectAsStateWithLifecycle()` |
| Concurrencia | Kotlin Coroutines + Flow (`viewModelScope`) |
| Build | Gradle Kotlin DSL + Version Catalog (`libs.versions.toml`) |
| SDK mínimo | API 24 — Android 7.0 Nougat |
| SDK objetivo | API 36 — Android 16 |
| IDE | Android Studio Panda 2025.3.2 |

---

## 3. Objetivo General

**Desarrollar** una aplicación móvil nativa para Android denominada
*Recipe Generator — Generador de Menús Semanales*, **mediante** la implementación
de Jetpack Compose con Material Design 3, arquitectura MVVM + Clean Architecture,
Room Database y Navigation Component, **con el fin de** proporcionar a los usuarios
una herramienta digital intuitiva que les permita planificar, visualizar y personalizar
sus menús alimenticios semanales de forma local, eficiente y sin dependencia de
servicios externos.

### Desglose estructural del objetivo general

La formulación del objetivo sigue la estructura académica estándar:

| Componente | Contenido |
|---|---|
| **Verbo infinitivo** | Desarrollar |
| **Qué** | Una aplicación móvil nativa para Android — *Recipe Generator* |
| **Cómo** | Mediante Jetpack Compose + Material Design 3, arquitectura MVVM + Clean Architecture (3 capas: Presentation / Domain / Data), Room Database como fuente de verdad local, Navigation Component para la navegación entre pantallas, StateFlow + Coroutines para el manejo del estado, y DataStore Preferences para la persistencia de configuración |
| **Para qué** | Proporcionar a los usuarios una herramienta que les permita planificar, explorar y personalizar sus menús semanales (Lunes–Domingo) de forma organizada, saludable y completamente offline |

### Justificación del objetivo

La selección de **Jetpack Compose** como framework de UI responde a la aprobación
explícita del docente y al hecho de ser la tecnología oficial recomendada por Google
para el desarrollo moderno de interfaces en Android (Google LLC, 2024a). Este framework
permite cumplir íntegramente los lineamientos LF1 a LF8 del módulo mediante estrategias
de compatibilidad documentadas en la tabla de equivalencias del Plan Maestro v3.0
(Politécnico Grancolombiano, 2026) (Actividades, Fragmentos con `ComposeView`,
`AndroidView{}` para controles específicos, `LazyColumn` como equivalente de `ListView`,
entre otros).

La elección de **Room Database** garantiza persistencia de datos completamente local
y sin dependencia de backend externo (Google LLC, 2024d), lo cual es coherente con el
alcance definido para la versión 1.0 de la aplicación.

---

## 4. Objetivos Específicos

Los siguientes cinco objetivos específicos son medibles, alcanzables y están
alineados directamente con los lineamientos de formación LF1 a LF8 del módulo
*Herramientas de Programación Móvil I* (Politécnico Grancolombiano, 2026).

---

### OE-01 — Implementar la arquitectura MVVM + Clean Architecture *(LF1, LF2)*

**Implementar** la arquitectura MVVM + Clean Architecture en tres capas
(Presentation, Domain, Data) **mediante** la definición de ViewModels con
`StateFlow`, casos de uso en la capa Domain, repositorios con interfaces e
implementaciones en la capa Data, y un contenedor de inyección de dependencias
manual (`AppContainer`) en la clase `Application`, **con el fin de** garantizar
una separación de responsabilidades clara, código mantenible y testeable que
demuestre el dominio de los principios de arquitectura de aplicaciones Android
modernas establecidos en LF1 y LF2.

**Indicador de cumplimiento:** La aplicación compila sin errores, las tres capas
están correctamente delimitadas en paquetes (`presentation/`, `domain/`, `data/`),
y cada ViewModel expone su estado únicamente a través de `StateFlow` inmutable.

---

### OE-02 — Construir la interfaz de usuario con Jetpack Compose y Material Design 3 *(LF3, LF4)*

**Construir** todas las pantallas de la aplicación (Menú Semanal, Detalle de Receta,
Favoritos, Generador de Menú, Ajustes y panel lateral) **mediante** Jetpack Compose
con componentes de Material Design 3 (`Scaffold`, `LazyColumn`, `LazyVerticalGrid`,
`NavigationBar`, `Card`, `TopAppBar`) y manejo de estado local con
`remember { mutableStateOf() }`, **con el fin de** ofrecer una interfaz moderna,
responsiva y consistente que cubra íntegramente los lineamientos LF3 (layouts e
interfaz) y LF4 (app interactiva con estado).

**Indicador de cumplimiento:** Cada pantalla está implementada como `@Composable`,
responde correctamente a las interacciones del usuario, y el estado visual se
actualiza de forma reactiva sin requerir `invalidate()` ni `notifyDataSetChanged()`.

---

### OE-03 — Integrar controles de interfaz requeridos por el módulo *(LF7, LF8)*

**Integrar** en la aplicación todos los controles de interfaz exigidos por los
lineamientos LF7 y LF8 **mediante** el uso de sus equivalentes modernos en
Jetpack Compose (`Slider` por `SeekBar`, `LazyColumn` por `ListView`,
`ExposedDropdownMenuBox` por `Spinner`, `Switch`, `Checkbox`, `RadioButton`,
`Button`, `IconButton`) y el uso de `AndroidView{}` para incrustar controles
nativos como `VideoView` y `WebView` dentro de Composables, **con el fin de**
demostrar el dominio y la equivalencia funcional entre los controles del framework
XML tradicional y el paradigma declarativo de Compose.

**Indicador de cumplimiento:** La pantalla `ControlsScreen` exhibe y responde
correctamente a todos los controles listados; `VideoScreen` reproduce video local
y `WebScreen` carga URLs mediante `WebView`; cada control genera una respuesta
visible en la UI al interactuar con él.

---

### OE-04 — Implementar navegación entre actividades y fragmentos *(LF5, LF6)*

**Implementar** la navegación completa de la aplicación **mediante** una
`MainActivity` como Single Activity host con `NavHost` de Navigation Compose,
una segunda actividad `RecipeDetailActivity` lanzada con `Intent.putExtra(recipeId)`
para el detalle de receta, y cada pantalla principal alojada en un `Fragment`
con `ComposeView` como host de la UI Compose, **con el fin de** cumplir
explícitamente los lineamientos LF5 (ciclo de vida de actividades, back stack,
`Intent` y `Bundle`) y LF6 (gestión de fragmentos con `FragmentManager`,
`replace()` y `addToBackStack()`).

**Indicador de cumplimiento:** La aplicación navega correctamente entre las
4 pestañas del `NavigationBar`, lanza `RecipeDetailActivity` al seleccionar
una receta, permite regresar con el botón Back, y el `FragmentManager`
gestiona el back stack de fragmentos sin fugas de memoria.

---

### OE-05 — Persistir datos localmente con Room Database y DataStore *(LF1, LF2)*

**Persistir** todas las recetas, favoritos y preferencias del usuario
**mediante** Room Database (SQLite local) para las entidades `RecipeEntity`,
`IngredientEntity` y `StepEntity`, y DataStore Preferences para la
configuración de tema, idioma, porciones y dietas, **con el fin de** garantizar
que la aplicación funcione completamente offline, que los favoritos y ajustes
del usuario se conserven entre sesiones, y que la capa de datos cumpla los
principios de fuente única de verdad (*Single Source of Truth*) del módulo.

**Indicador de cumplimiento:** Las 21 recetas (7 días × 3 comidas) se almacenan
en Room al primer inicio mediante `DatabaseSeeder`; marcar una receta como favorita
persiste en la base de datos y aparece en `FavoritesScreen` al relanzar la app;
los ajustes guardados en DataStore se restauran correctamente al reiniciar.

---

### Tabla resumen de objetivos específicos

*Tabla 5*

*Resumen de objetivos específicos y cobertura de lineamientos*

| ID | Verbo | Qué | Cómo | LF cubierto |
|---|---|---|---|---|
| OE-01 | Implementar | Arquitectura MVVM + Clean Architecture | ViewModels, StateFlow, UseCases, AppContainer | LF1, LF2 |
| OE-02 | Construir | UI con Compose + Material Design 3 | Composables, Scaffold, LazyColumn, remember | LF3, LF4 |
| OE-03 | Integrar | Controles requeridos por el módulo | Slider, LazyColumn, Dropdown, AndroidView | LF7, LF8 |
| OE-04 | Implementar | Navegación entre Activities y Fragments | NavHost, Intent, Fragment + ComposeView | LF5, LF6 |
| OE-05 | Persistir | Datos locales con Room y DataStore | Room DB (21 recetas), DataStore Preferences | LF1, LF2 |

---

## 5. Requerimientos Funcionales

Los requerimientos funcionales describen las capacidades y comportamientos que el
sistema **debe** proporcionar al usuario. Cada requerimiento está identificado con
el código **RF-XX**, su nombre, descripción, prioridad y la pantalla donde se
implementa.

**Escala de prioridad:** Alta — el sistema no puede funcionar sin este RF.
Media — mejora significativa de la experiencia. Baja — funcionalidad complementaria.

---

### RF-01 — Ver menú semanal por día

| Atributo | Detalle |
|---|---|
| **ID** | RF-01 |
| **Nombre** | Ver menú semanal por día |
| **Descripción** | El sistema debe mostrar las recetas del día seleccionado (Lunes a Domingo), organizadas por tipo de comida: Desayuno, Almuerzo y Cena. El usuario puede cambiar de día mediante un selector de pestañas horizontal. |
| **Actor** | Usuario |
| **Pantalla** | `RecipeListScreen` (Inicio) |
| **Prioridad** | Alta |
| **Criterio de aceptación** | Al seleccionar un día en el tab layout, la lista de recetas se actualiza automáticamente mostrando únicamente las 3 recetas (o las disponibles) del día elegido. |

---

### RF-02 — Buscar receta en favoritos

| Atributo | Detalle |
|---|---|
| **ID** | RF-02 |
| **Nombre** | Buscar receta por texto |
| **Descripción** | El sistema debe permitir al usuario filtrar las recetas guardadas como favoritas mediante un campo de búsqueda de texto libre. La búsqueda aplica sobre el título y la descripción de la receta en tiempo real. |
| **Actor** | Usuario |
| **Pantalla** | `FavoritesScreen` |
| **Prioridad** | Alta |
| **Criterio de aceptación** | Al escribir en el campo de búsqueda, la lista se filtra en tiempo real mostrando únicamente las recetas cuyos títulos o descripciones contienen el texto ingresado. Si no hay coincidencias, se muestra un estado vacío informativo. |

---

### RF-03 — Ver detalle completo de una receta

| Atributo | Detalle |
|---|---|
| **ID** | RF-03 |
| **Nombre** | Ver detalle de receta |
| **Descripción** | El sistema debe mostrar una pantalla de detalle al seleccionar una receta, con imagen hero, título, descripción, información nutricional (calorías, proteínas, carbohidratos, grasas), lista de ingredientes y pasos de preparación numerados. |
| **Actor** | Usuario |
| **Pantalla** | `RecipeDetailScreen` / `RecipeDetailActivity` |
| **Prioridad** | Alta |
| **Criterio de aceptación** | La pantalla de detalle muestra todos los campos de la receta. Los pasos de preparación se presentan en orden numérico. El usuario puede regresar a la pantalla anterior mediante el botón de retroceso. |

---

### RF-04 — Marcar y desmarcar receta como favorita

| Atributo | Detalle |
|---|---|
| **ID** | RF-04 |
| **Nombre** | Toggle de favorito |
| **Descripción** | El sistema debe permitir al usuario marcar o desmarcar cualquier receta como favorita desde la pantalla de lista o desde el detalle. El estado de favorito debe persistir localmente entre sesiones. |
| **Actor** | Usuario |
| **Pantalla** | `RecipeListScreen`, `RecipeDetailScreen`, `FavoritesScreen` |
| **Prioridad** | Alta |
| **Criterio de aceptación** | Al presionar el ícono de favorito, el estado cambia visualmente de forma inmediata (ícono relleno / vacío). Al cerrar y reabrir la app, el estado de favorito se conserva. La receta aparece o desaparece de `FavoritesScreen` según corresponda. |

---

### RF-05 — Filtrar favoritos por categoría

| Atributo | Detalle |
|---|---|
| **ID** | RF-05 |
| **Nombre** | Filtrar favoritos por categoría |
| **Descripción** | El sistema debe permitir filtrar las recetas favoritas por categoría de comida (Todos, Desayuno, Almuerzo, Cena) mediante chips de selección horizontal. |
| **Actor** | Usuario |
| **Pantalla** | `FavoritesScreen` |
| **Prioridad** | Media |
| **Criterio de aceptación** | Al seleccionar un chip de categoría, la cuadrícula de favoritos muestra únicamente las recetas de esa categoría. Al seleccionar "Todos", se muestran todas las recetas favoritas. |

---

### RF-06 — Generar menú personalizado

| Atributo | Detalle |
|---|---|
| **ID** | RF-06 |
| **Nombre** | Generar menú con filtros |
| **Descripción** | El sistema debe permitir al usuario configurar parámetros (nivel de dificultad mediante un Slider, tipo de comida mediante un Dropdown, tipo de dieta mediante Chips) y generar un menú filtrado al presionar el botón "Generar Menú". |
| **Actor** | Usuario |
| **Pantalla** | `MenuGeneratorScreen` |
| **Prioridad** | Alta |
| **Criterio de aceptación** | Al presionar "Generar Menú", el sistema consulta la base de datos y muestra en una `LazyColumn` las recetas que cumplen con todos los filtros seleccionados. Si no hay resultados, muestra un mensaje informativo. |

---

### RF-07 — Configurar preferencias de la aplicación

| Atributo | Detalle |
|---|---|
| **ID** | RF-07 |
| **Nombre** | Configurar ajustes |
| **Descripción** | El sistema debe permitir al usuario configurar: tema visual (claro/oscuro) mediante Switch, idioma (Español/Inglés/Portugués) mediante RadioButton group, número de porciones (1–10) mediante ExposedDropdownMenu, y tipos de dieta mediante Checkbox. Los cambios deben persistir con DataStore. |
| **Actor** | Usuario |
| **Pantalla** | `SettingsScreen` |
| **Prioridad** | Media |
| **Criterio de aceptación** | Los cambios en el tema se aplican de forma inmediata en toda la app. Al reiniciar la aplicación, las preferencias guardadas se restauran correctamente desde DataStore. |

---

### RF-08 — Reproducir video de receta

| Atributo | Detalle |
|---|---|
| **ID** | RF-08 |
| **Nombre** | Reproducir video |
| **Descripción** | El sistema debe mostrar una pantalla con un reproductor de video (`VideoView` dentro de `AndroidView`) con controles de reproducción (`MediaController`): play, pause, barra de progreso y control de volumen. |
| **Actor** | Usuario |
| **Pantalla** | `VideoScreen` |
| **Prioridad** | Media |
| **Criterio de aceptación** | El video se reproduce correctamente desde un recurso local. Los controles de reproducción responden a las interacciones del usuario. El video se pausa automáticamente al salir de la pantalla (`DisposableEffect`). |

---

### RF-09 — Navegar a URL en navegador integrado

| Atributo | Detalle |
|---|---|
| **ID** | RF-09 |
| **Nombre** | Navegador web integrado |
| **Descripción** | El sistema debe proporcionar una pantalla con un campo de texto para ingresar una URL, un botón para navegar, y un `WebView` (dentro de `AndroidView`) que cargue la página. Debe mostrar una barra de progreso lineal mientras carga. |
| **Actor** | Usuario |
| **Pantalla** | `WebScreen` |
| **Prioridad** | Media |
| **Criterio de aceptación** | Al ingresar una URL válida y presionar "Ir", el `WebView` carga la página. La barra de progreso se muestra durante la carga y desaparece al completarse. JavaScript está habilitado. |

---

### RF-10 — Mostrar galería de fotos de recetas

| Atributo | Detalle |
|---|---|
| **ID** | RF-10 |
| **Nombre** | Galería de fotos |
| **Descripción** | El sistema debe mostrar una pantalla con una `LazyColumn` de imágenes locales de recetas. Al seleccionar una imagen, debe mostrarse su título y descripción en un `Text` inferior con una animación de selección. |
| **Actor** | Usuario |
| **Pantalla** | `PhotosScreen` |
| **Prioridad** | Media |
| **Criterio de aceptación** | La galería muestra mínimo 5 imágenes locales. Al seleccionar una, la descripción se actualiza con animación. El estado de selección se mantiene mientras el usuario navega por la lista. |

---

### RF-11 — Mostrar widget de receta del día en el escritorio

| Atributo | Detalle |
|---|---|
| **ID** | RF-11 |
| **Nombre** | Widget de escritorio |
| **Descripción** | El sistema debe proveer un widget de Android Home Screen que muestre el título de la receta del día y un botón que abra la aplicación directamente en el detalle de esa receta. El widget se actualiza periódicamente. |
| **Actor** | Usuario |
| **Pantalla** | `RecipeWidgetProvider` (AppWidgetProvider) |
| **Prioridad** | Baja |
| **Criterio de aceptación** | El widget aparece disponible en el menú de widgets del launcher. Muestra el nombre de la receta del día. Al presionar el botón, abre la app. Se actualiza según el `updatePeriodMillis` configurado. |

---

### RF-12 — Demostrar controles de interfaz requeridos por el módulo

| Atributo | Detalle |
|---|---|
| **ID** | RF-12 |
| **Nombre** | Pantalla de controles LF8 |
| **Descripción** | El sistema debe incluir una pantalla que exhiba y haga funcionar todos los controles exigidos por LF8: Button, IconButton (ImageButton), Checkbox, RadioButton group (RadioGroup), Switch (ToggleButton), ExposedDropdownMenuBox (Spinner), LazyColumn (ListView) con scroll. Al presionar "Confirmar", todos los valores seleccionados se muestran en un Text. |
| **Actor** | Evaluador / Docente |
| **Pantalla** | `ControlsScreen` |
| **Prioridad** | Alta |
| **Criterio de aceptación** | Todos los controles responden correctamente a la interacción. El texto de resultado refleja el estado actual de cada control. La pantalla hace scroll cuando el contenido supera la altura de la pantalla. |

---

### Tabla resumen de Requerimientos Funcionales

| ID | Nombre | Pantalla | Prioridad | LF |
|---|---|---|---|---|
| RF-01 | Ver menú semanal por día | RecipeListScreen | Alta | LF3, LF8 |
| RF-02 | Buscar receta por texto | FavoritesScreen | Alta | LF4, LF8 |
| RF-03 | Ver detalle de receta | RecipeDetailScreen | Alta | LF5, LF7 |
| RF-04 | Toggle de favorito | RecipeListScreen, Detail | Alta | LF4, LF8 |
| RF-05 | Filtrar favoritos por categoría | FavoritesScreen | Media | LF8 |
| RF-06 | Generar menú con filtros | MenuGeneratorScreen | Alta | LF7, LF8 |
| RF-07 | Configurar ajustes | SettingsScreen | Media | LF8 |
| RF-08 | Reproducir video | VideoScreen | Media | LF7 |
| RF-09 | Navegador web integrado | WebScreen | Media | LF7 |
| RF-10 | Galería de fotos | PhotosScreen | Media | LF7, LF8 |
| RF-11 | Widget de escritorio | RecipeWidgetProvider | Baja | LF7 |
| RF-12 | Pantalla de controles LF8 | ControlsScreen | Alta | LF8 |

---

## 6. Requerimientos No Funcionales

Los requerimientos no funcionales (RNF) definen los atributos de calidad que el
sistema debe cumplir: cómo debe comportarse, no qué debe hacer. Están organizados
por categoría de calidad según el estándar ISO/IEC 25010 — *Systems and software
Quality Requirements and Evaluation (SQuaRE): System and software quality models*
(International Organization for Standardization [ISO/IEC], 2011).

---

### 6.1 Rendimiento

| ID | Requerimiento | Métrica |
|---|---|---|
| **RNF-01** | La aplicación debe iniciar completamente (splash → pantalla de inicio con recetas visibles) en un tiempo menor a **3 segundos** en el dispositivo Samsung SM-A528B con Android 12. | Tiempo medido desde el toque del ícono hasta que `RecipeListScreen` es interactuable. |
| **RNF-02** | La transición entre pantallas (navegación entre tabs del `NavigationBar`) debe completarse en menos de **300 milisegundos**, garantizando una experiencia fluida a 60 fps. | Medido con Android Profiler — Frame rendering time < 16 ms por fotograma. |
| **RNF-03** | La consulta a Room Database (obtener recetas del día) debe completarse en menos de **100 milisegundos** en dispositivos con API 24 o superior. | Verificado con Room Query instrumentation tests. |
| **RNF-04** | La generación de menú personalizado (`GenerateMenuUseCase`) debe retornar resultados en menos de **500 milisegundos** independientemente del número de filtros aplicados. | Medido en test unitario con Room In-Memory Database. |

---

### 6.2 Seguridad

| ID | Requerimiento | Métrica |
|---|---|---|
| **RNF-05** | Todos los datos del usuario (favoritos, preferencias) deben almacenarse **únicamente en el dispositivo local**, sin transmisión a servidores externos ni servicios de analítica de terceros. | Verificación mediante análisis de tráfico de red: la app no realiza ninguna petición HTTP en condiciones de uso normal. |
| **RNF-06** | La aplicación debe cumplir con el principio de **mínimos permisos**: solicitar únicamente el permiso `INTERNET` (requerido para `WebView`) y ningún otro permiso sensible (cámara, contactos, ubicación, micrófono). | Revisión del `AndroidManifest.xml`: máximo 1 permiso declarado. |
| **RNF-07** | Los datos persistidos en Room Database no deben contener información personal identificable (PII) del usuario. El perfil de usuario es estático y definido en código fuente, no ingresado por el usuario. | Revisión de esquema de base de datos: ninguna tabla contiene campos de nombre, correo, contraseña ni datos biométricos. |
| **RNF-08** | El `WebView` debe habilitar `WebViewClient` personalizado para controlar la navegación y evitar redirecciones a URLs maliciosas externas fuera de la intención del usuario. | El `WebViewClient` intercepta `shouldOverrideUrlLoading` y mantiene la navegación dentro del componente. |

---

### 6.3 Usabilidad

| ID | Requerimiento | Métrica |
|---|---|---|
| **RNF-09** | La interfaz debe seguir las guías de **Material Design 3** en todos los componentes: colores del tema (`Primary #4800B2`, `Secondary #00C2A8`, `Tertiary #FF0081`), tipografía, espaciado y formas redondeadas definidos en `AppTheme.kt`. | Revisión visual contra la paleta de colores y componentes M3 documentados. |
| **RNF-10** | Todos los elementos interactivos (botones, chips, ítems de lista) deben tener un área táctil mínima de **48 × 48 dp**, cumpliendo las directrices de accesibilidad de Android. | Revisión con el inspector de layouts de Android Studio. |
| **RNF-11** | La aplicación debe soportar **modo claro y modo oscuro** de forma completa, aplicando el tema correspondiente en todos los Composables sin textos ni fondos hardcodeados con colores absolutos. | Verificación visual en ambos modos con Android Studio Preview y en dispositivo físico. |
| **RNF-12** | La navegación debe ser **intuitiva y consistente**: la `NavigationBar` debe estar visible en todas las pantallas principales, el ícono activo debe resaltarse visualmente, y el botón de retroceso debe funcionar correctamente en toda la jerarquía de navegación. | Prueba de navegación manual recorriendo todos los destinos y verificando el estado activo del tab. |

---

### 6.4 Portabilidad

| ID | Requerimiento | Métrica |
|---|---|---|
| **RNF-13** | La aplicación debe ejecutarse correctamente en dispositivos Android con **API 24 (Android 7.0 Nougat) o superior**, cubriendo aproximadamente el 95 % de los dispositivos Android activos en el mercado. | `minSdk = 24` configurado en `build.gradle.kts`. Prueba en AVD con API 24. |
| **RNF-14** | La interfaz debe adaptarse correctamente a diferentes tamaños de pantalla: teléfonos (360 dp – 480 dp de ancho) y tabletas (600 dp o más), utilizando layouts responsivos con `LazyVerticalGrid` y `AdaptiveCells`. | Verificación en AVD con tamaños de pantalla 5" (1080×1920) y 10" (1200×1920). |
| **RNF-15** | La aplicación debe compilar y ejecutarse sin modificaciones en **Android Studio Panda 2025.3.2** con JDK 25.0.2, SDK API 36 y Kotlin 2.2.10. | Compilación exitosa con `./gradlew assembleDebug` sin errores ni warnings críticos. |

---

### 6.5 Mantenibilidad

| ID | Requerimiento | Métrica |
|---|---|---|
| **RNF-16** | El código fuente debe seguir la arquitectura **MVVM + Clean Architecture** con separación estricta en tres capas (`presentation/`, `domain/`, `data/`), sin que la capa de dominio importe clases de Android Framework. | Verificación estática: ningún archivo en `domain/` importa `android.*`. |
| **RNF-17** | Cada pantalla debe tener su propio **ViewModel** que exponga el estado mediante `StateFlow` inmutable, sin que la UI acceda directamente a repositorios o bases de datos. | Revisión de código: los Composables solo importan clases del paquete `presentation/` y el modelo de dominio. |
| **RNF-18** | El proyecto debe gestionar sus dependencias mediante **Gradle Version Catalog** (`libs.versions.toml`), evitando versiones hardcodeadas en los archivos `build.gradle.kts`. | Revisión de `build.gradle.kts`: todas las dependencias usan `libs.*` del catálogo. |

---

### 6.6 Confiabilidad

| ID | Requerimiento | Métrica |
|---|---|---|
| **RNF-19** | La aplicación **no debe cerrarse inesperadamente** (crash) durante el flujo principal de uso: navegar entre pantallas, marcar favoritos, generar menú y configurar ajustes. | Cero crashes en 30 minutos de uso continuo en el dispositivo Samsung SM-A528B. |
| **RNF-20** | Las operaciones de lectura y escritura en Room Database deben ejecutarse en **hilos de fondo** (Coroutines con `Dispatchers.IO`) para no bloquear el hilo principal de la UI. | Ninguna operación de Room se llama desde el hilo principal; uso exclusivo de `viewModelScope.launch {}`. |

---

### Tabla resumen de Requerimientos No Funcionales

| ID | Categoría | Nombre resumido |
|---|---|---|
| RNF-01 | Rendimiento | Inicio < 3 segundos |
| RNF-02 | Rendimiento | Transición < 300 ms a 60 fps |
| RNF-03 | Rendimiento | Consulta Room < 100 ms |
| RNF-04 | Rendimiento | Generación de menú < 500 ms |
| RNF-05 | Seguridad | Datos solo en dispositivo local (sin backend) |
| RNF-06 | Seguridad | Mínimos permisos (solo INTERNET) |
| RNF-07 | Seguridad | Sin PII en base de datos |
| RNF-08 | Seguridad | WebViewClient personalizado |
| RNF-09 | Usabilidad | Material Design 3 en toda la UI |
| RNF-10 | Usabilidad | Área táctil mínima 48×48 dp |
| RNF-11 | Usabilidad | Modo claro y oscuro completo |
| RNF-12 | Usabilidad | Navegación intuitiva y consistente |
| RNF-13 | Portabilidad | API 24+ (95 % de dispositivos) |
| RNF-14 | Portabilidad | Responsivo: teléfono y tableta |
| RNF-15 | Portabilidad | Compila en Android Studio Panda 2025.3.2 |
| RNF-16 | Mantenibilidad | Clean Architecture sin cruces de capas |
| RNF-17 | Mantenibilidad | ViewModel + StateFlow por pantalla |
| RNF-18 | Mantenibilidad | Gradle Version Catalog |
| RNF-19 | Confiabilidad | Cero crashes en flujo principal |
| RNF-20 | Confiabilidad | Room en hilos de fondo (Coroutines) |

---

## 7. Diagrama de Casos de Uso UML

### 7.1 Descripción general

El diagrama de casos de uso representa las interacciones entre el actor principal
(**Usuario**) y las funcionalidades del sistema **Recipe Generator**. Se identifican
**12 casos de uso** agrupados en 5 módulos funcionales, que cubren la totalidad de
los requerimientos funcionales RF-01 a RF-12.

**Actor principal:** Usuario — persona que interactúa con la aplicación móvil a
través de la pantalla táctil del dispositivo Android.

**Sistema:** Recipe Generator — Generador de Menús Semanales (`com.example.recipe_generator`).

---

### 7.2 Diagrama PlantUML

> Para renderizar el diagrama, pegar el código en [https://www.plantuml.com/plantuml/uml](https://www.plantuml.com/plantuml/uml)
> o usar la extensión PlantUML de VS Code / IntelliJ.

```plantuml
@startuml RecipeGenerator_UseCases

skinparam actorStyle awesome
skinparam packageStyle rectangle
skinparam usecase {
  BackgroundColor #EDE7F6
  BorderColor #4800B2
  FontColor #1B1B1E
  ArrowColor #4800B2
}
skinparam actor {
  BackgroundColor #00C2A8
  BorderColor #006F64
  FontColor #FFFFFF
}

left to right direction

actor "Usuario" as U

rectangle "Recipe Generator — Generador de Menús Semanales" {

  rectangle "Módulo: Menú Semanal" {
    usecase "CU-01\nVer menú semanal\npor día" as CU01
    usecase "CU-02\nVer detalle\nde receta" as CU02
  }

  rectangle "Módulo: Favoritos" {
    usecase "CU-03\nMarcar receta\ncomo favorita" as CU03
    usecase "CU-04\nBuscar receta\nen favoritos" as CU04
    usecase "CU-05\nFiltrar favoritos\npor categoría" as CU05
    usecase "CU-06\nEliminar receta\nde favoritos" as CU06
  }

  rectangle "Módulo: Generador de Menú" {
    usecase "CU-07\nConfigurar filtros\n(dificultad, tipo, dieta)" as CU07
    usecase "CU-08\nGenerar menú\npersonalizado" as CU08
  }

  rectangle "Módulo: Ajustes" {
    usecase "CU-09\nConfigurar tema\nclaro / oscuro" as CU09
    usecase "CU-10\nSeleccionar idioma\ny porciones" as CU10
  }

  rectangle "Módulo: Contenido Multimedia" {
    usecase "CU-11\nReproducir video\nde receta" as CU11
    usecase "CU-12\nNavegar URL en\nnavegador integrado" as CU12
  }
}

U --> CU01
U --> CU02
U --> CU03
U --> CU04
U --> CU05
U --> CU06
U --> CU07
U --> CU08
U --> CU09
U --> CU10
U --> CU11
U --> CU12

CU02 .> CU03 : <<extend>>\n(desde detalle)
CU07 .> CU08 : <<include>>\n(filtros requeridos)
CU04 .> CU05 : <<extend>>\n(filtro adicional)

@enduml
```

---

### 7.3 Representación textual del diagrama

```
┌─────────────────────────────────────────────────────────────────────────────┐
│              Recipe Generator — Generador de Menús Semanales                │
│                                                                             │
│  ┌──── Módulo: Menú Semanal ──────┐  ┌──── Módulo: Favoritos ────────────┐  │
│  │  (CU-01) Ver menú por día      │  │  (CU-03) Marcar como favorita    │  │
│  │  (CU-02) Ver detalle receta    │  │  (CU-04) Buscar en favoritos     │  │
│  └────────────────────────────────┘  │  (CU-05) Filtrar por categoría   │  │
│                                      │  (CU-06) Eliminar de favoritos   │  │
│  ┌──── Módulo: Generador ─────────┐  └──────────────────────────────────┘  │
│  │  (CU-07) Configurar filtros    │                                         │
│  │  (CU-08) Generar menú          │  ┌──── Módulo: Ajustes ──────────────┐  │
│  └────────────────────────────────┘  │  (CU-09) Tema claro/oscuro       │  │
│                                      │  (CU-10) Idioma y porciones      │  │
│  ┌──── Módulo: Multimedia ────────┐  └──────────────────────────────────┘  │
│  │  (CU-11) Reproducir video      │                                         │
│  │  (CU-12) Navegar URL web       │                                         │
│  └────────────────────────────────┘                                         │
└─────────────────────────────────────────────────────────────────────────────┘
                              ▲ asociación ▲
                         ┌────┴────┐
                         │ Usuario │
                         └─────────┘
```

---

### 7.4 Descripción de cada Caso de Uso

| ID | Caso de Uso | Actor | Pantalla | RF asociado |
|---|---|---|---|---|
| CU-01 | Ver menú semanal por día | Usuario | RecipeListScreen | RF-01 |
| CU-02 | Ver detalle de receta | Usuario | RecipeDetailScreen | RF-03 |
| CU-03 | Marcar receta como favorita | Usuario | RecipeListScreen, Detail | RF-04 |
| CU-04 | Buscar receta en favoritos | Usuario | FavoritesScreen | RF-02 |
| CU-05 | Filtrar favoritos por categoría | Usuario | FavoritesScreen | RF-05 |
| CU-06 | Eliminar receta de favoritos | Usuario | FavoritesScreen | RF-04 |
| CU-07 | Configurar filtros del generador | Usuario | MenuGeneratorScreen | RF-06 |
| CU-08 | Generar menú personalizado | Usuario | MenuGeneratorScreen | RF-06 |
| CU-09 | Configurar tema claro/oscuro | Usuario | SettingsScreen | RF-07 |
| CU-10 | Seleccionar idioma y porciones | Usuario | SettingsScreen | RF-07 |
| CU-11 | Reproducir video de receta | Usuario | VideoScreen | RF-08 |
| CU-12 | Navegar URL en navegador integrado | Usuario | WebScreen | RF-09 |

### 7.5 Relaciones entre casos de uso

| Relación | Tipo | Descripción |
|---|---|---|
| CU-02 → CU-03 | `<<extend>>` | Desde el detalle de receta el usuario **puede** marcar como favorita (extensión opcional). |
| CU-07 → CU-08 | `<<include>>` | Para generar el menú (CU-08) es **obligatorio** configurar los filtros (CU-07) previamente. |
| CU-04 → CU-05 | `<<extend>>` | El filtro por categoría (CU-05) es una extensión opcional de la búsqueda (CU-04). |

---

## 8. Diagrama de Clases UML

### 8.1 Descripción general

El diagrama de clases refleja la estructura real del código fuente del proyecto,
organizado en las tres capas de **Clean Architecture**:

- **Domain** — modelos de negocio puros e interfaces de repositorios (sin dependencias Android).
- **Data** — entidades Room, DAOs, implementaciones de repositorios y mappers.
- **Presentation** — ViewModels con StateFlow que consumen los casos de uso.

> Para renderizar el diagrama, pegar el código en [https://www.plantuml.com/plantuml/uml](https://www.plantuml.com/plantuml/uml)

---

### 8.2 Diagrama PlantUML — Capa Domain

```plantuml
@startuml RecipeGenerator_ClassDiagram_Domain

skinparam classBackgroundColor #EDE7F6
skinparam classBorderColor #4800B2
skinparam classArrowColor #4800B2
skinparam classFontColor #1B1B1E
skinparam packageBackgroundColor #F3E5F5
skinparam packageBorderColor #7E57C2

package "domain.model" #EDE7F6 {

  class Recipe {
    + id: String
    + title: String
    + imageRes: String
    + timeInMinutes: Int
    + calories: Int
    + difficulty: String
    + category: String
    + categorySubtitle: String
    + description: String
    + isFavorite: Boolean
    + rating: Double
    + proteinGrams: Int
    + carbsGrams: Int
    + fatGrams: Int
    + dayOfWeek: String
    + ingredients: List<Ingredient>
    + steps: List<RecipeStep>
  }

  class Ingredient {
    + id: Int
    + name: String
    + quantity: String
    + unit: String
  }

  class RecipeStep {
    + id: Int
    + stepNumber: Int
    + title: String
    + description: String
  }

  class UserPreferences {
    + theme: String
    + language: String
    + defaultPortions: Int
    + selectedDiets: Set<String>
  }
}

package "domain.repository" #E8EAF6 {

  interface RecipeRepository {
    + getAllRecipes(): Flow<List<Recipe>>
    + getRecipesByDay(day: String): Flow<List<Recipe>>
    + getRecipesByCategory(category: String): Flow<List<Recipe>>
    + getRecipeById(id: String): Flow<Recipe?>
    + searchRecipes(query: String): Flow<List<Recipe>>
    + insertAll(recipes: List<Recipe>)
    + count(): Int
  }

  interface FavoritesRepository {
    + getFavoriteRecipes(): Flow<List<Recipe>>
    + getFavoriteIds(): Flow<Set<String>>
    + toggleFavorite(recipeId: String)
    + removeFavorite(recipeId: String)
    + isFavorite(recipeId: String): Boolean
  }

  interface UserPrefsRepository {
    + getUserPreferences(): Flow<UserPreferences>
    + saveTheme(theme: String)
    + saveLanguage(language: String)
    + saveDefaultPortions(portions: Int)
    + saveSelectedDiets(diets: Set<String>)
  }
}

package "domain.usecase" #F3E5F5 {

  class GetMenuForDayUseCase {
    - recipeRepository: RecipeRepository
    + invoke(day: String): Flow<List<Recipe>>
  }

  class GetRecipeDetailUseCase {
    - recipeRepository: RecipeRepository
    + invoke(id: String): Flow<Recipe?>
  }

  class ToggleFavoriteUseCase {
    - favoritesRepository: FavoritesRepository
    + invoke(recipeId: String)
  }

  class GenerateMenuUseCase {
    - recipeRepository: RecipeRepository
    + invoke(maxDifficulty, selectedTypes, selectedDiets): Flow<List<Recipe>>
  }
}

Recipe "1" *-- "0..*" Ingredient : contains >
Recipe "1" *-- "0..*" RecipeStep : contains >

RecipeRepository ..> Recipe : uses >
FavoritesRepository ..> Recipe : uses >
UserPrefsRepository ..> UserPreferences : uses >

GetMenuForDayUseCase --> RecipeRepository : depends on >
GetRecipeDetailUseCase --> RecipeRepository : depends on >
ToggleFavoriteUseCase --> FavoritesRepository : depends on >
GenerateMenuUseCase --> RecipeRepository : depends on >

@enduml
```

---

### 8.3 Diagrama PlantUML — Capa Data

```plantuml
@startuml RecipeGenerator_ClassDiagram_Data

skinparam classBackgroundColor #E8F5E9
skinparam classBorderColor #2E7D32
skinparam classArrowColor #2E7D32
skinparam packageBackgroundColor #F1F8E9
skinparam packageBorderColor #66BB6A

package "data.local.entity" #E8F5E9 {

  class RecipeEntity {
    + id: String <<PrimaryKey>>
    + title: String
    + imageRes: String
    + timeInMinutes: Int
    + calories: Int
    + difficulty: String
    + category: String
    + categorySubtitle: String
    + description: String
    + isFavorite: Boolean
    + rating: Double
    + proteinGrams: Int
    + carbsGrams: Int
    + fatGrams: Int
    + dayOfWeek: String
  }

  class IngredientEntity {
    + id: Int <<PrimaryKey>>
    + recipeId: String <<ForeignKey>>
    + name: String
    + quantity: String
    + unit: String
  }

  class StepEntity {
    + id: Int <<PrimaryKey>>
    + recipeId: String <<ForeignKey>>
    + stepNumber: Int
    + title: String
    + description: String
  }
}

package "data.local.dao" #F1F8E9 {

  interface RecipeDao {
    + getAllRecipes(): Flow<List<RecipeEntity>>
    + getRecipesByDay(day: String): Flow<List<RecipeEntity>>
    + getRecipeById(id: String): Flow<RecipeEntity?>
    + searchRecipes(query: String): Flow<List<RecipeEntity>>
    + getFavoriteRecipes(): Flow<List<RecipeEntity>>
    + insertAll(recipes: List<RecipeEntity>)
    + updateFavorite(id: String, isFavorite: Boolean)
    + count(): Int
  }
}

package "data.repository" #DCEDC8 {

  class RecipeRepositoryImpl {
    - recipeDao: RecipeDao
    + getAllRecipes(): Flow<List<Recipe>>
    + getRecipesByDay(day: String): Flow<List<Recipe>>
    + getRecipesByCategory(category: String): Flow<List<Recipe>>
    + getRecipeById(id: String): Flow<Recipe?>
    + searchRecipes(query: String): Flow<List<Recipe>>
    + insertAll(recipes: List<Recipe>)
    + count(): Int
  }

  class FavoritesRepositoryImpl {
    - recipeDao: RecipeDao
    + getFavoriteRecipes(): Flow<List<Recipe>>
    + getFavoriteIds(): Flow<Set<String>>
    + toggleFavorite(recipeId: String)
    + removeFavorite(recipeId: String)
    + isFavorite(recipeId: String): Boolean
  }

  class UserPrefsRepositoryImpl {
    - dataStore: DataStore<Preferences>
    + getUserPreferences(): Flow<UserPreferences>
    + saveTheme(theme: String)
    + saveLanguage(language: String)
    + saveDefaultPortions(portions: Int)
    + saveSelectedDiets(diets: Set<String>)
  }
}

package "data.mapper" #F9FBE7 {
  class RecipeMapper {
    + {static} toDomain(entity: RecipeEntity): Recipe
    + {static} toEntity(domain: Recipe): RecipeEntity
  }
}

package "di" #FFF9C4 {
  class AppContainer {
    + recipeRepository: RecipeRepository
    + favoritesRepository: FavoritesRepository
    + userPrefsRepository: UserPrefsRepository
    + getMenuForDayUseCase: GetMenuForDayUseCase
    + getRecipeDetailUseCase: GetRecipeDetailUseCase
    + toggleFavoriteUseCase: ToggleFavoriteUseCase
    + generateMenuUseCase: GenerateMenuUseCase
  }
}

RecipeEntity "1" -- "0..*" IngredientEntity : has >
RecipeEntity "1" -- "0..*" StepEntity : has >
RecipeRepositoryImpl ..|> RecipeRepository
FavoritesRepositoryImpl ..|> FavoritesRepository
UserPrefsRepositoryImpl ..|> UserPrefsRepository
RecipeRepositoryImpl --> RecipeDao : uses >
FavoritesRepositoryImpl --> RecipeDao : uses >
RecipeRepositoryImpl --> RecipeMapper : uses >
AppContainer --> RecipeRepositoryImpl : creates >
AppContainer --> FavoritesRepositoryImpl : creates >
AppContainer --> UserPrefsRepositoryImpl : creates >

@enduml
```

---

### 8.4 Diagrama PlantUML — Capa Presentation (ViewModels)

```plantuml
@startuml RecipeGenerator_ClassDiagram_Presentation

skinparam classBackgroundColor #FFF3E0
skinparam classBorderColor #E65100
skinparam classArrowColor #E65100
skinparam packageBackgroundColor #FFF8E1
skinparam packageBorderColor #FFB300

package "presentation.home" #FFF3E0 {
  class HomeViewModel {
    - getMenuForDayUseCase: GetMenuForDayUseCase
    + selectedDay: StateFlow<String>
    + recipes: StateFlow<List<Recipe>>
    + isLoading: StateFlow<Boolean>
    + selectDay(day: String)
  }
}

package "presentation.detail" #FFF3E0 {
  class RecipeDetailViewModel {
    - getRecipeDetailUseCase: GetRecipeDetailUseCase
    - toggleFavoriteUseCase: ToggleFavoriteUseCase
    + recipe: StateFlow<Recipe?>
    + isLoading: StateFlow<Boolean>
    + loadRecipe(recipeId: String)
    + toggleFavorite()
  }
}

package "presentation.favorites" #FFF3E0 {
  class FavoritesViewModel {
    - favoritesRepository: FavoritesRepository
    + searchQuery: StateFlow<String>
    + selectedCategory: StateFlow<String>
    + filteredRecipes: StateFlow<List<Recipe>>
    + onSearchQueryChanged(query: String)
    + onCategorySelected(category: String)
  }
}

package "presentation.generator" #FFF3E0 {
  class MenuGeneratorViewModel {
    - generateMenuUseCase: GenerateMenuUseCase
    + maxDifficulty: StateFlow<String>
    + selectedDiets: StateFlow<Set<String>>
    + selectedTypes: StateFlow<Set<String>>
    + portions: StateFlow<Int>
    + generatedMenu: StateFlow<List<Recipe>>
    + isGenerating: StateFlow<Boolean>
    + generateMenu()
  }
}

package "presentation.settings" #FFF3E0 {
  class SettingsViewModel {
    - userPrefsRepository: UserPrefsRepository
    + preferences: StateFlow<UserPreferences>
    + saveTheme(theme: String)
    + saveLanguage(language: String)
    + saveDefaultPortions(portions: Int)
    + toggleDiet(diet: String)
  }
}

HomeViewModel --> GetMenuForDayUseCase : uses >
RecipeDetailViewModel --> GetRecipeDetailUseCase : uses >
RecipeDetailViewModel --> ToggleFavoriteUseCase : uses >
FavoritesViewModel --> FavoritesRepository : uses >
MenuGeneratorViewModel --> GenerateMenuUseCase : uses >
SettingsViewModel --> UserPrefsRepository : uses >

@enduml
```

---

### 8.5 Resumen de clases por capa

| Capa | Tipo | Clases / Interfaces |
|---|---|---|
| **Domain — Models** | `data class` | `Recipe`, `Ingredient`, `RecipeStep`, `UserPreferences` |
| **Domain — Repositories** | `interface` | `RecipeRepository`, `FavoritesRepository`, `UserPrefsRepository` |
| **Domain — Use Cases** | `class` | `GetMenuForDayUseCase`, `GetRecipeDetailUseCase`, `ToggleFavoriteUseCase`, `GenerateMenuUseCase` |
| **Data — Entities** | `@Entity` | `RecipeEntity`, `IngredientEntity`, `StepEntity` |
| **Data — DAOs** | `@Dao interface` | `RecipeDao` |
| **Data — Repositories** | `class` | `RecipeRepositoryImpl`, `FavoritesRepositoryImpl`, `UserPrefsRepositoryImpl` |
| **Data — Mapper** | `object` | `RecipeMapper` |
| **DI** | `class` | `AppContainer` |
| **Presentation** | `ViewModel` | `HomeViewModel`, `RecipeDetailViewModel`, `FavoritesViewModel`, `MenuGeneratorViewModel`, `SettingsViewModel` |

---

## 9. Diagrama de Secuencia UML

### 9.1 Descripción del flujo principal

El flujo analizado corresponde al caso de uso central de la aplicación: **el usuario selecciona una receta en la pantalla de inicio, visualiza su detalle y la agrega a favoritos**. Este flujo involucra las capas de Presentación, Dominio y Datos de la arquitectura MVVM + Clean Architecture.

**Actores y participantes:**

| Participante | Capa | Rol |
|---|---|---|
| `Usuario` | — | Actor externo que interactúa con la UI |
| `RecipeListScreen` | Presentación | Pantalla que lista las recetas disponibles |
| `HomeViewModel` | Presentación | Gestiona el estado de la lista de recetas |
| `RecipeDetailScreen` | Presentación | Pantalla que muestra el detalle de una receta |
| `RecipeDetailViewModel` | Presentación | Gestiona el estado del detalle y la acción de favorito |
| `GetRecipeByIdUseCase` | Dominio | Caso de uso: obtener receta por ID |
| `ToggleFavoriteUseCase` | Dominio | Caso de uso: alternar estado de favorito |
| `RecipeRepository` | Dominio | Contrato/interfaz del repositorio |
| `RecipeRepositoryImpl` | Datos | Implementación concreta del repositorio |
| `RecipeDao` | Datos | Data Access Object de Room (recetas) |
| `FavoritesDao` | Datos | Data Access Object de Room (favoritos) |

---

### 9.2 Diagrama PlantUML — Flujo: Seleccionar receta → Ver detalle → Agregar a favoritos

```plantuml
@startuml SD-01-SeleccionarRecetaYFavorito
title SD-01 : Seleccionar Receta → Ver Detalle → Agregar a Favoritos
skinparam sequenceArrowThickness 2
skinparam sequenceParticipantBackgroundColor #EDE7F6
skinparam sequenceLifeLineBorderColor #7E57C2
skinparam sequenceArrowColor #512DA8
skinparam noteBorderColor #7E57C2
skinparam noteBackgroundColor #F3E5F5

actor       "Usuario"                 as U
participant "RecipeListScreen"        as RLS
participant "HomeViewModel"           as HVM
participant "RecipeDetailScreen"      as RDS
participant "RecipeDetailViewModel"   as RDVM
participant "GetRecipeByIdUseCase"    as GUC
participant "ToggleFavoriteUseCase"   as TUC
participant "RecipeRepositoryImpl"    as REPO
database    "RecipeDao (Room)"        as RDAO
database    "FavoritesDao (Room)"     as FDAO

== Inicialización de la lista ==

U -> RLS : Abre la aplicación
activate RLS
RLS -> HVM : collectAsStateWithLifecycle(uiState)
activate HVM
HVM -> GUC : invoke()
activate GUC
GUC -> REPO : getAllRecipes()
activate REPO
REPO -> RDAO : getAllRecipes() : Flow<List<RecipeEntity>>
activate RDAO
RDAO --> REPO : Flow<List<RecipeEntity>>
deactivate RDAO
REPO --> GUC : Flow<List<Recipe>>
deactivate REPO
GUC --> HVM : Flow<List<Recipe>>
deactivate GUC
HVM --> RLS : HomeUiState.Success(recipes)
deactivate HVM
RLS --> U : Muestra lista de recetas
deactivate RLS

== Selección de receta ==

U -> RLS : Toca una RecipeCard
activate RLS
RLS -> RLS : onRecipeSelected(recipeId)
note right of RLS
  Se actualiza selectedRecipeId
  en MainActivity — navega a
  RecipeDetailScreen
end note
RLS --> U : Navega a detalle
deactivate RLS

== Carga del detalle ==

U -> RDS : Ve RecipeDetailScreen(recipeId)
activate RDS
RDS -> RDVM : collectAsStateWithLifecycle(uiState)
activate RDVM
RDVM -> GUC : invoke(recipeId)
activate GUC
GUC -> REPO : getRecipeById(recipeId)
activate REPO
REPO -> RDAO : getRecipeById(id) : Flow<RecipeEntity?>
activate RDAO
RDAO --> REPO : Flow<RecipeEntity?>
deactivate RDAO
REPO --> GUC : Flow<Recipe?>
deactivate REPO
GUC --> RDVM : Flow<Recipe?>
deactivate GUC
RDVM --> RDS : DetailUiState.Success(recipe, isFavorite=false)
deactivate RDVM
RDS --> U : Muestra título, imagen, ingredientes, pasos
deactivate RDS

== Agregar a favoritos ==

U -> RDS : Toca ícono de favorito ♥
activate RDS
RDS -> RDVM : onToggleFavorite(recipeId)
activate RDVM
note right of RDVM
  Optimistic update:
  isFavorite = true (inmediato)
end note
RDVM -> TUC : invoke(recipeId)
activate TUC
TUC -> REPO : toggleFavorite(recipeId)
activate REPO
REPO -> FDAO : insertFavorite(FavoriteEntity(recipeId))
activate FDAO
FDAO --> REPO : Unit
deactivate FDAO
REPO --> TUC : Unit
deactivate REPO
TUC --> RDVM : Unit
deactivate TUC
RDVM --> RDS : DetailUiState.Success(recipe, isFavorite=true)
deactivate RDVM
RDS --> U : Ícono cambia a relleno ♥ \n Snackbar: "Guardado en favoritos"
deactivate RDS

@enduml
```

---

### 9.3 Descripción paso a paso

**Fase 1 — Inicialización de la lista (pasos 1–8)**

1. El usuario abre la aplicación; `MainActivity` monta `RecipeListScreen`.
2. `RecipeListScreen` observa el `HomeUiState` mediante `collectAsStateWithLifecycle`.
3. `HomeViewModel` invoca `GetRecipeByIdUseCase` (o `GetAllRecipesUseCase`) al iniciarse.
4. El caso de uso delega en `RecipeRepositoryImpl.getAllRecipes()`.
5. `RecipeRepositoryImpl` consulta `RecipeDao` que retorna un `Flow<List<RecipeEntity>>`.
6. El flujo se mapea a `Flow<List<Recipe>>` (objetos de dominio) y sube hasta el ViewModel.
7. El ViewModel emite `HomeUiState.Success(recipes)` al Composable.
8. La pantalla renderiza las `RecipeCard` con la lista de recetas.

**Fase 2 — Selección de receta (pasos 9–11)**

9. El usuario toca una `RecipeCard`; se invoca `onRecipeSelected(recipeId)`.
10. `MainActivity` actualiza `selectedRecipeId` (estado hoisted).
11. La navegación muestra `RecipeDetailScreen` pasando el `recipeId`.

**Fase 3 — Carga del detalle (pasos 12–19)**

12. `RecipeDetailScreen` recibe el `recipeId` y obtiene su `RecipeDetailViewModel`.
13. El ViewModel invoca `GetRecipeByIdUseCase(recipeId)`.
14. El caso de uso consulta `RecipeRepositoryImpl.getRecipeById(id)`.
15. Room devuelve un `Flow<RecipeEntity?>` desde `RecipeDao`.
16. El mapper convierte la entidad a `Recipe` (modelo de dominio).
17. El ViewModel emite `DetailUiState.Success(recipe, isFavorite=false)`.
18. La pantalla renderiza el hero de imagen, ingredientes y pasos de preparación.

**Fase 4 — Agregar a favoritos (pasos 20–28)**

19. El usuario toca el ícono de favorito (corazón vacío).
20. `RecipeDetailScreen` llama `onToggleFavorite(recipeId)`.
21. `RecipeDetailViewModel` aplica **optimistic update**: actualiza `isFavorite = true` de inmediato en el `UiState`, sin esperar confirmación de BD.
22. Se invoca `ToggleFavoriteUseCase(recipeId)`.
23. El caso de uso llama `RecipeRepositoryImpl.toggleFavorite(recipeId)`.
24. `RecipeRepositoryImpl` inserta un `FavoriteEntity` en `FavoritesDao` (Room/SQLite).
25. Room confirma la inserción.
26. El resultado sube de vuelta al ViewModel (ya sincronizado con el optimistic update).
27. El `DetailUiState` emite `isFavorite=true` definitivo.
28. La UI: el ícono ♥ cambia a relleno y aparece el `Snackbar` con "Guardado en favoritos".

---

### 9.4 Flujo alternativo — Eliminar de favoritos

Si el usuario vuelve a tocar el ícono cuando `isFavorite = true`:

```plantuml
@startuml SD-02-EliminarFavorito
title SD-02 : Eliminar receta de favoritos (flujo alternativo)
skinparam sequenceArrowColor #512DA8
skinparam sequenceParticipantBackgroundColor #EDE7F6

actor       "Usuario"               as U
participant "RecipeDetailScreen"    as RDS
participant "RecipeDetailViewModel" as RDVM
participant "ToggleFavoriteUseCase" as TUC
participant "RecipeRepositoryImpl"  as REPO
database    "FavoritesDao (Room)"   as FDAO

U -> RDS : Toca ícono ♥ (isFavorite=true)
activate RDS
RDS -> RDVM : onToggleFavorite(recipeId)
activate RDVM
note right of RDVM
  Optimistic update:
  isFavorite = false
end note
RDVM -> TUC : invoke(recipeId)
activate TUC
TUC -> REPO : toggleFavorite(recipeId)
activate REPO
REPO -> FDAO : deleteFavorite(recipeId)
activate FDAO
FDAO --> REPO : Unit
deactivate FDAO
REPO --> TUC : Unit
deactivate REPO
TUC --> RDVM : Unit
deactivate TUC
RDVM --> RDS : DetailUiState.Success(recipe, isFavorite=false)
deactivate RDVM
RDS --> U : Ícono cambia a vacío ♡ \n Snackbar: "Eliminado de favoritos"
deactivate RDS

@enduml
```

---

### 9.5 Notas técnicas sobre el diseño

| Aspecto | Decisión técnica |
|---|---|
| **Optimistic update** | El ViewModel actualiza el estado de la UI antes de confirmar con Room, garantizando respuesta inmediata (< 16 ms) |
| **Flow / corrutinas** | Toda comunicación BD→Repo→UseCase→VM usa `Flow<T>` con colección en `viewModelScope` |
| **Snackbar** | Se gestiona mediante `SnackbarHostState` hoisted en `MainActivity`, evitando acoplamiento entre screens |
| **Navegación** | Se usa estado hoisted (`selectedRecipeId`) en `MainActivity` en lugar de `NavHost` en esta entrega; se migrará a Navigation Component en FASE 2 (F2-XX) |
| **Sin efectos secundarios en UseCase** | Los casos de uso son puros: solo coordinan repositorios y no acceden a `Context` ni a recursos Android |

---

## 10. Wireframes / Mockups de la UI

### 10.1 Descripción general del sistema de diseño

La interfaz sigue los principios de **Material Design 3** con un esquema de color editorial en tonos púrpura (`#7E57C2` / Deep Purple). Los wireframes documentan tres superficies clave de la aplicación:

| ID | Pantalla | Descripción |
|---|---|---|
| WF-01 | `MainScreen` | Pantalla principal: TopAppBar + LazyVerticalGrid de recetas + BottomNavBar |
| WF-02 | `RecipeDetailScreen` | Detalle de receta: Hero + metadatos + ingredientes + pasos |
| WF-03 | `LeftMenuPanel` | Panel lateral deslizante: filtros de categoría, dificultad y tiempo |

**Notación de los wireframes Salt (PlantUML):**

| Símbolo | Significado |
|---|---|
| `[Botón]` | Botón o ícono interactivo |
| `"campo"` | Campo de texto / etiqueta |
| `^Opción^` | Selector desplegable |
| `(*)` / `( )` | RadioButton seleccionado / vacío |
| `[X]` / `[ ]` | CheckBox marcado / vacío |
| `{/` | Pestaña (Tab) |
| `{+` | Contenedor con borde visible |
| `{-` | Contenedor sin borde (layout) |

---

### 10.2 WF-01 — MainScreen (NavBar + Content)

#### 10.2.1 Wireframe Salt

```plantuml
@startsalt
title WF-01 : MainScreen — RecipeListScreen
scale 1.5

{+
  {/ <b>Recipe Generator</b> }
  {-
    "  🔍 Buscar receta...          " | [⚙]
  }
  ==
  {-
    {+
      {-
        [🖼 img]
        "Pasta Carbonara"
        "⏱ 30 min  · 🍽 2 pers"
        "★★★★☆  Medio"
        [♡ Favorito]
      }
      |
      {+
        {-
          [🖼 img]
          "Ensalada César"
          "⏱ 15 min  · 🍽 4 pers"
          "★★★☆☆  Fácil"
          [♡ Favorito]
        }
      }
    }
    {+
      {-
        [🖼 img]
        "Salmón al horno"
        "⏱ 45 min  · 🍽 2 pers"
        "★★★★★  Difícil"
        [♡ Favorito]
      }
      |
      {+
        {-
          [🖼 img]
          "Tacos de pollo"
          "⏱ 25 min  · 🍽 3 pers"
          "★★★★☆  Medio"
          [♡ Favorito]
        }
      }
    }
  }
  ==
  {-
    [🏠\nInicio] | [♥\nFavoritos] | [📋\nGenerador] | [⚙\nAjustes]
  }
}

@endsalt
```

#### 10.2.2 ASCII Wireframe detallado

```
┌─────────────────────────────────────────┐
│  ≡  Recipe Generator            [⚙]    │  ← TopAppBar (EditorialTopAppBar)
│     Generador de Menús Semanales        │
├─────────────────────────────────────────┤
│  🔍 Buscar receta...          [Filtrar] │  ← SearchBar / FilterRow
├────────────────────┬────────────────────┤
│  ┌──────────────┐  │  ┌──────────────┐  │
│  │  [img 1:1]   │  │  │  [img 1:1]   │  │  ← LazyVerticalGrid (2 columnas)
│  │              │  │  │              │  │     ElevatedCard con shape rounded
│  └──────────────┘  │  └──────────────┘  │
│  Pasta Carbonara   │  Ensalada César    │
│  ⏱ 30 min  👤 2   │  ⏱ 15 min  👤 4   │
│  ⭐⭐⭐⭐  Medio  │  ⭐⭐⭐  Fácil     │
│  [♡]               │  [♡]               │
├────────────────────┼────────────────────┤
│  ┌──────────────┐  │  ┌──────────────┐  │
│  │  [img 1:1]   │  │  │  [img 1:1]   │  │
│  └──────────────┘  │  └──────────────┘  │
│  Salmón al horno   │  Tacos de pollo    │
│  ⏱ 45 min  👤 2   │  ⏱ 25 min  👤 3   │
│  ⭐⭐⭐⭐⭐ Difíc │  ⭐⭐⭐⭐  Medio  │
│  [♡]               │  [♡]               │
├────────────────────┴────────────────────┤
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  │
│  [🏠]      [♥]      [📋]      [⚙]      │  ← EditorialBottomNavBar
│  Inicio  Favoritos  Generador  Ajustes  │     NavigationBar M3
└─────────────────────────────────────────┘
```

#### 10.2.3 Especificaciones de componentes

| Componente | Composable | Notas |
|---|---|---|
| Barra superior | `HomeEditorialTopAppBar` | Título + subtítulo editorial, fondo `Background` |
| Barra de búsqueda | `SearchBar` (M3) | `query` en `HomeUiState`, filtra en `HomeViewModel` |
| Grid de recetas | `LazyVerticalGrid(columns = Fixed(2))` | `RecipeCard` con padding `4.dp` entre celdas |
| Tarjeta de receta | `ElevatedCard` | Imagen 1:1, título, tiempo, porciones, rating, ícono favorito |
| Ícono favorito | `IconToggleButton` | `♡` vacío / `♥` relleno según `isFavorite` |
| Barra inferior | `EditorialBottomNavBar` | 4 destinos: Home(0), Favorites(1), Generator(2), Settings(3) |

---

### 10.3 WF-02 — RecipeDetailScreen

#### 10.3.1 Wireframe Salt

```plantuml
@startsalt
title WF-02 : RecipeDetailScreen — Detalle de Receta
scale 1.5

{+
  {-
    [← Atrás] | "     Pasta Carbonara      " | [♥]
  }
  ==
  {+
    "         [  Imagen Hero 16:9  ]         "
    "                                        "
    "       Placeholder — img_placeholder    "
  }
  ==
  {-
    "  🍳 Pasta Carbonara               ★4.2"
    "  ⏱ 30 min   🍽 2 personas   📊 Medio "
  }
  ==
  {/ <b>Ingredientes</b> | Preparación | Info }
  {+
    " • 200 g de espaguetis              "
    " • 100 g de panceta o guanciale     "
    " • 2 yemas de huevo                 "
    " • 50 g de queso Pecorino Romano    "
    " • Pimienta negra al gusto          "
    " • Sal para el agua                 "
  }
  ==
  {-
    [♡  Guardar en favoritos            ]
  }
  ==
  {-
    [🏠\nInicio] | [♥\nFavoritos] | [📋\nGenerador] | [⚙\nAjustes]
  }
}

@endsalt
```

#### 10.3.2 ASCII Wireframe detallado

```
┌─────────────────────────────────────────┐
│  [←]   Pasta Carbonara           [♥]   │  ← TopAppBar con back + toggle favorito
├─────────────────────────────────────────┤
│                                         │
│  ┌─────────────────────────────────┐    │
│  │                                 │    │  ← HeroSection: AspectRatio(16/9)
│  │       [  img_placeholder  ]     │    │     Image con contentScale = Crop
│  │                                 │    │
│  └─────────────────────────────────┘    │
├─────────────────────────────────────────┤
│  🍳 Pasta Carbonara            ★ 4.2   │  ← Título + rating
│  ⏱ 30 min  ·  🍽 2 personas  ·  Medio │  ← MetaRow: tiempo, porciones, dificultad
│  🏷 Italiana · Pasta · Principal       │  ← CategoryChips (FilterChip M3)
├─────────────────────────────────────────┤
│  ┌──────────┐ ┌──────────┐ ┌────────┐  │
│  │Ingredien.│ │Preparac. │ │  Info  │  │  ← TabRow (M3): 3 pestañas
│  └──────────┘ └──────────┘ └────────┘  │     HorizontalPager
│  ─────────── ← indicador activo        │
│                                         │
│  • 200 g espaguetis                    │
│  • 100 g panceta (guanciale)           │  ← LazyColumn con IngredientRow
│  • 2 yemas de huevo                    │     Checkbox + cantidad + nombre
│  • 50 g queso Pecorino Romano          │
│  • Pimienta negra al gusto             │
│  • Sal gruesa para el agua             │
│                                         │
├─────────────────────────────────────────┤
│  [♥  Guardar en favoritos  ]            │  ← FilledButton hoisteado
├─────────────────────────────────────────┤
│  [🏠]      [♥]      [📋]      [⚙]      │  ← BottomNavBar (misma que MainScreen)
│  Inicio  Favoritos  Generador  Ajustes  │
└─────────────────────────────────────────┘

── Pestaña "Preparación" ──────────────────
│                                         │
│  1.  Cocer la pasta en agua con sal    │
│      hasta que esté al dente.          │  ← StepRow: número + descripción
│                                         │
│  2.  En un bol mezclar yemas, queso    │     Divider entre pasos
│      y pimienta negra.                 │
│                                         │
│  3.  Saltear panceta sin aceite        │
│      hasta que esté crujiente.         │
│                                         │
│  4.  Mezclar pasta caliente con        │
│      la panceta fuera del fuego...     │
│                                         │
└─────────────────────────────────────────┘
```

#### 10.3.3 Especificaciones de componentes

| Componente | Composable | Notas |
|---|---|---|
| Barra superior | `TopAppBar` (M3) | `navigationIcon` = `IconButton(←)`, `actions` = `IconToggleButton(♥)` |
| Hero imagen | `Image(painterResource)` + `AspectRatio(16/9f)` | `contentScale = ContentScale.Crop`, placeholder `img_placeholder` |
| Meta fila | `RecipeMetaRow` | `Row` con `Icon` + `Text` separados por `·` |
| Etiquetas | `FilterChip` (M3) | No interactivas en detalle; solo informativas |
| Pestañas | `TabRow` + `HorizontalPager` | 0=Ingredientes, 1=Preparación, 2=Info nutricional |
| Ingrediente | `IngredientRow` | `Checkbox` (lista de compras futura) + cantidad + unidad + nombre |
| Paso | `StepRow` | Número en `CircleBadge` + `Text` párrafo + `Divider` |
| Botón favorito | `FilledButton` | Cambia texto/ícono según `isFavorite`; debounce 300 ms |

---

### 10.4 WF-03 — LeftMenuPanel (Panel de Filtros)

#### 10.4.1 Descripción

El `LeftMenuPanel` es un panel lateral deslizante (`ModalDrawerSheet` de M3) que se abre desde el ícono `≡` (hamburger) en la `TopAppBar`. Permite al usuario filtrar las recetas visibles en `MainScreen` por categoría, nivel de dificultad y tiempo de preparación, sin abandonar la pantalla actual.

#### 10.4.2 Wireframe Salt

```plantuml
@startsalt
title WF-03 : LeftMenuPanel — Panel lateral de filtros
scale 1.5

{+
  {-
    "  Recipe Generator   " | [✕]
  }
  "  Generador de Menús  "
  ==
  {+
    "<b>Categoría</b>"
    (*)  Todas las categorías
    ( )  Desayuno
    ( )  Almuerzo
    ( )  Cena
    ( )  Merienda
    ( )  Postres
  }
  ==
  {+
    "<b>Dificultad</b>"
    [X]  Fácil
    [X]  Medio
    [ ]  Difícil
  }
  ==
  {+
    "<b>Tiempo máximo</b>"
    ^Sin límite^
  }
  ==
  {+
    "<b>Porciones</b>"
    ( )  1 persona
    (*)  2 personas
    ( )  3–4 personas
    ( )  5 o más
  }
  ==
  {-
    [  Aplicar filtros  ] | [Limpiar]
  }
}

@endsalt
```

#### 10.4.3 ASCII Wireframe detallado

```
┌────────────────────────┬──────────────────────────────┐
│  ≡  Recipe Generator   │                              │
│     Menús Semanales [✕]│                              │
│════════════════════════│     MainScreen               │
│                        │     (contenido semitranspa-  │
│  CATEGORÍA             │      rente / atenuado con    │
│  ───────────────────   │      scrim 0.32 alpha)       │
│  ◉ Todas               │                              │
│  ○ Desayuno            │                              │
│  ○ Almuerzo            │                              │
│  ○ Cena                │                              │
│  ○ Merienda            │                              │
│  ○ Postres             │                              │
│                        │                              │
│  DIFICULTAD            │                              │
│  ───────────────────   │                              │
│  ☑ Fácil               │                              │
│  ☑ Medio               │                              │
│  ☐ Difícil             │                              │
│                        │                              │
│  TIEMPO MÁXIMO         │                              │
│  ───────────────────   │                              │
│  ▾ Sin límite       ▾  │                              │
│    ┌──────────────┐    │                              │
│    │ ≤ 15 min     │    │                              │
│    │ ≤ 30 min     │    │                              │
│    │ ≤ 45 min     │    │                              │
│    │ Sin límite ✓ │    │                              │
│    └──────────────┘    │                              │
│                        │                              │
│  PORCIONES             │                              │
│  ───────────────────   │                              │
│  ○ 1 persona           │                              │
│  ◉ 2 personas          │                              │
│  ○ 3–4 personas        │                              │
│  ○ 5 o más             │                              │
│                        │                              │
│  ════════════════════  │                              │
│  [  Aplicar filtros  ] │                              │
│  [  Limpiar todo     ] │                              │
└────────────────────────┴──────────────────────────────┘
  ← 280 dp →              ← resto del ancho →
```

#### 10.4.4 Especificaciones de componentes

| Componente | Composable | Notas |
|---|---|---|
| Contenedor | `ModalNavigationDrawer` (M3) | `drawerContent = { ModalDrawerSheet { ... } }` |
| Apertura | `DrawerState` + `rememberDrawerState` | Se abre desde `IconButton(≡)` en `TopAppBar` |
| Cabecera | `DrawerHeader` | Logo + título + subtítulo + botón `✕` para cerrar |
| Sección | `DrawerSectionHeader` | `Text` en `labelSmall` con `Divider` inferior |
| Categoría | `RadioButton` + `Text` en `Row` | Valor en `FilterState.category: String?` |
| Dificultad | `Checkbox` + `Text` en `Row` | Valor en `FilterState.difficulties: Set<String>` |
| Tiempo | `ExposedDropdownMenuBox` (M3) | Opciones: `null`, 15, 30, 45 minutos |
| Porciones | `RadioButton` + `Text` en `Row` | Valor en `FilterState.servings: Int?` |
| Botón aplicar | `FilledButton` | Llama `HomeViewModel.applyFilters(filterState)` |
| Botón limpiar | `TextButton` | Llama `HomeViewModel.clearFilters()` |

#### 10.4.5 Estado del filtro — modelo de datos

```kotlin
// FilterState — objeto de dominio simple (sin Room, sin DataStore en esta fase)
data class FilterState(
    val category: String? = null,           // null = todas
    val difficulties: Set<String> = setOf("Fácil", "Medio", "Difícil"),
    val maxTimeMinutes: Int? = null,        // null = sin límite
    val servings: Int? = null               // null = cualquier cantidad
)
```

El `HomeViewModel` expone un `StateFlow<FilterState>` y aplica los filtros sobre la lista completa de recetas en memoria antes de emitirla como `HomeUiState.Success`.

---

### 10.5 Flujo de navegación entre pantallas

```plantuml
@startuml NAV-01-FlujoPantallas
title NAV-01 : Flujo de navegación entre pantallas principales
skinparam rectangleBackgroundColor #EDE7F6
skinparam rectangleBorderColor #7E57C2
skinparam arrowColor #512DA8
skinparam noteBackgroundColor #F3E5F5

rectangle "MainScreen\n(RecipeListScreen)" as MAIN
rectangle "RecipeDetailScreen" as DETAIL
rectangle "FavoritesScreen" as FAV
rectangle "MenuGeneratorScreen" as GEN
rectangle "SettingsScreen" as SET
rectangle "LeftMenuPanel\n(Drawer)" as DRAWER

MAIN -right-> DETAIL : onRecipeSelected(id)
DETAIL -left-> MAIN : onBackClick()

MAIN -up-> DRAWER : onMenuIconClick() [≡]
DRAWER -down-> MAIN : onApplyFilters() / onClose()

MAIN -down-> FAV : BottomNav[1]
MAIN -down-> GEN : BottomNav[2]
MAIN -down-> SET : BottomNav[3]

FAV -up-> DETAIL : onRecipeSelected(id)
FAV -left-> MAIN : BottomNav[0]

GEN --> MAIN : BottomNav[0]
SET --> MAIN : BottomNav[0]

note right of DETAIL
  isFavorite se pasa como
  parámetro desde MainActivity
  (estado hoisted)
end note

note left of DRAWER
  ModalNavigationDrawer
  ancho fijo 280 dp
  scrim semi-transparente
end note

@enduml
```

---

### 10.6 Paleta de color y tipografía del sistema de diseño

#### Color

| Token | Hex | Uso |
|---|---|---|
| `Background` | `#F5F0FF` | Fondo global de todas las pantallas |
| `Surface` | `#FFFFFF` | Cards, BottomSheet, Drawer |
| `Primary` | `#7E57C2` | Botones primarios, íconos activos, TabIndicator |
| `PrimaryContainer` | `#EDE7F6` | Chips seleccionados, highlights |
| `OnSurface` | `#1C1B1F` | Texto principal |
| `OnSurfaceVariant` | `#49454F` | Texto secundario, metadatos |
| `Error` | `#B3261E` | Validaciones, estados de error |

#### Tipografía (M3 TypeScale)

| Estilo | Uso |
|---|---|
| `displaySmall` | Título de sección en Placeholder screens |
| `headlineMedium` | Título de receta en `RecipeDetailScreen` |
| `titleLarge` | Cabecera de `TopAppBar` |
| `titleMedium` | Nombre de receta en `RecipeCard` |
| `bodyLarge` | Texto de descripción e ingredientes |
| `bodyMedium` | Metadatos (tiempo, porciones, dificultad) |
| `labelSmall` | Sección headers en `LeftMenuPanel` |

---

## Referencias

Google LLC. (2024a). *Jetpack Compose overview*. Android Developers.
&nbsp;&nbsp;&nbsp;&nbsp;https://developer.android.com/jetpack/compose

Google LLC. (2024b). *Material Design 3 for Compose*. Material Design.
&nbsp;&nbsp;&nbsp;&nbsp;https://m3.material.io/develop/android/jetpack-compose

Google LLC. (2024c). *Guide to app architecture*. Android Developers.
&nbsp;&nbsp;&nbsp;&nbsp;https://developer.android.com/topic/architecture

Google LLC. (2024d). *Save data in a local database using Room*. Android Developers.
&nbsp;&nbsp;&nbsp;&nbsp;https://developer.android.com/training/data-storage/room

Google LLC. (2024e). *Navigation component — Get started*. Android Developers.
&nbsp;&nbsp;&nbsp;&nbsp;https://developer.android.com/guide/navigation/get-started

Google LLC. (2024f). *DataStore*. Android Developers.
&nbsp;&nbsp;&nbsp;&nbsp;https://developer.android.com/topic/libraries/architecture/datastore

Google LLC. (2024g). *ViewModel overview*. Android Developers.
&nbsp;&nbsp;&nbsp;&nbsp;https://developer.android.com/topic/libraries/architecture/viewmodel

International Organization for Standardization. (2011). *ISO/IEC 25010:2011 —
Systems and software engineering — Systems and software Quality Requirements and
Evaluation (SQuaRE): System and software quality models*. ISO.
&nbsp;&nbsp;&nbsp;&nbsp;https://www.iso.org/standard/35733.html

JetBrains. (2024). *Kotlin documentation*. JetBrains.
&nbsp;&nbsp;&nbsp;&nbsp;https://kotlinlang.org/docs/home.html

Politécnico Grancolombiano. (2026). *Plan Maestro Recipe Generator v3.0 —
Herramientas de Programación Móvil I*. Bogotá, Colombia: Facultad de Ingeniería
y Ciencias Básicas.
