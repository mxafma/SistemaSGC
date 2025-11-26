package com.example.sistemasgc.Remote.model

import com.google.gson.annotations.SerializedName

data class CategoriaResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String
)