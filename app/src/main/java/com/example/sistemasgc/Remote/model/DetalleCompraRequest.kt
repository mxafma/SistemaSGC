package com.example.sistemasgc.Remote.model

data class DetalleCompraRequest(
    val compraId: Int? = null,
    val compra: String? = null,
    val producto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)
