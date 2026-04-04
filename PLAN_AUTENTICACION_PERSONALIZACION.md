# RECIPE GENERATOR

## Generador de Menus Semanales

### Jetpack Compose + Material Design 3 + Firebase

### Herramientas de Programacion Movil I  ·  Politecnico Grancolombiano

---

# PLAN DE AUTENTICACION Y PERSONALIZACION COMPLETA

**v1.0  ·  52 tareas  ·  5 Fases  ·  Para Omar y Julian**

---

| Campo | Detalle |
|-------|---------|
| **Proyecto** | Recipe Generator — Generador de Menus Semanales |
| **Materia** | Herramientas de Programacion Movil I |
| **Institucion** | Politecnico Grancolombiano — Bogota, Colombia |
| **Integrantes** | Omar Hernandez Rey · Julian David Ortiz Bedoya |
| **Docente** | Jose Ricardo Casallas Triana |
| **Grupo** | B03  ·  Abril 2026 |
| **Objetivo** | Autenticacion completa (Google + Formulario) + Datos por usuario + Personalizacion total + Persistencia en BD |

---

## RESUMEN GENERAL

| Fase | Descripcion | Tareas | Progreso |
|------|-------------|--------|----------|
| FASE A | Configuracion Firebase + Google Sign-In | 8 | 0% |
| FASE B | Autenticacion Completa (Formulario + Google) | 10 | 0% |
| FASE C | Base de Datos por Usuario + Recetas Personalizadas | 14 | 0% |
| FASE D | Perfil de Usuario Editable + Personalizacion | 10 | 0% |
| FASE E | Integracion Final + Persistencia Completa | 10 | 0% |
| **TOTAL** | | **52** | **0%** |

---

## ESTADO ACTUAL (Lo que YA existe)

### Ya implementado:
- ✅ `AuthScreen.kt` — Formulario basico email/password (login + registro)
- ✅ `AuthViewModel.kt` — ViewModel con signUp, signIn, logout, clearError
- ✅ `AuthRepository.kt` — Interface de dominio con getCurrentUser, signUp, signIn, logout, updateUserProfile
- ✅ `FirebaseAuthRepository.kt` — Implementacion Firebase (email/password + updateProfile)
- ✅ `MockAuthRepository.kt` — Mock para desarrollo sin Firebase
- ✅ `User.kt` — Modelo de dominio (uid, email, displayName, photoUrl, createdAt)
- ✅ `AppContainer.kt` — authRepository ya inyectado
- ✅ `MainActivity.kt` — Flujo: AuthScreen → AuthWelcomeScreen → AppShell
- ✅ Firebase BOM 32.7.1 + firebase-auth-ktx + firebase-firestore-ktx en build.gradle.kts
- ✅ Plugin google-services configurado en build.gradle.kts raiz
- ✅ Room Database con RecipeEntity, IngredientEntity, StepEntity, FavoriteEntity
- ✅ 21 recetas seed (7 dias x 3 comidas)

### Lo que FALTA (objetivo de este plan):
- ❌ `google-services.json` real (actual es placeholder con valores falsos)
- ❌ Google Sign-In (One Tap / Credential Manager)
- ❌ Formulario de registro con nombre completo + validaciones
- ❌ Recetas vinculadas al usuario (userId en BD)
- ❌ Recetas creadas por el usuario (CRUD completo)
- ❌ Plan de comidas semanal personalizado por usuario
- ❌ Perfil editable (nombre, foto, preferencias)
- ❌ Firestore para persistencia en la nube por usuario
- ❌ Pantalla de "Crear Receta" con todos los campos
- ❌ Pantalla de "Mi Plan Semanal" editable

---

## STACK TECNOLOGICO ADICIONAL

