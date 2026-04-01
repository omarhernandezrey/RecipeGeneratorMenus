# Recipe Generator - Generador de Menus Semanales

Aplicacion Android nativa desarrollada para la materia **Herramientas de Programacion Movil I** del **Politecnico Grancolombiano**.

La rama `JULIAN-F0-F10` deja cerrada la base de la **Fase 0** con:

- Jetpack Compose + Material Design 3 como UI principal
- Navigation Component con `Fragments` hosteando `ComposeView`
- Arquitectura por capas `presentation / domain / data / di / widget`
- Inyeccion manual de dependencias con `AppContainer`
- Room y DataStore configurados en Gradle
- Limpieza de restos legacy de Coil e imagenes remotas

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
| SDK | `minSdk 24` / `compileSdk 35` / `targetSdk 35` |

## Estado actual de Fase 0

### Implementado

- `F0-01` Definicion de arquitectura MVVM + Clean Architecture
- `F0-02` Reorganizacion de paquetes por capas
- `F0-03` Base de ViewModels con `StateFlow`
- `F0-04` `AppContainer` real inicializado desde `RecipeGeneratorApp`
- `F0-05` Compose confirmado como framework principal
- `F0-06` Navegacion base con `NavHostFragment` y destinos por `Fragment`
- `F0-07` Dependencias Room agregadas al proyecto
- `F0-08` Dependencias Compose, Navigation y Lifecycle alineadas
- `F0-09` README actualizado al stack real
- `F0-10` Limpieza de referencias a Coil, URLs remotas y componentes legacy removidos

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

- `compileSdk = 35`
- `minSdk = 24`
- `targetSdk = 35`
- Room con `kapt`
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
