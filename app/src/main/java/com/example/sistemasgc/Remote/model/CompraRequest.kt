package com.example.sistemasgc.Remote.model

data class CompraRequest(
    val proveedor: String,
    val formaPago: String,
    val fecha: String,
    val total: Double? = null,
    val detalles: List<DetalleCompraRequest> = emptyList()
)
