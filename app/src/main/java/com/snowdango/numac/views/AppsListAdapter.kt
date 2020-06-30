package com.snowdango.numac.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.snowdango.numac.models.AppListFormat
import com.snowdango.numac.activites.NumAcActivity
import com.snowdango.numac.R
import java.util.*

/*
This class listen AppListViewFragment.
can create custom list.
 */

class AppsListAdapter(private val context: Context) : BaseAdapter(), Filterable {
    private var layoutInflater: LayoutInflater? = null
    private var allAppList: ArrayList<AppListFormat>? = null
    private var searchList: ArrayList<AppListFormat>? = null
    private var wm: WindowManager? = null

    private val mFilter = ItemFilter()

    fun setSearchList(appList: ArrayList<AppListFormat>?, wm: WindowManager?) {
        searchList = appList
        allAppList = appList
        this.wm = wm
    }

    override fun getCount(): Int {
        return searchList!!.size
    }

    override fun getItem(position: Int): Any {
        return searchList!![position]
    }

    override fun getItemId(position: Int): Long {
        return searchList!![position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: layoutInflater!!.inflate(R.layout.app_list_layout, parent, false)
        val appNameWidth = (NumAcActivity.metrics!!.widthPixels - 70 * NumAcActivity.metrics!!.density - 200).toInt()
        val appIcon = view.findViewById<ImageView>(R.id.app_icon)
        val appName = view.findViewById<TextView>(R.id.app_name)
        val appCommand = view.findViewById<TextView>(R.id.app_command)
        appIcon.setImageDrawable(searchList!![position].appIcon)
        appName.text = searchList!![position].appName
        appCommand.text = searchList!![position].appCommand
        appName.width = appNameWidth
        return view
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    private inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterString = constraint.toString().toLowerCase()
            val results = FilterResults()
            if (filterString.isEmpty()) {
                results.values = allAppList
                results.count = allAppList!!.size
            } else {
                val list = allAppList
                val count = list!!.size
                val nlist = ArrayList<AppListFormat>(count)
                var filterableString: String
                for (i in 0 until count) {
                    filterableString = list[i].appName.toString()
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(list[i])
                    }
                }
                results.values = nlist
                results.count = nlist.size
            }
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            searchList = results.values as ArrayList<AppListFormat>
            notifyDataSetChanged()
        }
    }

    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}