package com.snowdango.numac.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.snowdango.numac.activites.AppDataEditorActivity
import com.snowdango.numac.views.AppsListAdapter
import com.snowdango.numac.controller.FirstLoadAppDb
import com.snowdango.numac.models.AppListFormat
import com.snowdango.numac.activites.NumAcActivity
import com.snowdango.numac.R

/*
This fragment listen by AppListViewActivity.
set EditText for search and custom list view (created by AppListAdapter).
get long click event , text change event and click event.
 */

class AppListViewFragment : Fragment(), TextWatcher, OnItemClickListener, OnItemLongClickListener {
    private lateinit var editText: EditText
    private lateinit var listView: AbsListView
    private var appsListAdapter: AppsListAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_list, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)
        appsListAdapter = AppsListAdapter(activity!!)
        appsListAdapter!!.setSearchList(NumAcActivity.list, NumAcActivity.windowManager)
        listView = v.findViewById(R.id.listView1)
        listView.adapter = appsListAdapter
        editText = v.findViewById(R.id.editText1)
        editText.addTextChangedListener(this)
        listView.onItemClickListener = this
        listView.onItemLongClickListener = this
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        appsListAdapter!!.filter.filter(s.toString())
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val selectApp = appsListAdapter!!.getItem(position) as AppListFormat
        try {
            val intent = Intent()
            if (selectApp.appPackageName != null) {
                if (selectApp.appClassName != null) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.setClassName(selectApp.appPackageName!!, selectApp.appClassName!!)
                }
                startActivity(intent)
                activity!!.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        } catch (e: Exception) {
            AlertCreate("Error ", "Sorry \n I missed open this application.",
                    "OK", null, null, null)
        }
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View, position: Int, id: Long): Boolean {
        val selectApp = appsListAdapter!!.getItem(position) as AppListFormat
        val intent = Intent(activity, AppDataEditorActivity::class.java)
        intent.putExtra("appName", selectApp.appName)
        startActivity(intent)
        return true
    }

    fun AlertCreate(title: String?, message: String?, positive: String?, positiveListener: DialogInterface.OnClickListener?,
                            negative: String?, negativeListener: DialogInterface.OnClickListener?) {
        NumAcActivity.builder!!.setTitle(title)
        NumAcActivity.builder!!.setMessage(message)
        NumAcActivity.builder!!.setPositiveButton(positive, positiveListener)
        NumAcActivity.builder!!.setNegativeButton(negative, negativeListener)
        NumAcActivity.builder!!.show()
    }
}