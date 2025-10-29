package com.example.sistemasgc.ui.components

import androidx.compose.material.icons.Icons // Conjunto de íconos Material
import androidx.compose.material.icons.filled.Home // Ícono Home
import androidx.compose.material.icons.filled.AccountCircle // Ícono Login
import androidx.compose.material.icons.filled.Menu // Ícono hamburguesa
import androidx.compose.material.icons.filled.MoreVert // Ícono 3 puntitos (overflow)
import androidx.compose.material.icons.filled.Person // Ícono Registro
import androidx.compose.material3.CenterAlignedTopAppBar // TopAppBar centrada
import androidx.compose.material3.DropdownMenu // Menú desplegable
import androidx.compose.material3.DropdownMenuItem // Opción del menú
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon // Para mostrar íconos
import androidx.compose.material3.IconButton // Botones con ícono
import androidx.compose.material3.MaterialTheme // Tema Material
import androidx.compose.material3.Text // Texto
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.* // remember / mutableStateOf
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
// AppTopBar.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit,
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    // 👇 NUEVO
    isLoggedIn: Boolean,
    onProductos: (() -> Unit)? = null,
    onProveedores: (() -> Unit)? = null,
    onCompras: (() -> Unit)? = null,
) {
    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                text = "Gestion Compras",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Filled.Menu, contentDescription = "Menú")
            }
        },
        actions = {
            // Siempre Home
            IconButton(onClick = onHome) {
                Icon(Icons.Filled.Home, contentDescription = "Home")
            }

            if (!isLoggedIn) {
                // 👇 Solo cuando NO estás logeado
                IconButton(onClick = onLogin) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = "Login")
                }
                IconButton(onClick = onRegister) {
                    Icon(Icons.Filled.Person, contentDescription = "Registro")
                }
            } else {
                // 👇 Cuando SÍ estás logeado, mostramos accesos de negocio en overflow
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Más")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Productos") },
                        onClick = { showMenu = false; onProductos?.invoke() },
                        enabled = onProductos != null
                    )
                    DropdownMenuItem(
                        text = { Text("Proveedores") },
                        onClick = { showMenu = false; onProveedores?.invoke() },
                        enabled = onProveedores != null
                    )
                    DropdownMenuItem(
                        text = { Text("Compras") },
                        onClick = { showMenu = false; onCompras?.invoke() },
                        enabled = onCompras != null
                    )
                }
            }
        }
    )
}
