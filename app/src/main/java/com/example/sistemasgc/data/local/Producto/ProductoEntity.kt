package com.example.sistemasgc.data.local.producto

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "productos",
    indices = [
        Index(value = ["sku"], unique = true) // permite NULL repetidos, único solo cuando no es null
    ]
)
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,          //  ID autogenerado

    val nombre: String,         //
    val sku: String?,           // opcional
    val photoUri: String?,      // opcional (guardaremos Uri.toString())
    val categoria: String?      // deja String? para no romper lo demás por ahora
)
