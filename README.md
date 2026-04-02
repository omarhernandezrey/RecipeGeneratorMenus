# Recipe Generator - Generador de Menus Semanales

Aplicacion Android nativa desarrollada para la materia **Herramientas de Programacion Movil I** del **Politecnico Grancolombiano**.

El estado actual del repositorio deja integradas las contribuciones de **Julian** en la **Fase 0** y de **Omar** en la **Fase 1 (Entrega 1)**.

## Estado consolidado

- **Fase 0** completada: arquitectura base, navegacion con `Fragments` + `ComposeView`, ViewModels con `StateFlow`, `AppContainer`, Gradle alineado y limpieza legacy.
- **Fase 1** completada: documento de Entrega 1 en [docs/Entrega1_RecipeGenerator.md](docs/Entrega1_RecipeGenerator.md) con titulo, descripcion, objetivos, requerimientos, UML, wireframes y referencias.

## Stack tecnico

| Capa | Tecnologia |
| --- | --- |
| Lenguaje | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material Design 3 |
| Navegacion | Navigation Component (`NavHostFragment`) |
| Host de pantallas | Fragments + `ComposeView` |
| Arquitectura | MVVM + Clean Architecture |
| Concurrencia | Kotlin Coroutines + Flow |
| Persistencia | DataStore Preferences |
| Base de datos | Room (dependencias preparadas para F2) |
| Estado reactivo | `StateFlow` + `collectAsStateWithLifecycle()` |
| SDK | `minSdk 24` / `compileSdk 36` / `targetSdk 36` |

## Fase 0 completada

- `F0-01` Arquitectura MVVM + Clean Architecture definida.
- `F0-02` Estructura reorganizada por capas `presentation / domain / data / di / widget`.
- `F0-03` Estado migrado a ViewModels con `StateFlow`.
- `F0-04` Inyeccion manual con `AppContainer` inicializado desde `RecipeGeneratorApp`.
- `F0-05` Jetpack Compose confirmado como UI principal con `ComposeView` por `Fragment`.
- `F0-06` `Navigation Component` configurado con `main_nav_graph.xml`.
- `F0-07` Dependencias de Room agregadas via `ksp`.
- `F0-08` Compose BOM, Material 3, Navigation, Coroutines y Lifecycle alineados.
- `F0-09` README y stack tecnico actualizados.
- `F0-10` Componentes legacy, imports de Coil y referencias remotas eliminados.

## Fase 1 completada

- `F1-01` Titulo de la aplicacion.
- `F1-02` Descripcion completa del proyecto.
- `F1-03` Objetivo general.
- `F1-04` Objetivos especificos alineados con LF1-LF8.
- `F1-05` Requerimientos funcionales.
- `F1-06` Requerimientos no funcionales.
- `F1-07` Diagrama de casos de uso UML.
- `F1-08` Diagrama de clases UML.
- `F1-09` Diagrama de secuencia.
- `F1-10` Wireframes / mockups de la UI.
- `F1-11` Documento de Entrega 1 en formato APA.

### Nota de transicion

Aunque Room ya esta configurado en Gradle y existen entidades base, la fuente de datos activa en esta fase sigue siendo local/mock a traves de `RecipeRepositoryImpl`. Eso permite terminar la migracion arquitectonica en F0 sin bloquear la implementacion de F2.

## Estructura principal

```text
app/src/main/java/com/example/recipe_generator/
├── MainActivity.kt
├── RecipeGeneratorApp.kt
├── di/
│   └── AppContainer.kt
├── data/
│   ├── legacy/
│   ├── local/entity/
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── presentation/
│   ├── components/
│   ├── detail/
│   ├── favorites/
│   ├── generator/
│   ├── home/
│   ├── navigation/
│   ├── settings/
│   └── theme/
└── widget/
```

## Flujo arquitectonico

```text
MainActivity
  -> NavHostFragment
    -> HomeFragment / FavoritesFragment / MenuGeneratorFragment / SettingsFragment
      -> ComposeView
        -> Screen Composable
          -> ViewModel / Repository / UseCase
```

## Componentes clave

- `RecipeGeneratorApp.kt`
  Inicializa `AppContainer` una sola vez al arrancar la app.

- `MainActivity.kt`
  Host unico con `FragmentContainerView` y `NavHostFragment`.

- `presentation/navigation/ComposeScreenFragment.kt`
  Clase base para pantallas que renderizan Compose dentro de un Fragment.

- `di/AppContainer.kt`
  Construye repositorios y casos de uso sin Hilt.

- `data/repository/RecipeRepositoryImpl.kt`
  Expone recetas via `Flow` usando la fuente local temporal.

- `data/repository/FavoritesRepositoryImpl.kt`
  Combina recetas + IDs favoritos persistidos en DataStore.

- `data/repository/UserPrefsRepositoryImpl.kt`
  Persiste tema, idioma, porciones y dietas con DataStore.

## Configuracion de build

- `compileSdk = 36`
- `minSdk = 24`
- `targetSdk = 36`
- Room con `ksp`
- Compose BOM + Material 3
- Navigation Compose y Navigation Fragment
- Lifecycle Runtime Compose + ViewModel KTX

## Compilar

En Windows:

```powershell
.\gradlew.bat assembleDebug
```

## Siguientes pasos naturales

- `F2-18` a `F2-21`: DAO + `AppDatabase` + seeding real
- Migrar mas pantallas desde estado local hacia ViewModels dedicados
- Reemplazar la fuente legacy temporal por Room como source of truth
- Completar las pantallas de soporte LF7/LF8 pendientes
