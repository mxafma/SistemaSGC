package com.example.sistemasgc.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sistemasgc.ui.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
// Data class para los productos del detalle
data class ProductoDetalle(
    val id: Int,
    val nombre: String,
    val cantidad: Int,
    val precioUnitario: Double
)

@Composable
fun DetallesComprasScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel
) {


    LaunchedEffect(Unit) {
        viewModel.loadProductos()
    }

    val productosNombres by viewModel.productosNombres.collectAsState()

    var query by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val filteredProducts = productosNombres.filter {
        it.contains(query, ignoreCase = true)
    }

    var cantidad by rememberSaveable { mutableStateOf("") }
    var precioUnitario by rememberSaveable { mutableStateOf("") }

    val comprasState by viewModel.compras.collectAsStateWithLifecycle()
    val productosSeleccionados = comprasState.detalles

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Compras",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Detalle Compra",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // -------- Buscador con lista desplegable --------
            @OptIn(ExperimentalMaterial3Api::class)
            ExposedDropdownMenuBox(
                expanded = expanded && filteredProducts.isNotEmpty(),
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        expanded = true
                    },
                    label = { Text("Buscar producto") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Desplegar")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
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
                            }
                        )
                    }

                    if (filteredProducts.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("(sin resultados)") },
                            onClick = { expanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // -------- Cantidad y Precio --------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = precioUnitario,
                    onValueChange = { precioUnitario = it },
                    label = { Text("Precio Unitario") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    singleLine = true
                )
            }

            // -------- Botón Agregar --------
            Button(
                onClick = {
                    val cantidadInt = cantidad.toIntOrNull() ?: 0
                    val precioDouble = precioUnitario.toDoubleOrNull() ?: 0.0

                    if (query.isNotBlank() && cantidadInt > 0 && precioDouble > 0.0) {
                        // Agregar al ViewModel (se sincroniza en submitCompra)
                        viewModel.addDetalle(query, cantidadInt, precioDouble)
                        // Limpiar campos
                        query = ""
                        cantidad = ""
                        precioUnitario = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Agregar producto")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------- Encabezados de la tabla --------
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

            // -------- Lista de productos seleccionados --------
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                items(productosSeleccionados) { producto ->
                    ProductoDetalleItem(
                        producto = com.example.sistemasgc.ui.screen.ProductoDetalle(
                            id = 0,
                            nombre = producto.producto,
                            cantidad = producto.cantidad,
                            precioUnitario = producto.precioUnitario
                        )
                    )
                }
            }

            // -------- Resumen de la compra --------
            val totalProductos = productosSeleccionados.sumOf { it.cantidad }
            val totalPrecio = productosSeleccionados.sumOf { it.precioUnitario * it.cantidad }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumen de Compra",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

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

            // -------- Botón Aceptar --------
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
fun ProductoDetalleItem(producto: ProductoDetalle) {
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
