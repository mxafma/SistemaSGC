package com.example.sistemasgc.data.local.Compra

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compras")
data class CompraEntity(
    @PrimaryKey(autoGenerate = true)
    val idAuto: Long = 0,
    val proveedor: String,
    val formaPago: String,
    val fecha: String,
    val total: Double = 0.0,
    val synced: Boolean = false // indica si se sincronizó con backend
)
