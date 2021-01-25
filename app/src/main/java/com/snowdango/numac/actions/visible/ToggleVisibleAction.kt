package com.snowdango.numac.actions.visible

data class ToggleVisibleAction(val state: ToggleVisibleActionState)


sealed class ToggleVisibleActionState{
    object None: ToggleVisibleActionState()
    object Success: ToggleVisibleActionState()
    data class Failed(val failed: String): ToggleVisibleActionState()
}