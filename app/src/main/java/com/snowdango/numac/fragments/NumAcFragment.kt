package com.snowdango.numac.fragments

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.snowdango.numac.controller.ButtonClickEvent
import com.snowdango.numac.controller.ClickTextChanger
import com.snowdango.numac.R
import com.snowdango.numac.activites.NumAcActivity

/*
This class listen by main Activity.
create view and on click event.
this class use handler and runnable.
 */

class NumAcFragment : Fragment(), View.OnClickListener {
    private lateinit var buttons: Array<Button>
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var buttonLinear: LinearLayout
    private lateinit var textLinear: LinearLayout
    private lateinit var textView: TextView
    private lateinit var loadSeek: SeekBar

    private var handler: Handler? = null
    private var upHandler: Handler? = null
    private var updateText: Runnable? = null

    /* first listen method
    return View */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        textPut = true
        updateText = Runnable {
            textView!!.text = ""
            textPut = true
        }
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    /* listener after on create view
       organize view and set button click events */
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        //buttonの取得
        buttons = arrayOf(
                v.findViewById(R.id.button1),
                v.findViewById(R.id.button2),
                v.findViewById(R.id.button3),
                v.findViewById(R.id.button4),
                v.findViewById(R.id.button5),
                v.findViewById(R.id.button6),
                v.findViewById(R.id.button7),
                v.findViewById(R.id.button8),
                v.findViewById(R.id.button9),
                v.findViewById(R.id.button10),
                v.findViewById(R.id.button0),
                v.findViewById(R.id.button11)
        )

        //setting button[0] Margin
        val mlp = buttons[0].layoutParams as MarginLayoutParams
        mlp.setMargins(NumAcActivity.metrics!!.widthPixels / 4 / 12, 10, NumAcActivity.metrics!!.widthPixels / 4 / 12, 10)

        //setting button layout
        for (i in buttons.indices) {
            buttons[i].height = NumAcActivity.metrics!!.heightPixels / 3 / 3
            buttons[i].width = NumAcActivity.metrics!!.widthPixels / 4
            buttons[i].layoutParams = mlp
        }

        //setting message padding
        textView = v.findViewById(R.id.some_things_message)
        textView.textSize = NumAcActivity.metrics!!.heightPixels / 70.toFloat() // textの設定
        textView.text = ""
        loadSeek = v.findViewById(R.id.loading_seek_bar)
        loadSeek.max = 100
        loadSeek.progress = 100
        loadSeek.thumb.mutate().alpha = 0
        loadSeek.setOnTouchListener(OnTouchListener { _: View?, _: MotionEvent? -> true })
        textLinear = v.findViewById(R.id.textLinear)
        textLinear.setPadding(0, NumAcActivity.metrics!!.heightPixels / 5, 0, NumAcActivity.metrics!!.heightPixels / 5)

        // get Layout
        relativeLayout = v.findViewById(R.id.relative)
        buttonLinear = v.findViewById(R.id.button_layout)

        //setting LinearLayout
        buttonLinear.minimumHeight = NumAcActivity.metrics!!.heightPixels / 2 / 3

        // set button Click Listener
        for (i in buttons.indices) buttons[i].setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val c = ClickTextChanger()
        c.clickEvents(v.id, textPut, textView)
        var updateLateTime = 1000
        if (textView.text.length == 4) {
            handler = null
            upHandler = null
            val command = textView.text as String
            for (s in NumAcActivity.sharpCommandList!!) {
                if (s.command == command) {
                    textView.text = s.text
                    textPut = false
                    updateLateTime = s.updateLateTime
                    if (s.text == "Loading Now") {
                        val animation = ObjectAnimator.ofInt(loadSeek, "progress", 0, 100)
                        // see this max value coming back here, we animale towards that value
                        animation.duration = 4000 //in milliseconds
                        animation.interpolator = DecelerateInterpolator()
                        animation.start()
                        Log.d("animation", "read")
                    }
                    handler = Handler()
                    handler?.postDelayed(s.runnable, s.lateTime.toLong())
                    upHandler = Handler()
                    upHandler?.postDelayed(updateText, updateLateTime.toLong())
                    break
                }
            }
            if (handler == null) {
                val buttonClickEvent = ButtonClickEvent()
                val intent = buttonClickEvent.onClickEvents(textView)
                if (intent != null) {
                    startActivity(intent)
                } else {
                    textView.setText(R.string.undefined_app)
                }
                upHandler = Handler()
                upHandler?.postDelayed(updateText, updateLateTime.toLong())
            }
        }
    }

    companion object {
        @JvmField
        var textPut = true
    }
}