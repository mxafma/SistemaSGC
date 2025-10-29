package com.example.sistemasgc.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemasgc.ui.viewmodel.AuthViewModel
import androidx.compose.runtime.getValue

@Composable
fun AgregarProveedorScreenVM(
    vm: AuthViewModel = viewModel(), // O la forma en que inyectes tu ViewModel
    onProveedorAgregado: () -> Unit, // ← CAMBIÉ ESTO: navega de vuelta a proveedores
) {
    val state by vm.proveedor.collectAsStateWithLifecycle() // ← ESTO ESTÁ BIEN con StateFlow

    // Cuando el proveedor se agrega exitosamente, vuelve a la pantalla de proveedores
    LaunchedEffect(state.success) {
        if (state.success) {
            vm.clearProveedorResult()
            onProveedorAgregado() // ← Vuelve a proveedores
        }
    }

    AgregarProveedorScreen(
        name = state.name,
        rut = state.rut,
        phone = state.phone,
        email = state.email,
        direccion = state.direccion,

        nameError = state.nameError,
        rutError = state.rutError,
        phoneError = state.phoneError,
        emailError = state.emailError,
        direccionError = state.direccionError,

        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,

        onNameChange = vm::onProveedorNameChange,
        onRutChange = vm::onProveedorRutChange,
        onPhoneChange = vm::onProveedorPhoneChange,
        onEmailChange = vm::onProveedorEmailChange,
        onDireccionChange = vm::onProveedorDireccionChange,
        onSubmit = vm::submitProveedor
    )
}

@Composable
private fun AgregarProveedorScreen(
    name: String,
    rut: String,
    phone: String,
    email: String,
    direccion: String,

    nameError: String?,
    rutError: String?,
    phoneError: String?,
    emailError: String?,
    direccionError: String?,

    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,

    onNameChange: (String) -> Unit,
    onRutChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onDireccionChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter // ← Alineado arriba
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Agregar Proveedor",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(Modifier.height(12.dp))

                // Campo Nombre
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Nombre del Proveedor *") },
                    singleLine = true,
                    isError = nameError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
                if (nameError != null) {
                    Text(
                        nameError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Campo RUT
                OutlinedTextField(
                    value = rut,
                    onValueChange = onRutChange,
                    label = { Text("RUT *") },
                    singleLine = true,
                    isError = rutError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
                if (rutError != null) {
                    Text(
                        rutError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Campo Teléfono
                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Teléfono *") },
                    singleLine = true,
                    isError = phoneError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                if (phoneError != null) {
                    Text(
                        phoneError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Campo Email
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email *") },
                    singleLine = true,
                    isError = emailError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                if (emailError != null) {
                    Text(
                        emailError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Campo Dirección
                OutlinedTextField(
                    value = direccion,
                    onValueChange = onDireccionChange,
                    label = { Text("Dirección") },
                    singleLine = true,
                    isError = direccionError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
                if (direccionError != null) {
                    Text(
                        direccionError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Botón Agregar
                Button(
                    onClick = onSubmit,
                    enabled = canSubmit && !isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Agregando proveedor...")
                    } else {
                        Text("Agregar Proveedor")
                    }
                }

                if (errorMsg != null) {
                    Text(
                        errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(12.dp))

            }
        }
    }
}

@Preview(name = "Agregar Proveedor – Light", showBackground = true, showSystemUi = true)
@Preview(name = "Agregar Proveedor – Dark", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AgregarProveedorScreenPreview() {
    MaterialTheme {
        AgregarProveedorScreen(
            name = "Proveedor Ejemplo",
            rut = "12345678-9",
            phone = "912345678",
            email = "proveedor@ejemplo.cl",
            direccion = "Calle Principal 123",

            nameError = null,
            rutError = null,
            phoneError = null,
            emailError = null,
            direccionError = null,

            canSubmit = true,
            isSubmitting = false,
            errorMsg = null,

            onNameChange = {},
            onRutChange = {},
            onPhoneChange = {},
            onEmailChange = {},
            onDireccionChange = {},
            onSubmit = {}
        )
    }
}