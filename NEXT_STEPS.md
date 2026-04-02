# 🚀 GUÍA DE PRÓXIMOS PASOS - Recipe Generator App

## 📊 Estado Actual del Proyecto

| Fase | Tareas | Completadas | Porcentaje | Estado |
|------|--------|-------------|-----------|--------|
| **FASE 0** - Arquitectura Base | 10 | 10 | 100% | ✅ COMPLETADO |
| **FASE 1** - Planificación y Diseño | 11 | 11 | 100% | ✅ COMPLETADO |
| **FASE 2** - Implementación Base | 25 | 0 | 0% | ⬜ PENDIENTE |
| **FASE 3** - App Completa + Sustentación | 43 | 0 | 0% | ⬜ PENDIENTE |
| **TOTAL** | **89** | **21** | **24%** | 🔄 EN PROGRESO |

---

## 🎨 CAMBIOS REALIZADOS HOY

### MenuGeneratorScreen.kt (REFINADO ✅)

**Lo que se hizo:**
1. ✅ Agregada sección HERO con título + descripción editorial
2. ✅ Mejorado espaciamiento vertical (spacing_10 entre secciones)
3. ✅ Layout asimétrico: Difficulty (60%) + Portions (40%)
4. ✅ Actualizado TopAppBar title a "Generador de Menú"
5. ✅ Agregado progress bar sutil antes del botón
6. ✅ Mejorado texto del botón: "GENERAR MENÚ SEMANAL"
7. ✅ Agregado mensaje de estado dinámico
8. ✅ Paleta de colores aplicada globalmente
9. ✅ Márgenes y padding coherentes sin solapamiento

**Cómo se ve ahora:**
```
Generador de Menú (Hero)
Personaliza tu experiencia culinaria...

Preferencias Dietéticas
[Vegetariano] [Vegano]
[Sin gluten] [Sin lácteos]
[Keto]       [Paleo]

[Difficulty Card]  [Portions Card]

Tipos de Recetas Preferidas
[Desayunos]  [Cenas Ligeras]
[Almuerzos]  [Postres]
[Snacks]

────────────────
[GENERAR MENÚ SEMANAL]
✓ Menú generado: 21 recetas
```

---

## 🔧 PRÓXIMOS PASOS (FASE 2 - Implementación Base)

### 1️⃣ **Pantalla de INICIO (HomeScreen)** 
Igual al generador, pero mostrando recetas del día actual.

**Tareas:**
- [ ] F2-01: MainActivity con NavHostFragment
- [ ] F2-02: activity_main.xml — dos paneles (30% / 70%)
- [ ] F2-03: BottomNavigationView funcional

**Archivo a crear:**
```kotlin
app/src/main/java/com/example/recipe_generator/presentation/home/HomeScreen.kt
```

**Estructura:**
```
┌─────────────┬──────────────────────────────────┐
│             │                                   │
│  LeftMenu   │       HomeScreen (Scrollable)    │
│   Panel     │  • Recetas del día                │
│ (30% wide)  │  • RecyclerView o LazyColumn     │
│             │  • Each RecipeCard clickable     │
│             │                                   │
└─────────────┴──────────────────────────────────┘
```

---

### 2️⃣ **Pantalla de FAVORITOS (FavoritesScreen)**

**Tareas:**
- [ ] F3-13: FavoritesFragment con RecyclerView
- [ ] [ ] Búsqueda en tiempo real
- [ ] Filtros por categoría

**Archivo a crear:**
```kotlin
app/src/main/java/com/example/recipe_generator/presentation/favorites/FavoritesScreen.kt
```

---

### 3️⃣ **Base de Datos (Room)**

**Tareas:**
- [ ] F2-16: RecipeEntity (@Entity)
- [ ] F2-17: IngredientEntity + StepEntity
- [ ] F2-18: RecipeDao (@Dao)
- [ ] F2-19: AppDatabase (@Database)
- [ ] F2-20: DatabaseSeeder (21 recetas)
- [ ] F2-21: RecipeRepository

**Archivos a crear:**
```
app/src/main/java/com/example/recipe_generator/data/
├── local/
│   ├── database/
│   │   ├── AppDatabase.kt
│   │   ├── DatabaseSeeder.kt
│   │   └── dao/
│   │       ├── RecipeDao.kt
│   │       ├── IngredientDao.kt
│   │       └── StepDao.kt
│   └── entity/
│       ├── RecipeEntity.kt
│       ├── IngredientEntity.kt
│       └── StepEntity.kt
└── repository/
    ├── RecipeRepositoryImpl.kt
    ├── FavoritesRepositoryImpl.kt
    └── UserPrefsRepositoryImpl.kt
```

---

## 🏗️ ARQUITECTURA PROPUESTA

### Clean Architecture (3 Capas)

```
┌─────────────────────────────────────────────────┐
│         PRESENTATION (UI)                       │
│  • Activities + Fragments                       │
│  • ViewModels (Compose)                         │
│  • Composables                                  │
│  • Adapters                                     │
└────────────┬────────────────────────────────────┘
             │ depends on
┌────────────▼────────────────────────────────────┐
│         DOMAIN (Business Logic)                 │
│  • UseCases                                     │
│  • Repository Interfaces                       │
│  • Domain Models (POJO)                         │
└────────────┬────────────────────────────────────┘
             │ implements
┌────────────▼────────────────────────────────────┐
│         DATA (Persistence)                      │
│  • Room Database                                │
│  • DAOs                                         │
│  • Entities                                     │
│  • Repository Implementations                  │
│  • DataStore                                    │
└─────────────────────────────────────────────────┘
```

---

## 📚 TECNOLOGÍAS A USAR

