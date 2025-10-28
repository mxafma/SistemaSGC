package com.example.sistemasgc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.sistemasgc.data.local.database.AppDatabase
import com.example.sistemasgc.data.repository.UserRepository
import com.example.sistemasgc.navigation.AppNavGraph
import com.example.sistemasgc.ui.viewmodel.AuthViewModel
import com.example.sistemasgc.ui.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppRoot() }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current.applicationContext
    val db = AppDatabase.getInstance(context)

    val userDao = db.userDao()
    val proveedorDao = db.proveedorDao()
    val productoDao = db.productoDao()
    val categoriaDao = db.categoriaDao()

    val userRepository = UserRepository(
        userDao = userDao,
        proveedorDao = proveedorDao,
        productoDao = productoDao,
        categoriaDao = categoriaDao
    )

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )

    val navController = rememberNavController()
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}
