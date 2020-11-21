package com.snowdango.numac

import android.app.Application
import android.content.Context

class SingletonContext: Application() {

    override fun onCreate() {
        super.onCreate()
    }

    init {
        instance = this
    }

    companion object {
        private var instance: SingletonContext? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}