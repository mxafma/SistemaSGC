package com.example.sistemasgc.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ReportesScreen(
    onSearch: (String) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Título "Reports"
            Text(
                text = "Reportes",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Selector de Tipo de Reporte
            var expanded by rememberSaveable { mutableStateOf(false) }
            var selectedReportType by rememberSaveable { mutableStateOf("") }
            val reportTypes = listOf("Reporte de Ventas", "Reporte de Inventario", "Reporte de Proveedores", "Reporte Financiero")

            Text(
                text = "Tipo Reporte:",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            OutlinedTextField(
                value = selectedReportType,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Desplegar")
                    }
                },
                placeholder = { Text("Seleccione un tipo de reporte") },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                reportTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedReportType = type
                            expanded = false
                        }
                    )
                }
            }


            // TABLA/CUADRO CON BORDES
            Text(
                text = "Datos del Reporte:",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            // Cuadro/tabla con bordes
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(200.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.dp, Color.Gray),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Fila de encabezados
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Proveedor",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(100.dp)
                        )
                        Text(
                            "Producto",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(100.dp)
                        )
                        Text(
                            "Fecha",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(100.dp)
                        )
                    }

                    // Línea separadora
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = Color.LightGray
                    ) {}

                    // Filas de datos (puedes agregar más según necesites)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mario", modifier = Modifier.width(100.dp))
                        Text("Computador", modifier = Modifier.width(100.dp))
                        Text("12/12/25", modifier = Modifier.width(100.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Matias", modifier = Modifier.width(100.dp))
                        Text("Peluca", modifier = Modifier.width(100.dp))
                        Text("23/02/25", modifier = Modifier.width(100.dp))
                    }

                    // Puedes agregar más filas aquí según necesites
                }
            }


        }
    }
}

@Preview
@Composable
fun ReportesScreenPreview() {
    MaterialTheme {
        ReportesScreen(
            onSearch = {}
        )
    }
}