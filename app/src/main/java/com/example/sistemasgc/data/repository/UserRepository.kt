package com.example.sistemasgc.data.repository

import com.example.sistemasgc.data.local.user.UserDao       // DAO de usuario
import com.example.sistemasgc.data.local.user.UserEntity    // Entidad de usuario
import com.example.sistemasgc.data.local.Proveedor.ProveedorDao
import com.example.sistemasgc.data.local.Proveedor.ProveedorEntity
// Repositorio: orquesta reglas de negocio para login/registro sobre el DAO.
class UserRepository(
    private val userDao: UserDao, // Inyección del DAO
    private val proveedorDao: ProveedorDao
) {

    // Login: busca por email y valida contraseña
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)                         // Busca usuario
        return if (user != null && user.password == password) {      // Verifica pass
            Result.success(user)                                     // Éxito
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas")) // Error
        }
    }

    // Registro: valida no duplicado y crea nuevo usuario (con teléfono)
    suspend fun register(name: String, email: String, phone: String, password: String): Result<Long> {
        val exists = userDao.getByEmail(email) != null               // ¿Correo ya usado?
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val id = userDao.insert(                                     // Inserta nuevo
            UserEntity(
                name = name,
                email = email,
                phone = phone,                                       // Teléfono incluido
                password = password
            )
        )
        return Result.success(id)                                    // Devuelve ID generado
    }

    suspend fun proveedor(Pname: String, Prut: String, Pphone: String, Pemail: String, Pdireccion: String): Result<Long> {
        val existeProveedor = proveedorDao.getByEmailP(Pemail) != null          // ¿Correo ya usado?
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
        return Result.success(id)                                    // Devuelve ID generado
    }

    suspend fun obtenerTodosLosProveedores(): List<ProveedorEntity> {
        return proveedorDao.getAllP()
    }
}