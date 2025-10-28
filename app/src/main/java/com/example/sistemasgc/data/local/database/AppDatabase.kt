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

import com.example.sistemasgc.data.local.Producto.ProductoDao
import com.example.sistemasgc.data.local.Producto.ProductoEntity

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
    version = 4,                // ✅ SUBE VERSIÓN PARA FORZAR RECREACIÓN
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
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                // Reusar la misma instancia
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
                                        Pname = "Proveedor A",
                                        Prut = "12345678-9",
                                        Pphone = "+56933333333",
                                        Pemail = "proveedora@empresa.cl",
                                        Pdireccion = "Calle 123"
                                    ),
                                    ProveedorEntity(
                                        Pname = "Proveedor B",
                                        Prut = "98765432-1",
                                        Pphone = "+56944444444",
                                        Pemail = "proveedorb@empresa.cl",
                                        Pdireccion = "Avenida 456"
                                    )
                                )

                                if (userDao.count() == 0) {
                                    seedUsers.forEach { userDao.insert(it) }
                                }
                                if (proveedorDao.count() == 0) {
                                    seedProveedores.forEach { proveedorDao.insert(it) }
                                }
                                // (Opcional) puedes sembrar productos aquí si quieres.
                            }
                        }
                    })
                    .fallbackToDestructiveMigration() // en dev: recrea al cambiar versión
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
