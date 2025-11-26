package com.example.sistemasgc.Remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

// Singleton que contiene la configuracion de Retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitInstance {
    // Se instancia el servicio de la API una sola vez
    val api: ApiService by lazy {
        // Interceptor que añade Authorization si existe token
        val authInterceptor = Interceptor { chain ->
            val request = chain.request()
            val requestPath = request.url.encodedPath
            val reqBuilder: Request.Builder = request.newBuilder()

            // No añadir header de auth para endpoints de login/registro u otros públicos
            val isAuthEndpoint = requestPath.startsWith("/api/auth") || requestPath.startsWith("/api/usuarios")

            val token = TokenStore.getToken()
            if (!token.isNullOrBlank() && !isAuthEndpoint) {
                reqBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(reqBuilder.build())
        }

        // Logging interceptor — útil para depurar solicitudes/respuestas desde la app.
        // Puedes bajar el nivel o removerlo en producción.
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            // Interceptor que detecta 401/403 y borra token local (forzar re-login)
            .addInterceptor { chain ->
                val response = chain.proceed(chain.request())
                if (response.code == 401 || response.code == 403) {
                    // Limpiar token/role/user
                    TokenStore.clear()
                }
                response
            }
            .build()

        Retrofit.Builder()
            .baseUrl("https://surprising-consideration-production-2946.up.railway.app/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}