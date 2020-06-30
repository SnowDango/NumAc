package com.snowdango.numac.NumAcMain

import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.TextView

class ButtonClickEvent {
    private var handler = Handler()
    fun onClickEvents(textView: TextView): Intent? {
        var intent: Intent? = null
        val updateText = Runnable {
            textView.text = ""
            NumAcFragment.textPut = true
        }
        val command = textView.text as String
        try {
            textView.text = "Load"
            NumAcFragment.textPut = false
            val info: Array<String> = NumAcActivity.dataBaseHelper!!.getPackageAndClass(NumAcActivity.dataBaseHelper!!, command)
            intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.setClassName(info[0]!!, info[1]!!)
        } catch (e: Exception) {
            Log.d("command", command)
            textView.text = "Error"
            NumAcFragment.textPut = false
            intent = null
            handler = Handler()
            handler.postDelayed(updateText, 1000)
        } finally {
            return intent
        }
    }
}