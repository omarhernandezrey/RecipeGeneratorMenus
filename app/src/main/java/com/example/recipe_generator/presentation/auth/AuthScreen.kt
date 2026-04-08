@file:Suppress("SpellCheckingInspection")

package com.example.recipe_generator.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.R
import com.example.recipe_generator.presentation.theme.Error
import com.example.recipe_generator.presentation.theme.ErrorContainer
import com.example.recipe_generator.presentation.theme.OnError
import com.example.recipe_generator.presentation.theme.OnSecondaryContainer
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Outline
import com.example.recipe_generator.presentation.theme.OutlineVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.SecondaryContainer
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_5
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.theme.spacing_8
import androidx.compose.material3.Surface as MaterialSurface

/**
 * AuthScreen — Pantalla de autenticación con estilo editorial Material Design 3.
 *
 * B-01: Rediseño con logo + título + campos + botones estilizados M3.
 * B-02: Campo 'Nombre completo' visible solo en registro (isSignUp = true).
 * B-03: Validaciones inline — email formato, password mín. 6 chars, nombre no vacío.
 * B-04: Botón 'Iniciar sesión con Google' — OutlinedButton con ícono Google.
 * B-09: TextButton 'Recuperar contraseña' con SnackBar de confirmación.
 *
 * Capa: Presentation
 */
