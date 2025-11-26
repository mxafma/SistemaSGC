package com.example.sistemasgc.Remote

import com.example.sistemasgc.data.local.Post.PostEntity
import com.example.sistemasgc.Remote.model.RegisterRequest
import com.example.sistemasgc.Remote.model.RegisterResponse
import com.example.sistemasgc.Remote.model.LoginRequest
import com.example.sistemasgc.Remote.model.LoginResponse
import com.example.sistemasgc.Remote.model.ProveedorRequest
import com.example.sistemasgc.Remote.model.ProveedorResponse
import com.example.sistemasgc.Remote.model.ProductoRequest
import com.example.sistemasgc.Remote.model.ProductoResponse
import com.example.sistemasgc.Remote.model.CategoriaRequest
import com.example.sistemasgc.Remote.model.CategoriaResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

//Esta interfaz define los endpoints HTTP
interface ApiService {

    //Define una solicitud get al endpoint/posts
    @GET("/posts")
    suspend fun getPosts(): List<PostEntity>

    //Define una solicitud POST para registrar un usuario
    @POST("api/usuarios")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse
    
    //Define una solicitud POST para login
    @POST("api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse

    // -------------------PROVEEDORES (Crear y listar) ---------------------------

    @GET("api/proveedores")
    suspend fun getProveedores(): List<ProveedorResponse>

    @POST("api/proveedores")
    suspend fun createProveedor(@Body request: ProveedorRequest): ProveedorResponse

    // -------------------PRODUCTOS (Crear y listar) ---------------------------
    @POST("api/productos")
    suspend fun createProducto(@Body request: ProductoRequest): ProductoResponse

    @GET("api/productos")
    suspend fun getProductos(): List<ProductoResponse>

    // --------------------------- Categorias (Crear, Listar y buscar poor nombre ) ---------------------------------------

    @GET("/api/categorias")
    suspend fun getCategorias(): List<CategoriaResponse>


    @POST("/api/categorias")
    suspend fun createCategoria(@Body request: CategoriaRequest): CategoriaResponse

    // ------------------- COMPRAS (Crear) ---------------------------
    @POST("/api/compras")
    suspend fun createCompra(@Body request: com.example.sistemasgc.Remote.model.CompraRequest): com.example.sistemasgc.Remote.model.CompraRequest

    // Obtener todas las compras (historial)
    @GET("/api/compras")
    suspend fun getCompras(): List<com.example.sistemasgc.Remote.model.CompraResponse>

    // Eliminar compra por id
    @DELETE("/api/compras/{id}")
    suspend fun deleteCompra(@Path("id") id: Long)

    // ------------------- DETALLE COMPRAS (Crear) -------------------
    @POST("/api/detalle-compras")
    suspend fun createDetalleCompra(@Body request: com.example.sistemasgc.Remote.model.DetalleCompraRequest): com.example.sistemasgc.Remote.model.DetalleCompraRequest


    @GET("/api/categorias/nombres")
    suspend fun getNombresCategorias(): List<String>
}


