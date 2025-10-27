package com.example.sistemasgc.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ComprasScreen(
    onDetallesCompras: () -> Unit,
    onNuevaCompra: (String, String, String) -> Unit, // Cambiado para aceptar parámetros
    onSearch: (String) -> Unit
) {

    var Proveedor by rememberSaveable { mutableStateOf("") }
    var FormaPago by rememberSaveable { mutableStateOf("") }
    var Fecha by rememberSaveable { mutableStateOf("") }

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Compras", // Cambiado de "Productos" a "Compras" para que sea coherente
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Compras anteriores", // Cambiado de "Productos" a "Compras" para que sea coherente
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                var query by rememberSaveable { mutableStateOf("") }

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("") },
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

                // ---- Proveedor ----
                OutlinedTextField(
                    value = Proveedor,
                    onValueChange = { Proveedor = it },
                    label = { Text("Proveedor") }, // Cambiado de "Nombre" a "Proveedor"
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- Forma de Pago ----
                OutlinedTextField(
                    value = FormaPago,
                    onValueChange = { FormaPago = it },
                    label = { Text("Forma Pago") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- Fecha ----
                OutlinedTextField(
                    value = Fecha,
                    onValueChange = { Fecha = it },
                    label = { Text("Fecha") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // ---- Detalles Compras ----
                Button(
                    onClick = onDetallesCompras,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text("Detalle Compras") // Espacio agregado para mejor legibilidad
                }

                // ---- Botón Agregar ----
                Button(
                    onClick = { onNuevaCompra(Proveedor, FormaPago, Fecha) },
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

@Preview(name = "Compras – Light", showBackground = true, showSystemUi = true)
@Preview(name = "Compras – Dark", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ComprasScreenPreview() {
    MaterialTheme {
        ComprasScreen(
            onSearch = {},
            onNuevaCompra = { proveedor, formaPago, fecha -> }, // Actualizado para aceptar parámetros
            onDetallesCompras = {}
        )
    }
}