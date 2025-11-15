package com.example.sistemasgc.Remote

import com.example.sistemasgc.data.local.Post.PostEntity
import retrofit2.http.GET

//Esta interfaz define los endpoints HTTP
interface ApiService {

    //Define una solicitud get al endpoint/posts
    @GET("/posts")
    suspend fun getPosts(): List<PostEntity>
}