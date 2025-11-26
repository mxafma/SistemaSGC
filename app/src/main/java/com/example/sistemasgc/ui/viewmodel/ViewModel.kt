package com.example.sistemasgc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemasgc.data.local.Categoria.CategoriaEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.sistemasgc.domain.validation.*
import com.example.sistemasgc.data.repository.DataRepository
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

data class ProductoUiState(
    val nombre: String = "",
    val sku: String = "",
    val categoria: String = "",
    val photoUri: String? = null,

    val nombreError: String? = null,
    val skuError: String? = null,
    val categoriaError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null,
    val savedId: Long? = null
)

data class CategoriaUiState(
    val nombre: String = "",
    val descripcion: String = "",

    val nombreError: String? = null,
    val descripcionError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null,

    val categorias: List<CategoriaEntity> = emptyList(),
    val nombresCategorias: List<String> = emptyList(),
    val isLoading: Boolean = false
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

class AuthViewModel(
    private val repository: DataRepository
) : ViewModel() {

    // --------- Estado global de sesión ---------
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // Flujos de estado de pantallas
    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    private val _proveedor = MutableStateFlow(ProveedoresUiState())
    val proveedor: StateFlow<ProveedoresUiState> = _proveedor

    // --------- Producto ---------
    private val _producto = MutableStateFlow(ProductoUiState())
    val producto: StateFlow<ProductoUiState> = _producto

    private val _productosNombres = MutableStateFlow<List<String>>(emptyList())
    val productosNombres: StateFlow<List<String>> = _productosNombres

    // --------- Categorias ---------
    private val _categoria = MutableStateFlow(CategoriaUiState())
    val categoria: StateFlow<CategoriaUiState> = _categoria

    // --------- Compras ---------
    private val _compras = MutableStateFlow(ComprasUiState())
    val compras: StateFlow<ComprasUiState> = _compras

    private val formasPago = listOf(
        "Efectivo",
        "Transferencia",
        "Tarjeta",
        "Crédito (Pago Pendiente)"
    )

    init {
        establecerFechaActualCompras()
        cargarProveedoresParaCompras()
        viewModelScope.launch {
            loadNombresCategorias()
        }
    }

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

    // --------- COMPRAS ---------

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
                        id = entity.id.toString(),
                        nombre = entity.name,
                        rut = entity.rut
                    )
                }
                _compras.update { it.copy(proveedores = proveedores) }
            } catch (e: Exception) {
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

            delay(500)

            try {
                println("Compra agregada: ${state.proveedorSeleccionado}")

                _compras.update {
                    it.copy(
                        isSubmitting = false,
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
                        isSubmitting = false,
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
                direccionError = validateDireccion(value)
            )
        }
        recomputeProveedorCanSubmit()
    }

    private fun recomputeProveedorCanSubmit() {
        val s = _proveedor.value
        val noErrors = listOf(
            s.nameError,
            s.rutError,
            s.phoneError,
            s.emailError
        ).all { it == null }

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

            val direccionParaGuardar = s.direccion.trim().takeIf { it.isNotBlank() }

            val result = repository.proveedor(
                Pname = s.name.trim(),
                Prut = s.rut.trim(),
                Pphone = s.phone.trim(),
                Pemail = s.email.trim(),
                Pdireccion = direccionParaGuardar
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

    fun onProductoSkuChange(value: String) {
        val v = value.trim()
        val error = if (v.isNotEmpty() && !v.all { it.isDigit() }) "Solo números" else null
        _producto.update { it.copy(sku = value, skuError = error) }
        recomputeProductoCanSubmit()
    }

    fun onProductoCategoriaChange(value: String) {
        _producto.update { it.copy(categoria = value, categoriaError = null) }
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

        // Validación local antes de enviar
        val nombreError = when {
            s.nombre.isBlank() -> "Requerido"
            s.nombre.length < 4 -> "Debe tener al menos 4 caracteres"
            else -> null
        }

        val skuError = if (s.sku.isNotEmpty() && !s.sku.all { it.isDigit() }) "Solo números" else null

        // Si hay errores locales, no enviar
        if (nombreError != null || skuError != null) {
            _producto.update {
                it.copy(
                    nombreError = nombreError,
                    skuError = skuError,
                    canSubmit = false
                )
            }
            return
        }

        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _producto.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            try {
                val result = repository.agregarProducto(
                    nombre = s.nombre.trim(),
                    sku = s.sku.trim().ifBlank { null },
                    photoUri = s.photoUri,
                    categoria = s.categoria.trim().ifBlank { null }
                )

                if (result.isSuccess) {
                    loadProductos()
                    _producto.update {
                        it.copy(
                            isSubmitting = false,
                            success = true,
                            errorMsg = null,
                            nombre = "",
                            sku = "",
                            categoria = "",
                            photoUri = null
                        )
                    }
                } else {
                    // ✅ Propaga el error del backend correctamente
                    val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _producto.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMsg = errorMessage
                        )
                    }
                }
            } catch (e: Exception) {
                // ✅ Captura cualquier excepción no manejada
                _producto.update {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }

    fun clearProductoResult() {
        _producto.update {
            it.copy(
                success = false,
                errorMsg = null,
                nombreError = null,
                skuError = null,
                categoriaError = null
            )
        }
    }

    // Sugerencias de categorías para el combo (solo nombres)
    suspend fun getCategoriasSugeridas(): List<String> {
        return try {
            repository.obtenerNombresCategorias()
        } catch (_: Exception) {
            emptyList()
        }
    }

    // --------- Cerrar sesión ---------
    fun logout() {
        _isLoggedIn.value = false
        _login.update { LoginUiState() }
        _register.update { RegisterUiState() }
    }

    // --------- CATEGORÍA ---------
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
        val noErrors = listOf(s.nombreError).all { it == null }
        val filled = s.nombre.isNotBlank()
        _categoria.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitCategoria() {
        val s = _categoria.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _categoria.update {
                it.copy(
                    isSubmitting = true,
                    errorMsg = null,
                    success = false
                )
            }

            val result = repository.agregarCategoria(
                nombre = s.nombre.trim(),
                descripcion = s.descripcion.trim()
            )

            _categoria.update {
                if (result.isSuccess) {
                    loadCategorias()
                    loadNombresCategorias()
                    it.copy(
                        isSubmitting = false,
                        success = true,
                        errorMsg = null,
                        nombre = "",
                        descripcion = ""
                    )
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo guardar la categoría"
                    )
                }
            }
        }
    }

    // Cargar todas las categorías con sus datos completos
    fun loadCategorias() {
        viewModelScope.launch {
            _categoria.update { it.copy(isLoading = true) }
            try {
                val categorias = repository.obtenerTodasLasCategorias()
                _categoria.update {
                    it.copy(
                        categorias = categorias,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _categoria.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = "Error al cargar categorías: ${e.message}"
                    )
                }
            }
        }
    }

    // Cargar solo los nombres de las categorías (para combobox, etc.)
    fun loadNombresCategorias() {
        viewModelScope.launch {
            try {
                val nombres = repository.obtenerNombresCategorias()
                _categoria.update {
                    it.copy(nombresCategorias = nombres)
                }
            } catch (e: Exception) {
                println("Error cargando nombres de categorías: ${e.message}")
            }
        }
    }

    // Sincronizar categorías con el backend
    fun sincronizarCategorias() {
        viewModelScope.launch {
            _categoria.update { it.copy(isLoading = true) }
            try {
                repository.sincronizarCategoriasDesdeBackend()
                loadCategorias()
                loadNombresCategorias()
                _categoria.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _categoria.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = "Error sincronizando categorías: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearCategoriaResult() {
        _categoria.update {
            it.copy(
                success = false,
                errorMsg = null,
                nombreError = null,
                descripcionError = null
            )
        }
    }

    // Función para limpiar completamente el formulario de categoría
    fun resetCategoriaForm() {
        _categoria.update {
            CategoriaUiState(
                categorias = it.categorias,
                nombresCategorias = it.nombresCategorias
            )
        }
    }

    // --------- Productos cargar lista ---------
    fun loadProductos() {
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