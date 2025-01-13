import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("com.android.application")
  kotlin("android") 
  id("org.jetbrains.kotlin.plugin.compose")
}

android {
  namespace = "org.robok.engine"
  compileSdk = 35

  defaultConfig {
    applicationId = "org.robok.engine.scene"
    minSdk = 26
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"
    
    vectorDrawables.useSupportLibrary = true
  }

  packaging {
    jniLibs.useLegacyPackaging = true
    exclude("META-INF/INDEX.LIST")
    exclude("META-INF/ALIAS")
    exclude("META-INF/DEPENDENCIES")
    exclude("META-INF/LICENSE")
    exclude("META-INF/LICENSE.txt")
    exclude("META-INF/NOTICE")
    exclude("META-INF/NOTICE.txt")
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_17)
  }
}

dependencies {
  implementation("androidx.constraintlayout:constraintlayout:2.2.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("androidx.fragment:fragment-ktx:1.8.5")

  implementation(platform("androidx.compose:compose-bom:2024.12.01"))
  implementation("androidx.core:core-ktx:1.15.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.activity:activity-compose:1.9.3")
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.material3:material3")

  implementation("com.badlogicgames.gdx:gdx:1.9.14")
  implementation("com.badlogicgames.gdx:gdx-backend-android:1.9.14")
  implementation("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-armeabi")
  implementation("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-armeabi-v7a")
  implementation("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-x86")
  implementation("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-x86_64")

  implementation(project(":feature:scene"))
}
