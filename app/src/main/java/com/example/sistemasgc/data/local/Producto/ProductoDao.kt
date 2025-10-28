package com.example.sistemasgc.data.local.Producto

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductoDao {

    @Insert
    suspend fun insert(producto: ProductoEntity): Long

    @Query("SELECT * FROM productos")
    suspend fun getAll(): List<ProductoEntity>

    @Query("SELECT * FROM productos WHERE idProducto = :id LIMIT 1")
    suspend fun getByIdProducto(id: String): ProductoEntity?

    @Query("SELECT * FROM productos WHERE nombre = :nombre LIMIT 1")
    suspend fun getByNombre(nombre: String): ProductoEntity?
}
