package com.example.sistemasgc.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sistemasgc.ui.viewmodel.AuthViewModel

@Composable
fun HistorialComprasScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val compras = viewModel.historial.collectAsState().value

    LaunchedEffect(Unit) { viewModel.loadHistorialCompras() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Historial de Compras", modifier = Modifier.padding(bottom = 8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(compras) { compra ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Fecha: ${compra.fecha}")
                            Text(text = "ID: ${compra.id}")
                            Text(text = "Total: ${compra.total}")
                        }
                        if (viewModel.isAdmin()) {
                            Button(onClick = { viewModel.eliminarCompra(compra.id, onSuccess = { viewModel.loadHistorialCompras() }, onError = { /* TODO mostrar error */ }) }) {
                                Text(text = "Eliminar")
                            }
                        }
                    }
                }
            }
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text(text = "Volver")
        }
    }
}
