package com.snowdango.numac.model

import android.graphics.drawable.Drawable

/*
this class app list format.
 */

class AppListFormat {
    var id: Long = 0
    var appIcon: Drawable? = null
    var appName: String? = null
    var appCommand: String? = null
    var appClassName: String? = null
    var appPackageName: String? = null
}