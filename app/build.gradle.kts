import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("com.android.application")
  kotlin("android") 
  id("org.jetbrains.kotlin.plugin.compose")
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_11)
  }
}

android {
  namespace = "org.robok.engine"
  compileSdk = 33

  defaultConfig {
    applicationId = "org.robok.engine.scene"
    minSdk = 26
    targetSdk = 33

    versionCode = 1
    versionName = "1.0"
    
    vectorDrawables.useSupportLibrary = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  signingConfigs {
    create("release") {
      // temporary keystore
      storeFile = file(layout.buildDirectory.dir("../release_key.jks"))
      storePassword = "release_temp"
      keyAlias = "release_temp"
      keyPassword = "release_temp"
    }
    getByName("debug") {
      storeFile = file(layout.buildDirectory.dir("../testkey.keystore"))
      storePassword = "testkey"
      keyAlias = "testkey"
      keyPassword = "testkey"
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
  }

  buildFeatures {
    compose = true
  }
}

dependencies {
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("com.google.android.material:material:1.9.0")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("androidx.fragment:fragment-ktx:1.6.1")

  implementation(platform("androidx.compose:compose-bom:2024.11.00"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
  implementation("androidx.core:core-ktx:1.15.0")
  implementation("androidx.core:core-splashscreen:1.0.1")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.activity:activity-compose:1.9.3")

  implementation("com.badlogicgames.gdx:gdx:1.9.14")
  implementation("com.badlogicgames.gdx:gdx-backend-android:1.9.14")
  implementation("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-armeabi")
  implementation("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-armeabi-v7a")
  implementation("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-x86")
  implementation("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-x86_64")

  implementation(project(":feature:scene"))
}