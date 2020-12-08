package com.snowdango.numac.dispatcher

import android.util.Log
import com.snowdango.numac.actions.applist.AppListAction
import com.snowdango.numac.actions.applistdb.DatabaseAction
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseAction
import com.snowdango.numac.actions.changecommnad.ChangeCommandAction
import com.snowdango.numac.actions.command.CommandAction
import com.snowdango.numac.actions.removeapp.RemoveAppAction
import java.util.*

class Dispatcher {

    private val appListListeners = Collections.synchronizedList(mutableListOf<AppListActionListener>())
    private val commandListeners = Collections.synchronizedList(mutableListOf<CommandActionListener>())
    private val databaseListeners = Collections.synchronizedList(mutableListOf<DatabaseActionListener>())
    private val recentlyListeners = Collections.synchronizedList(mutableListOf<RecentlyActionListener>())
    private val removeListeners = Collections.synchronizedList(mutableListOf<RemoveAppActionListener>())
    private val changeCommandListener = Collections.synchronizedList(mutableListOf<ChangeCommandActionListener>())

    interface AppListActionListener {
        fun on(action: AppListAction)
    }

    interface CommandActionListener{
        fun on(action: CommandAction)
    }

    interface DatabaseActionListener {
        fun on(action: DatabaseAction)
    }

    interface RecentlyActionListener{
        fun on(action: RecentlyAppDatabaseAction)
    }

    interface RemoveAppActionListener{
        fun on(action: RemoveAppAction)
    }

    interface ChangeCommandActionListener{
        fun on(action: ChangeCommandAction)
    }

    fun dispatchDatabase(action: DatabaseAction) {
        Log.d("dispatcher", "database")
        databaseListeners.forEach { it.on(action) }
    }

    fun dispatchRecently(action: RecentlyAppDatabaseAction){
        Log.d("dispatcher", "recently")
        recentlyListeners.forEach { it.on(action) }
    }
    fun dispatchAppList(action: AppListAction) {
        Log.d("dispatcher", "appList")
        appListListeners.forEach { it.on(action) }
    }

    fun dispatchCommand(action: CommandAction){
        Log.d("dispatcher", "command")
        commandListeners.forEach { it.on(action) }
    }

    fun dispatchRemoveApp(action: RemoveAppAction){
        Log.d("dispatcher", "remove")
        removeListeners.forEach { it.on(action) }
    }

    fun dispatchChangeCommand(action: ChangeCommandAction){
        Log.d("dispatcher","changeCommand")
        changeCommandListener.forEach { it.on(action) }
    }

    fun registerMain(listenerAppList: AppListActionListener,
                 listenerCommand: CommandActionListener) {
        Log.d(TAG,"registerMain")
        appListListeners.add(listenerAppList)
        commandListeners.add(listenerCommand)
    }

    fun registerAppView(listenerDatabaseActionListener: DatabaseActionListener,
                        listenerRecentlyActionListener: RecentlyActionListener,
                        listenerRemoveAppActionListener: RemoveAppActionListener,
                        listenerChangeCommand: ChangeCommandActionListener){
        Log.d(TAG,"registerAppView")
        databaseListeners.add(listenerDatabaseActionListener)
        recentlyListeners.add(listenerRecentlyActionListener)
        removeListeners.add(listenerRemoveAppActionListener)
        changeCommandListener.add(listenerChangeCommand)
    }

    fun unregisterMain(listenerAppList: AppListActionListener,
                   listenerCommand: CommandActionListener) {
        Log.d(TAG,"unregisterMain")
        appListListeners.remove(listenerAppList)
        commandListeners.remove(listenerCommand)
    }

    fun unregisterAppView(listenerDatabaseListener: DatabaseActionListener,
                          listenerRecentlyListener: RecentlyActionListener,
                          listenerRemoveAppActionListener: RemoveAppActionListener,
                          listenerChangeCommand: ChangeCommandActionListener){
        Log.d(TAG,"unregisterAppView")
        databaseListeners.remove(listenerDatabaseListener)
        recentlyListeners.remove(listenerRecentlyListener)
        removeListeners.remove(listenerRemoveAppActionListener)
        changeCommandListener.remove(listenerChangeCommand)
    }

    companion object {
        const val TAG = "Dispatcher"
    }
}