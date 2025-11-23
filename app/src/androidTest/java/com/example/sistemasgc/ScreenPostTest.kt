package com.example.sistemasgc

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composer
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.sistemasgc.data.local.Post.PostEntity
import com.example.sistemasgc.ui.screen.PostScreen
import com.example.sistemasgc.ui.viewmodel.PostViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class ScreenPostTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_titulo_del_post_debe_aparecer_en_pantalla() {
        val fakeposts = listOf(
            PostEntity(1, 1, "Titulo 1", "Cuerpo 1"),
            PostEntity(2, 2, "Titulo 2", "Cuerpo 2"),
        )

        val fakeViewModel = object : PostViewModel() {
            override val postList = MutableStateFlow(fakeposts)
        }
        composeRule.setContent {
            PostScreen(fakeViewModel)
        }
        composeRule.onNodeWithText("Titulo 1: Titulo 1").assertIsDisplayed()
        composeRule.onNodeWithText("Titulo 2: Titulo 2").assertIsDisplayed()
    }
}