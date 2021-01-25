import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.*
import dependencies.Dep

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("com.google.gms.oss.licenses.plugin")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("29.0.3")
    defaultConfig {
        applicationId = "com.snowdango.numac"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = 12
        versionName = "2.1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug"){
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility =  JavaVersion.VERSION_1_8
        targetCompatibility =  JavaVersion.VERSION_1_8
    }
    buildFeatures.dataBinding = true
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // local lib
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))

    //ossLicenses
    implementation(Dep.GoogleGMS.ossLicenses)

    // AndroidX
    implementation(Dep.AndroidX.appCompat)
    implementation(Dep.AndroidX.appCompatResource)
    implementation(Dep.AndroidX.constraintLayout)
    implementation(Dep.AndroidX.lifecycle)
    implementation(Dep.AndroidX.viewModelKtx)
    implementation(Dep.AndroidX.viewModelFragment)
    implementation(Dep.AndroidX.core)
    //recyclerview
    implementation(Dep.AndroidX.recyclerView)
    //room
    implementation(Dep.AndroidX.roomRuntime)
    implementation(Dep.AndroidX.roomKtx)
    kapt(Dep.AndroidX.roomCompiler)

    //test
    testImplementation(Dep.Junit.junit)
    androidTestImplementation(Dep.AndroidX.testExt)
    androidTestImplementation(Dep.AndroidX.testEspresso)

    //kotlinX
    implementation(Dep.KotlinX.coroutine)

    //permissionDispatcher
    implementation(Dep.PermissionsDispatcher.permissionDispatcher)
    kapt(Dep.PermissionsDispatcher.permissionDispatcherProcessor)

    //koin
    implementation(Dep.Koin.koin)
    implementation(Dep.Koin.koinScope)
    implementation(Dep.Koin.koinViewModel)

    // epoxy
    implementation(Dep.Airbnb.epoxy)
    kapt(Dep.Airbnb.epoxyProcessor)
    implementation(Dep.Airbnb.epoxyDataBinding)

    //MaterialProgressBar
    implementation(Dep.Zhanghai.materialProgressBar)

    //MaterialPopupMenu
    implementation(Dep.Zawadz88.materialPopupMenu)

    //AspectRatioImageView
    implementation(Dep.Santalu.aspectRatioImageView)

    //materialDialog
    implementation(Dep.Afollestad.materialDialogInput)
}
repositories {
    mavenCentral()
}