package com.example.sistemasgc.data.local.Proveedor

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity declara una tabla SQLite manejada por Room.
// tableName = "users" define el nombre exacto de la tabla.
@Entity(tableName = "proveedores")
data class ProveedorEntity(
    @PrimaryKey(autoGenerate = true)    // Clave primaria autoincremental
    val Pid: Long = 0L,

    val Pname: String,
    val Prut: String,
    val Pemail: String,
    val Pphone: String,
    val Pdireccion: String


)