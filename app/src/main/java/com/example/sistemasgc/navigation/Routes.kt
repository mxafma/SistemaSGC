package com.example.sistemasgc.navigation

// Clase sellada para rutas: evita "strings mágicos" y facilita refactors
sealed class Route(val path: String) {
    data object Home        : Route("home")        // Pantalla pública (Login/Registro)
    data object Login       : Route("login")
    data object Register    : Route("register")


    data object Home2       : Route("home2")       // Pantalla interna tras login
    data object Productos   : Route("productos")
    data object Categorias  : Route("categorias")
    data object Proveedores : Route("proveedores")
    data object Compras     : Route("compras")

    data object AgregarProveedor: Route("Agregar_Proveedor")

    data object DetallesCompras: Route("Detalle_Compras")
}
