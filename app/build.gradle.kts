plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("29.0.3")
    defaultConfig {
        applicationId = "com.snowdango.numac"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 12
        versionName = "2.1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility =  JavaVersion.VERSION_1_8
        targetCompatibility =  JavaVersion.VERSION_1_8
    }
    dataBinding.isEnabled = true
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    implementation("androidx.core:core-ktx:1.3.2")

    // epoxy
    implementation("com.airbnb.android:epoxy:4.1.0")
    kapt("com.airbnb.android:epoxy-processor:4.1.0")
    implementation("com.airbnb.android:epoxy-databinding:4.1.0")

    // recyclerview
    implementation("androidx.recyclerview:recyclerview:1.1.0")

    //progressbar
    implementation("me.zhanghai.android.materialprogressbar:library:1.6.1")

    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

    //room
    implementation("androidx.room:room-runtime:2.2.5")
    implementation("androidx.room:room-ktx:2.2.5")
    kapt("androidx.room:room-compiler:2.2.5")

    // image view
    implementation("com.github.santalu:aspect-ratio-imageview:1.0.9")

    //koin
    implementation("org.koin:koin-android:2.0.1")
    implementation("org.koin:koin-android-scope:2.0.1")
    implementation("org.koin:koin-android-viewmodel:2.0.1")

}
repositories {
    mavenCentral()
}