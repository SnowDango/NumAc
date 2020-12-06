package com.snowdango.numac.actions.removeapp

data class RemoveAppAction(
        val state: RemoveAppActionState
)

sealed class RemoveAppActionState{
    object Success: RemoveAppActionState()
    object Failed: RemoveAppActionState()
    object None: RemoveAppActionState()
}