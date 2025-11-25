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
        CategoriaEntity::class
    ],
    version = 6,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun proveedorDao(): ProveedorDao
    abstract fun productoDao(): ProductoDao
    abstract fun categoriaDao(): CategoriaDao

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
                            CoroutineScope(Dispatchers.IO).launch {
                                val appDb = getInstance(context)
                                val userDao = appDb.userDao()
                                val proveedorDao = appDb.proveedorDao()

                                val seedUsers = listOf(
                                    UserEntity(
                                        name = "Admin",
                                        email = "admin@duoc.cl",
                                        phone = "+56911111111",
                                        password = "Admin123!"
                                    ),
                                    UserEntity(
                                        name = "Víctor Rosendo",
                                        email = "victor@duoc.cl",
                                        phone = "+56922222222",
                                        password = "123456"
                                    )
                                )

                                val seedProveedores = listOf(
                                    ProveedorEntity(
                                        name = "Proveedor A",
                                        rut = "12345678-9",
                                        phone = "+56933333333",
                                        email = "proveedora@empresa.cl",
                                        direccion = "Calle 123"
                                    ),
                                    ProveedorEntity(
                                        name = "Proveedor B",
                                        rut = "98765432-1",
                                        phone = "+56944444444",
                                        email = "proveedorb@empresa.cl",
                                        direccion = "Avenida 456"
                                    )
                                )

                                try {
                                    // ⚠️ Estos métodos deben existir en tus DAOs
                                    if (userDao.count() == 0) {
                                        seedUsers.forEach { userDao.insert(it) }
                                    }
                                    if (proveedorDao.count() == 0) {
                                        seedProveedores.forEach { proveedorDao.insert(it) }
                                    }
                                } catch (_: Exception) {
                                    // Ignora seeds si los DAOs no tienen count() en este momento
                                }
                            }
                        }
                    })
                    // recrea la BD automáticamente si cambias versión/esquema
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