| Componente | Tecnología | Versión |
|-----------|-----------|---------|
| **Base de datos** | Room (SQLite) | Latest |
| **Persistencia liviana** | DataStore Preferences | Latest |
| **UI Framework** | Jetpack Compose | Latest |
| **Navegación** | Navigation Component | Latest |
| **Concurrencia** | Kotlin Coroutines + Flow | Latest |
| **Testing** | JUnit 4 + Espresso | Latest |
| **DI** | Manual (sin Hilt) | AppContainer |

---

## 📱 SCREENS PRIORITARIAS

### SEMANA ACTUAL (Próximo)
1. ✅ **MenuGeneratorScreen** - REFINADO
2. ⬜ **HomeScreen** - Con recetas del día
3. ⬜ **RecipeDetailScreen** - Detalles y favorito

### SEMANA 2
4. ⬜ **FavoritesScreen** - RecyclerView + búsqueda
5. ⬜ **SettingsScreen** - Tema, idioma, porciones

### SEMANA 3
6. ⬜ **Fragmentos XML** - Perfil, Fotos, Video, Web, Botones
7. ⬜ **LeftMenuFragment** - Navegación

---

## 🗃️ ESTRUCTURA FINAL DE CARPETAS

```
RecipeGeneratorMenus/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/recipe_generator/
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── home/
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   ├── HomeViewModel.kt
│   │   │   │   │   │   └── HomeFragment.kt
│   │   │   │   │   ├── generator/
│   │   │   │   │   │   ├── MenuGeneratorScreen.kt ✅ REFINADO
│   │   │   │   │   │   ├── MenuGeneratorViewModel.kt
│   │   │   │   │   │   └── MenuGeneratorFragment.kt
│   │   │   │   │   ├── favorites/
│   │   │   │   │   │   ├── FavoritesScreen.kt
│   │   │   │   │   │   ├── FavoritesViewModel.kt
│   │   │   │   │   │   └── FavoritesFragment.kt
│   │   │   │   │   ├── detail/
│   │   │   │   │   │   ├── RecipeDetailScreen.kt
│   │   │   │   │   │   ├── RecipeDetailViewModel.kt
│   │   │   │   │   │   └── RecipeDetailFragment.kt
│   │   │   │   │   ├── settings/
│   │   │   │   │   │   ├── SettingsScreen.kt
│   │   │   │   │   │   ├── SettingsViewModel.kt
│   │   │   │   │   │   └── SettingsFragment.kt
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Color.kt ✅ PALETA
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Recipe.kt
│   │   │   │   │   │   ├── Ingredient.kt
│   │   │   │   │   │   ├── RecipeStep.kt
│   │   │   │   │   │   └── UserPreferences.kt
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── RecipeRepository.kt
│   │   │   │   │   │   ├── FavoritesRepository.kt
│   │   │   │   │   │   └── UserPrefsRepository.kt
│   │   │   │   │   └── usecase/
│   │   │   │   │       ├── GetMenuForDayUseCase.kt
│   │   │   │   │       ├── GenerateMenuUseCase.kt
│   │   │   │   │       ├── ToggleFavoriteUseCase.kt
│   │   │   │   │       └── GetRecipeDetailUseCase.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── database/
│   │   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   │   ├── DatabaseSeeder.kt
│   │   │   │   │   │   │   └── dao/
│   │   │   │   │   │   │       ├── RecipeDao.kt
│   │   │   │   │   │   │       ├── IngredientDao.kt
│   │   │   │   │   │   │       └── StepDao.kt
│   │   │   │   │   │   └── entity/
│   │   │   │   │   │       ├── RecipeEntity.kt
│   │   │   │   │   │       ├── IngredientEntity.kt
│   │   │   │   │   │       └── StepEntity.kt
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── RecipeRepositoryImpl.kt
│   │   │   │   │   │   ├── FavoritesRepositoryImpl.kt
│   │   │   │   │   │   └── UserPrefsRepositoryImpl.kt
│   │   │   │   │   └── mapper/
│   │   │   │   │       └── RecipeMapper.kt
│   │   │   │   ├── di/
│   │   │   │   │   └── AppContainer.kt
│   │   │   │   ├── widget/
│   │   │   │   │   └── RecipeWidgetProvider.kt
│   │   │   │   ├── RecipeGeneratorApp.kt
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   ├── layout/
│   │   │   │   ├── values/
│   │   │   │   └── navigation/
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   │   └── java/...
│   │   └── androidTest/
│   │       └── java/...
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── PLAN_MAESTRO.md
├── REFINEMENT_SUMMARY.md ✅ NUEVO
└── README.md
```

---

## ✅ CHECKLIST ANTES DE COMPILAR

- [ ] Eliminar archivos legacy (si existen)
- [ ] Validar imports en todas las clases
- [ ] No hay referencias a `@Deprecated` APIs
- [ ] Kotlin 2.2.10 actualizado
- [ ] Gradle 8.x configurado
- [ ] Paleta de colores consistente
- [ ] Tipografía Material3
- [ ] Sin hardcoded strings (use strings.xml)

---

## 🎯 OBJETIVO FINAL (FASE 3)

Entregar una app completa Android que cumpla:
- ✅ Arquitectura MVVM + Clean
- ✅ Room Database (21 recetas)
- ✅ Navegación multi-fragment
- ✅ Widget Android
- ✅ RecyclerView + búsqueda
- ✅ Controles LF7 + LF8
- ✅ Tests unitarios e instrumentados
- ✅ Documento APA de entrega

---

## 📞 SOPORTE

Si encuentras errores de compilación:
1. Ejecuta: `gradlew clean`
2. Elimina carpeta `build/`
3. Invalida cachés IDE
4. Reconstruye el proyecto

---

**Última actualización**: 2026-04-01
**Próximo milestone**: F2-01 (MainActivity)
**Versión**: v2.1 RC


