package com.snowdango.numac.actions.changecommnad

import android.telephony.mbms.MbmsErrors


data class ChangeCommandAction(
        val state: ChangeCommandActionState
)


sealed class ChangeCommandActionState{
    object Success: ChangeCommandActionState()
    data class Failed(val errorString: String): ChangeCommandActionState()
    object None: ChangeCommandActionState()
}
