plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
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

    // 🚫 QUITADO para evitar que Android arrastre JUnit 5 al APK y falle
    // tasks.withType<Test>().configureEach {
    //     useJUnitPlatform()
    // }

    // ✅ Evita los duplicados META-INF de JUnit5/Kotest al construir androidTest
    packagingOptions {
        resources.excludes += setOf(
            "META-INF/LICENSE.md",
            "META-INF/LICENSE-notice.md",
            "META-INF/NOTICE",
            "META-INF/NOTICE.txt",
            "META-INF/LICENSE",
            "META-INF/LICENSE.txt"
        )
    }
}

dependencies {

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.01"))

    // Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose")

    // Compose Testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Jetpack libs
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation("androidx.navigation:navigation-compose:2.9.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Icons
    implementation("androidx.compose.material:material-icons-extended")

    // Room + KSP
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Coil
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Kotest (solo tests JVM — src/test/)
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")

    // JUnit5
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    // MockK
    testImplementation("io.mockk:mockk:1.13.10")
    androidTestImplementation("io.mockk:mockk-android:1.13.10")

    // Compose UI Test (androidTest)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.2")

    // Basic test libs
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
