package com.example.sistemasgc.data.local.Producto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val idAuto: Int = 0,
    val nombre: String,
    val idProducto: String,
    val categoria: String
)
