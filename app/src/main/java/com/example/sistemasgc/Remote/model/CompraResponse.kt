package com.example.sistemasgc.Remote.model

data class CompraResponse(
    val id: Long,
    val proveedor: String,
    val formaPago: String,
    val fecha: String,
    val total: Double
)
