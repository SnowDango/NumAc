package com.snowdango.numac.controller

import android.util.Log
import android.widget.TextView
import com.snowdango.numac.R

/*
Main Activity pushed button ,listen this class.
this class can textView's text.
 */

class ClickTextChanger {
    fun clickEvents(button: Int, b: Boolean, textView: TextView) {
        if (b) {
            when (button) {
                R.id.button1 -> {
                    textView.text = textView.text.toString() + "1"
                    Log.d("E", "1")
                }
                R.id.button2 -> {
                    textView.text = textView.text.toString() + "2"
                    Log.d("E", "2")
                }
                R.id.button3 -> {
                    textView.text = textView.text.toString() + "3"
                    Log.d("E", "3")
                }
                R.id.button4 -> {
                    textView.text = textView.text.toString() + "4"
                    Log.d("E", "4")
                }
                R.id.button5 -> {
                    textView.text = textView.text.toString() + "5"
                    Log.d("E", "5")
                }
                R.id.button6 -> {
                    textView.text = textView.text.toString() + "6"
                    Log.d("E", "6")
                }
                R.id.button7 -> {
                    textView.text = textView.text.toString() + "7"
                    Log.d("E", "7")
                }
                R.id.button8 -> {
                    textView.text = textView.text.toString() + "8"
                    Log.d("E", "8")
                }
                R.id.button9 -> {
                    textView.text = textView.text.toString() + "9"
                    Log.d("E", "9")
                }
                R.id.button10 -> textView.text = ""
                R.id.button0 -> {
                    textView.text = textView.text.toString() + "0"
                    Log.d("E", "0")
                }
                R.id.button11 -> {
                    textView.text = textView.text.toString() + "#"
                    Log.d("E", "#")
                }
            }
        } // button switch
    }
}