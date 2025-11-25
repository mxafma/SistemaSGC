package com.example.sistemasgc.data.local.Proveedor

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity declara una tabla SQLite manejada por Room.
// tableName = "users" define el nombre exacto de la tabla.
@Entity(tableName = "proveedores")
data class ProveedorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,
    val rut: String,
    val email: String,
    val phone: String,
    val direccion: String? = null
)