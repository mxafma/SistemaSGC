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

import com.example.sistemasgc.Remote.model.CategoriaRequest
import com.example.sistemasgc.Remote.model.CategoriaResponse

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
    ): Result<Long> {
        return try {
            val cleanName = nombre.trim()
            if (cleanName.length < 4) {
                return Result.failure(IllegalArgumentException("El nombre debe tener al menos 4 caracteres"))
            }

            val cleanSku = sku?.trim()?.ifBlank { null }
            if (cleanSku != null) {
                if (!cleanSku.all { it.isDigit() }) {
                    return Result.failure(IllegalArgumentException("El SKU debe contener solo números"))
                }
                val dupBySku = productoDao.getBySku(cleanSku)
                if (dupBySku != null) {
                    return Result.failure(IllegalStateException("Ya existe un producto con SKU \"$cleanSku\""))
                }
            }

            val cleanCategoria = categoria?.trim()?.ifBlank { null }

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

            val id = productoDao.insert(entity)
            Result.success(id)

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

            Result.failure(IllegalStateException(msg))

        } catch (e: java.net.UnknownHostException) {
            Result.failure(IllegalStateException("No hay conexión a internet"))
        } catch (e: Exception) {
            Result.failure(IllegalStateException("Error al crear producto: ${e.message}"))
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

    suspend fun agregarCategoria(nombre: String, descripcion: String): Result<Long> {
        return try {
            val cleanNombre = nombre.trim()
            val cleanDescripcion = descripcion.trim()

            // Validación local
            if (cleanNombre.length < 3) {
                return Result.failure(IllegalArgumentException("El nombre debe tener al menos 3 caracteres"))
            }

            // Verificar duplicado localmente
            val existeCategoriaLocal = categoriaDao.getByNombre(cleanNombre) != null
            if (existeCategoriaLocal) {
                return Result.failure(IllegalStateException("Ya existe una categoría con nombre \"$cleanNombre\""))
            }

            // Crear en el backend
            val request = CategoriaRequest(
                nombre = cleanNombre,
                descripcion = cleanDescripcion
            )
            val response = RetrofitInstance.api.createCategoria(request)

            // Guardar localmente
            val id = categoriaDao.insert(
                CategoriaEntity(
                    nombre = response.nombre ?: cleanNombre, // ✅ Usa el valor local si el backend retorna null
                    descripcion = response.descripcion ?: cleanDescripcion
                )
            )

            Result.success(id)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val backendMessage = parseBackendErrorMessage(errorBody)

            val errorMsg = when {
                backendMessage != null ->
                    backendMessage

                e.code() == 400 && errorBody?.contains("nombre", ignoreCase = true) == true ->
                    "El nombre de la categoría ya está registrado"

                e.code() == 400 && errorBody?.contains("existe", ignoreCase = true) == true ->
                    "La categoría ya existe"

                e.code() == 400 ->
                    "Datos inválidos de la categoría. Verifica la información ingresada"

                e.code() == 409 ->
                    "La categoría ya existe"

                e.code() == 500 ->
                    "Error del servidor. Intenta más tarde"

                else ->
                    "Error al crear categoría: ${e.message()}"
            }
            Result.failure(IllegalStateException(errorMsg))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(IllegalStateException("No hay conexión a internet"))
        } catch (e: Exception) {
            Result.failure(IllegalStateException("Error al crear categoría: ${e.message}"))
        }
    }

    suspend fun obtenerTodasLasCategorias(): List<CategoriaEntity> {
        return try {
            // Primero intentar obtener del backend
            val remoteCategorias = RetrofitInstance.api.getCategorias()

            // Sincronizar con la base local
            categoriaDao.deleteAllC()

            remoteCategorias.forEach { categoria ->
                categoriaDao.insert(
                    CategoriaEntity(
                        nombre = categoria.nombre,
                        descripcion = categoria.descripcion
                    )
                )
            }

            categoriaDao.getAllC()
        } catch (e: Exception) {
            // Si falla, devolver las categorías locales
            categoriaDao.getAllC()
        }
    }

    suspend fun obtenerNombresCategorias(): List<String> {
        return try {
            // Intentar obtener del backend primero
            val remoteNombres = RetrofitInstance.api.getNombresCategorias()
            remoteNombres
        } catch (e: Exception) {
            // Si falla, obtener de la base local
            categoriaDao.getAllC().map { it.nombre }.distinct().sorted()
        }
    }

    suspend fun sincronizarCategoriasDesdeBackend(): List<CategoriaEntity> {
        return try {
            val remoteCategorias = RetrofitInstance.api.getCategorias()

            // Limpiar y actualizar la base local
            categoriaDao.deleteAllC()

            remoteCategorias.forEach { categoria ->
                categoriaDao.insert(
                    CategoriaEntity(
                        nombre = categoria.nombre,
                        descripcion = categoria.descripcion
                    )
                )
            }

            categoriaDao.getAllC()
        } catch (e: Exception) {
            throw IllegalStateException("Error al sincronizar categorías: ${e.message}")
        }
    }

    // Función auxiliar para eliminar todas las categorías locales (útil para testing)
    suspend fun limpiarCategoriasLocales() {
        categoriaDao.deleteAllC()
    }
}
