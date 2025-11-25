package com.example.sistemasgc.Remote.model

data class RegisterRequest(
    val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)
