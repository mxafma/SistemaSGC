package com.example.sistemasgc.Remote.model

import com.google.gson.annotations.SerializedName

data class ProductoResponse(
    @SerializedName("id")       val id: Long?,
    @SerializedName("nombre")   val nombre: String?,
    @SerializedName("sku")      val sku: String?,
    @SerializedName("categoria")val categoria: String?,
    @SerializedName("photoUri") val photoUri: String?
)
