package com.example.sistemasgc.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sistemasgc.ui.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(viewModel: PostViewModel) {
    // observamos el flujo de datos del viewmodel
    val posts = viewModel.postList.collectAsState().value
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listado de Posts") }
            )
        }
    ) { innerPadding ->
        //Aplicamos el padding de seguridad del sistema
        Box(Modifier
            .fillMaxSize()
            .padding(innerPadding) // Esto garantiza el uso de edge-to-edge
     ) {
            //Lsta de publicaciones
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp) // Espaciado interior del contenido
            ) {
                items(posts) {post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Titulo: ${post.title}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = post.body,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
