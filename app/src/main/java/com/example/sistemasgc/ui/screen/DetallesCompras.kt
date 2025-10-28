package com.example.sistemasgc.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Data class para los productos del detalle
data class ProductoDetalle(
    val id: Int,
    val nombre: String,
    val cantidad: Int,
    val precioUnitario: Double
)

@Composable
fun DetallesComprasScreen(
    onBack: () -> Unit
) {
    // Datos de ejemplo para el detalle de compra
    val productosCompra = listOf(
        ProductoDetalle(1, "Producto A", 5, 25.50),
        ProductoDetalle(2, "Producto B", 3, 15.75),
        ProductoDetalle(3, "Producto C", 10, 8.99)
    )

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título principal
            Text(
                text = "Compras",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Subtítulo
            Text(
                text = "Detalle Compra",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Encabezados de la tabla
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Producto",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Cantidad",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Precio Unitario",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1.5f),
                    textAlign = TextAlign.End
                )
            }

            // Lista de productos del detalle
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                items(productosCompra) { producto ->
                    ProductoDetalleItem(producto = producto)
                }
            }

            // Resumen de la compra
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Resumen de Compra",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val totalProductos = productosCompra.sumOf { it.cantidad }
                    val totalPrecio = productosCompra.sumOf { it.precioUnitario * it.cantidad }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Productos:")
                        Text("$totalProductos")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Monto Total:", fontWeight = FontWeight.Bold)
                        Text("$${"%.2f".format(totalPrecio)}", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Botón Aceptar
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 16.dp)
            ) {
                Text("Aceptar")
            }
        }
    }
}

@Composable
fun ProductoDetalleItem(
    producto: ProductoDetalle
) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(2f)
            )

            Text(
                text = producto.cantidad.toString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            Text(
                text = "$${"%.2f".format(producto.precioUnitario)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1.5f),
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDetallesComprasScreen() {
    MaterialTheme {
        DetallesComprasScreen(onBack = {})
    }
}