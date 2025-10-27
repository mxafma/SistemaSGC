package com.example.sistemasgc.data.local.Proveedor

import androidx.room.Dao                       // Marca esta interfaz como DAO de Room
import androidx.room.Insert                    // Para insertar filas
import androidx.room.OnConflictStrategy        // Estrategia de conflicto en inserción
import androidx.room.Query                     // Para queries SQL


// @Dao indica que define operaciones para la tabla users.
@Dao
interface ProveedorDao {
    // Inserta un usuario. ABORT si hay conflicto de PK (no de email; ese lo controlamos a mano).
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(Proveedor: ProveedorEntity): Long

    // Devuelve un usuario por email (o null si no existe).
    @Query("SELECT * FROM proveedores  WHERE Pemail = :Pemail LIMIT 1")
    suspend fun getByEmailP(Pemail: String): ProveedorEntity?

    // Cuenta total de usuarios (para saber si hay datos y/o para seeds).
    @Query("SELECT COUNT(*) FROM proveedores ")
    suspend fun count(): Int

    // Lista completa (útil para debug/administración).
    @Query("SELECT * FROM proveedores  ORDER BY Pid ASC")
    suspend fun getAllP(): List<ProveedorEntity>
}