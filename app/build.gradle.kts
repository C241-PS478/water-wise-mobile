import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("kotlin-android")
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "bangkit.capstone.waterwise"
    compileSdk = 34

    defaultConfig {
        applicationId = "bangkit.capstone.waterwise"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = project.rootProject.file("local.properties")
        val properties = Properties()
        properties.load(localProperties.inputStream())
        val newsApiKey = properties.getProperty("news.api.key")
        val baseAPIUrl = properties.getProperty("base.api.url")

        buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")
        buildConfigField("String", "BASE_API_URL", "\"$baseAPIUrl\"")
        buildConfigField("String", "MAPS_API_KEY", "\"AIzaSyBL1ivhwWccabvJ1yxx2KD_dY3EFH5l9CM\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        mlModelBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.datastore.preferences.core.jvm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.retrofit)
    implementation (libs.retrofit2.converter.gson)
    implementation(libs.logging.interceptor)
    implementation (libs.kotlin.stdlib)
    implementation (libs.androidx.datastore.preferences)

    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.location)

    // Custom Component
    implementation(libs.material)
    implementation (libs.lottie)
    // Image loading and manipulation
    implementation(libs.glide)
    implementation(libs.circleimageview) // Circular ImageView

    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)

    implementation (libs.androidx.viewpager2)

    // toast
    implementation(libs.motiontoast)

    // tensor flow
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.tensorflow.lite.gpu)
    implementation(libs.tensorflow.lite.task.vision)
    // paging3
    implementation(libs.androidx.paging.runtime.ktx)

    // compressor
    implementation(libs.compressor)
}