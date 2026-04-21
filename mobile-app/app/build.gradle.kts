import java.util.Properties

val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "sarkari.exam_ai"
    compileSdk = 34

    defaultConfig {
        applicationId = "sarkari.exam_ai"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        // Inject API keys into BuildConfig from local.properties
        buildConfigField("String", "GROQ_API_KEY",    "\"${localProperties["GROQ_API_KEY"]   ?: ""}\"")
        buildConfigField("String", "GROQ_API_KEY_1",  "\"${localProperties["GROQ_API_KEY_1"] ?: ""}\"")
        buildConfigField("String", "GROQ_API_KEY_2",  "\"${localProperties["GROQ_API_KEY_2"] ?: ""}\"")
        buildConfigField("String", "GROQ_API_KEY_3",  "\"${localProperties["GROQ_API_KEY_3"] ?: ""}\"")
        buildConfigField("String", "GEMINI_API_KEY",   "\"${localProperties["GEMINI_API_KEY"]  ?: ""}\"")
        buildConfigField("String", "GEMINI_API_KEY_1", "\"${localProperties["GEMINI_API_KEY_1"] ?: ""}\"")
        buildConfigField("String", "GEMINI_API_KEY_2", "\"${localProperties["GEMINI_API_KEY_2"] ?: ""}\"")
        buildConfigField("String", "OPENROUTER_API_KEY",   "\"${localProperties["OPENROUTER_API_KEY"]   ?: ""}\"")
        buildConfigField("String", "OPENROUTER_API_KEY_1", "\"${localProperties["OPENROUTER_API_KEY_1"] ?: ""}\"")
        buildConfigField("String", "OPENROUTER_API_KEY_2", "\"${localProperties["OPENROUTER_API_KEY_2"] ?: ""}\"")
        buildConfigField("String", "OPENROUTER_API_KEY_3", "\"${localProperties["OPENROUTER_API_KEY_3"] ?: ""}\"")
        buildConfigField("String", "WEB_CLIENT_ID", "\"${localProperties["WEB_CLIENT_ID"] ?: ""}\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    // For per-app language switching (instant, no restart needed)
    implementation("androidx.appcompat:appcompat-resources:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.activity:activity-compose:1.9.0")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation("androidx.compose.ui:ui-text-google-fonts")

    // Official Gemini AI SDK
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Firebase (BOM — manages all versions)
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Retrofit + OkHttp (Still needed for Groq/OpenRouter)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines Play Services (for Tasks.await())
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // DataStore for Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Room Database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

