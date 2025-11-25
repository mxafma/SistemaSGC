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

// ✅ Retrofit para llamadas al API
import com.example.sistemasgc.Remote.RetrofitInstance
import com.example.sistemasgc.Remote.model.RegisterRequest
import com.example.sistemasgc.Remote.model.LoginRequest
import com.example.sistemasgc.Remote.model.ProveedorRequest


class DataRepository(
    private val userDao: UserDao,
    private val proveedorDao: ProveedorDao,
    private val productoDao: ProductoDao,
    private val categoriaDao: CategoriaDao
) {

    // -------------------- USUARIOS --------------------
    suspend fun login(email: String, password: String): Result<UserEntity> {
        return try {
            // Llamar al endpoint remoto del backend
            val request = LoginRequest(
                email = email,
                password = password
            )
            val response = RetrofitInstance.api.loginUser(request)

            // Guardar/actualizar usuario en base de datos local
            val localUser = userDao.getByEmail(email)
            if (localUser == null) {
                // Si no existe localmente, lo insertamos
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
                // Si ya existe, lo retornamos
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
            // Llamar al endpoint remoto del backend
            val request = RegisterRequest(
                name = name,
                email = email,
                phone = phone,
                password = password
            )
            val response = RetrofitInstance.api.registerUser(request)

            // Opcional: guardar también en la base de datos local
            // Usamos los datos originales enviados, no la respuesta
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
            // Error HTTP del servidor (400, 500, etc.)
            val errorBody = e.response()?.errorBody()?.string()
            val errorMsg = when {
                e.code() == 400 && errorBody?.contains("email", ignoreCase = true) == true -> "El correo ya está registrado"
                e.code() == 400 && errorBody?.contains("existe", ignoreCase = true) == true -> "El correo ya está registrado"
                e.code() == 400 -> "Datos inválidos. Verifica la información ingresada"
                e.code() == 409 -> "El correo ya está registrado"
                e.code() == 500 -> "Error del servidor. Intenta más tarde"
                else -> "Error al registrar: ${e.message()}"
            }
            Result.failure(IllegalStateException(errorMsg))
        } catch (e: java.net.UnknownHostException) {
            // Sin conexión a internet
            Result.failure(IllegalStateException("No hay conexión a internet"))
        } catch (e: Exception) {
            // Otros errores
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

            // Llamar al endpoint remoto del backend
            val request = ProveedorRequest(
                name = Pname,
                rut = Prut,
                phone = Pphone,
                email = Pemail,
                direccion = Pdireccion
            )
            val response = RetrofitInstance.api.createProveedor(request)

            // ✅ GUARDAR EN LOCAL SIN EL ID (igual que register)
            // Room generará automáticamente el id (Long)
            val id = proveedorDao.insert(
                ProveedorEntity(
                    name = response.name,
                    rut = response.rut,
                    phone = response.phone,
                    email = response.email,
                    direccion = response.direccion
                )
            )

            Result.success(id)  // ← Retornar el Long de Room
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMsg = when {
                e.code() == 400 && errorBody?.contains("email", ignoreCase = true) == true -> "El correo del proveedor ya está registrado"
                e.code() == 400 && errorBody?.contains("existe", ignoreCase = true) == true -> "El proveedor ya existe"
                e.code() == 400 -> "Datos inválidos del proveedor. Verifica la información ingresada"
                e.code() == 409 -> "El proveedor ya existe"
                e.code() == 500 -> "Error del servidor. Intenta más tarde"
                else -> "Error al crear proveedor: ${e.message()}"
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

            proveedorDao.deleteAllP() // Limpiar proveedores locales

            // Insertar los proveedores del backend SIN el id
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

            // Retornar desde local (que ahora está sincronizado)
            proveedorDao.getAllP()
        } catch (e: Exception) {
            // Si falla el backend, usar datos locales
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

    suspend fun obtenerCategorias(): List<CategoriaEntity> =
        categoriaDao.getAllC() // ajusta al nombre real de tu DAO

    suspend fun obtenerTodosLosProductos() = productoDao.getAll()
    suspend fun buscarProductos(q: String) = productoDao.search(q.trim())

    // -------------------- CATEGORIAS --------------------
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