| Tecnologia | Detalle |
|------------|---------|
| **Autenticacion** | Firebase Auth (Email/Password + Google Sign-In) |
| **Google Sign-In** | Credential Manager API (moderno, reemplaza legacy GoogleSignInClient) |
| **BD Nube** | Cloud Firestore — colecciones por usuario (`users/{uid}/recipes`, `users/{uid}/weeklyPlan`) |
| **BD Local** | Room Database — cache offline de recetas del usuario |
| **Imagenes Perfil** | Firebase Storage (foto de perfil) o drawable local |
| **Sincronizacion** | Firestore listeners en tiempo real → Room como cache local |

---

## ESTRUCTURA DE DATOS POR USUARIO

```
Firestore:
  users/
    {uid}/
      profile/          → nombre, email, photoUrl, preferencias
      recipes/          → recetas creadas por el usuario
        {recipeId}/     → titulo, ingredientes, pasos, calorias, etc.
      weeklyPlan/       → plan semanal personalizado
        {dayOfWeek}/    → desayuno, almuerzo, cena (referencias a recipeId)
      favorites/        → IDs de recetas favoritas

Room (cache local):
  user_recipes          → recetas del usuario (con campo userId)
  weekly_plan           → plan semanal del usuario
  user_profile          → perfil cached
```

---

## FASE A — Configuracion Firebase + Google Sign-In

**8 tareas  ·  Prerequisito de todo lo demas**

| ID | Categoria | Tarea / Descripcion | Asignado | Estado |
|----|-----------|---------------------|----------|--------|
| A-01 | CONFIG | Crear proyecto real en Firebase Console — Nombre: "recipe-generator-app". Activar Authentication + Firestore + Storage | Omar | ⬜ |
| A-02 | CONFIG | Descargar `google-services.json` real de Firebase Console y reemplazar el placeholder en `app/` | Omar | ⬜ |
| A-03 | CONFIG | Activar proveedores de autenticacion en Firebase Console — Email/Password + Google Sign-In | Omar | ⬜ |
| A-04 | CONFIG | Agregar SHA-1 y SHA-256 del keystore de debug al proyecto Firebase — Requerido para Google Sign-In | Omar | ⬜ |
| A-05 | CONFIG | Agregar dependencia `credentials` de Google al build.gradle.kts — `androidx.credentials:credentials` + `credentials-play-services-auth` + `googleid` | Julian | ⬜ |
| A-06 | CONFIG | Agregar plugin `com.google.gms.google-services` al `app/build.gradle.kts` (actualmente solo esta en root, falta `apply plugin`) | Julian | ⬜ |
| A-07 | CONFIG | Configurar reglas de seguridad en Firestore — Solo lectura/escritura a documentos propios: `request.auth.uid == resource.data.userId` | Omar | ⬜ |
| A-08 | CONFIG | Verificar compilacion exitosa con Firebase real — `./gradlew assembleDebug` sin errores | Julian | ⬜ |

---

## FASE B — Autenticacion Completa (Formulario + Google)

**10 tareas  ·  Login/Registro con formulario mejorado + Google One Tap**

