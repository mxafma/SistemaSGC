package com.example.sistemasgc.data.local.Post



data class PostEntity(
    val userID: Int, // ID del usuario que crea el post
    val id: Int, // ID DEL POST
    val title: String, // Titulo del post
    val body: String // Cuerpo o contenido del post
)