// Web Client ID del proyecto Firebase (google-services.json → oauth_client type 3)
private const val WEB_CLIENT_ID =
    "324878374213-3r1smbjh6qrnc0dp3vfoshav50qt03rf.apps.googleusercontent.com"

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Errores de validación inline
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }

    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    // B-07: Credential Manager para Google Sign-In
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentUser) {
        if (currentUser != null) onAuthSuccess()
    }

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            initialEmail = email,
            isLoading = isLoading,
            successMessage = successMessage,
            errorMessage = errorMessage,
            onDismiss = {
                showForgotPasswordDialog = false
                viewModel.clearError()
                viewModel.clearSuccess()
            },
            onSendReset = { resetEmail -> viewModel.sendPasswordReset(resetEmail) }
        )
    }

    // Función que lanza Google One Tap con Credential Manager (B-07)
    val launchGoogleSignIn: () -> Unit = {
        scope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(WEB_CLIENT_ID)
                    .setAutoSelectEnabled(false)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(result.credential.data)
                viewModel.signInWithGoogle(googleIdTokenCredential.idToken)
            } catch (e: GetCredentialException) {
                android.util.Log.e("GoogleSignIn", "GetCredentialException: ${e.message}", e)
                viewModel.setError("No se pudo iniciar sesión con Google: ${e.message}")
            } catch (e: Exception) {
                android.util.Log.e("GoogleSignIn", "Error inesperado: ${e.message}", e)
                viewModel.setError("Error inesperado al iniciar sesión con Google")
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F3FC))
    ) {
        val isCompactHeight = maxHeight < 760.dp
        val horizontalPadding = if (isCompactHeight) spacing_6 else spacing_8
        val sectionSpacing = if (isCompactHeight) spacing_3 else spacing_4

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = if (isCompactHeight) 16.dp else 40.dp,
                        bottom = spacing_4
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // ── Logo + Marca ────────────────────────────────────────
                LogoSection(isCompact = isCompactHeight)

                Spacer(modifier = Modifier.height(sectionSpacing))

                // ── Card de formulario ──────────────────────────────────
                MaterialSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = OutlineVariant.copy(alpha = 0.38f),
                            shape = RoundedCornerShape(32.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    color = SurfaceContainerLowest,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (isCompactHeight) 18.dp else 28.dp)
                            .padding(vertical = if (isCompactHeight) 16.dp else 28.dp),
                        verticalArrangement = Arrangement.spacedBy(if (isCompactHeight) spacing_2 else spacing_4)
                    ) {
                        // Título del formulario
                        Text(
                            text = if (isSignUp) "Crear tu cuenta" else "Bienvenido de nuevo",
                            style = MaterialTheme.typography.titleLarge,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )

                        // Campo Nombre (solo en registro — B-02)
                        AnimatedVisibility(
                            visible = isSignUp,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut()
                        ) {
                            Column {
                                OutlinedTextField(
                                    value = displayName,
                                    onValueChange = {
                                        displayName = it
                                        nameError = null
                                    },
                                    label = { Text("Nombre completo") },
                                    placeholder = { Text("Tu nombre y apellido") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Outlined.Person,
                                            contentDescription = null,
                                            tint = if (nameError != null) Error else Primary
                                        )
                                    },
                                    isError = nameError != null,
                                    supportingText = nameError?.let {
                                        { Text(it, color = Error) }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(18.dp),
                                    colors = authFieldColors()
                                )
                                Spacer(modifier = Modifier.height(spacing_3))
                            }
                        }

                        // Campo Email
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = null
                                viewModel.clearError()
                            },
                            label = { Text("Correo electrónico") },
                            placeholder = { Text("ejemplo@correo.com") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = null,
                                    tint = if (emailError != null) Error else Primary
                                )
                            },
                            isError = emailError != null,
                            supportingText = emailError?.let {
                                { Text(it, color = Error) }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp),
                            colors = authFieldColors()
                        )

                    // Campo Contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                            viewModel.clearError()
                        },
                        label = { Text("Contraseña") },
                        placeholder = { Text(if (isSignUp) "Mínimo 6 caracteres" else "Tu contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = if (passwordError != null) Error else Primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Outlined.VisibilityOff
                                    else
                                        Icons.Outlined.Visibility,
                                    contentDescription = if (passwordVisible)
                                        "Ocultar contraseña"
                                    else
                                        "Mostrar contraseña",
                                    tint = Outline
                                )
                            }
                        },
                        isError = passwordError != null,
                        supportingText = passwordError?.let {
                            { Text(it, color = Error) }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = authFieldColors()
                    )

                    // Error de Firebase (solo cuando el diálogo no está abierto)
                    AnimatedVisibility(visible = errorMessage != null && !showForgotPasswordDialog) {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing_2)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(rounded_md))
                                    .background(ErrorContainer)
                                    .padding(spacing_4)
                            ) {
                                Text(
                                    text = errorMessage ?: "",
                                    color = OnError,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            // Sugerencia de Google cuando falla el login
                            if (!isSignUp) {
                                Text(
                                    text = "¿Te registraste con Google? Usa el botón 'Continuar con Google'.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = spacing_2)
                                )
                            }
                        }
                    }

                    // Banner de éxito (recuperación enviada, etc.)
                    AnimatedVisibility(visible = successMessage != null && !showForgotPasswordDialog) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(rounded_md))
                                .background(SecondaryContainer)
                                .padding(spacing_4)
                        ) {
                            Text(
                                text = successMessage ?: "",
                                color = OnSecondaryContainer,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing_2))

                    // Botón principal
                    Button(
                        onClick = {
                            if (validateForm(
                                    isSignUp, email, password, displayName,
                                    onEmailError = { emailError = it },
                                    onPasswordError = { passwordError = it },
                                    onNameError = { nameError = it }
                                )
                            ) {
                                if (isSignUp) {
                                    viewModel.signUp(email, password, displayName)
                                } else {
                                    viewModel.signIn(email, password)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(22.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Primary,
                                            PrimaryContainer
                                        )
                                    ),
                                    shape = RoundedCornerShape(22.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    text = if (isSignUp) "Crear cuenta" else "Iniciar sesión",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                        AnimatedVisibility(visible = !isSignUp) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TextButton(
                                    onClick = {
                                        viewModel.clearError()
                                        viewModel.clearSuccess()
                                        showForgotPasswordDialog = true
                                    }
                                ) {
                                    Text(
                                        text = "¿Olvidaste tu contraseña?",
                                        color = Primary,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = if (isCompactHeight) 2.dp else spacing_3),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing_4)
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = OutlineVariant
                            )
                            Text(
                                text = "o continúa con",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = OutlineVariant
                            )
                        }

                        // ── Botón Google (B-04) ────────────────────────
                        androidx.compose.material3.OutlinedButton(
                            onClick = launchGoogleSignIn,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(22.dp),
                            enabled = !isLoading,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, OutlineVariant.copy(alpha = 0.65f)
                            ),
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White.copy(alpha = 0.88f)
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing_4)
                            ) {
                                GoogleIcon()
                                Text(
                                    text = "Continuar con Google",
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnSurface,
                                    fontSize = 14.sp,
                                    letterSpacing = 0.sp
                                )
                            }
                        }

                        // ── Toggle Login / Registro ─────────────────────
                        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.30f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isSignUp) "¿Ya tienes cuenta? " else "¿No tienes cuenta? ",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(
                                onClick = {
                                    isSignUp = !isSignUp
                                    emailError = null
                                    passwordError = null
                                    nameError = null
                                    viewModel.clearError()
                                    viewModel.clearSuccess()
                                },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = spacing_2
                                )
                            ) {
                                Text(
                                    text = if (isSignUp) "Inicia sesión" else "Regístrate",
                                    color = Primary,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

// ── Sección de logo y marca ─────────────────────────────────────────────
@Composable
private fun LogoSection(isCompact: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (isCompact) spacing_4 else spacing_5)
    ) {
        Box(
            modifier = Modifier
                .size(if (isCompact) 62.dp else 74.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PrimaryContainer,
                            Primary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🍽",
                fontSize = if (isCompact) 28.sp else 32.sp,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Recipe Generator",
                style = if (isCompact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
                color = OnSurface,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
        }
    }
}

// ── Ícono G de Google (colores oficiales) ──────────────────────────────
@Composable
private fun GoogleIcon() {
    Icon(
        painter = painterResource(id = R.drawable.google_g_logo),
        contentDescription = null,
        modifier = Modifier.size(20.dp),
        tint = Color.Unspecified
    )
}

// ── Colores compartidos para campos ───────────────────────────────────
@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Primary,
    unfocusedBorderColor = OutlineVariant.copy(alpha = 0.9f),
    focusedContainerColor = Color.White.copy(alpha = 0.98f),
    unfocusedContainerColor = Color(0xFFFCFBFE),
    disabledContainerColor = Color(0xFFFCFBFE),
    errorContainerColor = Color(0xFFFFFBFB),
    focusedTextColor = OnSurface,
    unfocusedTextColor = OnSurface,
    focusedLeadingIconColor = Primary,
    unfocusedLeadingIconColor = Primary.copy(alpha = 0.88f),
    focusedTrailingIconColor = Outline,
    unfocusedTrailingIconColor = Outline,
    focusedLabelColor = Primary,
    unfocusedLabelColor = OnSurfaceVariant,
    focusedPlaceholderColor = OnSurfaceVariant.copy(alpha = 0.65f),
    unfocusedPlaceholderColor = OnSurfaceVariant.copy(alpha = 0.65f),
    cursorColor = Primary
)

// ── Diálogo de recuperación de contraseña ─────────────────────────────
@Composable
private fun ForgotPasswordDialog(
    initialEmail: String,
    isLoading: Boolean,
    successMessage: String?,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSendReset: (String) -> Unit
) {
    var email by remember { mutableStateOf(initialEmail) }
    var emailError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainerLowest,
        shape = RoundedCornerShape(rounded_lg),
        title = {
            Text(
                text = "Recuperar contraseña",
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing_4)) {
                if (successMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(rounded_md))
                            .background(SecondaryContainer)
                            .padding(spacing_4)
                    ) {
                        Text(
                            text = successMessage,
                            color = OnSecondaryContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Text(
                        text = "Ingresa tu correo y te enviaremos un enlace para restablecer tu contraseña.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        label = { Text("Correo electrónico") },
                        placeholder = { Text("ejemplo@correo.com") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = if (emailError != null) Error else Primary
                            )
                        },
                        isError = emailError != null,
                        supportingText = emailError?.let { { Text(it, color = Error) } },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(rounded_md),
                        colors = authFieldColors(),
                        enabled = !isLoading
                    )
                    AnimatedVisibility(visible = errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(rounded_md))
                                .background(ErrorContainer)
                                .padding(spacing_4)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = OnError,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (successMessage != null) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(rounded_full)
                ) {
                    Text("Cerrar", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Button(
                    onClick = {
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailError = "Ingresa un correo electrónico válido"
                        } else {
                            onSendReset(email)
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(rounded_full)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = androidx.compose.ui.graphics.Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Enviar enlace", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        dismissButton = {
            if (successMessage == null) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = Primary)
                }
            }
        }
    )
}

// ── Validación del formulario (B-03) ──────────────────────────────────
private fun validateForm(
    isSignUp: Boolean,
    email: String,
    password: String,
    displayName: String,
    onEmailError: (String) -> Unit,
    onPasswordError: (String) -> Unit,
    onNameError: (String) -> Unit
): Boolean {
    val invalidName = isSignUp && displayName.isBlank()
    if (invalidName) {
        onNameError("El nombre no puede estar vacío")
    }

    val invalidEmail = !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    if (invalidEmail) {
        onEmailError("Ingresa un correo electrónico válido")
    }

    val invalidPassword = password.length < 6
    if (invalidPassword) {
        onPasswordError("La contraseña debe tener al menos 6 caracteres")
    }

    return !(invalidName || invalidEmail || invalidPassword)
}
