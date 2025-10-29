package com.example.sistemasgc.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemasgc.ui.viewmodel.AuthViewModel
import java.util.*
import java.text.SimpleDateFormat

@Composable
fun ComprasScreen(
    onDetallesCompras: () -> Unit,
    onNuevaCompra: (String, String, String) -> Unit,
    onSearch: (String) -> Unit,
    viewModel: AuthViewModel
) {
    val comprasState by viewModel.compras.collectAsStateWithLifecycle()
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    val formasPago = viewModel.getFormasPago()

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
                    text = "Compras",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Compras anteriores",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                // Buscador
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Buscar compras...") },
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

                // ---- Proveedor (Dropdown) ----
                DropdownMenuPersonalizado(
                    value = comprasState.proveedorSeleccionado,
                    onValueChange = viewModel::onComprasProveedorChange,
                    options = comprasState.proveedores.map { it.nombre },
                    label = "Proveedor",
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- Forma de Pago (Dropdown) ----
                DropdownMenuPersonalizado(
                    value = comprasState.formaPagoSeleccionada,
                    onValueChange = viewModel::onComprasFormaPagoChange,
                    options = formasPago,
                    label = "Forma de Pago",
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- Fecha (Selector) ----
                OutlinedTextField(
                    value = comprasState.fechaSeleccionada,
                    onValueChange = { }, // No permitir edición directa
                    label = { Text("Fecha") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { mostrarDatePicker = true }
                        ) {
                            Icon(Icons.Filled.CalendarToday, contentDescription = "Seleccionar fecha")
                        }
                    }
                )

                // Mostrar mensaje de error si existe
                comprasState.errorMsg?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // ---- Detalles Compras ----
                Button(
                    onClick = onDetallesCompras,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    enabled = !comprasState.isSubmitting
                ) {
                    Text("Detalle Compras")
                }

                // ---- Botón Agregar Compra ----
                Button(
                    onClick = {
                        viewModel.submitCompra {
                            // Callback de éxito
                            onNuevaCompra(
                                comprasState.proveedorSeleccionado,
                                comprasState.formaPagoSeleccionada,
                                comprasState.fechaSeleccionada
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    enabled = comprasState.proveedorSeleccionado.isNotBlank() &&
                            comprasState.formaPagoSeleccionada.isNotBlank() &&
                            !comprasState.isSubmitting
                ) {
                    if (comprasState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Agregando...")
                    } else {
                        Text("Agregar Compra")
                    }
                }
            }
        }
    }

    // Selector de fecha
    if (mostrarDatePicker) {
        DatePickerDialog(
            onDateSelected = { fecha ->
                viewModel.onComprasFechaChange(fecha)
                mostrarDatePicker = false
            },
            onDismiss = { mostrarDatePicker = false }
        )
    }
}

@Composable
fun DropdownMenuPersonalizado(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                        contentDescription = if (expanded) "Ocultar opciones" else "Mostrar opciones"
                    )
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No hay opciones disponibles") },
                    onClick = { }
                )
            } else {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Fecha") },
        text = {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Por ahora puedes usar la fecha actual:")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val calendar = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val fechaActual = dateFormat.format(calendar.time)
                    onDateSelected(fechaActual)
                }
            ) {
                Text("Usar fecha actual")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


