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
    onAddNewProduct: () -> Unit,
    productosExistentes: List<String> = emptyList()
) {
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Si no hay productos, se muestra lista vacía
    val filteredProducts = remember(query, productosExistentes) {
        if (query.isBlank()) productosExistentes
        else productosExistentes.filter { it.contains(query, ignoreCase = true) }
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Productos",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                // ---- Buscador con lista desplegable ----
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            expanded = true // siempre se abre cuando escribes
                        },
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
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = expanded && filteredProducts.isNotEmpty(),
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        filteredProducts.forEach { producto ->
                            DropdownMenuItem(
                                text = { Text(producto) },
                                onClick = {
                                    query = producto
                                    expanded = false
                                    onSearch(producto)
                                }
                            )
                        }

                        // Si no hay coincidencias
                        if (filteredProducts.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("(sin resultados)") },
                                onClick = { expanded = false }
                            )
                        }
                    }
                }

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
            onAddNewProduct = {},
            productosExistentes = listOf("Manzanas", "Peras", "Tomates", "Cebollas")
        )
    }
}
