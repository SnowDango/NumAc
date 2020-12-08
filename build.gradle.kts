// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion by extra{ "1.3.72" }
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.gms:oss-licenses:0.9.2")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.2")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
        maven(url = "http://dl.bintray.com/kotlin/kotlin-eap-1.2")
    }
}

task("clean" ,Delete::class){
    delete = setOf(rootProject.buildDir)
}