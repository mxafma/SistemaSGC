package com.example.sistemasgc.Remote.model

import com.google.gson.annotations.SerializedName

data class CategoriaRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String
)