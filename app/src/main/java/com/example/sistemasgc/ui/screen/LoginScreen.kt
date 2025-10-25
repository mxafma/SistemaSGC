package com.example.sistemasgc.ui.screen
import android.content.res.Configuration

import androidx.compose.foundation.background                 // Fondo
import androidx.compose.foundation.layout.*                   // Box/Column/Row/Spacer
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons                  // Íconos Material
import androidx.compose.material.icons.filled.Visibility      // Ícono mostrar contraseña
import androidx.compose.material.icons.filled.VisibilityOff   // Ícono ocultar contraseña
import androidx.compose.material3.*                           // Material 3
import androidx.compose.runtime.*                             // remember y Composable
import androidx.compose.ui.Alignment                          // Alineaciones
import androidx.compose.ui.Modifier                           // Modificador
import androidx.compose.ui.text.input.*                       // KeyboardOptions/Types/Transformations
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp                            // DPs
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Observa StateFlow con lifecycle
import com.example.sistemasgc.ui.viewmodel.AuthViewModel         // Nuestro ViewModel


//1 Lo primero que creamos en el archivo
@Composable                                                  // Pantalla Login conectada al VM
fun LoginScreenVm(
    vm: AuthViewModel,                            // MOD: recibimos el VM desde NavGraph
    onLoginOkNavigateHome: () -> Unit,                       // Navega a Home cuando el login es exitoso
    onGoRegister: () -> Unit                                 // Navega a Registro
) {

    val state by vm.login.collectAsStateWithLifecycle()      // Observa el StateFlow en tiempo real

    if (state.success) {                                     // Si login fue exitoso…
        vm.clearLoginResult()                                // Limpia banderas
        onLoginOkNavigateHome()                              // Navega a Home
    }

    LoginScreen(                                             // Delegamos a UI presentacional
        email = state.email,                                 // Valor de email
        pass = state.pass,                                   // Valor de password
        emailError = state.emailError,                       // Error de email
        passError = state.passError,                         // (Opcional) error de pass en login
        canSubmit = state.canSubmit,                         // Habilitar botón
        isSubmitting = state.isSubmitting,                   // Loading
        errorMsg = state.errorMsg,                           // Error global
        onEmailChange = vm::onLoginEmailChange,              // Handler email
        onPassChange = vm::onLoginPassChange,                // Handler pass
        onSubmit = vm::submitLogin,                          // Acción enviar
        onGoRegister = onGoRegister                          // Ir a Registro
    )
}


//2 modificamos la funcion principal haciendo private y agregando variable y elementos dle fiormulario
@Composable // Pantalla Login (solo navegación, sin formularios)
private fun LoginScreen(
    //3 Modificamos estos parametros
    email: String,                                           // Campo email
    pass: String,                                            // Campo contraseña
    emailError: String?,                                     // Error de email
    passError: String?,                                      // Error de password (opcional)
    canSubmit: Boolean,                                      // Habilitar botón
    isSubmitting: Boolean,                                   // Flag loading
    errorMsg: String?,                                       // Error global (credenciales)
    onEmailChange: (String) -> Unit,                         // Handler cambio email
    onPassChange: (String) -> Unit,                          // Handler cambio password
    onSubmit: () -> Unit,                                    // Acción enviar
    onGoRegister: () -> Unit                                 // Acción ir a registro
) {
    val bg = MaterialTheme.colorScheme.background // Fondo distinto para contraste
    //4 Agregamos la siguiente linea
    var showPass by remember { mutableStateOf(false) }        // Estado local para mostrar/ocultar contraseña

    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo
            .background(bg) // Fondo
            .padding(16.dp), // Margen
        contentAlignment = Alignment.Center // Centro
    ) {
        Column(
            //5 Anexamos el modificador
            modifier = Modifier.fillMaxWidth(),              // Ancho completo
            horizontalAlignment = Alignment.CenterHorizontally // Centrado horizontal
        ) {
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineSmall, // Título
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))

// ---------- EMAIL ----------

            OutlinedTextField(
                value = email,                               // Valor actual
                onValueChange = onEmailChange,               // Notifica VM (valida email)
                label = { Text("Email") },                   // Etiqueta
                singleLine = true,                           // Una línea
                isError = emailError != null,                // Marca error si corresponde
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email        // Teclado de email
                ),
                modifier = Modifier.fillMaxWidth()           // Ancho completo
            )
            if (emailError != null) {                        // Muestra mensaje si hay error
                Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))                    // Espacio

            // ---------- PASSWORD (oculta por defecto) ----------
            OutlinedTextField(
                value = pass,                                // Valor actual
                onValueChange = onPassChange,                // Notifica VM
                label = { Text("Contraseña") },              // Etiqueta
                singleLine = true,                           // Una línea
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(), // Toggle mostrar/ocultar
                trailingIcon = {                             // Ícono para alternar visibilidad
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                isError = passError != null,                 // (Opcional) marcar error
                modifier = Modifier.fillMaxWidth()           // Ancho completo
            )
            if (passError != null) {                         // (Opcional) mostrar error
                Text(passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))                   // Espacio

            // ---------- BOTÓN ENTRAR ----------
            Button(
                onClick = onSubmit,                          // Envía login
                enabled = canSubmit && !isSubmitting,        // Solo si válido y no cargando
                modifier = Modifier.fillMaxWidth()           // Ancho completo
            ) {
                if (isSubmitting) {                          // UI de carga
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Validando...")
                } else {
                    Text("Entrar")
                }
            }

            if (errorMsg != null) {                          // Error global (credenciales)
                Spacer(Modifier.height(8.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))                   // Espacio

            // ---------- BOTÓN IR A REGISTRO ----------
            OutlinedButton(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()) {
                Text("Crear cuenta")
            }
            //fin modificacion de formulario
        }
    }
}






@Preview(
    name = "Login – Light",
    showBackground = true,
    showSystemUi = true
)
@Preview(
    name = "Login – Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun LoginScreenPreview() {
    // Si tienes tu propio tema, reemplaza por: SistemaSGCTheme { ... }
    MaterialTheme {
        LoginScreen(
            email = "user@example.com",
            pass = "password",
            emailError = null,
            passError = null,
            canSubmit = true,
            isSubmitting = false,
            errorMsg = null,
            onEmailChange = {},
            onPassChange = {},
            onSubmit = {},
            onGoRegister = {}
        )
    }
}