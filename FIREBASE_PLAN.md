# PLAN DE INTEGRACIÓN FIREBASE
## Recipe Generator App — Autenticación + Firestore (Opción B)

### Estado Actual
- ✅ Proyecto buildea correctamente
- ✅ Room Database configurado con 21 recetas hardcodeadas
- ⬜ **Sin autenticación de usuarios**
- ⬜ **Sin CRUD dinámico**
- ⬜ **Datos estáticos en Room local**

---

## CAMBIO ARQUITECTÓNICO

### De: Room Local (100% local)
```
User 1 → App → Room Local (21 recetas compartidas)
User 2 → App → Room Local (21 recetas compartidas)
```

### A: Firebase Auth + Firestore (Opción B)
```
User 1 → App → Firebase Auth ← Login/Registro
         ↓
       Firestore Cloud
       ├── users/{uid}/
       │   ├── name, email, avatar
       │   └── preferences (dietas, etc)
       ├── recipes/{uid}/
       │   ├── recipe1 (usuario creó)
       │   ├── recipe2 (usuario creó)
       │   └── ...
       ├── sharedRecipes/ (recetas base: 21)
       └── favorites/{uid}/ (favoritos de cada user)

User 2 → App → Firebase Auth ← Login/Registro
         ↓
       Firestore Cloud (datos separados por uid)
```

---

## FASES DE IMPLEMENTACIÓN

### FASE 1: Setup Firebase + Dependencias
1. ✅ Crear proyecto Firebase en Firebase Console
2. ✅ Descargar `google-services.json` 
3. ✅ Agregar dependencias Firebase al build.gradle.kts
4. ✅ Sincronizar Gradle

### FASE 2: Autenticación (Firebase Auth)
1. Crear pantalla de Login/Registro
2. Implementar FirebaseAuth.createUserWithEmailAndPassword()
3. Implementar FirebaseAuth.signInWithEmailAndPassword()
4. Guardar estado de sesión (AuthRepository)
5. Proteger navegación: si no está logueado → Login

### FASE 3: CRUD de Recetas (Firestore)
1. Migrar 21 recetas base → `sharedRecipes/` (una sola vez)
2. Crear RecipeRepository que lea de Firestore en lugar de Room
3. Implementar CreateRecipe (usuario crea receta propia)
4. Implementar EditRecipe (usuario edita su receta)
5. Implementar DeleteRecipe (usuario elimina su receta)
6. Implementar GetMyRecipes (solo del usuario logueado)
7. Implementar GetSharedRecipes (recetas base + favoritas de otros)

### FASE 4: Favoritos en Firestore
1. Migrar favoritos de DataStore → Firestore
2. Actualizar ToggleFavorite para usar Firestore
3. Query: `favorites/{uid}/` para mostrar solo favoritos del user

### FASE 5: UI Actualizado
1. Agregar pantalla Login/Registro
2. Agregar botón "Mi Perfil" → mostrar datos del usuario
3. Agregar formulario "Crear Receta"
4. Agregar botones Editar/Eliminar en RecipeDetail
5. Logout desde Settings/Perfil

---

## IMPACTO EN LA ARQUITECTURA

### Antes (Room)
```
Presentation → ViewModel → Domain (UseCase) → Data (RecipeRepository) → Room
```

### Después (Firebase)
```
Presentation → ViewModel → Domain (UseCase) → Data (RecipeRepository) → Firestore
                                                                       ↓
                                                                   FirebaseAuth
                                                                   (Session)
```

### Cambios en capas:
- **Presentation**: Nuevas pantallas Login + Crear/Editar Receta
- **Domain**: Mismos UseCases (la lógica no cambia)
- **Data**: RecipeRepository ahora habla con Firestore en lugar de Room
- **Data**: Nueva AuthRepository para gestionar autenticación

---

## TAREAS DETALLADAS

### TAREA 1: Setup Firebase Console
- [ ] Ir a https://console.firebase.google.com
- [ ] Crear proyecto "RecipeGenerator"
- [ ] Agregar app Android
- [ ] Descargar `google-services.json`
- [ ] Copiarlo a `app/` carpeta del proyecto
- [ ] En Firebase Console: habilitar Email/Password Auth

### TAREA 2: Agregar Dependencias
- [ ] Agregar `com.google.gms:google-services` al build.gradle.kts raíz
- [ ] Agregar `firebase-auth-ktx` a build.gradle.kts app
- [ ] Agregar `firebase-firestore-ktx` a build.gradle.kts app
- [ ] Sync Gradle

