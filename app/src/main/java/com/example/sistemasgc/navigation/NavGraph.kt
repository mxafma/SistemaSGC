package com.example.sistemasgc.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sistemasgc.ui.components.AppDrawer
import com.example.sistemasgc.ui.components.AppTopBar
import com.example.sistemasgc.ui.components.defaultDrawerItems
import com.example.sistemasgc.ui.screen.*
import com.example.sistemasgc.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estado de login desde el VM
    val isLoggedIn = authViewModel.isLoggedIn.collectAsStateWithLifecycle(initialValue = false).value

    val go: (String) -> Unit = { route ->
        navController.navigate(route) { launchSingleTop = true }
    }

    val goHome       = { go(Route.Home.path) }
    val goHome2      = { go(Route.Home2.path) }
    val goLogin      = { go(Route.Login.path) }
    val goRegister   = { go(Route.Register.path) }
    val goProductos  = { go(Route.Productos.path) }
    val goCategorias = { go(Route.Categorias.path) }
    val goProveedores= { go(Route.Proveedores.path) }
    val goAgregarProveedores = { go(Route.AgregarProveedor.path) }
    val goCompras    = { go(Route.Compras.path) }
    val goDetallesCompras = { go(Route.DetallesCompras.path) }
    val goReportes = { go(Route.Reportes.path) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (!isLoggedIn) {
                AppDrawer(
                    currentRoute = navController.currentBackStackEntry?.destination?.route,
                    items = defaultDrawerItems(
                        onHome = { scope.launch { drawerState.close() }; goHome() },
                        onLogin = { scope.launch { drawerState.close() }; goLogin() },
                        onRegister = { scope.launch { drawerState.close() }; goRegister() }
                    )
                )
            } else {
                AppDrawer(
                    currentRoute = navController.currentBackStackEntry?.destination?.route,
                    items = defaultDrawerItems(
                        onHome = { scope.launch { drawerState.close() }; goHome2() },
                        onLogin = null,
                        onRegister = null,
                        onProductos = { scope.launch { drawerState.close() }; goProductos() },
                        onProveedores = { scope.launch { drawerState.close() }; goProveedores() },
                        onCompras = { scope.launch { drawerState.close() }; goCompras() },
                        onCategorias = { scope.launch { drawerState.close() }; goCategorias() }
                    )
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onHome = if (isLoggedIn) goHome2 else goHome,
                    onLogin = { if (!isLoggedIn) goLogin() },
                    onRegister = { if (!isLoggedIn) goRegister() },
                    isLoggedIn = isLoggedIn,
                    onProductos = if (isLoggedIn) goProductos else null,
                    onProveedores = if (isLoggedIn) goProveedores else null,
                    onCompras = if (isLoggedIn) goCompras else null,

                    // ✅ NUEVO: desloguear y navegar a Home limpiando el back stack
                    onLogout = {
                        scope.launch {
                            drawerState.close()
                            authViewModel.logout()
                            navController.navigate(Route.Home.path) {
                                // Limpia el stack anterior (cuando start era Home2 al estar logeado)
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                                restoreState = false
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (isLoggedIn) Route.Home2.path else Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Home.path) {
                    HomeScreen(
                        onGoLogin = goLogin,
                        onGoRegister = goRegister
                    )
                }
                composable(Route.Home2.path) {
                    HomeScreen2(
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoProveedores = goProveedores,
                        onGoProductos = goProductos,
                        onGoCompras = goCompras
                    )
                }
                composable(Route.Login.path) {
                    LoginScreenVm(
                        vm = authViewModel,
                        onLoginOkNavigateHome = {
                            navController.navigate(Route.Home2.path) {
                                popUpTo(Route.Home.path) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onGoRegister = goRegister
                    )
                }
                composable(Route.Register.path) {
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisteredNavigateLogin = {
                            navController.navigate(Route.Login.path) {
                                popUpTo(Route.Register.path) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onGoLogin = goLogin
                    )
                }
                composable(Route.Productos.path) {
                    val productos = authViewModel.productosNombres.collectAsStateWithLifecycle(emptyList()).value
                    LaunchedEffect(Unit) { authViewModel.loadProductos() }
                    ProductosScreen(
                        onSearch = { /* ... */ },
                        onAddNewProduct = { navController.navigate(Route.AgregarProducto.path) },
                        productosExistentes = productos
                    )
                }
                composable(Route.Categorias.path) {
                    val catState = authViewModel.categoria.collectAsStateWithLifecycle().value
                    LaunchedEffect(catState.success) {
                        if (catState.success) {
                            navController.popBackStack()
                            authViewModel.clearCategoriaResult()
                        }
                    }
                    CategoriaScreen(
                        onAddCategory = { nombre, descripcion ->
                            authViewModel.onCategoriaNombreChange(nombre)
                            authViewModel.onCategoriaDescripcionChange(descripcion)
                            authViewModel.submitCategoria()
                        },
                        onCancel = { navController.popBackStack() }
                    )
                }
                composable(Route.Proveedores.path) {
                    ProveedoresScreen(
                        onSearch = { },
                        onGoagregarProveedores = goAgregarProveedores
                    )
                }
                composable(Route.AgregarProveedor.path) {
                    AgregarProveedorScreenVM(
                        vm = authViewModel,
                        onProveedorAgregado = { navController.popBackStack() }
                    )
                }
                composable(Route.Compras.path) {
                    ComprasScreen(
                        onDetallesCompras = goDetallesCompras,
                        onNuevaCompra = { proveedor, formaPago, fecha ->
                            println("Nueva compra: $proveedor, $formaPago, $fecha")
                        },
                        onSearch = { query -> println("Buscando: $query") },
                        viewModel = authViewModel
                    )
                }
                composable(Route.AgregarProducto.path) {
                    val productoState = authViewModel.producto.collectAsStateWithLifecycle().value
                    LaunchedEffect(productoState.success) {
                        if (productoState.success) {
                            navController.popBackStack()
                            authViewModel.clearProductoResult()
                        }
                    }
                    var catOptions by remember { mutableStateOf<List<String>>(emptyList()) }
                    LaunchedEffect(Unit) {
                        catOptions = try { authViewModel.getCategoriasSugeridas() } catch (_: Exception) { emptyList() }
                    }
                    AgregarProductoScreen(
                        onAddProduct = { nombre, sku, categoria, photoUri ->
                            authViewModel.onProductoNombreChange(nombre)
                            authViewModel.onProductoSkuChange(sku ?: "")
                            authViewModel.onProductoCategoriaChange(categoria ?: "")
                            authViewModel.onProductoSetPhoto(photoUri)
                            authViewModel.submitProducto()
                        },
                        onEditCategory = { navController.navigate(Route.Categorias.path) },
                        initialCategories = catOptions
                    )
                }
                composable(Route.DetallesCompras.path) {
                    DetallesComprasScreen(onBack = { navController.popBackStack() })
                }
                composable(Route.Reportes.path) {
                    ReportesScreen(
                        onSearch = { query -> println("Buscando en reportes: $query") }
                    )
                }
            }
        }
    }
}
