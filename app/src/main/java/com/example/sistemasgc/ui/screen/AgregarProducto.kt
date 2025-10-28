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
fun AgregarProductoScreen(
    onAddProduct: (String, String, String) -> Unit,
    onEditCategory: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var id by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("") }

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
                    text = "Agregar producto",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                // ---- Nombre ----
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- ID ----
                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- Categoría ----
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- Botón Crear/Editar categoría ----
                Button(
                    onClick = onEditCategory,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text("Crear/Editar Categoría")
                }

                // ---- Botón Agregar ----
                Button(
                    onClick = { onAddProduct(nombre, id, categoria) },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text("Agregar")
                }
            }
        }
    }
}

/* =================== PREVIEWS =================== */

@Preview(
    name = "AgregarProducto – Light",
    showBackground = true,
    showSystemUi = true
)
@Preview(
    name = "AgregarProducto – Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AgregarProductoPreview() {
    MaterialTheme {
        AgregarProductoScreen(
            onAddProduct = { _, _, _ -> },
            onEditCategory = {}
        )
    }
}
