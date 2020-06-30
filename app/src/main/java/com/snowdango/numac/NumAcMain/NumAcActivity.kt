package com.snowdango.numac.NumAcMain

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.AppLaunchChecker
import com.snowdango.numac.AppListView.AppListViewActivity
import com.snowdango.numac.DBControl.DataBaseHelper
import com.snowdango.numac.DBControl.FirstLoadAppDb
import com.snowdango.numac.ListFormat.AppListFormat
import com.snowdango.numac.ListFormat.SharpCommandListFormat
import com.snowdango.numac.R
import java.lang.Exception
import java.util.*

class NumAcActivity : AppCompatActivity() {

    companion object {
        var list: ArrayList<AppListFormat>? = null
        var dataBaseHelper: DataBaseHelper? = null
        var windowManager: WindowManager? = null
        var metrics: DisplayMetrics? = null
        var builder: AlertDialog.Builder? = null
        var sharpCommandList: ArrayList<SharpCommandListFormat>? = null

        var modeThemeNight = "daylight";
    }

    private var mHomeKeyReceiver: HomeKeyReceiver? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBaseHelper = DataBaseHelper(this)
        if (metrics == null) {
            Companion.windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            metrics = DisplayMetrics()
            val display = windowManager!!.defaultDisplay
            display.getMetrics(metrics)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSharpCommandList()
        setContentView(R.layout.activity_fullscreen)
        mHomeKeyReceiver = HomeKeyReceiver()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        try{
            if (AppLaunchChecker.hasStartedFromLauncher(this)) {
                modeThemeNight = dataBaseHelper!!.getThemeColor(dataBaseHelper!!)
                Log.d("mode", modeThemeNight)
            }
        }catch (e : Exception) {
            modeThemeNight = "daylight"
        } finally {
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_UNSPECIFIED) {
                if (modeThemeNight == "dark" && AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    recreate()
                } else if (modeThemeNight == "daylight" && AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    recreate()
                }
            } else {
                if (modeThemeNight == "dark") {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else if (modeThemeNight == "daylight") {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
            loadAppList()
        }
    }

    public override fun onResume() {
        super.onResume()
        if (mHomeKeyReceiver!!.debugUnregister) unregisterReceiver(mHomeKeyReceiver)
    }

    override fun onBackPressed() {}
    override fun onPause() {
        super.onPause()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        if (mHomeKeyReceiver!!.debugUnregister) registerReceiver(mHomeKeyReceiver, filter)
    }

    fun setSharpCommandList() {
        sharpCommandList = ArrayList(Arrays.asList(
                SharpCommandListFormat(
                        "####", "Change Mode",
                        Runnable {
                            try {
                                modeThemeNight = if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                    dataBaseHelper!!.updateColor(dataBaseHelper!!, "daylight")
                                    Log.d("theme", " daylight")
                                    "daylight"
                                } else {
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                                    dataBaseHelper!!.updateColor(dataBaseHelper!!, "dark")
                                    Log.d("theme", " dark ")
                                    "dark"
                                }
                            }catch (e :Exception){
                                modeThemeNight = "daylight"
                            }finally {
                                recreate()
                            }
                        }, 500, 1500),
                SharpCommandListFormat(
                        "0000", "Open List",
                        Runnable {
                            val intent = Intent(this, AppListViewActivity::class.java)
                            startActivity(intent)
                        }, 500, 2000),
                SharpCommandListFormat(
                        "##00", "Loading Now",
                        Runnable { appListUpdate() }, 0, 4000)
        ))
    }

    private fun appListUpdate() {
        if(dataBaseHelper == null){
            dataBaseHelper = DataBaseHelper(this)
        }

        val firstLoadAppDb = FirstLoadAppDb()
        firstLoadAppDb.updateDbList(dataBaseHelper!!, this)
    }

    fun loadAppList() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)
        if (list == null) {
            fragmentTransaction.replace(R.id.fragment_main, WaitTimeFragment())
        } else {
            fragmentTransaction.replace(R.id.fragment_main, NumAcFragment())
        }
        fragmentTransaction.commit()
    }

    private inner class HomeKeyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val intent2 = Intent(application, NumAcActivity::class.java)
            startActivity(intent2)
            finish()
        }
    }
}