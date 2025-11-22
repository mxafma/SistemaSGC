package com.example.sistemasgc.data.repository

import com.example.sistemasgc.data.local.Post.PostEntity
import com.example.sistemasgc.Remote.RetrofitInstance

// Este repositorio se encarga de acceder a los datos usando Retrofit
open class PostRepository {

    //Funcion que obtiene los posts desde la API
    open suspend fun getPosts(): List<PostEntity> {
        return RetrofitInstance.api.getPosts()
    }
}