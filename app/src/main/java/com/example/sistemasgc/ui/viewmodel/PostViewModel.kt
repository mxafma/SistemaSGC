package com.example.sistemasgc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemasgc.data.local.Post.PostEntity
import com.example.sistemasgc.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
// ViewModel que mantiene el estado de los datos obtenidos

open class PostViewModel : ViewModel() {
    private val repository = PostRepository()
    //Flujo mutable que contiene la lista de posts

    protected  val _postList = MutableStateFlow<List<PostEntity>>(emptyList())
    //Flujo publico de solo lectura

    val postList: StateFlow<List<PostEntity>> = _postList
    // Se llama automaticamente al iniciar
    init {
        fetchPosts()
    }
    // Funcion que obtiene los datos en segundo plano
    open fun fetchPosts() {
        viewModelScope.launch {
            try {
                _postList.value = repository.getPosts()
            } catch (e: Exception) {
                println("Error al obtener datos: ${e.localizedMessage}")
            }
        }
    }
}
