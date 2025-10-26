package com.example.sistemasgc.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProductosScreen(
    onSearch: (String) -> Unit,
    onAddNewProduct: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center // 👈 centra todo vertical y horizontalmente
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp) // espacio entre elementos
            ) {
                Text(
                    text = "Productos",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                // ---- Buscador ----
                var query by rememberSaveable { mutableStateOf("") }

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Buscar producto") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { onSearch(query) },
                            enabled = query.isNotBlank()
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = "Buscar")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.85f)
                )

                // ---- Botón agregar ----
                Button(
                    onClick = onAddNewProduct,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text("Agregar Producto")
                }
            }
        }
    }
}

/* =================== PREVIEWS =================== */

@Preview(
    name = "Productos – Light",
    showBackground = true,
    showSystemUi = true
)
@Preview(
    name = "Productos – Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ProductosScreenPreview() {
    MaterialTheme {
        ProductosScreen(
            onSearch = {},
            onAddNewProduct = {}
        )
    }
}
