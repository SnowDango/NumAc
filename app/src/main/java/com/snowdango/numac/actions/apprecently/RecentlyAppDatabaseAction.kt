package com.snowdango.numac.actions.apprecently

import com.snowdango.numac.data.repository.dao.entity.RecentlyAppInfo

data class RecentlyAppDatabaseAction(val state: RecentlyAppDatabaseActionState)


sealed class RecentlyAppDatabaseActionState{
    object None: RecentlyAppDatabaseActionState()
    data class Success(val recentlyList: ArrayList<RecentlyAppInfo>): RecentlyAppDatabaseActionState()
    object Failed: RecentlyAppDatabaseActionState()
}