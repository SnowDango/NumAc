package com.snowdango.numac.actions.command

import android.content.Intent

data class CommandAction(
        val state: CommandActionState
)

sealed class CommandActionState{
    object None: CommandActionState()
    data class Success(val intent: Intent): CommandActionState()
    object Road: CommandActionState()
    object AppViewIntent: CommandActionState()
    object OssLicense: CommandActionState()
    data class Failed(val failedState: String): CommandActionState()
}