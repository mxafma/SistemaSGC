package com.example.sistemasgc.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

import com.example.sistemasgc.data.local.user.UserDao
import com.example.sistemasgc.data.local.user.UserEntity

import com.example.sistemasgc.data.local.Proveedor.ProveedorDao
import com.example.sistemasgc.data.local.Proveedor.ProveedorEntity

import com.example.sistemasgc.data.local.Categoria.CategoriaDao
import com.example.sistemasgc.data.local.Categoria.CategoriaEntity

// ✅ IMPORTS ACTUALIZADOS PARA PRODUCTO (paquete en minúsculas)
import com.example.sistemasgc.data.local.producto.ProductoDao
import com.example.sistemasgc.data.local.producto.ProductoEntity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ProveedorEntity::class,
        ProductoEntity::class,
        CategoriaEntity::class,
        com.example.sistemasgc.data.local.Compra.CompraEntity::class,
        com.example.sistemasgc.data.local.Compra.DetalleCompraEntity::class
    ],
    version = 8,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun proveedorDao(): ProveedorDao
    abstract fun productoDao(): ProductoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun compraDao(): com.example.sistemasgc.data.local.Compra.CompraDao
    abstract fun detalleCompraDao(): com.example.sistemasgc.data.local.Compra.DetalleCompraDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "ui_navegacion.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // 🔹 Callback de seed inicial (solo al crear)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // ❌ REMOVIMOS la inserción automática de usuarios
                            // Los usuarios ahora se sincronizan con el backend
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            // Opcional: Aquí podrías hacer una sincronización inicial
                            // si necesitas datos locales por defecto
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
