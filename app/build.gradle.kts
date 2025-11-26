plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.finalproject"
    //applicationId = "com.example.finalproject"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.finalproject"
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
}

dependencies {

    // --- Firebase BOM (controls versions for all Firebase libs) ---
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))

    // --- Firebase Analytics ---
    implementation("com.google.firebase:firebase-analytics")

    // --- Firebase Realtime Database ---
    implementation("com.google.firebase:firebase-database")

    // --- RecyclerView ---
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // --- Picasso (for loading images) ---
    implementation("com.squareup.picasso:picasso:2.71828")

    // Existing dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.storage)


    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
