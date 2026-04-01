# POLITÉCNICO GRANCOLOMBIANO

## Facultad de Ingeniería, Diseño e Innovación

### Herramientas de Programación Móvil I

---

# PLAN MAESTRO DE DESARROLLO

## Recipe Generator App

### Kotlin + MVVM + Clean Architecture + Room DB + Fragments

---

| Campo | Detalle |
|---|---|
| **Módulo** | Herramientas de Programación Móvil I |
| **Tipo de entrega** | Proyecto Grupal — Aplicación Móvil para Android |
| **Integrantes** | Omar Hernández Rey (100349113) · Julian David Ortiz Bedoya · Yonatan Ferney Fernández · Juan David Rivera Casallas |
| **Docente** | Jose Ricardo Casallas Triana |
| **Grupo** | B03 — Politécnico Grancolombiano, Bogotá, Colombia |
| **Fecha** | Marzo 2026 |
| **Versión** | v2.0 — Completo (Fases 0, 1, 2 y 3 — 89 tareas) |

---

## TABLA DE CONTENIDO

1. [Introducción y Contexto del Proyecto](#1-introducción-y-contexto-del-proyecto)
2. [Arquitectura de Software Propuesta](#2-arquitectura-de-software-propuesta)
3. [Stack Tecnológico Completo](#3-stack-tecnológico-completo)
4. [Estructura de Paquetes Objetivo](#4-estructura-de-paquetes-objetivo)
5. [Modelo de Base de Datos (Room)](#5-modelo-de-base-de-datos-room)
6. [FASE 0 — Configuración y Arquitectura Base](#6-fase-0--configuración-y-arquitectura-base)
7. [FASE 1 — Entrega 1: Planificación y Diseño](#7-fase-1--entrega-1-planificación-y-diseño)
8. [FASE 2 — Entrega 2: Implementación Base](#8-fase-2--entrega-2-implementación-base)
9. [FASE 3 — Entrega 3: Aplicación Completa](#9-fase-3--entrega-3-aplicación-completa)
10. [Resumen Consolidado de Tareas](#10-resumen-consolidado-de-tareas)
11. [Cobertura LF1–LF8](#11-cobertura-de-requisitos-del-módulo-lf1lf8)
12. [Cobertura Requisitos Proyecto Grupal](#12-cobertura-requisitos-proyecto-grupal)
13. [Leyenda y Convenciones](#13-leyenda-y-convenciones)

---

## 1. INTRODUCCIÓN Y CONTEXTO DEL PROYECTO

Este documento es el Plan Maestro de Desarrollo para la aplicación Recipe Generator, proyecto grupal del módulo Herramientas de Programación Móvil I. Está diseñado para guiar el equipo en tres entregas formales, garantizando cobertura total de los requisitos académicos (LF1–LF8) y las especificaciones del proyecto grupal.

### Estado Actual de la Base de Código

- Navegación entre 5 pantallas (Home, Favoritos, Generador, Ajustes, Detalle) con Jetpack Compose
- Sistema de favoritos persistido en DataStore con actualización optimista
- 9 recetas mock — solo Lunes, Martes y Miércoles tienen datos completos
- UI con paleta de color: Primary #4800B2, Secondary #00C2A8, Tertiary #FF0081
- Coil para carga de imágenes remotas (se reemplazará por drawables locales)
- Sin arquitectura en capas, sin Room, sin ViewModel, sin Fragments XML

### Transformaciones Requeridas

- Migrar de Jetpack Compose a Views XML + Fragments (LF5, LF6 — requisito académico)
- Implementar MVVM + Clean Architecture con 3 capas separadas
- Agregar Room Database como backend local sin dependencias de terceros
- Cubrir explícitamente todos los controles LF7 y LF8 en pantallas dedicadas
- Completar datos de los 7 días de la semana (21 recetas totales)
- Implementar AppWidget visible en el escritorio del dispositivo (LF7)
- Agregar tests unitarios y de instrumentación reales

---

## 2. ARQUITECTURA DE SOFTWARE PROPUESTA

### Patrón: MVVM + Clean Architecture en 3 Capas

| CAPA | COMPONENTES | RESPONSABILIDAD |
|---|---|---|
| **Presentation** | Activities, Fragments, ViewModels, Adapters, Layouts XML, Widgets | UI, interacción usuario, observar estados del ViewModel |
| **Domain** | UseCases, Repository Interfaces, Domain Models (POJO) | Lógica de negocio pura, independiente de frameworks |
| **Data** | Room Database, DAOs, Entities, Repository Impl, DataStore | Persistencia local, fuente única de verdad, mapeo |

### Principios SOLID Aplicados

- **S** — Un ViewModel por pantalla, un UseCase por operación de negocio
- **O** — Interfaces de Repository permiten extender sin modificar implementaciones
- **L** — Implementaciones de repositorio son intercambiables entre sí
- **I** — RecipeRepository separado de FavoritesRepository y UserPrefsRepository
- **D** — La capa Presentation depende de interfaces (domain), nunca de Room directamente

---

## 3. STACK TECNOLÓGICO COMPLETO

| Tecnología | Detalle |
|---|---|
| **Lenguaje** | Kotlin 2.2.10 |
| **SDK Mínimo** | API 24 — Android 7.0 Nougat (~95% de dispositivos activos) |
| **SDK Objetivo** | API 35 — Android 15 |
| **UI Framework** | Android Views (XML Layouts) + Fragments + Activities |
| **Arquitectura** | MVVM + Clean Architecture (3 capas explícitas) |
| **Base de Datos** | Room Database (SQLite) — completamente local, sin backend externo |
| **Persistencia Liviana** | DataStore Preferences (tema, idioma, porciones por defecto) |
| **Imágenes** | Drawables locales en res/drawable/ (sin Coil — sin dependencias externas) |
| **Video** | VideoView + MediaController con archivo en res/raw/ |
| **Web** | WebView con JavaScript habilitado y WebViewClient interno |
| **Concurrencia** | Kotlin Coroutines + Flow (sin RxJava) |
| **Widget** | AppWidgetProvider + RemoteViews (nativo Android) |
| **Build System** | Gradle Kotlin DSL + Version Catalog (libs.versions.toml) |
| **Testing** | JUnit 4 + Espresso + Room In-Memory DB |
| **IDE** | Android Studio Panda 2025.3.2 (JDK 25.0.2, SDK API 36) |
| **Dispositivo** | Samsung SM-A528B (físico) + AVD emulador |

---

## 4. ESTRUCTURA DE PAQUETES OBJETIVO

```
com.example.recipe_generator/
├── RecipeGeneratorApp.kt                  # Application — AppContainer
├── MainActivity.kt                        # Single Activity host
│
├── presentation/                          # CAPA DE PRESENTACIÓN
│   ├── home/
│   │   ├── HomeFragment.kt
│   │   ├── HomeViewModel.kt
│   │   └── RecipeAdapter.kt
│   ├── leftmenu/
│   │   ├── LeftMenuFragment.kt
│   │   └── MenuAdapter.kt                # ArrayAdapter personalizado
│   ├── profile/
│   │   └── ProfileFragment.kt
│   ├── photos/
│   │   ├── PhotosFragment.kt
│   │   └── PhotoAdapter.kt
│   ├── video/
│   │   └── VideoFragment.kt
│   ├── web/
│   │   └── WebFragment.kt
│   ├── buttons/
│   │   └── ButtonsFragment.kt            # Cubre LF8 completo
│   ├── detail/
│   │   ├── RecipeDetailActivity.kt        # Segunda Activity — LF5
│   │   └── RecipeDetailViewModel.kt
│   ├── favorites/
│   │   ├── FavoritesFragment.kt
│   │   ├── FavoritesViewModel.kt
│   │   └── FavoritesAdapter.kt
│   ├── generator/
│   │   ├── MenuGeneratorFragment.kt       # SeekBar + filtrado real
│   │   └── MenuGeneratorViewModel.kt
│   └── settings/
│       ├── SettingsFragment.kt
│       └── SettingsViewModel.kt
│
├── domain/                                # CAPA DE DOMINIO
│   ├── model/
│   │   ├── Recipe.kt
│   │   ├── RecipeStep.kt
│   │   └── UserPreferences.kt
│   ├── repository/                        # Interfaces
│   │   ├── RecipeRepository.kt
│   │   ├── FavoritesRepository.kt
│   │   └── UserPrefsRepository.kt
│   └── usecase/
│       ├── GetMenuForDayUseCase.kt
│       ├── ToggleFavoriteUseCase.kt
│       ├── GenerateMenuUseCase.kt
│       └── GetRecipeDetailUseCase.kt
│
├── data/                                  # CAPA DE DATOS
│   ├── local/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt             # @Database Room
│   │   │   ├── DatabaseSeeder.kt          # 21 recetas (7 días × 3)
│   │   │   └── dao/
│   │   │       ├── RecipeDao.kt           # @Dao CRUD + queries
│   │   │       ├── IngredientDao.kt
│   │   │       └── StepDao.kt
│   │   ├── entity/
│   │   │   ├── RecipeEntity.kt            # @Entity
│   │   │   ├── IngredientEntity.kt        # @Entity
│   │   │   └── StepEntity.kt              # @Entity
│   │   └── datastore/
│   │       └── UserPreferencesDataStore.kt # Tema, idioma, porciones
│   ├── repository/                        # Implementaciones
│   │   ├── RecipeRepositoryImpl.kt
│   │   ├── FavoritesRepositoryImpl.kt
│   │   └── UserPrefsRepositoryImpl.kt
│   └── mapper/
│       └── RecipeMapper.kt                # Entity ↔ Domain Model
│
├── di/                                    # INYECCIÓN DE DEPENDENCIAS
│   └── AppContainer.kt                   # Manual DI — sin Hilt
│
└── widget/                                # WIDGET ANDROID
    └── RecipeWidgetProvider.kt            # AppWidgetProvider — LF7
```

---

## 5. MODELO DE BASE DE DATOS (ROOM)

### Entidades y Relaciones

| Entidad / Tabla | Clave Primaria | Tipo PK | Campos Principales |
|---|---|---|---|
| `recipes` | id | String (UUID) | title, imageRes, timeMinutes, calories, difficulty, category, description, isFavorite, rating, dayOfWeek, mealType, proteinG, carbsG, fatG |
| `ingredients` | id (autoGen) | Int | recipeId (FK → recipes.id), name, quantity, unit |
| `steps` | id (autoGen) | Int | recipeId (FK → recipes.id), stepNumber, title, description |
| `user_preferences` | key | String | value — par clave/valor para cualquier preferencia de usuario |

### Consultas DAO Principales

- `getAllRecipes(): Flow<List<RecipeEntity>>` — observable reactivo de todas las recetas
- `getRecipeById(id): Flow<RecipeEntity?>` — receta específica por ID
- `getRecipesByDay(day): Flow<List<RecipeEntity>>` — recetas del día seleccionado
- `getRecipesByCategory(cat): Flow<List<RecipeEntity>>` — filtro por categoría
- `getFavoriteRecipes(): Flow<List<RecipeEntity>>` — WHERE isFavorite = 1
- `searchRecipes(query): Flow<List<RecipeEntity>>` — LIKE %query% en título y descripción
- `updateFavoriteStatus(id, isFavorite): suspend fun` — toggle favorito
- `getRecipeWithDetails(id): @Transaction` → RecipeEntity + List + List
- `insertAll(recipes): suspend fun` — usado por DatabaseSeeder al inicio

---

## 6. FASE 0 — Configuración y Arquitectura Base

> **Semanas 1-2 | Prerequisito de todas las fases | 10 tareas**

Establece los fundamentos arquitectónicos. Debe completarse antes de cualquier otra fase para garantizar consistencia en todo el desarrollo.

| ID | Categoría | Tarea / Descripción | Semana | Estado |
|---|---|---|---|---|
| F0-01 | ARQUITECTURA | **Definir arquitectura MVVM + Clean Architecture** — Layers: Presentation / Domain / Data | 1-2 | ✅ COMPLETADO |
| F0-02 | ARQUITECTURA | **Refactorizar estructura de paquetes por capas** — presentation/ · domain/ · data/ · di/ · widget/ | 1-2 | ⬜ PENDIENTE |
| F0-03 | ARQUITECTURA | **Migrar estado a ViewModels con StateFlow** — Un ViewModel por pantalla principal | 1-2 | ⬜ PENDIENTE |
| F0-04 | ARQUITECTURA | **Implementar inyección de dependencias manual** — AppContainer en Application class (sin Hilt) | 1-2 | ⬜ PENDIENTE |
| F0-05 | ARQUITECTURA | **Migrar de Jetpack Compose a Views XML + Fragments** — Requisito académico: actividades y fragmentos (LF5, LF6) | 1-2 | ⬜ PENDIENTE |
| F0-06 | ARQUITECTURA | **Configurar NavHostFragment + nav_graph.xml** — Navegación declarativa entre fragmentos | 1-2 | ⬜ PENDIENTE |
| F0-07 | BACKEND | **Agregar dependencias Room al build.gradle.kts** — room-runtime + room-ktx + kapt room-compiler | 1 | ⬜ PENDIENTE |
| F0-08 | BACKEND | **Agregar dependencias Kotlin Coroutines** — kotlinx-coroutines-android | 1 | ⬜ PENDIENTE |
| F0-09 | DOCS | **Actualizar README con arquitectura y dependencias reales** — Corregir minSdk(24), compileSdk(35), stack tecnológico | 1 | ⬜ PENDIENTE |
| F0-10 | DATOS | **Eliminar archivos legacy: RecipeCard.kt, EditorialRecipeCard.kt** — No se usan en ninguna pantalla activa | 1 | ⬜ PENDIENTE |

---

## 7. FASE 1 — Entrega 1: Planificación y Diseño

> **Semana 3 | Documento APA: Título + Descripción + Objetivos + UML + Wireframes | 11 tareas**

### Checklist Rápido Entrega 1 — Semana 3

- ⬜ [F1-01] Redactar título de la aplicación
- ⬜ [F1-02] Redactar descripción completa — Propósito + público objetivo + alcance
- ⬜ [F1-03] Objetivo General (verbo + qué + cómo + para qué)
- ⬜ [F1-04] 4–5 Objetivos Específicos medibles — Alineados con LF1–LF8
- ⬜ [F1-05] Requerimientos Funcionales (mín. 10 RF-XX)
- ⬜ [F1-06] Requerimientos No Funcionales — Rendimiento, seguridad, usabilidad
- ⬜ [F1-07] Diagrama Casos de Uso UML
- ⬜ [F1-08] Diagrama de Clases UML
- ⬜ [F1-09] Diagrama de Secuencia UML (flujo principal)
- ⬜ [F1-10] Wireframes de la interfaz gráfica — MainActivity + 2 fragmentos + RecipeDetailActivity
- ⬜ [F1-11] Entregar Documento Entrega 1 normas APA — Carátula + TOC + referencias

| ID | Categoría | Tarea / Descripción | Semana | Estado |
|---|---|---|---|---|
| F1-01 | DOCS | **Redactar Título de la aplicación** — "Recipe Generator — Generador de Menús Semanales" | 3 | ⬜ PENDIENTE |
| F1-02 | DOCS | **Redactar Descripción completa del proyecto** — Propósito, público objetivo, alcance funcional | 3 | ⬜ PENDIENTE |
| F1-03 | DOCS | **Definir Objetivo General del proyecto** — Verbo infinitivo + qué + cómo + para qué | 3 | ⬜ PENDIENTE |
| F1-04 | DOCS | **Definir 4-5 Objetivos Específicos medibles** — Alineados con LF1-LF8 del módulo | 3 | ⬜ PENDIENTE |
| F1-05 | DOCS | **Levantar Requerimientos Funcionales (mín. 10 RF-XX)** — RF-01: Ver menú semanal, RF-02: Buscar receta, etc. | 3 | ⬜ PENDIENTE |
| F1-06 | DOCS | **Levantar Requerimientos No Funcionales** — Rendimiento, seguridad, usabilidad, portabilidad | 3 | ⬜ PENDIENTE |
| F1-07 | DOCS | **Crear Diagrama de Casos de Uso UML** — Actor: Usuario. CU: Ver menú, buscar, favorito, generar, configurar | 3 | ⬜ PENDIENTE |
| F1-08 | DOCS | **Crear Diagrama de Clases UML** — Entidades: Recipe, RecipeStep, Ingredient, UserPrefs, Repos (interfaces) | 3 | ⬜ PENDIENTE |
| F1-09 | DOCS | **Crear Diagrama de Secuencia UML (flujo principal)** — Seleccionar receta → ver detalle → agregar favorito | 3 | ⬜ PENDIENTE |
| F1-10 | DOCS | **Diseñar wireframes de la interfaz gráfica** — MainActivity + 2 fragmentos + RecipeDetailActivity | 3 | ⬜ PENDIENTE |
| F1-11 | DOCS | **Entregar Documento Entrega 1 en normas APA** — Carátula, TOC, cuerpo, referencias | 3 | ⬜ PENDIENTE |

---

## 8. FASE 2 — Entrega 2: Implementación Base

> **Semanas 4-5 | Fragments, Layouts XML, Room base, Variables y Eventos | 25 tareas**

### Checklist Rápido Entrega 2 — Semana 5

**Activity + Navegación**
- ⬜ [F2-01] MainActivity con NavHostFragment
- ⬜ [F2-02] activity_main.xml — dos paneles horizontal (30% / 70%)
- ⬜ [F2-03] BottomNavigationView con 4 ítems

**Fragmento Izquierdo — Menú de opciones**
- ⬜ [F2-04] LeftMenuFragment + fragment_left_menu.xml — ListView: Perfil, Fotos, Video, Web, Botones
- ⬜ [F2-05] MenuAdapter (ArrayAdapter personalizado) — item_menu.xml: icono + texto
- ⬜ [F2-06] Evento onItemClick → carga fragmento derecho — Cubre LF8: ListView + ArrayAdapter + OnItemClickListener

**Fragmento Derecho — Perfil → Fotos → Video → Web → Botones**
- ⬜ [F2-07] ProfileFragment + fragment_profile.xml — ImageView (LF7) + ScrollView + TextViews
- ⬜ [F2-08] PhotosFragment + fragment_photos.xml + PhotoAdapter — ListView imágenes locales. Cubre LF7: ImageView + LF8: ListView
- ⬜ [F2-09] VideoFragment + fragment_video.xml + res/raw/ — Cubre LF7: VideoView + setVideoURI + MediaController
- ⬜ [F2-10] WebFragment + fragment_web.xml + permiso INTERNET — Cubre LF7: WebView + LF8: EditText + Button
- ⬜ [F2-12] ButtonsFragment + fragment_buttons.xml — Button, ImageButton, CheckBox, RadioGroup, ToggleButton, Switch, Spinner, ScrollView
- ⬜ [F2-13] Controles y eventos en ButtonsFragment — onClick, isChecked, getCheckedId, onItemSelected — cubre LF8 completo
- ⬜ [F2-15] Variables declaradas + findViewByIds en TODOS los fragmentos — Cubre LF4: vínculos variable–interfaz

**Backend — Room Database**
- ⬜ [F2-16] RecipeEntity, IngredientEntity, StepEntity (@Entity)
- ⬜ [F2-18] RecipeDao con @Query, @Insert, @Update, @Delete
- ⬜ [F2-19] AppDatabase (@Database version=1)
- ⬜ [F2-20] DatabaseSeeder con 21 recetas (7 días × 3 comidas)
- ⬜ [F2-21] RecipeRepository interface + implementación

**Documento Entrega 2**
- ⬜ [F2-22] Pantallazos de todas las interfaces — Mínimo 1 por fragmento
- ⬜ [F2-23] Código XML de cada fragmento en el documento
- ⬜ [F2-24] Código Kotlin: variables + findViewByIds + métodos declarados
- ⬜ [F2-25] Entregar Documento Entrega 2 normas APA — Semana 5

| ID | Categoría | Tarea / Descripción | Semana | Estado |
|---|---|---|---|---|
| F2-01 | FRONTEND | **Crear MainActivity con NavHostFragment** — Reemplaza Compose RecipeGeneratorApp() | 4-5 | ⬜ PENDIENTE |
| F2-02 | FRONTEND | **Crear activity_main.xml — layout dos paneles horizontal** — LinearLayout: fragmento izq (30%) + derecho (70%) | 4-5 | ⬜ PENDIENTE |
| F2-03 | FRONTEND | **Implementar BottomNavigationView en XML** — Ítems: Inicio, Favoritos, Generador, Ajustes | 4-5 | ⬜ PENDIENTE |
| F2-04 | FRONTEND | **Crear LeftMenuFragment + fragment_left_menu.xml** — ListView con 5 opciones: Perfil, Fotos, Video, Web, Botones | 4-5 | ⬜ PENDIENTE |
| F2-05 | FRONTEND | **Crear MenuAdapter (ArrayAdapter personalizado)** — item_menu.xml: ImageView icono + TextView texto | 4-5 | ⬜ PENDIENTE |
| F2-06 | FRONTEND | **Evento onItemClick en LeftMenuFragment** — Carga fragmento derecho según posición. Cubre LF8: ListView | 4-5 | ⬜ PENDIENTE |
| F2-07 | FRONTEND | **Crear ProfileFragment + fragment_profile.xml** — ImageView foto + ScrollView + TextViews. Cubre LF7: ImageView | 4-5 | ⬜ PENDIENTE |
| F2-08 | FRONTEND | **Crear PhotosFragment + fragment_photos.xml + PhotoAdapter** — ListView imágenes locales + descripción al click. Cubre LF7+LF8 | 4-5 | ⬜ PENDIENTE |
| F2-09 | FRONTEND | **Crear VideoFragment + fragment_video.xml** — VideoView + res/raw/ local. Cubre LF7: VideoView | 4-5 | ⬜ PENDIENTE |
| F2-10 | FRONTEND | **Crear WebFragment + fragment_web.xml** — EditText URL + Button + WebView. Cubre LF7: WebView + LF8: Button | 4-5 | ⬜ PENDIENTE |
| F2-11 | FRONTEND | **Agregar permiso INTERNET en AndroidManifest.xml** — `<uses-permission android:name="android.permission.INTERNET"/>` | 4 | ⬜ PENDIENTE |
| F2-12 | FRONTEND | **Crear ButtonsFragment + fragment_buttons.xml** — Todos los controles LF8 visibles | 4-5 | ⬜ PENDIENTE |
| F2-13 | FRONTEND | **Controles en ButtonsFragment: Button, ImageButton, CheckBox, RadioGroup, ToggleButton, Switch, Spinner, ScrollView** — Formulario completo de preferencias del usuario | 4-5 | ⬜ PENDIENTE |
| F2-14 | FRONTEND | **Eventos en ButtonsFragment: onClick, isChecked, getCheckedId, onItemSelected** — Resultado visible al presionar Aceptar — cubre LF8 completo | 4-5 | ⬜ PENDIENTE |
| F2-15 | FRONTEND | **Declarar y vincular variables con findViewById en cada fragmento** — TextView, EditText, Button, ListView, Spinner — cubre LF4 | 4-5 | ⬜ PENDIENTE |
| F2-16 | BACKEND | **Crear RecipeEntity (@Entity Room)** — id, title, imageRes, timeMinutes, calories, difficulty, category, isFavorite | 4 | ⬜ PENDIENTE |
| F2-17 | BACKEND | **Crear IngredientEntity + StepEntity** — Con FK a recipes.id | 4 | ⬜ PENDIENTE |
| F2-18 | BACKEND | **Crear RecipeDao con @Query, @Insert, @Update, @Delete** — getAllRecipes, getByDay, getFavorites, search, updateFavorite | 4 | ⬜ PENDIENTE |
| F2-19 | BACKEND | **Crear AppDatabase (@Database, version=1)** — entities=[RecipeEntity, IngredientEntity, StepEntity] | 4 | ⬜ PENDIENTE |
| F2-20 | BACKEND | **Crear DatabaseSeeder con 21 recetas (7 días × 3 comidas)** — Completa los días Jueves-Domingo que actualmente no tienen datos | 4 | ⬜ PENDIENTE |
| F2-21 | BACKEND | **Crear RecipeRepository (interface + implementación)** — Fuente única de verdad. Expone Flow<List<Recipe>> | 4-5 | ⬜ PENDIENTE |
| F2-22 | DOCS | **Tomar pantallazos de todas las interfaces implementadas** — Mínimo 1 por fragmento/actividad | 5 | ⬜ PENDIENTE |
| F2-23 | DOCS | **Incluir código XML de cada fragmento en el documento** — fragment_left_menu, profile, photos, video, web, buttons | 5 | ⬜ PENDIENTE |
| F2-24 | DOCS | **Incluir código Kotlin con variables, findViewByIds y métodos declarados** — Por cada fragmento | 5 | ⬜ PENDIENTE |
| F2-25 | DOCS | **Entregar Documento Entrega 2 normas APA — Semana 5** | 5 | ⬜ PENDIENTE |

---

## 9. FASE 3 — Entrega 3: Aplicación Completa + Sustentación

> **Semanas 6-8 | App funcional + Widget + ViewModels + Tests + Documento Final | 43 tareas**

### Checklist Rápido Entrega 3 — Semanas 7-8

**Segunda Actividad — LF5: Navegación entre Actividades**
- ⬜ [F3-04] Crear RecipeDetailActivity (segunda Activity) — Cubre LF5: pila de actividades
- ⬜ [F3-05] Intent + putExtra(recipeId) → getExtras() en DetailActivity — Cubre LF5 completo: Intent, Bundle, back stack
- ⬜ [F3-06] Layout detalle: imagen + info + ingredientes + pasos (ScrollView)
- ⬜ [F3-07] ImageButton favorito con cambio visual de srcCompat — Cubre LF8: ImageButton

**Navegación y Comunicación entre Fragmentos — LF6**
- ⬜ [F3-01] Navegación completa con FragmentManager — replace + addToBackStack por cada opción
- ⬜ [F3-02] Interface callback Fragment→Activity→Fragment — Carga fragmento derecho desde selección izquierda
- ⬜ [F3-03] BottomNav funcional con 4 destinos — Inicio (2 paneles), Favoritos, Generador, Ajustes

**Fragmentos Completados**
- ⬜ [F3-08] ProfileFragment datos reales + imagen local (drawable) — Sin URLs externas
- ⬜ [F3-09] PhotosFragment: 5+ imágenes locales + click → descripción + ScrollView
- ⬜ [F3-10] VideoFragment completo + ciclo de vida (pause/resume)
- ⬜ [F3-11] WebFragment: JavaScript + WebViewClient + barra de progreso
- ⬜ [F3-12] ButtonsFragment: formulario funcional + resultado visible
- ⬜ [F3-13] FavoritesFragment: RecyclerView + búsqueda + filtros por categoría
- ⬜ [F3-14] MenuGeneratorFragment: SeekBar + Spinner + CheckBox + botón funcional — Cubre LF7: SeekBar
- ⬜ [F3-17] SettingsFragment con persistencia real (DataStore)
- ⬜ [F3-18] ToggleButton/Switch para tema real Claro/Oscuro — Cubre LF8: ToggleButton + Switch
- ⬜ [F3-19] RadioGroup selector idioma + Spinner porciones en Settings — Cubre LF8: RadioGroup + Spinner

**Widget Android — LF7 Completo**
- ⬜ [F3-21] RecipeWidgetProvider (AppWidgetProvider) — Muestra receta del día en el escritorio
- ⬜ [F3-22] Layout widget new_widget.xml (RemoteViews) — TextView título + Button abrir app
- ⬜ [F3-23] XML configuración: appwidget-provider info — updatePeriodMillis, minWidth/Height, resizeMode
- ⬜ [F3-24] onUpdate(): Intent + PendingIntent + RemoteViews + setOnClickPendingIntent — Cubre LF7: AppWidget completo
- ⬜ [F3-25] Declarar en AndroidManifest: receiver + meta-data

**Backend Completo**
- ⬜ [F3-26] Completar DatabaseSeeder: Jueves–Domingo (4 días × 3 = 12 recetas) — Total meta: 21 recetas
- ⬜ [F3-27] Ingredientes y pasos completos en las 21 recetas — Solo ID-2 tiene datos completos actualmente
- ⬜ [F3-28] Implementar GenerateMenuUseCase con filtrado real de Room
- ⬜ [F3-29] FavoritesRepository con Room (reemplaza DataStore)
- ⬜ [F3-30] UserPreferencesRepository con DataStore

**ViewModels — MVVM**
- ⬜ [F3-31] HomeViewModel: StateFlow<List<Recipe>> por día
- ⬜ [F3-32] RecipeDetailViewModel: receta + toggleFavorite()
- ⬜ [F3-33] FavoritesViewModel: Flow con búsqueda + filtro categoría
- ⬜ [F3-34] MenuGeneratorViewModel: parámetros + resultado generado
- ⬜ [F3-35] SettingsViewModel: lee/escribe UserPreferencesRepository

**Tests**
- ⬜ [F3-36] Test unitario: RecipeRepository (CRUD + filtros + favoritos)
- ⬜ [F3-37] Test unitario: GenerateMenuUseCase (lógica de filtrado)
- ⬜ [F3-38] Test instrumentación: navegación entre fragmentos
- ⬜ [F3-39] Test instrumentación: toggle favorito persiste en BD

**Documento Entrega 3 + Sustentación**
- ⬜ [F3-40] Documento Entrega 3 completo normas APA — Entrega 1 + pantallazos + XML + Kotlin + conclusiones
- ⬜ [F3-41] Conclusiones del proyecto (mín. 1 página)
- ⬜ [F3-42] Recomendaciones (mín. 0.5 páginas)
- ⬜ [F3-43] Sustentación: presentación + demo APK en dispositivo físico

| ID | Categoría | Tarea / Descripción | Semana | Estado |
|---|---|---|---|---|
| F3-01 | FRONTEND | **Implementar navegación completa con FragmentManager** — beginTransaction + replace + addToBackStack | 6-7 | ⬜ PENDIENTE |
| F3-02 | FRONTEND | **Implementar comunicación Fragment→Activity→Fragment** — Interface callback para cargar fragmento derecho desde izquierdo | 6 | ⬜ PENDIENTE |
| F3-03 | FRONTEND | **BottomNav funcional con 4 destinos** — Inicio (2 paneles), Favoritos, Generador, Ajustes | 6 | ⬜ PENDIENTE |
| F3-04 | FRONTEND | **Crear RecipeDetailActivity (segunda actividad)** — Cubre LF5: Segunda Activity en la pila | 6-7 | ⬜ PENDIENTE |
| F3-05 | FRONTEND | **Pasar datos con Intent + putExtra(recipeId)** — Recuperar con Bundle.getExtras() — cubre LF5 completo | 6-7 | ⬜ PENDIENTE |
| F3-06 | FRONTEND | **Layout RecipeDetailActivity: imagen hero + info + ingredientes + pasos** — ScrollView con todas las secciones | 6-7 | ⬜ PENDIENTE |
| F3-07 | FRONTEND | **Botón Favorito en detalle (ImageButton) con cambio visual** — srcCompat cambia al presionar — cubre LF8: ImageButton | 7 | ⬜ PENDIENTE |
| F3-08 | FRONTEND | **ProfileFragment con datos reales e imagen local (drawable)** — Sin URLs externas — sin dependencias de terceros | 6 | ⬜ PENDIENTE |
| F3-09 | FRONTEND | **PhotosFragment con 5+ imágenes locales** — Click → descripción en TextView. ScrollView en lista | 6 | ⬜ PENDIENTE |
| F3-10 | FRONTEND | **VideoFragment con video local res/raw/ + ciclo de vida** — onPause: pausar; onResume: reanudar video | 6 | ⬜ PENDIENTE |
| F3-11 | FRONTEND | **WebFragment: JavaScript habilitado + WebViewClient** — Evitar apertura browser externo. Barra de progreso | 6 | ⬜ PENDIENTE |
| F3-12 | FRONTEND | **ButtonsFragment completo: formulario funcional + resultado** — TextView muestra resultado de todos los controles LF8 | 6 | ⬜ PENDIENTE |
| F3-13 | FRONTEND | **FavoritesFragment con RecyclerView + búsqueda + filtros** — TextWatcher para filtro en tiempo real por título | 6-7 | ⬜ PENDIENTE |
| F3-14 | FRONTEND | **MenuGeneratorFragment funcional** — SeekBar (dificultad), Spinner (tipo), CheckBox (dieta) — LF7+LF8 | 7 | ⬜ PENDIENTE |
| F3-15 | FRONTEND | **Implementar SeekBar para nivel de dificultad** — onSeekBarChangeListener — cubre LF7: SeekBar | 7 | ⬜ PENDIENTE |
| F3-16 | FRONTEND | **Botón GENERAR MENÚ con lógica real de filtrado** — Filtra recetas de Room según preferencias seleccionadas | 7 | ⬜ PENDIENTE |
| F3-17 | FRONTEND | **SettingsFragment con persistencia real (DataStore)** — Guardar tema, idioma, porciones, dietas preferidas | 7 | ⬜ PENDIENTE |
| F3-18 | FRONTEND | **ToggleButton/Switch para tema Claro/Oscuro real** — Aplica tema al Activity sin reiniciar. Cubre LF8: ToggleButton + Switch | 7 | ⬜ PENDIENTE |
| F3-19 | FRONTEND | **RadioGroup para selector de idioma en Settings** — Español / Inglés / Portugués. Cubre LF8: RadioGroup | 7 | ⬜ PENDIENTE |
| F3-20 | FRONTEND | **Spinner de porciones por defecto en Settings** — Valores 1-10 personas. Cubre LF8: Spinner | 7 | ⬜ PENDIENTE |
| F3-21 | WIDGET | **Crear RecipeWidgetProvider (AppWidgetProvider)** — Muestra receta del día en el escritorio del dispositivo | 7 | ⬜ PENDIENTE |
| F3-22 | WIDGET | **Crear layout del widget new_widget.xml** — RemoteViews: TextView (título) + Button (abrir app) | 7 | ⬜ PENDIENTE |
| F3-23 | WIDGET | **Crear archivo XML configuración del widget** — appwidget-provider: minWidth, minHeight, updatePeriodMillis, resizeMode | 7 | ⬜ PENDIENTE |
| F3-24 | WIDGET | **Implementar onUpdate(): Intent + PendingIntent + RemoteViews + setOnClickPendingIntent** — Cubre LF7: AppWidget completo | 7 | ⬜ PENDIENTE |
| F3-25 | WIDGET | **Declarar widget en AndroidManifest.xml** — receiver + intent-filter APPWIDGET_UPDATE + meta-data provider | 7 | ⬜ PENDIENTE |
| F3-26 | BACKEND | **Completar DatabaseSeeder: Jueves-Domingo con datos completos** — Actualmente solo Lunes-Miércoles tienen datos. Meta: 21 recetas | 6 | ⬜ PENDIENTE |
| F3-27 | BACKEND | **Agregar ingredientes y pasos completos a las 21 recetas** — Actualmente solo receta ID-2 tiene datos completos | 6 | ⬜ PENDIENTE |
| F3-28 | BACKEND | **Implementar GenerateMenuUseCase con filtrado real** — Filtra por dieta / dificultad / tipo de comida | 7 | ⬜ PENDIENTE |
| F3-29 | BACKEND | **Implementar FavoritesRepository con Room** — Reemplaza DataStore por Room para favoritos | 6 | ⬜ PENDIENTE |
| F3-30 | BACKEND | **Implementar UserPreferencesRepository con DataStore** — Idioma, tema, porciones, dietas seleccionadas | 7 | ⬜ PENDIENTE |
| F3-31 | ARQUITECTURA | **Crear HomeViewModel con StateFlow<List<Recipe>> por día** — selectedDay: MutableStateFlow | 6 | ⬜ PENDIENTE |
| F3-32 | ARQUITECTURA | **Crear RecipeDetailViewModel: receta + toggleFavorite()** | 6 | ⬜ PENDIENTE |
| F3-33 | ARQUITECTURA | **Crear FavoritesViewModel: Flow<List<Recipe>> + filtros** — searchQuery + categoryFilter como MutableStateFlow | 6 | ⬜ PENDIENTE |
| F3-34 | ARQUITECTURA | **Crear MenuGeneratorViewModel: parámetros + resultado** | 7 | ⬜ PENDIENTE |
| F3-35 | ARQUITECTURA | **Crear SettingsViewModel: lee/escribe UserPreferencesRepository** | 7 | ⬜ PENDIENTE |
| F3-36 | TESTING | **Test unitario: RecipeRepository (CRUD + filtros + favoritos)** — Room In-Memory Database para tests | 8 | ⬜ PENDIENTE |
| F3-37 | TESTING | **Test unitario: GenerateMenuUseCase (lógica de filtrado)** — Verificar filtros por dieta, dificultad y tipo | 8 | ⬜ PENDIENTE |
| F3-38 | TESTING | **Test instrumentación: navegación entre fragmentos** — Seleccionar ítem izquierdo → fragmento derecho correcto aparece | 8 | ⬜ PENDIENTE |
| F3-39 | TESTING | **Test instrumentación: toggle favorito persiste** — Agregar favorito → aparece en FavoritesFragment | 8 | ⬜ PENDIENTE |
| F3-40 | DOCS | **Compilar Documento Entrega 3 completo normas APA** — Incluye: Entrega 1 + pantallazos + XML + Kotlin + conclusiones | 8 | ⬜ PENDIENTE |
| F3-41 | DOCS | **Redactar Conclusiones del proyecto (mín. 1 página)** — Qué se logró, dificultades, aprendizajes obtenidos | 8 | ⬜ PENDIENTE |
| F3-42 | DOCS | **Redactar Recomendaciones (mín. 0.5 páginas)** — Mejoras futuras: IA generativa, backend en nube, Compose | 8 | ⬜ PENDIENTE |
| F3-43 | DOCS | **Preparar sustentación: presentación + APK en dispositivo físico** — Demo en vivo + slides con arquitectura y decisiones técnicas | 8 | ⬜ PENDIENTE |

---

## 10. RESUMEN CONSOLIDADO DE TAREAS

| FASE | N.º TAREAS | SEMANAS | ENTREGA | ESTADO |
|---|---|---|---|---|
| **FASE 0** — Arquitectura Base | 10 | 1–2 | Prerequisito | ✅ 1/10 |
| **FASE 1** — Planificación | 11 | 3 | Entrega 1 | ⬜ 0/11 |
| **FASE 2** — Implementación Base | 25 | 4–5 | Entrega 2 | ⬜ 0/25 |
| **FASE 3** — App Completa | 43 | 6–8 | Entrega 3 | ⬜ 0/43 |
| **TOTAL DEL PROYECTO** | **89 tareas** | **1–8** | **Proyecto Grupal** | **1% completado (1/89)** |

---

## 11. COBERTURA DE REQUISITOS DEL MÓDULO (LF1–LF8)

| LF | Título | Implementación en la App | Tareas |
|---|---|---|---|
| **LF1** | Intro móvil Android | App nativa Android Kotlin. Arquitectura de 5 capas en código fuente. Tipos de apps cubiertos (nativa). | F0-01 a F0-05 |
| **LF2** | Android Studio | Estructura paquetes por capas. manifests, java, res, Gradle configurados. AVD + USB (Samsung SM-A528B). | F0-02, F0-07, F0-08 |
| **LF3** | Layouts / Interfaz | ConstraintLayout (MainActivity), LinearLayout (dos paneles), FrameLayout (contenedor fragmento), TableLayout (grid nutrición en detalle). | F2-02, F2-05, F2-07 |
| **LF4** | App interactiva | ButtonsFragment y todos los fragmentos: TextView(setText), EditText(getText), Button(onClick), variables vinculadas con findViewByIds. | F2-15, F2-12 a F2-14 |
| **LF5** | Actividades | MainActivity + RecipeDetailActivity (segunda Activity). Intent, putExtra, getExtras, back stack. | F3-04 a F3-07 |
| **LF6** | Fragmentos | LeftMenuFragment + 5 fragmentos derecha + FavoritesFragment + MenuGeneratorFragment + SettingsFragment. FragmentManager + replace + addToBackStack. | F2-04 a F2-15, F3-01, F3-02 |
| **LF7** | Widgets paleta | ImageView (fotos y perfil), WebView (web), VideoView + MediaController (video), SeekBar (generador), AppWidget en escritorio (RecipeWidgetProvider). | F2-07 a F2-10, F3-15, F3-21 a F3-25 |
| **LF8** | Controles | TextView, EditText, Button, ImageButton, CheckBox, RadioGroup+RadioButton, ToggleButton, Switch, ListView, Spinner, ScrollView — todos en ButtonsFragment + SettingsFragment. | F2-12 a F2-15, F3-07, F3-18 a F3-20 |

---

## 12. COBERTURA REQUISITOS PROYECTO GRUPAL

| Requisito del Proyecto | Implementación | Tareas Clave |
|---|---|---|
| **a. Interfaz actividades y fragmentos** | MainActivity + RecipeDetailActivity + 9 fragmentos (izq+der) | F2-01 a F2-06, F3-01 a F3-04 |
| **b. Imágenes** | ProfileFragment (foto), PhotosFragment (galería local), RecipeCard imagen | F2-07, F2-08, F3-08, F3-09 |
| **c. Vistas de texto** | TextView en todos los fragmentos, ScrollView en perfil y fotos | F2-07, F2-09, F2-12 |
| **d. Controles de botones** | Button, ImageButton en ButtonsFragment + FAB + botón favorito | F2-12, F2-13, F3-07 |
| **e. Listas** | ListView (menú izq, fotos), RecyclerView (favoritos, generador) | F2-04, F2-08, F3-13 |
| **f. Eventos** | onItemClick, onClick, onSeekBarChange, onDateChange, onItemSelected, isChecked | F2-06, F2-13 a F2-15, F3-15 |
| **g. Widgets** | RecipeWidgetProvider en escritorio del dispositivo físico | F3-21 a F3-25 |
| **Frag. Der. — PERFIL** | Foto + estudios + experiencia + ScrollView — LF7: ImageView | F2-07, F3-08 |
| **Frag. Der. — FOTOS** | Lista imágenes scroll + descripción al click — LF7+LF8 | F2-08, F3-09 |
| **Frag. Der. — VIDEO** | VideoView + MediaController (controles reproducción) — LF7 | F2-09, F3-10 |
| **Frag. Der. — WEB** | EditText URL + Button + WebView funcional — LF7+LF8 | F2-10, F2-11, F3-11 |
| **Frag. Der. — BOTONES** | Todos los controles LF8 con eventos reales en formulario | F2-12 a F2-14, F3-12 |

---

## 13. LEYENDA Y CONVENCIONES

### Estados de Tarea

| Icono | Estado | Descripción |
|---|---|---|
| ⬜ | **PENDIENTE** | Tarea aún no iniciada |
| 🔶 | **EN PROGRESO** | Tarea en desarrollo activo |
| ✅ | **COMPLETADO** | Tarea finalizada y validada |

### Categorías de Tarea

| Categoría | Color | Descripción |
|---|---|---|
| **ARQUITECTURA** | Violeta | Capas, ViewModels, navegación, inyección de dependencias |
| **BACKEND** | Verde | Room Database, DAOs, Entidades, Repositorios, Seeder |
| **FRONTEND** | Azul | Activities, Fragments, Layouts XML, Adapters, eventos UI |
| **DATOS** | Naranja | Domain models, mappers, casos de uso de negocio |
| **WIDGET** | Teal | AppWidgetProvider, layout widget, configuración XML, Manifest |
| **TESTING** | Rojo | Tests unitarios (JUnit 4), tests instrumentados (Espresso) |
| **DOCS** | Gris | Documentos de entrega, UML, pantallazos, conclusiones |

### Nomenclatura de IDs de Tarea

- **F0-XX**: Fase 0 — Configuración y Arquitectura Base (Semanas 1-2)
- **F1-XX**: Fase 1 — Planificación y Diseño (Entrega 1, Semana 3)
- **F2-XX**: Fase 2 — Implementación Base (Entrega 2, Semanas 4-5)
- **F3-XX**: Fase 3 — App Completa + Sustentación (Entrega 3, Semanas 6-8)

---

> **FIN DEL DOCUMENTO**
>
> Plan Maestro Recipe Generator App | v2.0 | Marzo 2026
>
> Herramientas de Programación Móvil I | Politécnico Grancolombiano
