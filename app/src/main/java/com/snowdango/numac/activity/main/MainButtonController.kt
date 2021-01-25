package com.snowdango.numac.activity.main

import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.snowdango.numac.epoxyMainButton

class MainButtonController(
        private val clickListener: ClickListener
): TypedEpoxyController<Array<String>>() {

    interface ClickListener {
        fun itemClickListener(string: String)
    }

    override fun buildModels(data: Array<String>) {
        data.forEach {stringData ->
            epoxyMainButton{
                id("main_button")
                clickListener(View.OnClickListener { clickListener.itemClickListener(stringData) })
            }
        }
    }
}