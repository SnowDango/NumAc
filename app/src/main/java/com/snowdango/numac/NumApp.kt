package com.snowdango.numac

import android.app.Application
import android.content.Context
import com.snowdango.numac.actions.applist.AppListActionCreator
import com.snowdango.numac.actions.applistdb.AppListDatabaseActionCreator
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionCreator
import com.snowdango.numac.actions.command.CommandActionCreator
import com.snowdango.numac.actions.removeapp.RemoveAppActionCreator
import com.snowdango.numac.dispatcher.Dispatcher
import com.snowdango.numac.domain.usecase.*
import com.snowdango.numac.store.appview.AppViewStore
import com.snowdango.numac.store.main.MainStore
import com.snowdango.numac.utility.CancellableCoroutineScope
import kotlinx.coroutines.coroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class NumApp: Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: NumApp? = null

        fun singletonContext() : Context {
            return instance!!.applicationContext
        }
    }


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NumApp.applicationContext)
            modules(singletonModule)
            modules(mainActivityModule)
            modules(appViewModule)
        }
    }

    private val singletonModule = module {
        single(override = true) { Dispatcher() }
    }

    private val mainActivityModule = module {
        factory {(coroutineScope: CancellableCoroutineScope) ->
            AppListActionCreator(coroutineScope,get(), AppListCreate(), AppListDatabaseUse()) }
        factory {(coroutineScope: CancellableCoroutineScope) ->
            CommandActionCreator(coroutineScope,get(), LaunchApp(), SharpCommandExecute(coroutineScope)) }
        viewModel { MainStore(get()) }
    }

    private val appViewModule = module {
        factory { (coroutineScope: CancellableCoroutineScope) ->
            AppListDatabaseActionCreator(coroutineScope,get(), AppListDatabaseUse()) }
        factory { (coroutineScope: CancellableCoroutineScope) ->
            RecentlyAppDatabaseActionCreator(coroutineScope,get(),SaveRecentlyApp()) }
        factory {(coroutineScope: CancellableCoroutineScope) ->
            RemoveAppActionCreator(coroutineScope,get(),AppListDatabaseUse())}
        viewModel { AppViewStore(get()) }
    }
}