package com.example.sistemasgc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.sistemasgc.domain.validation.*
import com.example.sistemasgc.data.repository.UserRepository
import com.example.sistemasgc.data.local.Proveedor.ProveedorEntity
import java.util.*
import java.text.SimpleDateFormat

// ----------------- ESTADOS DE UI (observable con StateFlow) -----------------

data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val emailError: String? = null,
    val passError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val pass: String = "",
    val confirm: String = "",

    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

data class ProveedoresUiState(
    val name: String = "",
    val rut: String = "",
    val phone: String = "",
    val email: String = "",
    val direccion: String = "",

    val nameError: String? = null,
    val rutError: String? = null,
    val phoneError: String? = null,
    val emailError: String? = null,
    val direccionError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

/**
 * 🔄 NUEVO MODELO para Agregar Producto
 * - id: lo genera Room; no se ingresa. Tras guardar, lo exponemos en savedId.
 * - sku: opcional
 * - photoUri: opcional
 * - nombre: obligatorio (≥ 6)
 * - categoria: opcional (si quieres exigirla, marca el comentario en validación)
 */
data class ProductoUiState(
    val nombre: String = "",
    val sku: String = "",
    val categoria: String = "",
    val photoUri: String? = null,

    val nombreError: String? = null,
    val skuError: String? = null,        // por ahora no validamos formato; reservado si quieres agregar
    val categoriaError: String? = null,  // si decides hacerla obligatoria

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null,
    val savedId: Long? = null            // 👈 ID autogenerado por Room después de guardar
)

data class CategoriaUiState(
    val nombre: String = "",
    val descripcion: String = "",

    val nombreError: String? = null,
    val descripcionError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

data class Proveedor(
    val id: String,
    val nombre: String,
    val rut: String
)



data class ComprasUiState(
    val proveedorSeleccionado: String = "",
    val formaPagoSeleccionada: String = "",
    val fechaSeleccionada: String = "",
    val proveedores: List<Proveedor> = emptyList(),
    val mostrarSelectorFecha: Boolean = false,
    val isSubmitting: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

// ELIMINA completamente la data class CompraState

class AuthViewModel(
    // Repositorio real (Room/SQLite o el que uses)
    private val repository: UserRepository
) : ViewModel() {

    // --------- NUEVO: estado global de sesión ---------
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // Flujos de estado de pantallas
    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    private val _proveedor = MutableStateFlow(ProveedoresUiState())
    val proveedor: StateFlow<ProveedoresUiState> = _proveedor

    // --------- NUEVO: Producto ---------
    private val _producto = MutableStateFlow(ProductoUiState())
    val producto: StateFlow<ProductoUiState> = _producto

    private val _productosNombres = MutableStateFlow<List<String>>(emptyList())
    val productosNombres: StateFlow<List<String>> = _productosNombres

    // --------- Categorias ---------
    private val _categoria = MutableStateFlow(CategoriaUiState())
    val categoria: StateFlow<CategoriaUiState> = _categoria

    // --------- LOGIN ---------

    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String) {
        _login.update { it.copy(pass = value) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        val can = s.emailError == null &&
                s.email.isNotBlank() &&
                s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(500)

            val result = repository.login(s.email.trim(), s.pass)

            _login.update {
                if (result.isSuccess) {
                    // ✅ Marca sesión iniciada
                    _isLoggedIn.value = true
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error de autenticación"
                    )
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // --------- NUEVO: Estado de Compras ---------
    private val _compras = MutableStateFlow(ComprasUiState())
    val compras: StateFlow<ComprasUiState> = _compras

    // Formas de pago predefinidas
    private val formasPago = listOf(
        "Efectivo",
        "Transferencia",
        "Tarjeta",
        "Crédito (Pago Pendiente)"
    )

    init {
        // Inicializar compras con fecha actual y cargar proveedores
        establecerFechaActualCompras()
        cargarProveedoresParaCompras()
    }

    // --------- FUNCIONES DE COMPRAS ---------

    private fun establecerFechaActualCompras() {
        val fechaActual = obtenerFechaActualFormateada()
        _compras.update { it.copy(fechaSeleccionada = fechaActual) }
    }

    private fun obtenerFechaActualFormateada(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun cargarProveedoresParaCompras() {
        viewModelScope.launch {
            try {
                val proveedoresEntities = repository.obtenerTodosLosProveedores()
                val proveedores = proveedoresEntities.map { entity ->
                    Proveedor(
                        id = entity.Pid.toString(),
                        nombre = entity.Pname,
                        rut = entity.Prut
                    )
                }
                _compras.update { it.copy(proveedores = proveedores) }
            } catch (e: Exception) {
                // En caso de error, usar lista vacía o datos de ejemplo
                val proveedoresEjemplo = listOf(
                    Proveedor("1", "Proveedor A", "12345678-9"),
                    Proveedor("2", "Proveedor B", "87654321-0")
                )
                _compras.update { it.copy(proveedores = proveedoresEjemplo) }
            }
        }
    }

    fun onComprasProveedorChange(proveedor: String) {
        _compras.update { it.copy(proveedorSeleccionado = proveedor) }
    }

    fun onComprasFormaPagoChange(formaPago: String) {
        _compras.update { it.copy(formaPagoSeleccionada = formaPago) }
    }

    fun onComprasFechaChange(fecha: String) {
        _compras.update { it.copy(fechaSeleccionada = fecha) }
    }


    fun getFormasPago(): List<String> = formasPago

    fun submitCompra(
        onSuccess: () -> Unit = {}
    ) {
        val state = _compras.value
        if (state.proveedorSeleccionado.isBlank() || state.formaPagoSeleccionada.isBlank()) {
            _compras.update {
                it.copy(errorMsg = "Complete todos los campos requeridos")
            }
            return
        }

        viewModelScope.launch {
            _compras.update { it.copy(isSubmitting = true, errorMsg = null) }

            delay(500) // Simular procesamiento

            try {
                // Lógica de guardado...
                println("Compra agregada: ${state.proveedorSeleccionado}")

                _compras.update {
                    it.copy(
                        isSubmitting = false,  // ✅ IMPORTANTE: resetear isSubmitting
                        success = true,
                        proveedorSeleccionado = "",
                        formaPagoSeleccionada = "",
                        errorMsg = null
                    )
                }
                establecerFechaActualCompras()
                onSuccess()

            } catch (e: Exception) {
                _compras.update {
                    it.copy(
                        isSubmitting = false,  // ✅ IMPORTANTE: resetear incluso en error
                        success = false,
                        errorMsg = "Error al guardar la compra: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearComprasResult() {
        _compras.update {
            it.copy(
                success = false,
                errorMsg = null,
                mostrarSelectorFecha = false
            )
        }
    }

    // --------- PROVEEDOR ---------

    // --------- PROVEEDOR ---------

    fun onProveedorNameChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _proveedor.update {
            it.copy(
                name = filtered,
                nameError = validateNameLettersOnly(filtered)
            )
        }
        recomputeProveedorCanSubmit()
    }

    fun onProveedorEmailChange(value: String) {
        _proveedor.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeProveedorCanSubmit()
    }

    fun onProveedorPhoneChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _proveedor.update {
            it.copy(
                phone = digitsOnly,
                phoneError = validatePhoneDigitsOnly(digitsOnly)
            )
        }
        recomputeProveedorCanSubmit()
    }

    fun onProveedorRutChange(value: String) {
        _proveedor.update { it.copy(rut = value, rutError = validateRutChileno(value)) }
        recomputeProveedorCanSubmit()
    }

    fun onProveedorDireccionChange(value: String) {
        _proveedor.update {
            it.copy(
                direccion = value,
                direccionError = validateDireccion(value) // ← ¡AGREGA ESTA LÍNEA!
            )
        }
        recomputeProveedorCanSubmit() // ← También actualiza el estado del botón
    }

    private fun recomputeProveedorCanSubmit() {
        val s = _proveedor.value
        val noErrors = listOf(
            s.nameError,
            s.rutError,
            s.phoneError,
            s.emailError
            // s.direccionError se omite porque es opcional
        ).all { it == null }

        // Dirección NO es requerida
        val filled = s.name.isNotBlank() && s.rut.isNotBlank() && s.phone.isNotBlank() && s.email.isNotBlank()

        _proveedor.update { it.copy(canSubmit = noErrors && filled) }
    }

    suspend fun obtenerProveedores(): List<ProveedorEntity> {
        return repository.obtenerTodosLosProveedores()
    }

    fun submitProveedor() {
        val s = _proveedor.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _proveedor.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(700)

            // ✅ Dirección opcional: solo se envía si no está vacía
            val direccionParaGuardar = s.direccion.trim().takeIf { it.isNotBlank() }

            val result = repository.proveedor(
                Pname = s.name.trim(),
                Prut = s.rut.trim(),
                Pphone = s.phone.trim(),
                Pemail = s.email.trim(),
                Pdireccion = direccionParaGuardar // ← Opcional
            )

            _proveedor.update {
                if (result.isSuccess) {
                    cargarProveedoresParaCompras()
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar"
                    )
                }
            }
        }
    }

    fun clearProveedorResult() {
        _proveedor.update { it.copy(success = false, errorMsg = null) }
    }



    // --------- REGISTER ---------

    fun onNameChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update { it.copy(name = filtered, nameError = validateNameLettersOnly(filtered)) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onPhoneChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _register.update {
            it.copy(
                phone = digitsOnly,
                phoneError = validatePhoneDigitsOnly(digitsOnly)
            )
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(pass = value, passError = validateStrongPassword(value)) }
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        _register.update {
            it.copy(
                confirm = value,
                confirmError = validateConfirm(it.pass, value)
            )
        }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(
            s.nameError,
            s.emailError,
            s.phoneError,
            s.passError,
            s.confirmError
        ).all { it == null }
        val filled =
            s.name.isNotBlank() && s.email.isNotBlank() && s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(700)

            val result = repository.register(
                name = s.name.trim(),
                email = s.email.trim(),
                phone = s.phone.trim(),
                password = s.pass
            )

            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar"
                    )
                }
            }
        }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    // --------- PRODUCTO ---------

    fun onProductoNombreChange(value: String) {
        val trimmed = value
        val error = when {
            trimmed.isBlank()   -> "Requerido"
            trimmed.length < 4  -> "Debe tener al menos 4 caracteres"
            else                -> null
        }
        _producto.update { it.copy(nombre = trimmed, nombreError = error) }
        recomputeProductoCanSubmit()
    }

    fun clearProductoResult() {
        _producto.value = ProductoUiState()
    }

    fun onProductoSkuChange(value: String) {
        // SKU es opcional, pero si hay contenido DEBE ser numérico
        val v = value.trim()
        val error = if (v.isNotEmpty() && !v.all { it.isDigit() }) "Solo números" else null
        _producto.update { it.copy(sku = value, skuError = error) }
        recomputeProductoCanSubmit()
    }

    fun onProductoCategoriaChange(value: String) {
        // Categoría sigue siendo opcional; si la quieres obligatoria, agrega validación aquí.
        _producto.update { it.copy(categoria = value, categoriaError = null) }
        // sin impacto en canSubmit
    }

    fun onProductoSetPhoto(uri: String?) {
        _producto.update { it.copy(photoUri = uri) }
    }

    private fun recomputeProductoCanSubmit() {
        val s = _producto.value
        val noErrors = listOf(
            s.nombreError,
            s.skuError
        ).all { it == null }

        val filled = s.nombre.isNotBlank() && (s.nombreError == null)
        _producto.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitProducto() {
        val s = _producto.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _producto.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            val result = try {
                val id = repository.agregarProducto(
                    nombre   = s.nombre.trim(),
                    sku      = s.sku.trim().ifBlank { null },
                    photoUri = s.photoUri,
                    categoria= s.categoria.trim().ifBlank { null }
                )
                Result.success(id)
            } catch (e: Exception) {
                Result.failure(e)
            }

            _producto.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message
                            ?: "No se pudo guardar el producto"
                    )
                }
            }

            // ✅ NUEVO: refrescar la lista de nombres tras un alta exitosa
            if (result.isSuccess) {
                loadProductos() // ← vuelve a publicar _productosNombres
            }
        }
    }

    // Sugerencias de categorías para el combo (solo nombres)
    suspend fun getCategoriasSugeridas(): List<String> {
        return try {
            repository.obtenerCategoriasNombres()   // <- este método lo agregamos en el repo
        } catch (_: Exception) {
            emptyList()
        }
    }

    // --------- NUEVO: Cerrar sesión ---------
    fun logout() {
        _isLoggedIn.value = false
        _login.update { LoginUiState() }
        _register.update { RegisterUiState() }
    }

    // --------- CATEGORÍA: onChange ---------
    fun onCategoriaNombreChange(value: String) {
        _categoria.update {
            it.copy(
                nombre = value,
                nombreError = when {
                    value.isBlank() -> "Requerido"
                    value.trim().length < 3 -> "Debe tener al menos 3 caracteres"
                    else -> null
                }
            )
        }
        recomputeCategoriaCanSubmit()
    }

    fun onCategoriaDescripcionChange(value: String) {
        _categoria.update { it.copy(descripcion = value, descripcionError = null) }
        recomputeCategoriaCanSubmit()
    }

    private fun recomputeCategoriaCanSubmit() {
        val s = _categoria.value
        val noErrors = listOf(s.nombreError /*, s.descripcionError*/).all { it == null }
        val filled = s.nombre.isNotBlank() // descripción opcional
        _categoria.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitCategoria() {
        val s = _categoria.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _categoria.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            val result = try {
                repository.agregarCategoria(
                    nombre = s.nombre.trim(),
                    descripcion = s.descripcion.trim()
                )
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }

            _categoria.update {
                if (result.isSuccess) it.copy(isSubmitting = false, success = true, errorMsg = null)
                else it.copy(
                    isSubmitting = false,
                    success = false,
                    errorMsg = result.exceptionOrNull()?.message ?: "No se pudo guardar la categoría"
                )
            }
        }
    }

    fun clearCategoriaResult() {
        _categoria.update { CategoriaUiState() }
    }

    // --------- NUEVO: Productos cargar lista ---------
    fun loadProductos() {
        // Lée desde Room -> Repository y publica los nombres
        viewModelScope.launch {
            val nombres = try {
                repository.obtenerTodosLosProductos().map { it.nombre }
            } catch (e: Exception) {
                emptyList()
            }
            _productosNombres.value = nombres.distinct().sorted()
        }
    }
}
