package com.example.sistemasgc.data.local.Categoria

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoriaDao {

    @Insert
    suspend fun insert(categoria: CategoriaEntity): Long

    @Query("SELECT * FROM categorias")
    suspend fun getAll(): List<CategoriaEntity>

    @Query("SELECT * FROM categorias WHERE idCategoria = :id LIMIT 1")
    suspend fun getByIdCategoria(id: String): CategoriaEntity?

    @Query("SELECT * FROM categorias WHERE nombre = :nombre LIMIT 1")
    suspend fun getByNombre(nombre: String): CategoriaEntity?

    @Query("SELECT COUNT(*) FROM categorias")
    suspend fun count(): Int


    @Query("SELECT * FROM categorias ORDER BY nombre")
    suspend fun getAllC(): List<CategoriaEntity>
}
