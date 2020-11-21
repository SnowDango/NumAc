package com.snowdango.numac.dispatcher.main

import android.util.Log
import com.snowdango.numac.actions.applist.AppListAction
import com.snowdango.numac.actions.command.CommandAction
import java.util.*

class Dispatcher {

    private val appListListeners = Collections.synchronizedList(mutableListOf<AppListActionListener>())
    private val commandListeners = Collections.synchronizedList(mutableListOf<CommandActionListener>())

    interface AppListActionListener {
        fun on(action: AppListAction)
    }

    interface CommandActionListener{
        fun on(action: CommandAction)
    }

    fun dispatchAppList(action: AppListAction) {
        Log.d("AppListAction","dispatch")
        appListListeners.forEach { it.on(action) }
    }

    fun dispatchCommand(action: CommandAction){
        commandListeners.forEach { it.on(action) }
    }

    fun register(listenerAppList: AppListActionListener, listenerCommand: CommandActionListener) {
        appListListeners.add(listenerAppList)
        commandListeners.add(listenerCommand)
    }

    fun unregister(listenerAppList: AppListActionListener, listenerCommand: CommandActionListener) {
        appListListeners.remove(listenerAppList)
        commandListeners.remove(listenerCommand)
    }
}