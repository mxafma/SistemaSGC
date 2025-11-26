package com.example.sistemasgc

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.ViewModel
import com.example.sistemasgc.data.local.Categoria.CategoriaEntity
import com.example.sistemasgc.ui.screen.CategoriaScreenVM
import com.example.sistemasgc.ui.viewmodel.AuthViewModel
import com.example.sistemasgc.ui.viewmodel.CategoriaUiState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class CategoriaScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_titulo_de_la_pantalla_categoria_debe_aparecer_en_pantalla() {
        val fakeViewModel = object : ViewModel() {
            override val categoria = MutableStateFlow(
                CategoriaUiState()
            )
        }

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
    fun los_campos_de_nombre_y_descripcion_deben_aparecer_en_pantalla() {
        val fakeViewModel = object : ViewModel() {
            override val categoria = MutableStateFlow(
                CategoriaUiState()
            )
        }

        composeRule.setContent {
            CategoriaScreenVM(
                viewModel = fakeViewModel,
                onCancel = {},
                onSuccess = {}
            )
        }

        composeRule.onNodeWithText("Nombre").assertIsDisplayed()
        composeRule.onNodeWithText("Descripción").assertIsDisplayed()
    }

    @Test
    fun los_botones_cancelar_y_guardar_deben_aparecer_en_pantalla() {
        val fakeViewModel = object : ViewModel() {
            override val categoria = MutableStateFlow(
                CategoriaUiState()
            )
        }

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
        val fakeViewModel = object : ViewModel() {
            override val categoria = MutableStateFlow(
                CategoriaUiState(
                    errorMsg = "El nombre de la categoría ya existe"
                )
            )
        }

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
        val fakeViewModel = object : ViewModel() {
            override val categoria = MutableStateFlow(
                CategoriaUiState(
                    isSubmitting = true
                )
            )
        }

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
        val fakeViewModel = object : ViewModel() {
            override val categoria = MutableStateFlow(
                CategoriaUiState(
                    nombreError = "Debe tener al menos 3 caracteres"
                )
            )
        }

        composeRule.setContent {
            CategoriaScreenVM(
                viewModel = fakeViewModel,
                onCancel = {},
                onSuccess = {}
            )
        }

        composeRule.onNodeWithText("Debe tener al menos 3 caracteres").assertIsDisplayed()
    }

    @Test
    fun lista_de_categorias_debe_mostrar_las_categorias_existentes() {
        val fakeCategorias = listOf(
            CategoriaEntity(1, "Electrónicos", "Productos electrónicos"),
            CategoriaEntity(2, "Ropa", "Prendas de vestir"),
            CategoriaEntity(3, "Hogar", "Artículos para el hogar")
        )

        val fakeViewModel = object : ViewModel() {
            override val categoria = MutableStateFlow(
                CategoriaUiState(
                    categorias = fakeCategorias
                )
            )
        }

        composeRule.setContent {
            CategoriaScreenVM(
                viewModel = fakeViewModel,
                onCancel = {},
                onSuccess = {}
            )
        }

        // Verificar que las categorías se muestran en pantalla
        composeRule.onNodeWithText("Electrónicos").assertIsDisplayed()
        composeRule.onNodeWithText("Ropa").assertIsDisplayed()
        composeRule.onNodeWithText("Hogar").assertIsDisplayed()
    }

    @Test
    fun mensaje_exitoso_debe_aparecer_cuando_categoria_se_guarda_correctamente() {
        val fakeViewModel = object : ViewModel() {
            override val categoria = MutableStateFlow(
                CategoriaUiState(
                    success = true
                )
            )
        }

        composeRule.setContent {
            CategoriaScreenVM(
                viewModel = fakeViewModel,
                onCancel = {},
                onSuccess = {}
            )
        }

        composeRule.onNodeWithText("Categoría guardada exitosamente").assertIsDisplayed()
    }
}