<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Design-Material%203-6200EE?style=for-the-badge&logo=materialdesign&logoColor=white" />
  <img src="https://img.shields.io/badge/Min%20SDK-23-brightgreen?style=for-the-badge" />
</p>

# Recipe Generator - Menu Semanal

> Aplicacion Android moderna con estetica editorial tipo revista gastronomica, disenada para planificar menus semanales, descubrir recetas y generar nuevas ideas culinarias con inteligencia artificial.

---

## Tabla de Contenidos

- [Descripcion General](#descripcion-general)
- [Capturas de Pantalla](#capturas-de-pantalla)
- [Arquitectura](#arquitectura)
- [Stack Tecnologico](#stack-tecnologico)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Pantallas de la Aplicacion](#pantallas-de-la-aplicacion)
  - [Carcasa Principal](#1-carcasa-principal)
  - [Inicio: Menu Semanal](#2-inicio-menu-semanal)
  - [Detalle de Receta](#3-detalle-de-receta)
  - [Favoritos](#4-favoritos)
  - [Generador de Recetas](#5-generador-de-recetas)
  - [Configuracion](#6-configuracion)
- [Sistema de Diseno](#sistema-de-diseno)
- [Modelo de Datos](#modelo-de-datos)
- [Instalacion y Configuracion](#instalacion-y-configuracion)
- [Compilacion](#compilacion)
- [Contribucion](#contribucion)
- [Licencia](#licencia)

---

## Descripcion General

**Recipe Generator** es una aplicacion Android nativa construida con **Jetpack Compose** y **Material Design 3** que permite a los usuarios:

- Consultar un menu semanal organizado por dia y tipo de comida
- Explorar recetas detalladas con ingredientes, pasos e informacion nutricional
- Guardar recetas favoritas para acceso rapido
- Generar nuevas recetas con inteligencia artificial segun preferencias y restricciones
- Personalizar la experiencia a traves de ajustes de perfil y preferencias alimentarias

La aplicacion sigue una **estetica editorial de revista gastronomica**, con tipografia premium, gradientes sutiles y un sistema de diseno cohesivo que prioriza la experiencia visual del contenido culinario.

---

## Capturas de Pantalla

| Inicio - Menu Semanal | Detalle de Receta | Favoritos |
|:---:|:---:|:---:|
| *Menu organizado por dias* | *Vista completa de la receta* | *Coleccion de favoritos* |

| Generador de Recetas | Configuracion |
|:---:|:---:|
| *Generacion con IA* | *Preferencias del usuario* |

> Para agregar capturas de pantalla, coloque las imagenes en una carpeta `/screenshots` y actualice las rutas en esta tabla.

---

## Arquitectura

La aplicacion implementa una arquitectura moderna basada en componentes de Android:

```
┌─────────────────────────────────────────────────────┐
│                    MainActivity                      │
│              (Single Activity Pattern)               │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌─────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │   Screens   │  │  Components  │  │   Theme   │  │
│  │             │  │              │  │           │  │
│  │ - Home      │  │ - TopAppBar  │  │ - Colors  │  │
│  │ - Detail    │  │ - BottomNav  │  │ - Type    │  │
│  │ - Favorites │  │ - RecipeCard │  │ - Dimens  │  │
│  │ - Generator │  │ - DayTabs   │  │ - Shapes  │  │
│  │ - Settings  │  │ - Chips     │  │           │  │
│  └─────────────┘  └──────────────┘  └───────────┘  │
│                                                     │
├─────────────────────────────────────────────────────┤
│                   Data Layer                         │
│         (Models, Mock Data, Repository)              │
└─────────────────────────────────────────────────────┘
```

**Patrones utilizados:**
- **Single Activity** con navegacion por Compose
- **State Hoisting** para manejo de estado en composables
- **Unidirectional Data Flow** (flujo de datos unidireccional)
- **Component-Based UI** con composables reutilizables

---

## Stack Tecnologico

| Tecnologia | Version | Proposito |
|:---|:---:|:---|
| **Kotlin** | 2.2.10 | Lenguaje de programacion principal |
| **Jetpack Compose** | BOM 2024.09.00 | Framework de UI declarativa |
| **Material Design 3** | Latest | Sistema de diseno y componentes |
| **Coil** | 2.6.0 | Carga asincrona de imagenes |
| **Google Fonts** | 1.6.7 | Tipografia personalizada (Plus Jakarta Sans, Work Sans) |
| **AndroidX Core KTX** | 1.10.1 | Extensiones de Kotlin para Android |
| **Lifecycle Runtime KTX** | 2.6.1 | Gestion del ciclo de vida |
| **Activity Compose** | 1.13.0 | Integracion Activity-Compose |
| **JUnit** | 4.13.2 | Testing unitario |
| **Espresso** | 3.7.0 | Testing de UI |

**Configuracion del SDK:**
- **Compile SDK:** 36.1 (Android 16)
- **Target SDK:** 36 (Android 15)
- **Min SDK:** 23 (Android 6.0 Marshmallow)
- **Java Compatibility:** 11

---

## Estructura del Proyecto

```
app/src/main/java/com/example/recipe_generator/
│
├── MainActivity.kt                    # Punto de entrada de la aplicacion
│
├── data/
│   └── Recipe.kt                      # Modelo de datos y datos mock
│
└── ui/
    ├── RecipeCard.kt                  # Componente de tarjeta de receta
    │
    ├── screens/
    │   └── RecipeListScreen.kt        # Pantalla principal del menu semanal
    │
    ├── components/
    │   ├── DesignSystem.kt            # Componentes reutilizables del sistema de diseno
    │   │                              #   - PrimaryButton (boton con gradiente)
    │   │                              #   - EditorialCard (tarjeta editorial)
    │   │                              #   - IngredientChip (chip de ingrediente)
    │   │                              #   - InfoChip (chip informativo)
    │   │                              #   - DifficultyChip (chip de dificultad)
    │   │                              #   - AppTextField (campo de texto editorial)
    │   │
    │   ├── TopAppBar.kt               # Barra superior con perfil y notificaciones
    │   ├── BottomNavBar.kt            # Navegacion inferior con 4 secciones
    │   ├── DayTabLayout.kt            # Selector horizontal de dias de la semana
    │   └── EditorialRecipeCard.kt     # Variante editorial de tarjeta de receta
    │
    └── theme/
        ├── Theme.kt                   # Configuracion del tema Material 3
        ├── Color.kt                   # Paleta de colores personalizada
        ├── Type.kt                    # Sistema tipografico con Google Fonts
        └── Dimens.kt                  # Tokens de espaciado y bordes redondeados
```

---

## Pantallas de la Aplicacion

### 1. Carcasa Principal

La **Carcasa Principal** es el contenedor raiz de la aplicacion que engloba toda la experiencia de usuario.

**Componentes:**
- **Top App Bar Editorial** - Barra superior con avatar de perfil circular (32dp), titulo centrado "Gastronomia Editorial" y icono de notificaciones
- **Area de Contenido** - Zona central donde se renderizan las pantallas segun la navegacion activa
- **Bottom Navigation Bar** - Barra de navegacion inferior con esquinas redondeadas superiores (32dp), fondo semi-transparente blanco (95% alfa) y sombra de 8dp
- **FAB Flotante** - Boton "Sorprendeme" con gradiente primario, icono de estrella y tamaño de 64dp posicionado sobre la barra de navegacion

**Secciones de navegacion:**

| Icono | Seccion | Descripcion |
|:---:|:---|:---|
| Home | **Inicio** | Menu semanal con recetas del dia |
| Heart | **Favoritos** | Coleccion de recetas guardadas |
| Sparkle | **Generador** | Crear recetas con IA |
| Gear | **Ajustes** | Configuracion y preferencias |

---

### 2. Inicio: Menu Semanal

La pantalla principal de la aplicacion presenta el **menu semanal** con un diseno editorial sofisticado.

**Secciones de la pantalla:**

1. **Cabecera Editorial**
   - Overline: "CURADO PARA TI" (texto primario, small caps, espaciado de 2sp)
   - Titulo hero: "Menu Semanal" (36sp, ExtraBold)

2. **Selector de Dias**
   - Pestanas horizontales desplazables para los 7 dias de la semana (Lunes a Domingo)
   - Estado seleccionado: fondo primario, texto blanco, elevacion de 8dp
   - Estado no seleccionado: fondo contenedor bajo, texto variante, sin elevacion
   - Forma de pastilla con bordes de 50dp

3. **Secciones de Comidas** (por cada dia seleccionado)
   - **Desayuno** - Subtitulo: "Impulso de Energia"
   - **Almuerzo** - Subtitulo: variable segun el dia
   - **Cena** - Subtitulo: variable segun el dia
   - Cada seccion muestra tarjetas de receta con:
     - Imagen en proporcion 16:9 con carga asincrona
     - Boton de favorito superpuesto (fondo blanco 20% alfa)
     - Titulo de la receta (headlineLarge)
     - Chips informativos: Tiempo, Calorias, Dificultad
     - Indicador de carga circular y placeholder de error

4. **Boton "Sorprendeme"**
   - FAB con gradiente de Primary a PrimaryContainer
   - Sugiere una receta aleatoria al usuario

---

### 3. Detalle de Receta

La pantalla de **Detalle de Receta** muestra toda la informacion completa de una receta seleccionada.

**Secciones planificadas:**

| Seccion | Contenido |
|:---|:---|
| **Cabecera** | Imagen hero a pantalla completa con overlay de gradiente |
| **Titulo** | Nombre de la receta con tipografia editorial (Plus Jakarta Sans) |
| **Metadatos** | Chips de tiempo de preparacion, calorias y nivel de dificultad |
| **Descripcion** | Resumen breve de la receta |
| **Ingredientes** | Lista con chips interactivos (IngredientChip) con cantidades |
| **Instrucciones** | Pasos numerados con formato editorial |
| **Informacion Nutricional** | Tabla detallada de macronutrientes |
| **Recetas Relacionadas** | Carrusel horizontal de sugerencias similares |

**Interacciones:**
- Boton para agregar/quitar de favoritos
- Compartir receta externamente
- Ajustar numero de porciones (recalculo de ingredientes)
- Modo paso a paso (pantalla completa por instruccion)

---

### 4. Favoritos

La pantalla de **Favoritos** permite al usuario gestionar su coleccion personal de recetas guardadas.

**Diseno:**

| Elemento | Descripcion |
|:---|:---|
| **Cabecera** | Titulo "Mis Favoritos" con contador de recetas guardadas |
| **Filtros** | Chips de categoria (Desayuno, Almuerzo, Cena, Todos) |
| **Grid de Recetas** | Cuadricula de tarjetas editoriales con imagen, titulo y metadatos |
| **Estado Vacio** | Ilustracion y mensaje motivacional cuando no hay favoritos |
| **Busqueda** | Campo de busqueda para filtrar dentro de favoritos |

**Funcionalidades:**
- Vista en cuadricula o lista (toggle)
- Filtrado por categoria de comida
- Ordenamiento por fecha de guardado, nombre o dificultad
- Deslizar para eliminar de favoritos (swipe-to-dismiss)
- Busqueda en tiempo real dentro de la coleccion

---

### 5. Generador de Recetas

El **Generador de Recetas** utiliza inteligencia artificial para crear recetas personalizadas segun las preferencias del usuario.

**Flujo del generador:**

```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  Seleccionar │ -> │   Generar    │ -> │  Resultado   │
│ Preferencias │    │   Receta     │    │   Generado   │
└──────────────┘    └──────────────┘    └──────────────┘
```

**Parametros de entrada:**

| Parametro | Tipo | Opciones |
|:---|:---|:---|
| **Tipo de comida** | Selector | Desayuno, Almuerzo, Cena, Snack |
| **Cocina** | Chips multiples | Mexicana, Italiana, Asiatica, Mediterranea, etc. |
| **Restricciones** | Toggle | Vegetariano, Vegano, Sin gluten, Sin lactosa |
| **Tiempo disponible** | Slider | 15 min - 120 min |
| **Dificultad** | Selector | Facil, Medio, Dificil |
| **Ingredientes disponibles** | TextField | Entrada libre de ingredientes |

**Resultado generado:**
- Receta completa con nombre creativo
- Lista de ingredientes con cantidades
- Instrucciones paso a paso
- Estimacion de tiempo y calorias
- Opcion de guardar en favoritos
- Opcion de regenerar con los mismos parametros

---

### 6. Configuracion

La pantalla de **Configuracion** permite personalizar la experiencia de la aplicacion.

**Secciones:**

| Seccion | Opciones |
|:---|:---|
| **Perfil** | Nombre, foto de perfil, correo electronico |
| **Preferencias Alimentarias** | Dieta (Omnivora, Vegetariana, Vegana), alergias, ingredientes excluidos |
| **Notificaciones** | Recordatorio de menu diario, nuevas recetas, tips de cocina |
| **Apariencia** | Tema (Claro/Oscuro/Sistema), tamaño de fuente |
| **Unidades** | Sistema metrico / Imperial para cantidades |
| **Datos** | Exportar favoritos, borrar cache, restablecer preferencias |
| **Acerca de** | Version de la app, licencias, creditos |

---

## Sistema de Diseno

### Paleta de Colores

```
Primario            #4800B2    ██████  Purple profundo
Primario Container  #6200EE    ██████  Purple brillante
Secundario          #00C2A8    ██████  Teal vibrante
Secundario Container#4AF8E3    ██████  Teal claro
Terciario           #FF0081    ██████  Pink editorial

Fondo               #FAF9FC    ██████  Gris muy claro
Superficie          #FAF9FC    ██████  Gris muy claro
Contenedor Bajo     #F5F3F7    ██████  Gris suave
Contenedor Alto     #E9E7EB    ██████  Gris medio

Texto Principal     #1B1B1E    ██████  Gris oscuro (nunca negro puro)
```

### Tipografia

| Estilo | Fuente | Peso | Tamaño |
|:---|:---|:---|:---:|
| Display Large | Plus Jakarta Sans | ExtraBold (800) | 36sp |
| Display Medium | Plus Jakarta Sans | Bold (700) | 28sp |
| Headline Large | Plus Jakarta Sans | Bold (700) | 24sp |
| Headline Medium | Plus Jakarta Sans | SemiBold (600) | 20sp |
| Title Large | Work Sans | SemiBold (600) | 18sp |
| Title Medium | Work Sans | Medium (500) | 16sp |
| Title Small | Work Sans | Medium (500) | 14sp |
| Body Large | Work Sans | Normal (400) | 16sp |
| Body Medium | Work Sans | Normal (400) | 14sp |
| Body Small | Work Sans | Normal (400) | 12sp |
| Label Large | Work Sans | SemiBold (600) | 14sp |
| Label Medium | Work Sans | Medium (500) | 12sp |
| Label Small | Work Sans | Medium (500) | 10sp |

### Espaciado

```
spacing_1   =  4dp    spacing_5  = 20dp    spacing_9  = 36dp
spacing_2   =  8dp    spacing_6  = 24dp    spacing_10 = 40dp
spacing_3   = 12dp    spacing_7  = 28dp    spacing_11 = 44dp
spacing_4   = 16dp    spacing_8  = 32dp    spacing_12 = 48dp
```

### Bordes Redondeados

```
rounded_sm   =  8dp   Inputs, chips pequenos
rounded_md   = 16dp   Tarjetas de receta
rounded_lg   = 24dp   Contenedores, modales
rounded_xl   = 32dp   Barra de navegacion inferior
rounded_full = 50dp   Pastillas, FAB, avatares
```

### Principios de Diseno

1. **Sin bordes visibles** - Solo color y capas de superficie para separacion visual
2. **Esquinas muy redondeadas** - Estetica moderna y amigable
3. **Gradientes en CTAs** - Botones principales con gradiente de Primary a PrimaryContainer
4. **Estetica editorial** - Inspirada en revistas gastronomicas de alta gama
5. **Elevacion sutil** - Sombras suaves para crear profundidad sin distraccion

---

## Modelo de Datos

### Recipe

```kotlin
data class Recipe(
    val id: String,              // Identificador unico
    val title: String,           // Nombre de la receta
    val imageUrl: String,        // URL de la imagen
    val timeInMinutes: Int,      // Tiempo de preparacion en minutos
    val calories: Int,           // Calorias por porcion
    val difficulty: String,      // "Facil" | "Medio" | "Dificil"
    val category: String,        // "Desayuno" | "Almuerzo" | "Cena"
    val categorySubtitle: String,// Subtitulo descriptivo de la categoria
    val isFavorite: Boolean      // Estado de favorito (default: false)
)
```

### Estructura del Menu Semanal

```
Semana
├── Lunes
│   ├── Desayuno  → Recipe (Impulso de Energia)
│   ├── Almuerzo  → Recipe
│   └── Cena      → Recipe
├── Martes
│   ├── Desayuno  → Recipe
│   ├── Almuerzo  → Recipe
│   └── Cena      → Recipe
├── Miercoles
│   ├── Desayuno  → Recipe
│   ├── Almuerzo  → Recipe
│   └── Cena      → Recipe
├── Jueves ... Domingo
│   └── (Extensible)
```

---

## Instalacion y Configuracion

### Prerrequisitos

- **Android Studio** Ladybug (2024.2) o superior
- **JDK** 11 o superior
- **Android SDK** con API Level 36 instalado
- **Dispositivo/Emulador** con Android 6.0 (API 23) o superior

### Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/RecipeGeneratorMenus.git
cd RecipeGeneratorMenus
```

### Abrir en Android Studio

1. Abrir Android Studio
2. Seleccionar **File > Open**
3. Navegar hasta la carpeta del proyecto y seleccionarla
4. Esperar a que Gradle sincronice las dependencias

### Configuracion del entorno

```properties
# local.properties (generado automaticamente)
sdk.dir=/ruta/a/tu/Android/Sdk
```

---

## Compilacion

### Debug

```bash
./gradlew assembleDebug
```

El APK se genera en: `app/build/outputs/apk/debug/app-debug.apk`

### Release

```bash
./gradlew assembleRelease
```

### Ejecutar tests

```bash
# Tests unitarios
./gradlew test

# Tests de instrumentacion
./gradlew connectedAndroidTest
```

### Limpiar y reconstruir

```bash
./gradlew clean build
```

---

## Contribucion

Las contribuciones son bienvenidas. Por favor sigue estos pasos:

1. Haz un **fork** del repositorio
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Realiza tus cambios y haz commit (`git commit -m 'Agrega nueva funcionalidad'`)
4. Sube tu rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un **Pull Request** describiendo tus cambios

### Convenciones de codigo

- Seguir las [convenciones de Kotlin](https://kotlinlang.org/docs/coding-conventions.html)
- Usar composables `@Preview` para cada componente de UI
- Mantener los componentes pequenos y reutilizables
- Documentar funciones publicas con KDoc

---

## Licencia

Este proyecto esta bajo la licencia MIT. Consulta el archivo [LICENSE](LICENSE) para mas detalles.

---

<p align="center">
  Desarrollado con Kotlin y Jetpack Compose
  <br/>
  <strong>Recipe Generator</strong> - Tu asistente culinario inteligente
</p>
