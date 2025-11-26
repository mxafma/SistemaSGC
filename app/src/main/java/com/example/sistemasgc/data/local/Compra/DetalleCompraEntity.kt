package com.example.sistemasgc.data.local.Compra

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detalle_compras")
data class DetalleCompraEntity(
    @PrimaryKey(autoGenerate = true)
    val idAuto: Long = 0,
    val compraId: Long,
    val producto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double,
    val synced: Boolean = false
)
