package com.example.sistemasgc.data.local.producto

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(producto: ProductoEntity): Long

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    suspend fun getAll(): List<ProductoEntity>

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ProductoEntity?

    @Query("SELECT * FROM productos WHERE nombre = :nombre LIMIT 1")
    suspend fun getByNombre(nombre: String): ProductoEntity?

    @Query("SELECT * FROM productos WHERE sku = :sku LIMIT 1")
    suspend fun getBySku(sku: String): ProductoEntity?

    @Query("""
        SELECT * FROM productos
        WHERE nombre LIKE '%' || :q || '%'
           OR sku    LIKE '%' || :q || '%'
        ORDER BY nombre ASC
    """)
    suspend fun search(q: String): List<ProductoEntity>
}
