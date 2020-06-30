package com.snowdango.numac.activites

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.snowdango.numac.R

/*
This class is third listen activity .
pressed BackKey , launch AppListViewActivity.
pressed HomeKey , launch NumAcActivity .
begin AppDataEditorFragment.
 */

class AppDataEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (NumAcActivity.metrics == null) {
            NumAcActivity.windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            NumAcActivity.metrics = DisplayMetrics()
            val display = NumAcActivity.windowManager!!.defaultDisplay
            display.getMetrics(NumAcActivity.metrics)
        }
        NumAcActivity.builder = AlertDialog.Builder(this)
        setContentView(R.layout.activity_appdata)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    public override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        val intent = Intent(this, AppListViewActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    fun searchAppFormList(appName: String): Int {
        var appPosition = -1
        for (i in NumAcActivity.list!!.indices) {
            if (NumAcActivity.list!![i].appName == appName) {
                appPosition = i
                break
            }
        }
        return appPosition
    }
}