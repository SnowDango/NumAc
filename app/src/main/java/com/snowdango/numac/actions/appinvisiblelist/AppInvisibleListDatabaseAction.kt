package com.snowdango.numac.actions.appinvisiblelist

import com.snowdango.numac.data.repository.dao.entity.AppInfo

data class AppInvisibleListDatabaseAction(val state: AppInvisibleListDatabaseActionState)

sealed class AppInvisibleListDatabaseActionState{
    object None: AppInvisibleListDatabaseActionState()
    data class Success(val appInfo: ArrayList<AppInfo>): AppInvisibleListDatabaseActionState()
    object Failed: AppInvisibleListDatabaseActionState()
}