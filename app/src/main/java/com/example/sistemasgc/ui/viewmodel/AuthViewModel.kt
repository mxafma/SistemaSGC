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
    val id: String = "",
    val categoria: String = "",

    val nombreError: String? = null,
    val idError: String? = null,
    val categoriaError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)
data class CategoriaUiState(
    val nombre: String = "",
    val id: String = "",
    val descripcion: String = "",

    val nombreError: String? = null,
    val idError: String? = null,
    val descripcionError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

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
        _proveedor.update { it.copy(rut = value, rutError = validateRut(value)) }
        recomputeProveedorCanSubmit()
    }

    fun onProveedorDireccionChange(value: String) {
        _proveedor.update { it.copy(direccion = value, direccionError = validateDireccion(value)) }
        recomputeProveedorCanSubmit()
    }

    private fun recomputeProveedorCanSubmit() {
        val s = _proveedor.value
        val noErrors = listOf(
            s.nameError,
            s.rutError,
            s.phoneError,
            s.emailError,
            s.direccionError
        ).all { it == null }
        val filled =
            s.name.isNotBlank() && s.rut.isNotBlank() && s.phone.isNotBlank() && s.email.isNotBlank() && s.direccion.isNotBlank()
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

            val result = repository.proveedor(
                Pname = s.name.trim(),
                Prut = s.rut.trim(),
                Pphone = s.phone.trim(),
                Pemail = s.email.trim(),
                Pdireccion = s.direccion.trim()
            )

            _proveedor.update {
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
        _producto.update {
            it.copy(
                nombre = value,
                nombreError = if (value.isBlank()) "Requerido" else null
            )
        }
        recomputeProductoCanSubmit()
    }

    fun onProductoIdChange(value: String) {
        _producto.update {
            it.copy(
                id = value,
                idError = if (value.isBlank()) "Requerido" else null
            )
        }
        recomputeProductoCanSubmit()
    }

    fun onProductoCategoriaChange(value: String) {
        _producto.update {
            it.copy(
                categoria = value,
                categoriaError = if (value.isBlank()) "Requerido" else null
            )
        }
        recomputeProductoCanSubmit()
    }

    private fun recomputeProductoCanSubmit() {
        val s = _producto.value
        val noErrors = listOf(s.nombreError, s.idError, s.categoriaError).all { it == null }
        val filled = s.nombre.isNotBlank() && s.id.isNotBlank() && s.categoria.isNotBlank()
        _producto.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitProducto() {
        val s = _producto.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _producto.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(500)

            // 🔧 Asegúrate de implementar este método en UserRepository
            val result = try {
                repository.agregarProducto(
                    nombre = s.nombre.trim(),
                    id = s.id.trim(),
                    categoria = s.categoria.trim()
                )
                Result.success(Unit)
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
        }
    }

    fun clearProductoResult() {
        _producto.update { ProductoUiState() }
    }

    // --------- NUEVO: Cerrar sesión ---------
    fun logout() {
        // Si tu repo tiene logout, puedes llamarlo aquí dentro de un launch try/catch.
        // viewModelScope.launch { repository.logout() }
        _isLoggedIn.value = false

        // (Opcional) Limpia estados de formularios
        _login.update { LoginUiState() }
        _register.update { RegisterUiState() }
    }

    // --------- CATEGORÍA: onChange ---------
    fun onCategoriaNombreChange(value: String) {
        _categoria.update {
            it.copy(
                nombre = value,
                nombreError = if (value.isBlank()) "Requerido" else null
            )
        }
        recomputeCategoriaCanSubmit()
    }

    fun onCategoriaIdChange(value: String) {
        _categoria.update {
            it.copy(
                id = value,
                idError = if (value.isBlank()) "Requerido" else null
            )
        }
        recomputeCategoriaCanSubmit()
    }

    fun onCategoriaDescripcionChange(value: String) {
        _categoria.update {
            it.copy(
                descripcion = value,
                descripcionError = if (value.isBlank()) "Requerido" else null
            )
        }
        recomputeCategoriaCanSubmit()
    }

    private fun recomputeCategoriaCanSubmit() {
        val s = _categoria.value
        val noErrors = listOf(s.nombreError, s.idError, s.descripcionError).all { it == null }
        val filled = s.nombre.isNotBlank() && s.id.isNotBlank() && s.descripcion.isNotBlank()
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
                    id = s.id.trim(),
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
                    errorMsg = result.exceptionOrNull()?.message
                        ?: "No se pudo guardar la categoría"
                )
            }
        }
    }

    fun clearCategoriaResult() {
        _categoria.update { CategoriaUiState() }
    }
}