| ID | Categoria | Tarea / Descripcion | Asignado | Estado |
|----|-----------|---------------------|----------|--------|
| B-01 | FRONTEND | Redisenar `AuthScreen.kt` con estilo editorial — Logo de la app + titulo + campos + botones estilizados con Material 3 | Julian | ⬜ |
| B-02 | FRONTEND | Agregar campo "Nombre completo" al formulario de registro — Solo visible cuando `isSignUp = true`. Guardarlo en Firebase displayName | Julian | ⬜ |
| B-03 | FRONTEND | Agregar validaciones al formulario — Email formato valido, password min 6 chars, nombre no vacio. Mostrar errores inline bajo cada campo | Julian | ⬜ |
| B-04 | FRONTEND | Agregar boton "Iniciar sesion con Google" al `AuthScreen` — Boton con icono de Google + texto. Estilo OutlinedButton M3 | Julian | ⬜ |
| B-05 | BACKEND | Implementar `signInWithGoogle(idToken)` en `AuthRepository` interface — Nuevo metodo que recibe el token de Google | Omar | ⬜ |
| B-06 | BACKEND | Implementar `signInWithGoogle()` en `FirebaseAuthRepository` — `GoogleAuthProvider.getCredential(idToken)` → `firebaseAuth.signInWithCredential()` | Omar | ⬜ |
| B-07 | BACKEND | Implementar flujo Credential Manager en `AuthViewModel` — Lanzar Google One Tap → obtener idToken → llamar `signInWithGoogle(idToken)` | Omar | ⬜ |
| B-08 | BACKEND | Implementar `signInWithGoogle()` en `MockAuthRepository` — Simular login con Google para desarrollo sin Firebase | Julian | ⬜ |
| B-09 | FRONTEND | Agregar opcion "Recuperar contrasena" — Boton TextButton → llama `firebaseAuth.sendPasswordResetEmail(email)` + SnackBar de confirmacion | Omar | ⬜ |
| B-10 | TESTING | Probar flujo completo: Registro email → Login email → Login Google → Logout → Login otra vez. Verificar persistencia de sesion | Ambos | ⬜ |

---

## FASE C — Base de Datos por Usuario + Recetas Personalizadas

**14 tareas  ·  CRUD de recetas + Plan semanal por usuario**

| ID | Categoria | Tarea / Descripcion | Asignado | Estado |
|----|-----------|---------------------|----------|--------|
| C-01 | BACKEND | Crear `UserRecipeEntity` en Room — Campos: id, **userId**, title, imageRes, timeInMinutes, calories, difficulty, category, description, dayOfWeek, mealType, ingredients (JSON string), steps (JSON string) | Omar | ⬜ |
| C-02 | BACKEND | Crear `WeeklyPlanEntity` en Room — Campos: id, **userId**, dayOfWeek, mealType ("Desayuno"/"Almuerzo"/"Cena"), recipeId (FK), notes | Omar | ⬜ |
| C-03 | BACKEND | Crear `UserProfileEntity` en Room — Campos: uid (PK), displayName, email, photoUrl, preferredDiets (JSON), defaultPortions, createdAt | Omar | ⬜ |
| C-04 | BACKEND | Crear `UserRecipeDao` — Queries: getByUserId(userId), getByDay(userId, day), insert, update, delete, searchByTitle(userId, query) | Julian | ⬜ |
| C-05 | BACKEND | Crear `WeeklyPlanDao` — Queries: getPlanForUser(userId), getPlanForDay(userId, day), upsert (insert or replace), deleteMeal(userId, day, mealType) | Julian | ⬜ |
| C-06 | BACKEND | Crear `UserProfileDao` — Queries: getProfile(uid), insertOrUpdate, deleteProfile | Julian | ⬜ |
| C-07 | BACKEND | Actualizar `AppDatabase` — Agregar las 3 nuevas entities + DAOs. Incrementar version. fallbackToDestructiveMigration() | Omar | ⬜ |
| C-08 | BACKEND | Crear `UserRecipeRepository` (interface + impl) — CRUD completo de recetas del usuario. Metodos: getMyRecipes(), addRecipe(), updateRecipe(), deleteRecipe(), getRecipesForDay() | Omar | ⬜ |
| C-09 | BACKEND | Crear `WeeklyPlanRepository` (interface + impl) — getWeeklyPlan(userId), setMeal(day, mealType, recipeId), removeMeal(), getDay() | Julian | ⬜ |
| C-10 | BACKEND | Crear `UserProfileRepository` (interface + impl) — getProfile(), saveProfile(), updateName(), updatePhoto() | Julian | ⬜ |
| C-11 | BACKEND | Registrar los 3 nuevos repositorios en `AppContainer` — Inyectarlos con lazy, pasar userId del usuario autenticado | Omar | ⬜ |
| C-12 | BACKEND | Crear `FirestoreSyncService` — Sincronizar recetas del usuario entre Room (local) y Firestore (nube). Al crear/editar/eliminar receta: escribir en Room + Firestore. Al iniciar sesion: descargar de Firestore → poblar Room | Omar | ⬜ |
| C-13 | BACKEND | Crear `FirestoreWeeklyPlanSync` — Sincronizar plan semanal entre Room y Firestore. Estructura: `users/{uid}/weeklyPlan/{day}` con campos breakfast, lunch, dinner | Julian | ⬜ |
| C-14 | TESTING | Test: Crear receta → aparece en "Mis Recetas". Asignar a dia → aparece en plan semanal. Eliminar → desaparece de ambos | Ambos | ⬜ |

