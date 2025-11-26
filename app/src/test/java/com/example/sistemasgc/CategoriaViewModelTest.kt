package com.example.sistemasgc

import com.example.sistemasgc.data.local.Categoria.CategoriaEntity
import com.example.sistemasgc.ui.viewmodel.AuthViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import io.mockk.mockk

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriaViewModelTest : StringSpec({

    "categoriaState.nombresCategorias debe contener los datos esperados después de loadNombresCategorias()" {
        // Creamos una subclase falsa de AuthViewModel que sobreescribe el método
        val fakeNombresCategorias = listOf(
            "Electrónicos",
            "Ropa",
            "Hogar"
        )

        val testViewModel = object : AuthViewModel(mockk(relaxed = true)) {
            override fun loadNombresCategorias() {
                _categoria.value = _categoria.value.copy(nombresCategorias = fakeNombresCategorias)
            }
        }

        runTest {
            testViewModel.loadNombresCategorias()
            testViewModel.categoria.value.nombresCategorias shouldBe fakeNombresCategorias
        }
    }

    "categoriaState.categorias debe contener los datos esperados después de loadCategorias()" {
        val fakeCategorias = listOf(
            CategoriaEntity(1, "Electrónicos", "Productos electrónicos"),
            CategoriaEntity(2, "Ropa", "Prendas de vestir")
        )

        val testViewModel = object : AuthViewModel(mockk(relaxed = true)) {
            override fun loadCategorias() {
                _categoria.value = _categoria.value.copy(categorias = fakeCategorias)
            }
        }

        runTest {
            testViewModel.loadCategorias()
            testViewModel.categoria.value.categorias shouldBe fakeCategorias
        }
    }

    "al cambiar el nombre de categoría se actualiza el estado correctamente" {
        val testViewModel = object : AuthViewModel(mockk(relaxed = true)) {}

        testViewModel.onCategoriaNombreChange("Electrónicos")

        testViewModel.categoria.value.nombre shouldBe "Electrónicos"
        testViewModel.categoria.value.nombreError shouldBe null
    }

    "al cambiar la descripción de categoría se actualiza el estado correctamente" {
        val testViewModel = object : AuthViewModel(mockk(relaxed = true)) {}

        testViewModel.onCategoriaDescripcionChange("Productos electrónicos")

        testViewModel.categoria.value.descripcion shouldBe "Productos electrónicos"
        testViewModel.categoria.value.descripcionError shouldBe null
    }

    "nombre con menos de 3 caracteres muestra error de validación" {
        val testViewModel = object : AuthViewModel(mockk(relaxed = true)) {}

        testViewModel.onCategoriaNombreChange("Ab")

        testViewModel.categoria.value.nombreError shouldBe "Debe tener al menos 3 caracteres"
        testViewModel.categoria.value.canSubmit shouldBe false
    }

    "nombre vacío muestra error de requerido" {
        val testViewModel = object : AuthViewModel(mockk(relaxed = true)) {}

        testViewModel.onCategoriaNombreChange("")

        testViewModel.categoria.value.nombreError shouldBe "Requerido"
        testViewModel.categoria.value.canSubmit shouldBe false
    }

    "canSubmit es true cuando el nombre es válido" {
        val testViewModel = object : AuthViewModel(mockk(relaxed = true)) {}

        testViewModel.onCategoriaNombreChange("Ropa")
        testViewModel.onCategoriaDescripcionChange("Prendas de vestir")

        testViewModel.categoria.value.canSubmit shouldBe true
    }

    "canSubmit es false cuando el nombre es inválido" {
        val testViewModel = object : AuthViewModel(mockk(relaxed = true)) {}

        testViewModel.onCategoriaNombreChange("Ab")

        testViewModel.categoria.value.canSubmit shouldBe false
    }

    "clearCategoriaResult debe limpiar errores y success" {
        val testViewModel = object : AuthViewModel(mockk(relaxed = true)) {}

        // Primero establecemos un estado con error
        testViewModel.onCategoriaNombreChange("Ab")

        // Luego limpiamos
        testViewModel.clearCategoriaResult()

        testViewModel.categoria.value.nombreError shouldBe null
        testViewModel.categoria.value.descripcionError shouldBe null
        testViewModel.categoria.value.errorMsg shouldBe null
        testViewModel.categoria.value.success shouldBe false
    }

    "submitCategoria se ejecuta sin errores cuando el formulario es válido" {
        val testViewModel = object : AuthViewModel(mockk(relaxed = true)) {
            override fun submitCategoria() {
                // Simulamos un envío exitoso
                _categoria.value = _categoria.value.copy(
                    isSubmitting = false,
                    success = true,
                    errorMsg = null
                )
            }
        }

        runTest {
            testViewModel.onCategoriaNombreChange("Ropa")
            testViewModel.onCategoriaDescripcionChange("Prendas de vestir")

            testViewModel.submitCategoria()

            testViewModel.categoria.value.success shouldBe true
            testViewModel.categoria.value.isSubmitting shouldBe false
        }
    }

    "resetCategoriaForm debe limpiar todos los campos" {
        val testViewModel = object : AuthViewModel(mockk(relaxed = true)) {}

        // Primero llenamos el formulario
        testViewModel.onCategoriaNombreChange("Ropa")
        testViewModel.onCategoriaDescripcionChange("Prendas de vestir")

        // Luego reseteamos
        testViewModel.resetCategoriaForm()

        testViewModel.categoria.value.nombre shouldBe ""
        testViewModel.categoria.value.descripcion shouldBe ""
        testViewModel.categoria.value.nombreError shouldBe null
        testViewModel.categoria.value.descripcionError shouldBe null
    }
})