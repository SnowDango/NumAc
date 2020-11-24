package com.snowdango.numac.dispatcher.appview

import com.snowdango.numac.actions.applistdb.DatabaseAction
import java.util.*

class AppViewDispatcher {

    private val databaseListeners = Collections.synchronizedList(mutableListOf<DatabaseActionListener>())

    interface DatabaseActionListener {
        fun on(action: DatabaseAction)
    }

    fun dispatchDatabase(action: DatabaseAction) {
        databaseListeners.forEach { it.on(action) }
    }

    fun register(listener: DatabaseActionListener) {
        databaseListeners.add(listener)
    }

    fun unregister(listener: DatabaseActionListener) {
        databaseListeners.remove(listener)
    }
}