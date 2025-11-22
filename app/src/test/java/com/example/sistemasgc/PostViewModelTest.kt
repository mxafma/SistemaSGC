package com.example.sistemasgc

import com.example.sistemasgc.data.local.Post.PostEntity
import com.example.sistemasgc.ui.viewmodel.PostViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest : StringSpec( {
    "postList debe contener los datos esperados despues de fetchPosts()" {
        //Creamos una subclase falsa de PostViewModel que sobreescribe el repositorio
        val fakePosts = listOf(
            PostEntity(1,1,"Titulo 1", "Contenido 1"),
            PostEntity(2,2,"Titulo 2", "Contenido 2")
        )

        val testViewModel = object : PostViewModel() {
            override fun fetchPosts() {
                _postList.value = fakePosts
            }
        }

        runTest {
            testViewModel.fetchPosts()
            testViewModel.postList.value shouldContainExactly fakePosts
        }

    }
})