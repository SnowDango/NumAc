/*
 * Copyright (C) 2019-2020 snowdango
 */
package com.snowdango.numac.activites

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.snowdango.numac.fragments.WaitTimeFragment
import com.snowdango.numac.R
import com.snowdango.numac.fragments.AppListViewFragment

/*
This class is second listen activity.
pushed HomeKey or BackKey , launch Main Activity.
If Main Activity's companion app list is null , begin WaitTimeFragment to load app list.
default begin AppListViewFragment.
 */

class AppListViewActivity : AppCompatActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (NumAcActivity.windowManager == null) {
            NumAcActivity.windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            NumAcActivity.metrics = DisplayMetrics()
            val display = NumAcActivity.windowManager!!.defaultDisplay
            display.getMetrics(NumAcActivity.metrics)
        }
        NumAcActivity.builder = AlertDialog.Builder(this)
        setContentView(R.layout.activity_app_list)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    public override fun onResume() {
        super.onResume()
        loadAppList()
    }

    public override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        val intent = Intent(application, NumAcActivity::class.java)
        startActivity(intent)
    }

    private fun loadAppList() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)
        if (NumAcActivity.list == null) {
            fragmentTransaction.replace(R.id.fragment_app_list, WaitTimeFragment())
        } else {
            fragmentTransaction.replace(R.id.fragment_app_list, AppListViewFragment())
        }
        fragmentTransaction.commit()
    }
}