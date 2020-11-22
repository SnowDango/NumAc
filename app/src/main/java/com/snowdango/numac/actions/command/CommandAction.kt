package com.snowdango.numac.actions.command

data class CommandAction(
        val state: CommandActionState
)

sealed class CommandActionState{
    object None: CommandActionState()
    object Success: CommandActionState()
    object Recreate: CommandActionState()
    object Road: CommandActionState()
    data class Failed(val failedState: String): CommandActionState()
}