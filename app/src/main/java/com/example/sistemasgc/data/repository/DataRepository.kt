package com.example.sistemasgc.data.repository

import com.example.sistemasgc.data.local.user.UserDao
import com.example.sistemasgc.data.local.user.UserEntity

import com.example.sistemasgc.data.local.Proveedor.ProveedorDao
import com.example.sistemasgc.data.local.Proveedor.ProveedorEntity

import com.example.sistemasgc.data.local.Categoria.CategoriaDao
import com.example.sistemasgc.data.local.Categoria.CategoriaEntity

import com.example.sistemasgc.data.local.producto.ProductoDao
import com.example.sistemasgc.data.local.producto.ProductoEntity

import com.example.sistemasgc.Remote.RetrofitInstance
import com.example.sistemasgc.Remote.model.RegisterRequest
import com.example.sistemasgc.Remote.model.LoginRequest
import com.example.sistemasgc.Remote.model.ProveedorRequest
import com.example.sistemasgc.Remote.model.ProductoRequest
import org.json.JSONObject

class DataRepository(
    private val userDao: UserDao,
    private val proveedorDao: ProveedorDao,
    private val productoDao: ProductoDao,
    private val categoriaDao: CategoriaDao
) {

    // -------------------- Helper backend error --------------------
    private fun parseBackendErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            JSONObject(errorBody)
                .optString("error")
                .takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }

    // -------------------- USUARIOS --------------------
    suspend fun login(email: String, password: String): Result<UserEntity> {
        return try {
            val request = LoginRequest(
                email = email,
                password = password
            )
            val response = RetrofitInstance.api.loginUser(request)

            val localUser = userDao.getByEmail(email)
            if (localUser == null) {
                val id = userDao.insert(
                    UserEntity(
                        name = response.name,
                        email = response.email,
                        phone = response.phone ?: "",
                        password = password
                    )
                )
                val newUser = userDao.getByEmail(email)!!
                Result.success(newUser)
            } else {
                Result.success(localUser)
            }
        } catch (e: retrofit2.HttpException) {
            val errorMsg = when (e.code()) {
                401 -> "Credenciales inválidas"
                404 -> "Usuario no encontrado"
                400 -> "Email o contraseña incorrectos"
                else -> "Error al iniciar sesión: ${e.message()}"
            }
            Result.failure(IllegalArgumentException(errorMsg))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(IllegalArgumentException("No hay conexión a internet"))
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Error al iniciar sesión: ${e.message}"))
        }
    }

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<Long> {
        return try {
            val request = RegisterRequest(
                name = name,
                email = email,
                phone = phone,
                password = password
            )
            val response = RetrofitInstance.api.registerUser(request)

            val id = userDao.insert(
                UserEntity(
                    name = name,
                    email = email,
                    phone = phone,
                    password = password
                )
            )

            Result.success(id)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val backendMessage = parseBackendErrorMessage(errorBody)

            val errorMsg = when {
                backendMessage != null ->
                    backendMessage

                e.code() == 400 && errorBody?.contains("email", ignoreCase = true) == true ->
                    "El correo ya está registrado"

                e.code() == 400 && errorBody?.contains("existe", ignoreCase = true) == true ->
                    "El correo ya está registrado"

                e.code() == 400 ->
                    "Datos inválidos. Verifica la información ingresada"

                e.code() == 409 ->
                    "El correo ya está registrado"

                e.code() == 500 ->
                    "Error del servidor. Intenta más tarde"

                else ->
                    "Error al registrar: ${e.message()}"
            }
            Result.failure(IllegalStateException(errorMsg))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(IllegalStateException("No hay conexión a internet"))
        } catch (e: Exception) {
            Result.failure(IllegalStateException("Error al registrar: ${e.message}"))
        }
    }

    // -------------------- PROVEEDORES --------------------
    suspend fun proveedor(
        Pname: String,
        Prut: String,
        Pphone: String,
        Pemail: String,
        Pdireccion: String? = null
    ): Result<Long> {
        return try {
            val existeProveedor = proveedorDao.getByEmailP(Pemail) != null
            if (existeProveedor) {
                return Result.failure(IllegalStateException("El correo ya está registrado"))
            }

            val request = ProveedorRequest(
                name = Pname,
                rut = Prut,
                phone = Pphone,
                email = Pemail,
                direccion = Pdireccion
            )
            val response = RetrofitInstance.api.createProveedor(request)

            val id = proveedorDao.insert(
                ProveedorEntity(
                    name = Pname,
                    rut = Prut,
                    phone = Pphone,
                    email = Pemail,
                    direccion = Pdireccion
                )
            )

            Result.success(id)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val backendMessage = parseBackendErrorMessage(errorBody)

            val errorMsg = when {
                backendMessage != null ->
                    backendMessage

                e.code() == 400 && errorBody?.contains("email", ignoreCase = true) == true ->
                    "El correo del proveedor ya está registrado"

                e.code() == 400 && errorBody?.contains("existe", ignoreCase = true) == true ->
                    "El proveedor ya existe"

                e.code() == 400 ->
                    "Datos inválidos del proveedor. Verifica la información ingresada"

                e.code() == 409 ->
                    "El proveedor ya existe"

                e.code() == 500 ->
                    "Error del servidor. Intenta más tarde"

                else ->
                    "Error al crear proveedor: ${e.message()}"
            }
            Result.failure(IllegalStateException(errorMsg))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(IllegalStateException("No hay conexión a internet"))
        } catch (e: Exception) {
            Result.failure(IllegalStateException("Error al crear proveedor: ${e.message}"))
        }
    }

    suspend fun obtenerTodosLosProveedores(): List<ProveedorEntity> {
        return try {
            val remoteProveedores = RetrofitInstance.api.getProveedores()

            proveedorDao.deleteAllP()

            remoteProveedores.forEach { proveedor ->
                proveedorDao.insert(
                    ProveedorEntity(
                        name = proveedor.name,
                        rut = proveedor.rut,
                        phone = proveedor.phone,
                        email = proveedor.email,
                        direccion = proveedor.direccion
                    )
                )
            }

            proveedorDao.getAllP()
        } catch (e: Exception) {
            proveedorDao.getAllP()
        }
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

        val cleanCategoria = categoria?.trim()?.ifBlank { null }

        try {
            val request = ProductoRequest(
                nombre = cleanName,
                sku = cleanSku,
                categoria = cleanCategoria,
                photoUri = photoUri
            )

            val response = RetrofitInstance.api.createProducto(request)

            val entity = ProductoEntity(
                nombre = response.nombre ?: cleanName,
                sku = response.sku ?: cleanSku,
                photoUri = response.photoUri ?: photoUri,
                categoria = response.categoria ?: cleanCategoria
            )

            return productoDao.insert(entity)

        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val backendMessage = parseBackendErrorMessage(errorBody)

            val msg = when {
                backendMessage != null ->
                    backendMessage

                e.code() == 400 && errorBody?.contains("sku", ignoreCase = true) == true ->
                    "El SKU del producto ya está registrado"

                e.code() == 400 && errorBody?.contains("nombre", ignoreCase = true) == true ->
                    "El nombre del producto ya está registrado"

                e.code() == 400 ->
                    "Datos inválidos del producto. Verifica la información ingresada"

                e.code() == 409 ->
                    "El producto ya existe"

                e.code() == 500 ->
                    "Error del servidor. Intenta más tarde"

                else ->
                    "Error al crear producto: ${e.message()}"
            }

            throw IllegalStateException(msg)

        } catch (e: java.net.UnknownHostException) {
            throw IllegalStateException("No hay conexión a internet")
        } catch (e: Exception) {
            throw IllegalStateException("Error al crear producto: ${e.message}")
        }
    }

    suspend fun sincronizarProductosDesdeBackend(): List<ProductoEntity> {
        return try {
            val remoteProductos = RetrofitInstance.api.getProductos()

            productoDao.deleteAll()

            remoteProductos.forEach { p ->
                productoDao.insert(
                    ProductoEntity(
                        nombre = p.nombre ?: "",
                        sku = p.sku,
                        photoUri = p.photoUri,
                        categoria = p.categoria
                    )
                )
            }

            productoDao.getAll()
        } catch (e: Exception) {
            productoDao.getAll()
        }
    }

    suspend fun obtenerTodosLosProductos(): List<ProductoEntity> =
        productoDao.getAll()

    suspend fun buscarProductos(q: String) =
        productoDao.search(q.trim())

    // -------------------- CATEGORIAS --------------------
    suspend fun obtenerCategorias(): List<CategoriaEntity> =
        categoriaDao.getAllC()

    suspend fun agregarCategoria(nombre: String, descripcion: String) {
        if (nombre.trim().length < 3) {
            throw IllegalArgumentException("El nombre debe tener al menos 3 caracteres")
        }

        if (categoriaDao.getByNombre(nombre.trim()) != null) {
            throw IllegalStateException("Ya existe una categoría con nombre \"${nombre.trim()}\"")
        }

        categoriaDao.insert(
            CategoriaEntity(
                nombre = nombre.trim(),
                descripcion = descripcion.trim()
            )
        )
    }

    suspend fun obtenerCategoriasNombres(): List<String> =
        categoriaDao.getAllC().map { it.nombre }.distinct().sorted()
}
