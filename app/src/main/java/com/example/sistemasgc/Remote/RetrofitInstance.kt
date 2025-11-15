package com.example.sistemasgc.Remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

// Singleton que contiene la configuracion de Retrofit

object RetrofitInstance {
    // Se instancia el servicio de la API una sola vez
    val api : ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")  // URL base de la API
            .addConverterFactory(GsonConverterFactory.create()) // Conversor Json
            .build()
            .create(ApiService::class.java) // Implementa la interfaz ApiService

    }
}