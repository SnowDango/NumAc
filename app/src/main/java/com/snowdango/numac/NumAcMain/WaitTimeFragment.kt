package com.snowdango.numac.NumAcMain

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.app.AppLaunchChecker
import androidx.fragment.app.Fragment
import com.snowdango.numac.AppListView.AppListViewFragment
import com.snowdango.numac.DBControl.FirstLoadAppDb
import com.snowdango.numac.R

class WaitTimeFragment : Fragment() {
    private var progressBar: ProgressBar? = null
    private var firstAppLoading: Runnable? = null
    private var appChecker: Runnable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wait, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)
        progressBar = v.findViewById(R.id.wait_progress)
        firstAppLoading = Runnable {
            val firstLoadAppDb = FirstLoadAppDb()
            firstLoadAppDb.firstCreateDb(NumAcActivity.dataBaseHelper!!, activity)
            changeFragment()
        }
        appChecker = Runnable {
            val firstLoadAppDb = FirstLoadAppDb()
            firstLoadAppDb.updateDbList(NumAcActivity.dataBaseHelper!!, activity)
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

    fun changeFragment() {
        val activity: Activity? = activity
        if (activity != null) {
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            if (activity.localClassName == "NumAcMain.NumAcActivity") {
                fragmentTransaction.replace(R.id.fragment_main, NumAcFragment())
            } else if (activity.localClassName == "AppListView.AppListActivity") {
                fragmentTransaction.replace(R.id.fragment_main, AppListViewFragment())
            }
            fragmentTransaction.commitAllowingStateLoss()
        }
    }
}