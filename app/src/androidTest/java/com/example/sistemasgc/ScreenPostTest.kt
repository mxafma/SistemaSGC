package com.example.sistemasgc

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.sistemasgc.data.local.Post.PostEntity
import com.example.sistemasgc.ui.screen.PostScreen
import com.example.sistemasgc.ui.viewmodel.PostViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
