package com.example.sistemasgc.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProveedoresScreen(
    onSearch: (String) -> Unit,
    onGoagregarProveedores: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter  // 👈 centra todo vertical y horizontalmente
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp) // espacio entre elementos
            ) {
                Text(
                    text = "Proveedores",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                // ---- Buscador ----
                var query by rememberSaveable { mutableStateOf("") }

                Text(
                    text = "Busqueda de Proveedores",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                OutlinedTextField(

                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Proveedor 1") },
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
                    onClick = onGoagregarProveedores,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text("Agregar Proveedores")
                }
            }
        }
    }
}

/* =================== PREVIEWS =================== */

@Preview(
    name = "Proveedores – Light",
    showBackground = true,
    showSystemUi = true
)
@Preview(
    name = "Proveedores – Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ProveedoresScreenPreview() {
    MaterialTheme {
        ProveedoresScreen(
            onSearch = {},
            onGoagregarProveedores = {}
        )
    }
}
