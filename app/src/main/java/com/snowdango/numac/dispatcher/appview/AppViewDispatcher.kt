package com.snowdango.numac.dispatcher.appview

import com.snowdango.numac.actions.applistdb.DatabaseAction
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseAction
import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo
import java.util.*

class AppViewDispatcher {

    private val databaseListeners = Collections.synchronizedList(mutableListOf<DatabaseActionListener>())
    private val recentlyListeners = Collections.synchronizedList(mutableListOf<RecentlyActionListener>())

    interface DatabaseActionListener {
        fun on(action: DatabaseAction)
    }
    interface RecentlyActionListener{
        fun on(action: RecentlyAppDatabaseAction)
    }

    fun dispatchDatabase(action: DatabaseAction) {
        databaseListeners.forEach { it.on(action) }
    }

    fun dispatchRecently(action: RecentlyAppDatabaseAction){
        recentlyListeners.forEach { it.on(action) }
    }

    fun register(listener: DatabaseActionListener, listener2: RecentlyActionListener) {
        databaseListeners.add(listener)
        recentlyListeners.add(listener2)
    }

    fun unregister(listener: DatabaseActionListener, listener2: RecentlyActionListener) {
        databaseListeners.remove(listener)
        recentlyListeners.remove(listener2)
    }
}