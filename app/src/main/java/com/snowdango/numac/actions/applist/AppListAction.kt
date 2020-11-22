package com.snowdango.numac.actions.applist

import com.snowdango.numac.data.repository.dao.entity.AppInfo

data class AppListAction(
        val state: AppListActionState
)

sealed class AppListActionState{
    object None: AppListActionState()
    data class Success(val appList: ArrayList<AppInfo>): AppListActionState()
    object Failed: AppListActionState()
}