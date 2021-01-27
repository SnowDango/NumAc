package com.snowdango.numac.activity.appview

import android.graphics.drawable.Drawable

interface CommandChangeListener {
    fun commandChangeListener(appIcon: Drawable, appName: String, packageName: String, command: String)
}