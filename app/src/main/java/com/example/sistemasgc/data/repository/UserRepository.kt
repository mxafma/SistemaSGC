package com.example.sistemasgc.data.repository

import com.example.sistemasgc.data.local.user.UserDao
import com.example.sistemasgc.data.local.user.UserEntity

import com.example.sistemasgc.data.local.Proveedor.ProveedorDao
import com.example.sistemasgc.data.local.Proveedor.ProveedorEntity

import com.example.sistemasgc.data.local.Categoria.CategoriaDao
import com.example.sistemasgc.data.local.Categoria.CategoriaEntity

// ✅ Producto (paquete en minúsculas)
import com.example.sistemasgc.data.local.producto.ProductoDao
import com.example.sistemasgc.data.local.producto.ProductoEntity

class UserRepository(
    private val userDao: UserDao,
    private val proveedorDao: ProveedorDao,
    private val productoDao: ProductoDao,
    private val categoriaDao: CategoriaDao
) {

    // -------------------- USUARIOS --------------------
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == password) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

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
    suspend fun agregarProducto(
        nombre: String,
        sku: String?,
        photoUri: String?,
        categoria: String?
    ): Long {
        val cleanName = nombre.trim()
        if (cleanName.length < 4) {
            throw IllegalArgumentException("El nombre debe tener al menos 4 caracteres")
        }

        // Unicidad por nombre (ajusta si quieres permitir duplicados)
        val dupByNombre = productoDao.getByNombre(cleanName)
        if (dupByNombre != null) {
            throw IllegalStateException("Ya existe un producto con nombre \"$cleanName\"")
        }

        // SKU opcional, pero si viene debe ser SOLO numérico y único
        val cleanSku = sku?.trim()?.ifBlank { null }
        if (cleanSku != null) {
            if (!cleanSku.all { it.isDigit() }) {
                throw IllegalArgumentException("El SKU debe contener solo números")
            }
            val dupBySku = productoDao.getBySku(cleanSku)
            if (dupBySku != null) {
                throw IllegalStateException("Ya existe un producto con SKU \"$cleanSku\"")
            }
        }

        val entity = ProductoEntity(
            nombre = cleanName,
            sku = cleanSku,
            photoUri = photoUri,
            categoria = categoria?.trim()?.ifBlank { null }
        )

        return productoDao.insert(entity)
    }
    suspend fun obtenerCategorias(): List<CategoriaEntity> = categoriaDao.getAllC() // ajusta al nombre real de tu DAO

    suspend fun obtenerTodosLosProductos() = productoDao.getAll()
    suspend fun buscarProductos(q: String) = productoDao.search(q.trim())
    // -------------------- CATEGORIAS --------------------
    suspend fun agregarCategoria(nombre: String, id: String, descripcion: String) {
        if (categoriaDao.getByIdCategoria(id) != null) {
            throw IllegalStateException("Ya existe una categoría con ID \"$id\"")
        }
        if (categoriaDao.getByNombre(nombre) != null) {
            throw IllegalStateException("Ya existe una categoría con nombre \"$nombre\"")
        }
        categoriaDao.insert(
            CategoriaEntity(
                nombre = nombre,
                idCategoria = id,
                descripcion = descripcion
            )
        )
    }
}
