package com.example.sistemasgc.data.local.Categoria

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sistemasgc.data.local.Categoria.CategoriaEntity

@Dao
interface CategoriaDao {
    @Insert
    suspend fun insert(categoria: CategoriaEntity): Long

    @Query("SELECT * FROM categorias WHERE nombre = :nombre LIMIT 1")
    suspend fun getByNombre(nombre: String): CategoriaEntity?

    @Query("SELECT * FROM categorias ORDER BY nombre")
    suspend fun getAllC(): List<CategoriaEntity>

}
