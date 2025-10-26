package com.example.sistemasgc.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
// ✅ Estructura para cada ítem del Drawer
data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val onClick: (() -> Unit)?
)

@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
    ) {
        // Título o encabezado del Drawer
        Text(
            text = "Menú principal",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        Divider()

        // Recorremos los ítems del menú
        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = false, // puedes comparar con currentRoute
                onClick = { item.onClick?.invoke() },
                icon = { Icon(item.icon, contentDescription = item.label) },
                modifier = Modifier,
                colors = NavigationDrawerItemDefaults.colors()
            )
        }
    }
}

// ✅ Helper para generar ítems estándar del Drawer
@Composable
fun defaultDrawerItems(
    onHome: (() -> Unit)? = null,
    onLogin: (() -> Unit)? = null,
    onRegister: (() -> Unit)? = null,
    onProductos: (() -> Unit)? = null,
    onProveedores: (() -> Unit)? = null,
    onCompras: (() -> Unit)? = null,
    onCategorias: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null
): List<DrawerItem> {
    return listOf(
        DrawerItem("Inicio", Icons.Filled.Home, onHome),
        DrawerItem("Login", Icons.Filled.AccountCircle, onLogin),
        DrawerItem("Registro", Icons.Filled.Person, onRegister),
        DrawerItem("Productos", Icons.Filled.ShoppingCart, onProductos),
        DrawerItem("Proveedores", Icons.Filled.Store, onProveedores),
        DrawerItem("Compras", Icons.Filled.ShoppingBag, onCompras),
        DrawerItem("Categorías", Icons.Filled.Category, onCategorias),
        DrawerItem("Cerrar sesión", Icons.Filled.ExitToApp, onLogout)
    ).filter { it.onClick != null }
}
