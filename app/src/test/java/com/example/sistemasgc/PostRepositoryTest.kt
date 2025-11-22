package com.example.sistemasgc

import com.example.sistemasgc.data.local.Post.PostEntity
import com.example.sistemasgc.Remote.ApiService
import com.example.sistemasgc.data.repository.PostRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.mockk
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest

// Creamos una subclase de PostRepository para poder inyectar el ApiService manualmente

class TestablePostRepository(private val testApi: ApiService) : PostRepository() {
    override suspend fun getPosts(): List<PostEntity> {
        return testApi.getPosts()
    }
}

class PostRepositoryTest : StringSpec( {
    "getPost() debe retornar una lista de posts simulada" {
        val fakePosts = listOf(
            PostEntity(1,1,"Titulo 1", "Cuerpo 1"),
            PostEntity(2,2,"Titulo 2", "Cuerpo 2")
        )
        //2 Creamos un mock de ApiService
        val mockApi = mockk<ApiService>()
        coEvery { mockApi.getPosts() } returns fakePosts
        // Usamos la clase de test que inyectaste al mock
        val repo= TestablePostRepository(mockApi)
        // Ejecutamos el test
        runTest {
            val result = repo.getPosts()
            result shouldContainExactly fakePosts
        }
    }
})