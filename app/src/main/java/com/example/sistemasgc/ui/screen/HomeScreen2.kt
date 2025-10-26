package com.example.sistemasgc.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoProveedores: () -> Unit,
    onGoProductos: () -> Unit,
    onGoCompras: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Inicio",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // Accesos básicos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                }

                Spacer(Modifier.height(16.dp))

                // Accesos a páginas del negocio
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onGoProveedores,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Proveedores") }

                    Button(
                        onClick = onGoProductos,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Productos") }

                    Button(
                        onClick = onGoCompras,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Compras") }
                }
            }
        }
    }
}

// ---------- PREVIEWS ----------

@Preview(
    name = "Home – Light",
    showBackground = true,
    showSystemUi = true
)
@Preview(
    name = "Home – Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onGoLogin = {},
            onGoRegister = {},
            onGoProveedores = {},
            onGoProductos = {},
            onGoCompras = {}
        )
    }
}
