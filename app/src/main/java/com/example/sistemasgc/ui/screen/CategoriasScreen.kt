package com.example.sistemasgc.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CategoriaScreen(
    onAddCategory: (String, String, String) -> Unit,
    onCancel: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var id by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }

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

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .fillMaxWidth(0.45f)
                            .height(50.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    ) { Text("Cancelar") }

                    Button(
                        onClick = { onAddCategory(nombre, id, descripcion) },
                        modifier = Modifier
                            .fillMaxWidth(0.45f)
                            .height(50.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    ) { Text("Guardar") }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "AgregarCategoria – Light")
@Preview(showBackground = true, showSystemUi = true, name = "AgregarCategoria – Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CategoriaPreview() {
    MaterialTheme {
        CategoriaScreen(
            onAddCategory = { _, _, _ -> },
            onCancel = {}
        )
    }
}
