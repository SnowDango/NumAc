package com.snowdango.numac.AppListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.snowdango.numac.ListFormat.AppListFormat;
import com.snowdango.numac.R;

import java.util.ArrayList;

import static com.snowdango.numac.NumAcMain.NumAcActivity.metrics;


public class AppsListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private LayoutInflater layoutInflater = null;
    private ArrayList<AppListFormat> allAppList = null;
    private ArrayList<AppListFormat> searchList = null;
    private ArrayList<AppListFormat> filteredData = null;
    private ItemFilter mFilter = new ItemFilter();
    private WindowManager wm;

    public AppsListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setSearchList(ArrayList<AppListFormat> appList, WindowManager wm) {
        this.searchList = appList;
        this.allAppList = appList;
        this.wm = wm;
    }

    @Override
    public int getCount() {
        return searchList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return searchList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.app_list_layout,parent,false);

        int appNameWidth = (int) (metrics.widthPixels - (70 * metrics.density) - 200);

        ImageView appIcon = convertView.findViewById(R.id.app_icon);
        TextView appName = convertView.findViewById(R.id.app_name);
        TextView appCommand = convertView.findViewById(R.id.app_command);

        appIcon.setImageDrawable(searchList.get(position).getAppIcon());
        appName.setText(searchList.get(position).getAppName());
        appCommand.setText(searchList.get(position).getAppCommand());


        appName.setWidth(appNameWidth);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            if(filterString.isEmpty()){
                results.values = allAppList;
                results.count = allAppList.size();
            }else {

                final ArrayList<AppListFormat> list = allAppList;

                int count = list.size();
                final ArrayList<AppListFormat> nlist = new ArrayList<AppListFormat>(count);

                String filterableString;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i).getAppName();
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(list.get(i));
                    }
                }

                results.values = nlist;
                results.count = nlist.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            searchList = (ArrayList<AppListFormat>) results.values;
            notifyDataSetChanged();
        }
    }
}
