package com.snowdango.numac.actions.removeapp

data class RemoveAppAction(
        val state: RemoveAppActionState
)

sealed class RemoveAppActionState{
    object Success: RemoveAppActionState()
    data class Failed(val packageName: String) : RemoveAppActionState()
    object None: RemoveAppActionState()
}