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
fun CategoriasScreen(
    onAddCategory: (id: String, nombre: String, descripcion: String) -> Unit
) {
    var id by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
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
                    text = "Categorías",
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

                // ---- Descripción (opcional) ----
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    singleLine = false,
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- Botón Agregar ----
                Button(
                    onClick = { onAddCategory(id.trim(), nombre.trim(), descripcion.trim()) },
                    enabled = nombre.isNotBlank(),
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
    name = "Categorías – Light",
    showBackground = true,
    showSystemUi = true
)
@Preview(
    name = "Categorías – Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CategoriasScreenPreview() {
    MaterialTheme {
        CategoriasScreen(
            onAddCategory = { _, _, _ -> }
        )
    }
}
