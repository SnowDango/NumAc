package com.snowdango.numac.activity.appview

import com.snowdango.numac.actions.appinvisiblelist.AppInvisibleListDatabaseActionState
import com.snowdango.numac.actions.applistdb.DatabaseActionState
import com.snowdango.numac.actions.apprecently.RecentlyAppDatabaseActionState
import com.snowdango.numac.actions.removeapp.RemoveAppActionState
import com.snowdango.numac.data.repository.dao.entity.AppInfo
import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo

interface DataObserver {
    fun searchViewListener(filter: String)  // SearchViewの検索更新用
    fun onTextQueryChangeEmpty()
    fun viewDataBaseChangeListener(appList: ArrayList<AppInfo>,recentlyList: ArrayList<RecentlyAppInfo>)    // 表示Appの変更用
    fun viewInvisibleDataChangeListener(appList: ArrayList<AppInfo>) // 非表示
    fun removeAppListener() // アプリを削除した時のcallback用
    fun removeAppFailedListener(packageName: String) // アプリを削除時のFailed
    fun changeCommandListener() //commandの変更用
    fun updateFavoriteListener()
    fun updateVisibleListener() // favorite or visible
}