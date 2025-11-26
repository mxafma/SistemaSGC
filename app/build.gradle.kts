plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" // <-- KSP Room
}

android {
    namespace = "com.example.sistemasgc"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.uinavegacion"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

dependencies {
    // -------------------- Compose (alineado con BOM) --------------------
    // BOM único y reciente (controla versiones de los artefactos Compose *)
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))

    // Artefactos Compose sin versión (la aporta el BOM)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose")

    // Tests/UI tooling con BOM
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // -------------------- Tus libs existentes --------------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Navegación / Lifecycle Compose
    implementation("androidx.navigation:navigation-compose:2.9.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Material icons (para Visibility / VisibilityOff, etc.)
    implementation("androidx.compose.material:material-icons-extended")

    // Room (SQLite) + KSP
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Coil (carga de imágenes en Compose)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Jetpack Compose y Material 3 (version 2025)

    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Retrofit y Gson converter
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // OkHttp logging interceptor (usado para depuración de requests/responses)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    //Kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")

    //Junit5
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    //MockK
    testImplementation("io.mockk:mockk:1.13.10")

    // Compose Ui Test
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.2")

    // -------------------- Tests base --------------------
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}
