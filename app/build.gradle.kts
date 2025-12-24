plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    kotlin("kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.dp.radar"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dp.radar"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "env"

    buildTypes {
        debug {
            isDebuggable = true
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    productFlavors {
        create("mock") {
            dimension = "env"

            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/\"")
        }

        create("prod") {
            dimension = "env"

            buildConfigField("String", "BASE_URL", "\"https://api.production.com\"")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes.add("META-INF/gradle/incremental.annotation.processors")
        }
    }
    configurations.all {
        resolutionStrategy.force("com.squareup:javapoet:1.13.0")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.runtime)
    implementation(libs.google.auth)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play)
    implementation(libs.googleid)
    implementation(libs.material)
    implementation(libs.play.services.location)
    //implementation(libs.androidx.junit.ktx)

    kapt(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.datastore.preferences)
    implementation(libs.hilt.compiler)
    // Networking
    implementation(libs.bundles.networking)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.coil)
    implementation(libs.coil.network)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.db)

    implementation(libs.accompanist.permissions)


    //Splash screen
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.datastore.core)
    testImplementation(libs.junit)
    //testImplementation(libs.bundles.mockito)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(kotlin("test"))

}