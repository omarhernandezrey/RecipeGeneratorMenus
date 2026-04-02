# REQUERIMIENTO INMEDIATO: Setup Firebase Console

## ⚠️ ACCIÓN MANUAL NECESARIA

He integrado Firebase Auth + Firestore en el código. Ahora necesitas:

### PASO 1: Crear Proyecto Firebase
1. Ve a https://console.firebase.google.com
2. Haz click en "Crear un nuevo proyecto"
3. Nombre: `Recipe Generator App`
4. Acepta términos y continúa

### PASO 2: Agregar Aplicación Android
1. En Firebase Console, haz click en el ícono de Android
2. **Package name**: `com.example.recipe_generator`
3. **SHA-1**: (dejar en blanco por ahora, es opcional)
4. Descarga el archivo `google-services.json`

### PASO 3: Colocar google-services.json en el proyecto
1. Ve a tu proyecto local en: `C:\Users\herna\OneDrive\Documents\RecipeGeneratorMenus\app\`
2. Copia el archivo `google-services.json` descargado en esa carpeta

### PASO 4: Habilitar Email/Password Authentication
1. En Firebase Console, ve a: **Authentication** → **Sign-in method**
2. Haz click en **Email/Password**
3. Habilita ambas opciones (Email/Password y Email link)
4. Guarda

### PASO 5: Crear Firestore Database
1. En Firebase Console, ve a: **Firestore Database**
2. Haz click en **Create Database**
3. Selecciona región: **Sudamérica (Porque estamos en Colombia)**
4. Modo: **Iniciar en modo de prueba** (para desarrollo)
5. Crea la base de datos

### RESULTADO ESPERADO
- ✅ Archivo `google-services.json` en `app/`
- ✅ Firebase Auth Email/Password habilitado
- ✅ Firestore Database activo
- ✅ App compila sin errores
- ✅ Pantalla de Login aparece al iniciar

---

## LO QUE HICE YA

✅ Agregué dependencias Firebase al build.gradle.kts
✅ Creé AuthRepository (interfaz + implementación FirebaseAuthRepository)
✅ Creé AuthViewModel (manejo de estado de login/registro)
✅ Creé AuthScreen en Compose (UI de Login/Registro)
✅ Actualicé AppContainer para inyectar FirebaseAuth

## PRÓXIMO PASO DESPUÉS DE FIREBASE SETUP

1. Actualiza MainActivity para mostrar Login si no está autenticado
2. Crea Firestore collections y reglas de seguridad
3. Migramos RecipeRepository para leer de Firestore en lugar de Room
4. Agregaremos funcionalidad CRUD (crear, editar, eliminar recetas)

---

**Documentación:** https://firebase.google.com/docs/auth/android/start


