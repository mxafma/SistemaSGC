package com.example.sistemasgc.Remote.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String?,

    @SerializedName("role")
    val role: String?,

    @SerializedName("user")
    val user: UserDto?
)

data class UserDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("role")
    val role: String?
)
