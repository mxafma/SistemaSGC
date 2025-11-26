package com.example.sistemasgc.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import java.io.File
import com.example.sistemasgc.ui.viewmodel.AuthViewModel

@Composable
fun AgregarProductoScreen(
    viewModel: AuthViewModel,
    onEditCategory: () -> Unit,
    onBack: () -> Unit = {}
) {
    // ✅ Observa el estado del ViewModel
    val productoState by viewModel.producto.collectAsStateWithLifecycle()
    val categoriaState by viewModel.categoria.collectAsStateWithLifecycle()

    // Estado local sincronizado con ViewModel
    var nombre by rememberSaveable { mutableStateOf(productoState.nombre) }
    var sku by rememberSaveable { mutableStateOf(productoState.sku) }
    var categoria by rememberSaveable { mutableStateOf(productoState.categoria) }
    var photoUri by rememberSaveable { mutableStateOf(productoState.photoUri) }

    val ctx = LocalContext.current

    // ✅ Sincroniza cambios con el ViewModel
    LaunchedEffect(nombre) {
        if (nombre != productoState.nombre) {
            viewModel.onProductoNombreChange(nombre)
        }
    }

    LaunchedEffect(sku) {
        if (sku != productoState.sku) {
            viewModel.onProductoSkuChange(sku)
        }
    }

    LaunchedEffect(categoria) {
        if (categoria != productoState.categoria) {
            viewModel.onProductoCategoriaChange(categoria)
        }
    }

    // ✅ Efecto para manejar éxito
    LaunchedEffect(productoState.success) {
        if (productoState.success) {
            // Limpia el estado local cuando el ViewModel reporta éxito
            nombre = ""
            sku = ""
            categoria = ""
            photoUri = null
            // Opcional: navegar atrás después de guardar
            // onBack()
        }
    }

    // --------- Lanzadores Galería ---------
    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        photoUri = uri?.toString()
        viewModel.onProductoSetPhoto(photoUri)
    }

    val legacyPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        photoUri = uri?.toString()
        viewModel.onProductoSetPhoto(photoUri)
    }

    // --------- Cámara: permiso + take picture ---------
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri = pendingCameraUri?.toString()
            viewModel.onProductoSetPhoto(photoUri)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val imagesDir = File(ctx.cacheDir, "images").apply { mkdirs() }
            val file = File.createTempFile("prod_", ".jpg", imagesDir)
            val authority = "${ctx.packageName}.fileprovider"
            pendingCameraUri = FileProvider.getUriForFile(ctx, authority, file)
            pendingCameraUri?.let { cameraLauncher.launch(it) }
        }
    }

    fun launchCameraWithPermission() {
        val granted = ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            val imagesDir = File(ctx.cacheDir, "images").apply { mkdirs() }
            val file = File.createTempFile("prod_", ".jpg", imagesDir)
            val authority = "${ctx.packageName}.fileprovider"
            pendingCameraUri = FileProvider.getUriForFile(ctx, authority, file)
            pendingCameraUri?.let { cameraLauncher.launch(it) }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // --------- Categorías (DropdownMenu) ---------
    val catOptions = remember(categoriaState.nombresCategorias) {
        categoriaState.nombresCategorias.map { it.trim() }.filter { it.isNotEmpty() }.distinct().sorted()
    }
    var catExpanded by remember { mutableStateOf(false) }
    val filteredCat = remember(categoria, catOptions) {
        if (categoria.isBlank()) catOptions
        else catOptions.filter { it.contains(categoria, ignoreCase = true) }
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Agregar producto",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                // ✅ Muestra error del backend si existe
                if (productoState.errorMsg != null) {
                    Text(
                        text = productoState.errorMsg!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // ---- Nombre (obligatorio ≥ 4) ----
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre* (≥ 4)") },
                    isError = productoState.nombreError != null,
                    supportingText = { productoState.nombreError?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- SKU (opcional, solo numérico) ----
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU (opcional, solo números)") },
                    isError = productoState.skuError != null,
                    supportingText = { productoState.skuError?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- Categoría (opcional): escribible + desplegable ----
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = { categoria = it },
                        label = { Text("Categoría (opcional)") },
                        trailingIcon = {
                            IconButton(onClick = { catExpanded = !catExpanded }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Ver categorías"
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        val items = if (filteredCat.isEmpty()) listOf("(sin categorías)") else filteredCat
                        items.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    if (option != "(sin categorías)") categoria = option
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                // ---- Botón Crear categoría ----
                Button(
                    onClick = onEditCategory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) { Text("Crear categoría") }

                // ---- Foto: Galería / Cámara ----
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= 33) {
                                galleryPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } else {
                                legacyPicker.launch("image/*")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    ) { Text(if (photoUri == null) "Galería" else "Cambiar foto") }

                    Button(
                        onClick = { launchCameraWithPermission() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    ) { Text("Cámara") }
                }

                if (photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = photoUri),
                        contentDescription = "Foto producto",
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                // ---------- BOTÓN GUARDAR ----------
                Button(
                    onClick = {
                        viewModel.submitProducto()
                    },
                    enabled = productoState.canSubmit && !productoState.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text(if (productoState.isSubmitting) "Guardando..." else "Guardar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AgregarProductoScreenPreview() {
    // Para el preview, necesitarías un ViewModel fake o usar viewModel() si está en un contexto adecuado
    MaterialTheme {
        Surface {
            Text("Preview no disponible - necesita ViewModel")
        }
    }
}