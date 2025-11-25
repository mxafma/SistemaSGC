package com.example.sistemasgc.Remote.model

import com.google.gson.annotations.SerializedName

data class ProveedorRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("rut")
    val rut: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("direccion")
    val direccion: String? = null
)
