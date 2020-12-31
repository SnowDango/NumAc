package com.snowdango.numac.actions.controlfavorite

data class ControlFavoriteAction(
        val state: ControlFavoriteActionState
)


sealed class ControlFavoriteActionState{
    object Success: ControlFavoriteActionState()
    object Failed: ControlFavoriteActionState()
    object None: ControlFavoriteActionState()
}