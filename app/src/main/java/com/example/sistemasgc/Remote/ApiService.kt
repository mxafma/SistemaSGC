package com.example.sistemasgc.Remote

import com.example.sistemasgc.data.local.Post.PostEntity
import com.example.sistemasgc.Remote.model.RegisterRequest
import com.example.sistemasgc.Remote.model.RegisterResponse
import com.example.sistemasgc.Remote.model.LoginRequest
import com.example.sistemasgc.Remote.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

//Esta interfaz define los endpoints HTTP
interface ApiService {

    //Define una solicitud get al endpoint/posts
    @GET("/posts")
    suspend fun getPosts(): List<PostEntity>

    //Define una solicitud POST para registrar un usuario
    @POST("api/usuarios")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse
    
    //Define una solicitud POST para login
    @POST("api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse
}