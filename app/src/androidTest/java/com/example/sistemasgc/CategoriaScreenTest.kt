package com.example.sistemasgc

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.sistemasgc.ui.screen.CategoriaScreenVM
import com.example.sistemasgc.ui.viewmodel.AuthViewModel
import com.example.sistemasgc.ui.viewmodel.CategoriaUiState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class CategoriaScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * ViewModel mock con estado reactivo real
     */
    private fun mockCategoriaViewModel(initial: CategoriaUiState): AuthViewModel {
        val flow = MutableStateFlow(initial)

        val vm = mockk<AuthViewModel>(relaxed = true)

        // Estado reactivo
        every { vm.categoria } returns flow

        // Simula escribir nombre
        every { vm.onCategoriaNombreChange(any()) } answers {
            flow.value = flow.value.copy(nombre = arg(0))
        }

        // Simula escribir descripción
        every { vm.onCategoriaDescripcionChange(any()) } answers {
            flow.value = flow.value.copy(descripcion = arg(0))
        }

        // Simula limpiar errores
        every { vm.clearCategoriaResult() } answers {
            flow.value = flow.value.copy(
                errorMsg = null,
                success = false,
                nombreError = null,
                descripcionError = null
            )
        }

        return vm
    }

    @Test
    fun el_titulo_de_la_pantalla_categoria_debe_aparecer_en_pantalla() {
        val fakeViewModel = mockCategoriaViewModel(CategoriaUiState())

        composeRule.setContent {
            CategoriaScreenVM(
                viewModel = fakeViewModel,
                onCancel = {},
                onSuccess = {}
            )
        }

        composeRule.onNodeWithText("Agregar categoría").assertIsDisplayed()
    }



    @Test
    fun los_botones_cancelar_y_guardar_deben_aparecer_en_pantalla() {
        val fakeViewModel = mockCategoriaViewModel(CategoriaUiState())

        composeRule.setContent {
            CategoriaScreenVM(
                viewModel = fakeViewModel,
                onCancel = {},
                onSuccess = {}
            )
        }

        composeRule.onNodeWithText("Cancelar").assertIsDisplayed()
        composeRule.onNodeWithText("Guardar").assertIsDisplayed()
    }

    @Test
    fun mensaje_de_error_debe_aparecer_cuando_hay_error() {
        val fakeViewModel = mockCategoriaViewModel(
            CategoriaUiState(errorMsg = "El nombre de la categoría ya existe")
        )

        composeRule.setContent {
            CategoriaScreenVM(
                viewModel = fakeViewModel,
                onCancel = {},
                onSuccess = {}
            )
        }

        composeRule.onNodeWithText("El nombre de la categoría ya existe").assertIsDisplayed()
    }

    @Test
    fun texto_guardando_debe_aparecer_cuando_esta_procesando() {
        val fakeViewModel = mockCategoriaViewModel(
            CategoriaUiState(isSubmitting = true)
        )

        composeRule.setContent {
            CategoriaScreenVM(
                viewModel = fakeViewModel,
                onCancel = {},
                onSuccess = {}
            )
        }

        composeRule.onNodeWithText("Guardando...").assertIsDisplayed()
    }

    @Test
    fun mensaje_de_error_de_validacion_debe_aparecer_cuando_nombre_es_invalido() {
        val fakeViewModel = mockCategoriaViewModel(
            CategoriaUiState(nombreError = "Debe tener al menos 3 caracteres")
        )

        composeRule.setContent {
            CategoriaScreenVM(
                viewModel = fakeViewModel,
                onCancel = {},
                onSuccess = {}
            )
        }

        composeRule.onNodeWithText("Debe tener al menos 3 caracteres").assertIsDisplayed()
    }
}
