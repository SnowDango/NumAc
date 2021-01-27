package com.snowdango.numac

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentManager
import com.ncapdevi.fragnav.FragNavController
import com.snowdango.numac.actions.appinvisiblelist.AppInvisibleListDatabaseActionCreator
import com.snowdango.numac.actions.applist.AppListActionCreator
import com.snowdango.numac.actions.applistdb.AppListDatabaseActionCreator
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionCreator
import com.snowdango.numac.actions.changecommnad.ChangeCommandActionCreator
import com.snowdango.numac.actions.command.CommandActionCreator
import com.snowdango.numac.actions.controlfavorite.ControlFavoriteActionCreator
import com.snowdango.numac.actions.removeapp.RemoveAppActionCreator
import com.snowdango.numac.actions.visible.ToggleVisibleActionCreator
import com.snowdango.numac.activity.appview.AppViewActivity
import com.snowdango.numac.activity.appview.invisible.InvisibleAppViewFragment
import com.snowdango.numac.activity.appview.visible.VisibleAppViewFragment
import com.snowdango.numac.dispatcher.Dispatcher
import com.snowdango.numac.domain.usecase.*
import com.snowdango.numac.store.appview.AppViewStore
import com.snowdango.numac.store.main.MainStore
import kotlinx.coroutines.CoroutineScope
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
        viewModel { MainStore(get()) }
        factory {(coroutineScope: CoroutineScope) ->
            AppListActionCreator(coroutineScope,get(), AppListCreate(), AppListDatabaseUse()) }
        factory {(coroutineScope: CoroutineScope) ->
            CommandActionCreator(coroutineScope,get(), LaunchApp(), SharpCommandExecute(coroutineScope)) }
    }

    private val appViewModule = module {
        factory { (fragmentManager: FragmentManager,id: Int) -> FragNavController(fragmentManager,id)  }
        factory { VisibleAppViewFragment() }
        factory { InvisibleAppViewFragment() }
        factory { (coroutineScope: CoroutineScope) ->
            ToggleVisibleActionCreator(coroutineScope,get(),AppListDatabaseUse())}
        factory { (coroutineScope: CoroutineScope) ->
            AppInvisibleListDatabaseActionCreator(coroutineScope,get(), AppListDatabaseUse()) }
        factory { (coroutineScope: CoroutineScope) ->
            AppListDatabaseActionCreator(coroutineScope,get(), AppListDatabaseUse()) }
        factory { (coroutineScope: CoroutineScope) ->
            RecentlyAppDatabaseActionCreator(coroutineScope,get(),SaveRecentlyApp()) }
        factory {(coroutineScope: CoroutineScope) ->
            RemoveAppActionCreator(coroutineScope,get(),AppListDatabaseUse())}
        factory {(coroutineScope: CoroutineScope) ->
            ChangeCommandActionCreator(coroutineScope,get(),AppListDatabaseUse()) }
        factory {(coroutineScope: CoroutineScope) ->
            ControlFavoriteActionCreator(coroutineScope,get(),AppListDatabaseUse()) }
        viewModel { AppViewStore(get()) }
    }
}