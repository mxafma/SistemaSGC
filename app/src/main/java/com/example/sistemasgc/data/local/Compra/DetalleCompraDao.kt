package com.example.sistemasgc.data.local.Compra

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DetalleCompraDao {
    @Insert
    suspend fun insert(detalle: DetalleCompraEntity): Long

    @Query("SELECT * FROM detalle_compras WHERE compraId = :compraId")
    suspend fun getByCompraId(compraId: Long): List<DetalleCompraEntity>

    @Query("DELETE FROM detalle_compras")
    suspend fun deleteAll()
}
