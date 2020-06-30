@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.snowdango.numac.AppDataEditor

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.snowdango.numac.NumAcMain.NumAcActivity
import com.snowdango.numac.R

class AppDataEditorFragment : Fragment(), View.OnClickListener {
    private lateinit var buttons: Array<Button>
    lateinit var appNameView: TextView
    private lateinit var appCommand: EditText
    var appName = "Error"
    var oldCommand = "0000"
    private var appPosition = -1
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        appName = activity!!.intent.getStringExtra("appName")
        val appDataEditorActivity = AppDataEditorActivity()
        appPosition = appDataEditorActivity.searchAppFormList(appName)
        if (appPosition != -1) {
            val appData = NumAcActivity.list!![appPosition]
            oldCommand = appData.appCommand.toString()
        }
        return inflater.inflate(R.layout.fragment_appdata, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)
        val filters = arrayOf<InputFilter>(LengthFilter(4))
        appCommand = v.findViewById(R.id.target_app_command)
        appCommand.filters = filters
        appCommand.setText(oldCommand)
        appNameView = v.findViewById(R.id.target_app_name)
        appNameView.setPadding(0, NumAcActivity.metrics!!.heightPixels / 4, 0, 9 * 0)
        appNameView.text = appName
        buttons = arrayOf(v.findViewById(R.id.uninstall_button), v.findViewById(R.id.replace_button))
        for (b in buttons) {
            b.width = NumAcActivity.metrics!!.widthPixels / 3
            b.height = NumAcActivity.metrics!!.heightPixels / 14
            b.setOnClickListener(this)
        }
        if (appPosition == -1) {
            val c = ClickButtonEvents()
            c.AlertCreate("Sorry", """Sorry, Can't find this app.please push reload appList command("##00")""".trimIndent(),
                    "OK", null, null, null)
        }
    }

    override fun onClick(v: View) {
        val c = ClickButtonEvents()
        if (appPosition != -1) {
            when (v.id) {
                R.id.uninstall_button -> c.AlertCreate("UnInstallApp", "Really? Do you want yo uninstall this app?", "OK"
                        , DialogInterface.OnClickListener { _: DialogInterface?, _: Int ->
                    val uninstallIntent = c.uninstallApp(oldCommand)
                    if (uninstallIntent != null) {
                        startActivity(uninstallIntent)
                        c.deleteApp(appPosition, appName, activity)
                    } else {
                        c.AlertCreate("Sorry", "I can't  this app.", "OK"
                                , null, null, null)
                    }
                }, "Cancel", null)
                R.id.replace_button -> c.AlertCreate("Change Command", "Really? Do you want yo change this app's command ?", " OK"
                        , DialogInterface.OnClickListener { _: DialogInterface?, _: Int ->
                    val newCommand = appCommand.text.toString()
                    if (c.checkCommandFormat(newCommand)) {
                        if (c.checkNewCommandForList(newCommand)) {
                            if (c.changeAppCommand(appPosition, appName, newCommand)) {
                                c.AlertCreate("Finish", "Finish update!! \n I change this app's command"
                                        , "OK", null, null, null)
                            }
                        }
                    }
                }, "Cancel", null)
            }
        } else {
            c.AlertCreate("Sorry", """Sorry, Can't find this app.please push reload appList command("##00")""".trimIndent(),
                    "OK", null, null, null)
        }
    }
}