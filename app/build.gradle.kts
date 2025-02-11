import java.io.ByteArrayOutputStream
import kotlin.math.pow

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.chtibizoux.adeapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.chtibizoux.adeapp"
        minSdk = 24
        targetSdk = 35

        versionName = getLatestGitTag()
        versionCode = getVersionCode(versionName!!)

        println("Version:\nname: $versionName\ncode: $versionCode")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

fun getLatestGitTag(): String {
    try {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine = listOf("git", "describe", "--tags", "--abbrev=0")
            standardOutput = stdout
        }
        return stdout.toString().trim().replace("v", "")
    } catch (_: Exception) {
        throw Error("Enable to get the latest git tag")
    }
}

fun getVersionCode(tag: String): Int {
    val versionParts = tag.split(".").map { it.toInt() }
    return versionParts.reversed().reduceIndexed { index, code, part ->
        code + part * 100.0.pow(index).toInt()
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
    implementation(libs.androidx.material.icons)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.datastore)
    implementation(libs.serialization)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}