---

## FASE D — Perfil de Usuario Editable + Personalizacion

**10 tareas  ·  Pantallas de perfil, crear receta, y plan semanal**

| ID | Categoria | Tarea / Descripcion | Asignado | Estado |
|----|-----------|---------------------|----------|--------|
| D-01 | FRONTEND | Redisenar `ProfileScreen.kt` — Mostrar datos REALES del usuario autenticado (nombre, email, foto). Boton "Editar perfil". Estadisticas reales (# recetas, # favoritos, # dias con plan) | Julian | ⬜ |
| D-02 | FRONTEND | Crear `EditProfileScreen.kt` — Formulario: nombre, foto (seleccionar de galeria o camara), preferencias dieteticas, porciones default. Boton "Guardar" → actualiza Firebase + Room | Julian | ⬜ |
| D-03 | FRONTEND | Crear `CreateRecipeScreen.kt` — Formulario completo: titulo, descripcion, tiempo, calorias, dificultad (Slider), categoria (Desayuno/Almuerzo/Cena), dia de la semana, ingredientes (lista dinamica con + y -), pasos (lista dinamica con + y -). Boton "Guardar Receta" | Omar | ⬜ |
| D-04 | FRONTEND | Crear `CreateRecipeViewModel.kt` — Estado del formulario con StateFlow. Metodos: addIngredient(), removeIngredient(), addStep(), removeStep(), saveRecipe(). Validaciones antes de guardar | Omar | ⬜ |
| D-05 | FRONTEND | Crear `MyWeeklyPlanScreen.kt` — Vista semanal tipo calendario: 7 columnas (Lun-Dom) x 3 filas (Desayuno, Almuerzo, Cena). Cada celda muestra receta asignada o boton "+" para agregar. Click en celda → seleccionar receta de "Mis Recetas" o del catalogo general | Julian | ⬜ |
| D-06 | FRONTEND | Crear `MyWeeklyPlanViewModel.kt` — Carga plan del usuario. Metodos: assignRecipe(day, mealType, recipeId), removeMeal(day, mealType), getPlanForDay(). Observa cambios en tiempo real | Julian | ⬜ |
| D-07 | FRONTEND | Crear `SelectRecipeDialog.kt` — Dialog/BottomSheet para seleccionar receta al llenar una celda del plan semanal. Muestra "Mis Recetas" + catalogo general con buscador | Omar | ⬜ |
| D-08 | FRONTEND | Crear `MyRecipesScreen.kt` — Lista de recetas creadas por el usuario. Card con titulo + categoria + acciones (editar/eliminar). FAB "+" para crear nueva receta | Omar | ⬜ |
| D-09 | FRONTEND | Crear `EditRecipeScreen.kt` — Reutilizar formulario de CreateRecipeScreen pero precargado con datos existentes. Boton "Actualizar" | Julian | ⬜ |
| D-10 | FRONTEND | Crear `ProfileViewModel.kt` — Carga perfil del usuario desde UserProfileRepository. Metodos: updateName(), updatePhoto(), loadStats(). Expone StateFlow<UserProfile> | Omar | ⬜ |

---

## FASE E — Integracion Final + Persistencia Completa

**10 tareas  ·  Conectar todo, navegacion, sincronizacion y pruebas**

| ID | Categoria | Tarea / Descripcion | Asignado | Estado |
|----|-----------|---------------------|----------|--------|
| E-01 | FRONTEND | Actualizar `AppShell.kt` — Agregar tab/boton de acceso a "Mi Plan Semanal" y "Mis Recetas" desde la NavigationBar o menu lateral | Omar | ⬜ |
| E-02 | FRONTEND | Actualizar `RecipeListScreen` (Tab Inicio) — Mostrar recetas del plan semanal del usuario SI tiene plan configurado. Si no, mostrar catalogo general como ahora | Julian | ⬜ |
| E-03 | FRONTEND | Pasar `userId` a todos los ViewModels que lo necesiten — HomeViewModel, FavoritesViewModel, etc. Filtrar datos por usuario autenticado | Omar | ⬜ |
| E-04 | BACKEND | Implementar sincronizacion al login — Al autenticarse: descargar perfil + recetas + plan semanal + favoritos de Firestore → guardar en Room local | Omar | ⬜ |
| E-05 | BACKEND | Implementar sincronizacion al logout — Limpiar datos locales del usuario en Room (o marcarlos como cached). Preparar para otro usuario | Julian | ⬜ |
| E-06 | BACKEND | Implementar persistencia offline — Si no hay internet, trabajar con Room local. Cuando vuelva la conexion, sincronizar cambios pendientes con Firestore | Omar | ⬜ |
| E-07 | FRONTEND | Agregar boton "Cerrar Sesion" accesible desde el perfil o ajustes — Confirmar con AlertDialog antes de cerrar | Julian | ⬜ |
| E-08 | FRONTEND | Agregar indicador de sincronizacion — Mostrar icono/texto cuando los datos se estan sincronizando con la nube | Julian | ⬜ |
| E-09 | TESTING | Test completo end-to-end: Crear cuenta → Editar perfil → Crear 3 recetas → Armar plan semanal → Cerrar sesion → Login de nuevo → Verificar que TODO persiste | Ambos | ⬜ |
| E-10 | TESTING | Test multi-usuario: Login usuario A → crear recetas → logout → Login usuario B → verificar que NO ve recetas de A → crear sus propias → logout → Login A → verificar que sus datos siguen intactos | Ambos | ⬜ |

---

## NAVEGACION ACTUALIZADA

```
AuthScreen (Login/Registro/Google)
    ↓ (autenticado)
AuthWelcomeScreen
    ↓ (continuar)
AppShell (NavigationBar con 5-6 tabs)
    ├── Tab 0: Inicio (RecipeListScreen — muestra plan del usuario o catalogo)
    ├── Tab 1: Favoritos (FavoritesScreen — favoritos del usuario)
    ├── Tab 2: Mi Plan (MyWeeklyPlanScreen — plan semanal personalizado)
    ├── Tab 3: Generador (MenuGeneratorScreen — generar menu automatico)
    ├── Tab 4: Ajustes (SettingsScreen — preferencias)
    └── Perfil (ProfileScreen — accesible desde TopBar avatar)
         ├── EditProfileScreen
         ├── MyRecipesScreen
         │    ├── CreateRecipeScreen
         │    └── EditRecipeScreen
         └── Cerrar Sesion
```

---

## MODELO DE DATOS COMPLETO

### UserRecipeEntity (Room)
```kotlin
@Entity(tableName = "user_recipes")
data class UserRecipeEntity(
    @PrimaryKey val id: String,           // UUID generado
    val userId: String,                    // Firebase UID del dueno
    val title: String,
    val description: String,
    val imageRes: String,                  // drawable name o URI local
    val timeInMinutes: Int,
    val calories: Int,
    val difficulty: String,                // "Facil" | "Medio" | "Dificil"
    val category: String,                  // "Desayuno" | "Almuerzo" | "Cena"
    val proteinGrams: Int = 0,
    val carbsGrams: Int = 0,
    val fatGrams: Int = 0,
    val ingredientsJson: String = "[]",    // JSON array de ingredientes
    val stepsJson: String = "[]",          // JSON array de pasos
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false          // Para sync offline
)
```

### WeeklyPlanEntity (Room)
```kotlin
@Entity(
    tableName = "weekly_plan",
    primaryKeys = ["userId", "dayOfWeek", "mealType"]
)
data class WeeklyPlanEntity(
    val userId: String,                    // Firebase UID
    val dayOfWeek: String,                 // "Lunes" ... "Domingo"
    val mealType: String,                  // "Desayuno" | "Almuerzo" | "Cena"
    val recipeId: String,                  // ID de la receta asignada
    val recipeTitle: String,               // Cache del titulo para UI rapida
    val notes: String = "",                // Notas personales
    val updatedAt: Long = System.currentTimeMillis()
)
```

### UserProfileEntity (Room)
```kotlin
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val uid: String,           // Firebase UID
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val preferredDietsJson: String = "[]", // JSON array de dietas
    val defaultPortions: Int = 2,
    val createdAt: Long = System.currentTimeMillis()
)
```

---

## DISTRIBUCION SUGERIDA DE TRABAJO

### Omar (Backend + Arquitectura + Firebase):
- Fase A: Configuracion Firebase Console (A-01 a A-04, A-07)
- Fase B: Google Sign-In backend (B-05, B-06, B-07, B-09)
- Fase C: Entities Room + Repositories + Firestore Sync (C-01 a C-03, C-07, C-08, C-11, C-12)
- Fase D: Crear Receta + Mis Recetas + ViewModels (D-03, D-04, D-07, D-08, D-10)
- Fase E: Integracion + Sync + userId (E-01, E-03, E-04, E-06)

### Julian (Frontend + UI + Diseno):
- Fase A: Dependencias Gradle (A-05, A-06, A-08)
- Fase B: Rediseno AuthScreen + formulario + Google button (B-01 a B-04, B-08)
- Fase C: DAOs + Repositories (C-04 a C-06, C-09, C-10, C-13)
- Fase D: Perfil + Plan Semanal + Editar (D-01, D-02, D-05, D-06, D-09)
- Fase E: UI integracion + logout + sync indicator (E-02, E-05, E-07, E-08)

### Ambos (Testing):
- B-10, C-14, E-09, E-10

---

## NOTAS Y CONVENCIONES

| Simbolo | Significado |
|---------|-------------|
| ⬜ | Pendiente |
| ✅ | Completada |
| 🔄 | En progreso |
| ⚠️ | Bloqueada |

- Cada tarea = 1 rama Git → completar → push → merge con commit en espanol
- Cada tarea debe compilar sin errores ni warnings
- Usar Jetpack Compose para todas las pantallas nuevas
- Mantener patron MVVM + Clean Architecture (Presentation / Domain / Data)
- Firestore es la fuente de verdad en la nube, Room es cache local
- Todos los datos del usuario se filtran por `userId` (Firebase UID)
- Al cerrar sesion, los datos locales se limpian o se marcan como del usuario anterior
- Al iniciar sesion, se descargan los datos del usuario desde Firestore

---

## ORDEN DE EJECUCION RECOMENDADO

```
1. FASE A completa (sin Firebase real, nada funciona)
2. FASE B (autenticacion funcional con Google + formulario)
3. FASE C-01 a C-11 (BD local por usuario)
4. FASE D-01 a D-04 (Perfil + Crear Receta)
5. FASE D-05 a D-09 (Plan Semanal + Editar)
6. FASE C-12 a C-13 (Sincronizacion Firestore)
7. FASE E completa (integracion + tests)
```

---

**— Plan de Autenticacion y Personalizacion  ·  v1.0  ·  52 tareas  ·  Recipe Generator  ·  Abril 2026 —**