### TAREA 3: Crear AuthRepository
- [ ] Interfaz: signUp(), signIn(), logout(), getCurrentUser()
- [ ] Implementación con FirebaseAuth
- [ ] Manejar errors: usuario duplicado, contraseña débil, etc

### TAREA 4: Crear Login/Registro Screen
- [ ] Fragment o Activity con Compose/XML
- [ ] Email + Password + Botones SignUp/SignIn
- [ ] Llamar AuthRepository al presionar
- [ ] Navegar a Home si login exitoso
- [ ] Mostrar error si falla

### TAREA 5: Migrar RecipeRepository a Firestore
- [ ] Crear colecciones en Firestore: `sharedRecipes/`, `recipes/`, `favorites/`
- [ ] Reemplazar RecipeRepositoryImpl para leer de Firestore
- [ ] Usar `collectionReference.document(uid).collection("userRecipes")` para recetas del user
- [ ] Retornar Flow<> observable desde Firestore

### TAREA 6: Crear/Editar/Eliminar Recetas
- [ ] Pantalla formulario: título, descripción, ingredientes, pasos
- [ ] Botón "Guardar" → Firestore.collection("recipes").document(uid).collection("myRecipes").add()
- [ ] Botón "Editar" → .update() o reemplazar documento
- [ ] Botón "Eliminar" → .delete()

### TAREA 7: Migrar Favoritos a Firestore
- [ ] Cambiar DataStore → Firestore `favorites/{uid}/`
- [ ] ToggleFavorite usa Firestore.collection("favorites").document(uid)

### TAREA 8: Proteger Navegación
- [ ] Si user no logueado: mostrar Login
- [ ] Si user logueado: mostrar Home normal
- [ ] Persistir sesión: AuthRepository.getCurrentUser() al iniciar app

---

## DEPENDENCIAS A AGREGAR

```kotlin
// build.gradle.kts (raíz)
plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
}

// build.gradle.kts (app)
dependencies {
    // Firebase Auth
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    
    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")
    
    // Google Play Services (para Google Sign-In opcional)
    implementation("com.google.android.gms:play-services-auth:20.7.0")
}

plugins {
    id("com.google.gms.google-services")
}
```

---

## FIRESTORE SCHEMA

```
firestore-root/
├── users/
│   └── {uid}/
│       ├── email: string
│       ├── displayName: string
│       ├── avatar: string (URL storage)
│       ├── createdAt: timestamp
│       └── preferences: {
│           ├── dieta: array ["vegetarian", "vegan"]
│           ├── idioma: "es"
│           └── tema: "light"
│       }
├── sharedRecipes/
│   ├── {recipeId} (21 recetas base)
│   │   ├── title: string
│   │   ├── description: string
│   │   ├── ingredients: array
│   │   ├── steps: array
│   │   └── metadata (createdBy: "SYSTEM", etc)
├── recipes/
│   └── {uid}/
│       ├── myRecipes/ (subcol)
│       │   ├── {recipeId}
│       │   │   ├── title, description, etc
│       │   │   └── createdAt: timestamp
├── favorites/
│   └── {uid}/
│       ├── recipeIds: array<string>
│       ├── updatedAt: timestamp
```

---

## ORDEN DE IMPLEMENTACIÓN RECOMENDADO

1. **Setup** (1h) — Firebase console + dependencias
2. **AuthRepository** (2h) — Login/registro básico
3. **Login Screen** (1.5h) — UI para autenticación
4. **Proteger navegación** (0.5h) — Show Login si no autenticado
5. **RecipeRepository → Firestore** (2h) — Migrar lectura de Room a Firestore
6. **CreateRecipe** (1h) — Pantalla + guardar a Firestore
7. **EditRecipe** (1h) — Actualizar documento existente
8. **DeleteRecipe** (0.5h) — Eliminar documento
9. **Favoritos → Firestore** (1h) — Migrar DataStore a Firestore
10. **Testing + Polish** (1h) — Validar flujo completo

**Total estimado: ~11-12 horas de desarrollo**

---

## PRÓXIMOS PASOS

¿Deseas que comenzar inmediatamente?

**Confirma:**
- [ ] Crear Firebase project
- [ ] Descargar `google-services.json`
- [ ] Yo integraré las dependencias
- [ ] Yo crearé AuthRepository
- [ ] Yo crearé Login Screen
- [ ] Continuamos con Firestore integration

**O prefieres:**
- [ ] Que hagas Firebase setup primero y me pases google-services.json
- [ ] Que yo continúe sin esperar

---

**Fecha**: Abril 2026
**Estado**: Plan preparado, esperando confirmación

