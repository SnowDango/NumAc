package com.snowdango.numac.models


/*
This class is sharp command list format.
 */

class SharpCommandListFormat(
        var command: String,
        var text: String,
        var runnable: Runnable,
        var lateTime: Int,
        var updateLateTime: Int)