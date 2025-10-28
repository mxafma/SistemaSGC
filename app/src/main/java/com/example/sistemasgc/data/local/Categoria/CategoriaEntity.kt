package com.example.sistemasgc.data.local.Categoria

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true) val idAuto: Int = 0,
    val nombre: String,
    val idCategoria: String,
    val descripcion: String
)
