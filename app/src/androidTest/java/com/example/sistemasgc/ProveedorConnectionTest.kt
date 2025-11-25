package com.example.sistemasgc

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sistemasgc.Remote.RetrofitInstance
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProveedorConnectionTest {

    @Test
    fun probarCrearProveedorEnBackend() = runTest {
        println("🎯 PROBANDO CREAR PROVEEDOR EN BACKEND...")

        try {
            val timestamp = System.currentTimeMillis()
            val nuevoProveedor = com.example.sistemasgc.Remote.model.ProveedorRequest(
                name = "Proveedor Test $timestamp",
                rut = "123456789",
                phone = "987654321",
                email = "test$timestamp@ejemplo.com",
                direccion = "Calle Test 123"
            )

            println("📤 Enviando datos:")
            println("   Nombre: ${nuevoProveedor.name}")
            println("   RUT: ${nuevoProveedor.rut}")
            println("   Teléfono: ${nuevoProveedor.phone}")
            println("   Email: ${nuevoProveedor.email}")
            println("   Dirección: ${nuevoProveedor.direccion}")

            val response = RetrofitInstance.api.createProveedor(nuevoProveedor)

            println("✅ ¡PROVEEDOR CREADO EXITOSAMENTE!")
            println("   ID asignado: ${response.id}")

        } catch (e: retrofit2.HttpException) {
            println("❌ ERROR HTTP ${e.code()}")
            // Intentar leer el cuerpo del error
            try {
                val errorBody = e.response()?.errorBody()?.string()
                println("📝 Mensaje de error: $errorBody")
            } catch (ex: Exception) {
                println("📝 No se pudo leer el cuerpo del error")
            }
            throw e
        } catch (e: Exception) {
            println("❌ ERROR: ${e.message}")
            throw e
        }
    }
}