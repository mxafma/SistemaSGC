package com.example.sistemasgc.Remote.model

import com.google.gson.annotations.SerializedName

data class ProductoRequest(
    @SerializedName("nombre")   val nombre: String,
    @SerializedName("sku")      val sku: String?,      // opcional
    @SerializedName("categoria")val categoria: String?,// opcional
    @SerializedName("photoUri") val photoUri: String?  // opcional
)
