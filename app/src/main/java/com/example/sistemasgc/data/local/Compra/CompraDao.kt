package com.example.sistemasgc.data.local.Compra

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CompraDao {
    @Insert
    suspend fun insert(compra: CompraEntity): Long

    @Query("SELECT * FROM compras ORDER BY idAuto DESC")
    suspend fun getAll(): List<CompraEntity>

    @Query("DELETE FROM compras")
    suspend fun deleteAll()
}
