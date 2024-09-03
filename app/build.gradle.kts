import java.io.ByteArrayOutputStream
import kotlin.math.pow

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.chtibizoux.adeapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.chtibizoux.adeapp"
        minSdk = 24
        targetSdk = 34

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
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
    } catch (e: Exception) {
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
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation(platform("androidx.compose:compose-bom:2024.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material-icons-extended:1.6.8")
    implementation("androidx.datastore:datastore:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")
}