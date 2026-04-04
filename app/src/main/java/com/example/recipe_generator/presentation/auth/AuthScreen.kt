package com.example.recipe_generator.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_generator.presentation.theme.Error
import com.example.recipe_generator.presentation.theme.ErrorContainer
import com.example.recipe_generator.presentation.theme.OnError
import com.example.recipe_generator.presentation.theme.OnSurface
import com.example.recipe_generator.presentation.theme.OnSurfaceVariant
import com.example.recipe_generator.presentation.theme.Outline
import com.example.recipe_generator.presentation.theme.OutlineVariant
import com.example.recipe_generator.presentation.theme.Primary
import com.example.recipe_generator.presentation.theme.PrimaryContainer
import com.example.recipe_generator.presentation.theme.Secondary
import com.example.recipe_generator.presentation.theme.Surface
import com.example.recipe_generator.presentation.theme.SurfaceContainerLowest
import com.example.recipe_generator.presentation.theme.Tertiary
import com.example.recipe_generator.presentation.theme.rounded_full
import com.example.recipe_generator.presentation.theme.rounded_lg
import com.example.recipe_generator.presentation.theme.rounded_md
import com.example.recipe_generator.presentation.theme.spacing_10
import com.example.recipe_generator.presentation.theme.spacing_12
import com.example.recipe_generator.presentation.theme.spacing_2
import com.example.recipe_generator.presentation.theme.spacing_3
import com.example.recipe_generator.presentation.theme.spacing_4
import com.example.recipe_generator.presentation.theme.spacing_6
import com.example.recipe_generator.presentation.theme.spacing_8

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
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit,
    onGoogleSignIn: () -> Unit = {}
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

    LaunchedEffect(currentUser) {
        if (currentUser != null) onAuthSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Surface,
                        SurfaceContainerLowest
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing_8),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(spacing_12))

            // ── Logo + Marca ────────────────────────────────────────────
            LogoSection()

            Spacer(modifier = Modifier.height(spacing_10))

            // ── Card de formulario ──────────────────────────────────────
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(rounded_lg),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = SurfaceContainerLowest
                ),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing_8),
                    verticalArrangement = Arrangement.spacedBy(spacing_4)
                ) {
                    // Título del formulario
                    Text(
                        text = if (isSignUp) "Crear cuenta" else "Bienvenido",
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSignUp)
                            "Regístrate para personalizar tu menú semanal"
                        else
                            "Inicia sesión para continuar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(spacing_2))

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
                                        imageVector = Icons.Outlined.Email,
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
                                shape = RoundedCornerShape(rounded_md),
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
                        shape = RoundedCornerShape(rounded_md),
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
                        shape = RoundedCornerShape(rounded_md),
                        colors = authFieldColors()
                    )

                    // Error de Firebase
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
                            .height(52.dp),
                        shape = RoundedCornerShape(rounded_full),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
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
            }

            Spacer(modifier = Modifier.height(spacing_4))

            // ── Recuperar contraseña (B-09) ─────────────────────────────
            AnimatedVisibility(visible = !isSignUp) {
                TextButton(
                    onClick = {
                        if (email.isNotBlank()) {
                            viewModel.sendPasswordReset(email)
                        } else {
                            emailError = "Ingresa tu correo para recuperar la contraseña"
                        }
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

            // ── Separador ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing_4),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing_4)
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = OutlineVariant
                )
                Text(
                    text = "o",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = OutlineVariant
                )
            }

            // ── Botón Google (B-04) ────────────────────────────────────
            androidx.compose.material3.OutlinedButton(
                onClick = onGoogleSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(rounded_full),
                enabled = !isLoading,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, OutlineVariant
                ),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    containerColor = SurfaceContainerLowest
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing_4)
                ) {
                    // Ícono G de Google con colores oficiales
                    GoogleIcon()
                    Text(
                        text = "Continuar con Google",
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing_6))

            // ── Toggle Login / Registro ─────────────────────────────────
            Row(
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

            Spacer(modifier = Modifier.height(spacing_10))
        }
    }
}

// ── Sección de logo y marca ─────────────────────────────────────────────
@Composable
private fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing_4)
    ) {
        // Logo circular con gradiente
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(PrimaryContainer, Primary)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🍽",
                fontSize = 36.sp,
                textAlign = TextAlign.Center
            )
        }

        // Nombre de la app
        Text(
            text = "Recipe Generator",
            style = MaterialTheme.typography.headlineMedium,
            color = Primary,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
        )

        // Subtítulo editorial
        Text(
            text = "Generador de Menús Semanales",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // Chips de categoría editorial
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing_3)
        ) {
            CategoryChip(text = "⭐ Compose", color = Primary)
            CategoryChip(text = "🔥 Firebase", color = Tertiary)
            CategoryChip(text = "🥗 Recetas", color = Secondary)
        }
    }
}

@Composable
private fun CategoryChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(rounded_full))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = spacing_4, vertical = spacing_2)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp
        )
    }
}

// ── Ícono G de Google (colores oficiales) ──────────────────────────────
@Composable
private fun GoogleIcon() {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "G",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4285F4),
            textAlign = TextAlign.Center
        )
    }
}

// ── Colores compartidos para campos ───────────────────────────────────
@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Primary,
    unfocusedBorderColor = OutlineVariant,
    focusedLabelColor = Primary,
    cursorColor = Primary
)

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
    var valid = true

    if (isSignUp && displayName.isBlank()) {
        onNameError("El nombre no puede estar vacío")
        valid = false
    }

    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onEmailError("Ingresa un correo electrónico válido")
        valid = false
    }

    if (password.length < 6) {
        onPasswordError("La contraseña debe tener al menos 6 caracteres")
        valid = false
    }

    return valid
}
