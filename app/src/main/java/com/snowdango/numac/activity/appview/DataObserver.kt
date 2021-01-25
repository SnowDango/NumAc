package com.snowdango.numac.activity.appview

import com.snowdango.numac.actions.applistdb.DatabaseActionState
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionState
import com.snowdango.numac.actions.removeapp.RemoveAppActionState

interface DataObserver {
    fun searchViewListener(filter: String)  // SearchViewの検索更新用
    fun onTextQueryChangeEmpty()
    fun viewDataBaseChangeListener(databaseActionState: DatabaseActionState.Success)    // 表示Appの変更用
    fun recentlyAppDataBaseListener(recentlyAppDatabaseActionState: RecentlyAppDatabaseActionState.Success) // 履歴Appの更新用
    fun removeAppListener() // アプリを削除した時のcallback用
    fun removeAppFailedListener(removeAppActionState: RemoveAppActionState.Failed)
    fun changeCommandListener() //commandの変更用
    fun changeFavoriteListener() //お気に入りの変更用
    fun changeVisibleListener() //表示の変更用
}