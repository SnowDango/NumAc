package dependencies

object Dep {
    object AndroidX {
        // base
        const val appCompat = "androidx.appcompat:appcompat:1.2.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
        const val lifecycle = "androidx.lifecycle:lifecycle-extensions:2.2.0"
        const val core = "androidx.core:core-ktx:1.3.2"

        //add libs
        private const val recyclerViewVersion = "1.1.0"
        const val recyclerView = "androidx.recyclerview:recyclerview:${recyclerViewVersion}"

        private const val roomVersion = "2.2.5"
        const val roomRuntime = "androidx.room:room-runtime:${roomVersion}"
        const val roomKtx = "androidx.room:room-ktx:${roomVersion}"
        const val roomCompiler = "androidx.room:room-compiler:${roomVersion}"

        //test
        const val testExt = "androidx.test.ext:junit:1.1.2"
        const val testEspresso = "androidx.test.espresso:espresso-core:3.3.0"
    }

    object Junit {
        //test
        const val junit = "junit:junit:4.13.1"
    }

    object KotlinX {
        private const val coroutineVersion = "1.3.9"
        const val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutineVersion}"
    }

    object Koin {
        private const val koinVersion = "2.0.1"
        const val koin = "org.koin:koin-android:${koinVersion}"
        const val koinScope = "org.koin:koin-android-scope:${koinVersion}"
        const val koinViewModel = "org.koin:koin-android-viewmodel:${koinVersion}"
    }

    object Airbnb {
        private const val epoxyVersion = "4.1.0"
        const val epoxy = "com.airbnb.android:epoxy:${epoxyVersion}"
        const val epoxyProcessor = "com.airbnb.android:epoxy-processor:${epoxyVersion}"
        const val epoxyDataBinding = "com.airbnb.android:epoxy-databinding:${epoxyVersion}"
    }

    //
    object Zhanghai {
        private const val materialProgressBarVersion = "1.6.1"
        const val materialProgressBar = "me.zhanghai.android.materialprogressbar:library:${materialProgressBarVersion}"
    }

    //ImageのSizeを等倍するImageView
    object Santalu {
        private const val aspectRatioImageViewVersion = "1.0.9"
        const val aspectRatioImageView = "com.github.santalu:aspect-ratio-imageview:${aspectRatioImageViewVersion}"
    }

    object Zawadz88 {
        private const val materialPopupMenuVersion = "4.1.0"
        const val materialPopupMenu = "com.github.zawadz88.materialpopupmenu:material-popup-menu:${materialPopupMenuVersion}"
    }

    object Afollestad {
        private const val materialDialogBaseVersion = "0.9.6"
        private const val materialDialogVersion = "3.3.0"
        const val materialDialogCore = "com.afollestad.material-dialogs:core:${materialDialogBaseVersion}"
        const val materialDialogCommon = "com.afollestad.material-dialogs:commons:${materialDialogBaseVersion}"
        const val materialDialog = "com.afollestad.material-dialogs:lifecycle:${materialDialogVersion}"
        const val materialDialogInput = "com.afollestad.material-dialogs:input:${materialDialogVersion}"

    }
    object GoogleGMS{
        private const val ossLicensesVersion = "11.6.0"
        const val ossLicenses = "com.google.android.gms:play-services-oss-licenses:${ossLicensesVersion}"
    }
}