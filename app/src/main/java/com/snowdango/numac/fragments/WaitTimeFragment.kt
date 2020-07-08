package com.snowdango.numac.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.app.AppLaunchChecker
import androidx.fragment.app.Fragment
import com.snowdango.numac.controller.FirstLoadAppDb
import com.snowdango.numac.R
import com.snowdango.numac.activites.NumAcActivity

/*
This fragment listen by Main and AppListViewActivity.
can load app list.
create wait view when load app list.
 */

class WaitTimeFragment : Fragment() {
    private var progressBar: ProgressBar? = null
    private var firstAppLoading: Runnable? = null
    private var appChecker: Runnable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wait, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        val loadLinear: LinearLayout = v.findViewById(R.id.loadLinear)
        val logoLinear: LinearLayout = v.findViewById(R.id.logoLinear)

        progressBar = v.findViewById(R.id.wait_progress)

        loadLinear.minimumHeight = NumAcActivity.metrics!!.heightPixels *  3/5
        logoLinear.minimumHeight = NumAcActivity.metrics!!.heightPixels *  2/5

        firstAppLoading = Runnable {
            val firstLoadAppDb = FirstLoadAppDb()
            firstLoadAppDb.firstCreateDb(NumAcActivity.dataBaseHelper!!, activity)
            changeFragment()
        }
        appChecker = Runnable {
            val firstLoadAppDb = FirstLoadAppDb()
            firstLoadAppDb.updateList(NumAcActivity.dataBaseHelper!!, activity)
            Log.d("appCheckFinish", NumAcActivity.list!!.size.toString())
            changeFragment()
        }
        if (!AppLaunchChecker.hasStartedFromLauncher(activity!!)) {
            Thread(firstAppLoading).start()
        } else {
            if (NumAcActivity.list == null) {
                Thread(appChecker).start()
            } else {
                changeFragment()
            }
        }
        AppLaunchChecker.onActivityCreate(activity!!)
    }

    private fun changeFragment() {
        val activity: Activity? = activity
        if (activity != null) {
            Log.d("localName", activity.localClassName )
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            if (activity.localClassName == "activites.NumAcActivity") {
                fragmentTransaction.replace(R.id.fragment_main, NumAcFragment())
            } else if (activity.localClassName == "activites.AppListActivity") {
                fragmentTransaction.replace(R.id.fragment_main, AppListViewFragment())
            }
            fragmentTransaction.commitAllowingStateLoss()
        }
    }
}