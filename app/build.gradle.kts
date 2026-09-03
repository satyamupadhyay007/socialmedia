plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.login"

    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.login"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
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

    // ==================================================
    // ANDROID
    // ==================================================

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)


    // ==================================================
    // FIREBASE
    // ==================================================

    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Firebase Storage - kept for existing project compatibility
    implementation(libs.firebase.storage)


    // ==================================================
    // FIREBASE BOM
    // ==================================================

    implementation(
        platform(
            "com.google.firebase:firebase-bom:34.17.0"
        )
    )

    implementation(
        "com.google.firebase:firebase-analytics"
    )


    // ==================================================
    // CLOUDINARY
    // ==================================================

    implementation(
        "com.cloudinary:cloudinary-android:3.1.2"
    )


    // ==================================================
    // AUTHENTICATION
    // ==================================================

    implementation(libs.credentials)

    implementation(
        libs.credentials.play.services.auth
    )

    implementation(libs.googleid)


    // ==================================================
    // GLIDE
    // ==================================================

    implementation(
        "com.github.bumptech.glide:glide:4.16.0"
    )

    annotationProcessor(
        "com.github.bumptech.glide:compiler:4.16.0"
    )


    // ==================================================
    // TESTING
    // ==================================================

    testImplementation(libs.junit)

    androidTestImplementation(
        libs.ext.junit
    )

    androidTestImplementation(
        libs.espresso.core
    )
}