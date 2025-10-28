package com.example.sistemasgc.data.repository

import com.example.sistemasgc.data.local.user.UserDao       // DAO de usuario
import com.example.sistemasgc.data.local.user.UserEntity    // Entidad de usuario
import com.example.sistemasgc.data.local.Proveedor.ProveedorDao
import com.example.sistemasgc.data.local.Proveedor.ProveedorEntity
import com.example.sistemasgc.data.local.Producto.ProductoDao
import com.example.sistemasgc.data.local.Producto.ProductoEntity

// Repositorio: orquesta reglas de negocio para login/registro sobre los DAOs.
class UserRepository(
    private val userDao: UserDao,
    private val proveedorDao: ProveedorDao,
    private val productoDao: ProductoDao
) {

    // -------------------- USUARIOS --------------------

    // Login: busca por email y valida contraseña
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == password) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    // Registro: valida no duplicado y crea usuario
    suspend fun register(name: String, email: String, phone: String, password: String): Result<Long> {
        val exists = userDao.getByEmail(email) != null
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val id = userDao.insert(
            UserEntity(
                name = name,
                email = email,
                phone = phone,
                password = password
            )
        )
        return Result.success(id)
    }

    // -------------------- PROVEEDORES --------------------

    suspend fun proveedor(
        Pname: String,
        Prut: String,
        Pphone: String,
        Pemail: String,
        Pdireccion: String
    ): Result<Long> {
        val existeProveedor = proveedorDao.getByEmailP(Pemail) != null
        if (existeProveedor) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val id = proveedorDao.insert(
            ProveedorEntity(
                Pname = Pname,
                Prut = Prut,
                Pphone = Pphone,
                Pemail = Pemail,
                Pdireccion = Pdireccion
            )
        )
        return Result.success(id)
    }

    suspend fun obtenerTodosLosProveedores(): List<ProveedorEntity> {
        return proveedorDao.getAllP()
    }

    // -------------------- PRODUCTOS --------------------

    /**
     * Agrega un producto; lanza IllegalStateException si ya existe por idProducto o por nombre.
     * Nota: tu ViewModel captura la excepción y construye un Result allí.
     */
    suspend fun agregarProducto(nombre: String, id: String, categoria: String) {
        // Chequeos de unicidad simples (ajusta reglas si quieres permitir repetidos)
        val dupById = productoDao.getByIdProducto(id)
        if (dupById != null) {
            throw IllegalStateException("Ya existe un producto con ID \"$id\"")
        }

        val dupByNombre = productoDao.getByNombre(nombre)
        if (dupByNombre != null) {
            throw IllegalStateException("Ya existe un producto con nombre \"$nombre\"")
        }

        productoDao.insert(
            ProductoEntity(
                nombre = nombre,
                idProducto = id,
                categoria = categoria
            )
        )
    }

    suspend fun obtenerTodosLosProductos(): List<ProductoEntity> {
        return productoDao.getAll()
    }
}
