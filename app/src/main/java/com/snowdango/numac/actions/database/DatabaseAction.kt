package com.snowdango.numac.actions.database

import com.snowdango.numac.data.repository.dao.entity.AppInfo

data class DatabaseAction(val state: DatabaseActionState)




sealed class DatabaseActionState{
    data class Success(val appList: ArrayList<AppInfo>): DatabaseActionState()
    object Failed: DatabaseActionState()
    object None: DatabaseActionState()
}