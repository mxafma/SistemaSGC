package com.example.sistemasgc.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ComprasScreen(
    onNuevaCompra: (() -> Unit)? = null
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Compras", style = MaterialTheme.typography.headlineSmall)
                Text("Pantalla en construcción. Próximamente: listado y creación de compras.")
                Button(
                    onClick = { onNuevaCompra?.invoke() },
                    enabled = onNuevaCompra != null
                ) { Text("Nueva compra") }
            }
        }
    }
}

@Preview(name = "Compras – Light", showBackground = true, showSystemUi = true)
@Preview(name = "Compras – Dark", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ComprasScreenPreview() {
    MaterialTheme { ComprasScreen() }
}
