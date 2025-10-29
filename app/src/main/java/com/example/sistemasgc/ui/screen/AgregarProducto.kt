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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.File
import android.content.res.Configuration

@Composable
fun AgregarProductoScreen(
    onAddProduct: (String, String?, String?, String?) -> Unit, // nombre, sku?, categoria?, photoUri?
    onEditCategory: () -> Unit,
    initialCategories: List<String> = emptyList()
) {
    // --------- Estado de formulario ---------
    var nombre by rememberSaveable { mutableStateOf("") }
    var sku by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("") }
    var photoUri by rememberSaveable { mutableStateOf<String?>(null) }

    // Errores
    var nombreError by remember { mutableStateOf<String?>(null) }
    var skuError by remember { mutableStateOf<String?>(null) }

    val ctx = LocalContext.current

    // --------- Lanzadores Galería ---------
    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> photoUri = uri?.toString() }

    val legacyPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> photoUri = uri?.toString() }

    // --------- Cámara: permiso + take picture ---------
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) photoUri = pendingCameraUri?.toString()
    }

    // Pedido de permiso CAMARA en runtime (API 23+)
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // Crear el archivo y lanzar cámara
            val imagesDir = File(ctx.cacheDir, "images").apply { mkdirs() }
            val file = File.createTempFile("prod_", ".jpg", imagesDir)
            val authority = "${ctx.packageName}.fileprovider"
            pendingCameraUri = FileProvider.getUriForFile(ctx, authority, file)
            pendingCameraUri?.let { cameraLauncher.launch(it) }
        } else {
            // opcional: mostrar un mensaje/snackbar si quieres
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

    // --------- Categorías (DropdownMenu normal) ---------
    val catOptions = remember(initialCategories) {
        initialCategories.map { it.trim() }.filter { it.isNotEmpty() }.distinct().sorted()
    }
    var catExpanded by remember { mutableStateOf(false) }
    val filteredCat = remember(categoria, catOptions) {
        if (categoria.isBlank()) catOptions else catOptions.filter { it.contains(categoria, ignoreCase = true) }
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

                // ---- Nombre (obligatorio ≥ 4) ----
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        nombreError = when {
                            it.isBlank()  -> "Requerido"
                            it.length < 4 -> "Debe tener al menos 4 caracteres"
                            else          -> null
                        }
                    },
                    label = { Text("Nombre* (≥ 4)") },
                    isError = nombreError != null,
                    supportingText = { nombreError?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- SKU (opcional, solo numérico) ----
                OutlinedTextField(
                    value = sku,
                    onValueChange = {
                        sku = it
                        skuError = if (it.isNotEmpty() && !it.all { ch -> ch.isDigit() }) "Solo números" else null
                    },
                    label = { Text("SKU (opcional, solo números)") },
                    isError = skuError != null,
                    supportingText = { skuError?.let { Text(it) } },
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

                // ---- Foto: Galería / Cámara (fila pareja) ----
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

                // ---- Guardar ----
                val canSubmit = nombreError == null && skuError == null && nombre.isNotBlank()
                Button(
                    onClick = {
                        val skuOrNull = sku.trim().ifBlank { null }
                        val catOrNull = categoria.trim().ifBlank { null }
                        onAddProduct(nombre.trim(), skuOrNull, catOrNull, photoUri)
                    },
                    enabled = canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) { Text("Guardar") }
            }
        }
    }
}

/* =================== PREVIEWS =================== */
@Preview(
    name = "AgregarProducto – Light",
    showBackground = true,
    showSystemUi = true
)
@Preview(
    name = "AgregarProducto – Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AgregarProductoPreview() {
    MaterialTheme {
        AgregarProductoScreen(
            onAddProduct = { _, _, _, _ -> },
            onEditCategory = {},
            initialCategories = listOf("Bebidas", "Snacks", "Lácteos")
        )
    }
}
