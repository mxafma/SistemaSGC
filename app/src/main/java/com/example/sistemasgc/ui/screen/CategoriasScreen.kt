package com.example.sistemasgc.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemasgc.ui.viewmodel.AuthViewModel

@Composable
fun CategoriaScreenVM(
    viewModel: AuthViewModel = viewModel(),
    onCancel: () -> Unit,
    onSuccess: () -> Unit  // ← Para navegar a productos después de guardar
) {
    val categoriaState by viewModel.categoria.collectAsStateWithLifecycle()

    // Manejar el estado de éxito - ESTO ES CLAVE PARA LA NAVEGACIÓN
    LaunchedEffect(categoriaState.success) {
        if (categoriaState.success) {
            // Limpiar el estado y navegar
            viewModel.clearCategoriaResult()
            onSuccess() // ← Esto navega a productos
        }
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Agregar categoría",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                // Mostrar loading si está procesando
                if (categoriaState.isSubmitting) {
                    CircularProgressIndicator()
                }

                // Campo Nombre
                OutlinedTextField(
                    value = categoriaState.nombre,
                    onValueChange = viewModel::onCategoriaNombreChange,
                    label = { Text("Nombre* (≥ 3 caracteres)") },
                    singleLine = true,
                    isError = categoriaState.nombreError != null,
                    supportingText = {
                        categoriaState.nombreError?.let { Text(it) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !categoriaState.isSubmitting
                )

                // Campo Descripción
                OutlinedTextField(
                    value = categoriaState.descripcion,
                    onValueChange = viewModel::onCategoriaDescripcionChange,
                    label = { Text("Descripción (opcional)") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !categoriaState.isSubmitting
                )

                // Botones
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            if (!categoriaState.isSubmitting) {
                                viewModel.clearCategoriaResult()
                                onCancel()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        enabled = !categoriaState.isSubmitting
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = { viewModel.submitCategoria() },
                        enabled = categoriaState.canSubmit && !categoriaState.isSubmitting,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        if (categoriaState.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Guardando...")
                        } else {
                            Text("Guardar")
                        }
                    }
                }

                // Mostrar error si existe
                if (categoriaState.errorMsg != null) {
                    Text(
                        text = categoriaState.errorMsg!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "AgregarCategoria – Light"
)
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "AgregarCategoria – Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CategoriaScreenVMPreview() {
    MaterialTheme {
        CategoriaScreenVM(
            onCancel = {},
            onSuccess = {}
        )
    }
}