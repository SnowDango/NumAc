package com.snowdango.numac.AppListView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.snowdango.numac.AppDataEditor.AppDataEditorActivity;
import com.snowdango.numac.ListFormat.AppListFormat;
import com.snowdango.numac.DBControl.FirstLoadAppDb;
import com.snowdango.numac.NumAcMain.NumAcActivity;
import com.snowdango.numac.R;

import static com.snowdango.numac.NumAcMain.NumAcActivity.builder;
import static com.snowdango.numac.NumAcMain.NumAcActivity.dataBaseHelper;
import static com.snowdango.numac.NumAcMain.NumAcActivity.windowManager;

public class AppListViewFragment extends Fragment implements TextWatcher, AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{

    public EditText editText;
    public AbsListView listView;
    private AppsListAdapter appsListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_list,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        appsListAdapter = new AppsListAdapter(getActivity());
        appsListAdapter.setSearchList(NumAcActivity.list,windowManager);

        listView = v.findViewById(R.id.listView1);
        listView.setAdapter(appsListAdapter);

        editText = v.findViewById(R.id.editText1);

        editText.addTextChangedListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        appsListAdapter.getFilter().filter(s.toString());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppListFormat selectApp = (AppListFormat) appsListAdapter.getItem(position);
        try {
            Intent intent = new Intent();
            if(selectApp.getAppPackageName() != null){
                if(selectApp.getAppClassName() != null) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClassName(selectApp.getAppPackageName(), selectApp.getAppClassName());
                }
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }catch (Exception e){
            AlertCreate("Error ","Sorry \n I missed open this application.",
                    "OK",null, null ,null );
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AppListFormat selectApp = (AppListFormat) appsListAdapter.getItem(position);

        Intent intent = new Intent(getActivity(), AppDataEditorActivity.class);
        intent.putExtra("appName",selectApp.getAppName());
        startActivity(intent);
        return true;
    }

    public void appChecker(){
        FirstLoadAppDb firstLoadAppDb = new FirstLoadAppDb();
        firstLoadAppDb.updateDbList(dataBaseHelper,getActivity());
    }

    public void AlertCreate(String title, String message, String positive, DialogInterface.OnClickListener positiveListener,
                            String negative , DialogInterface.OnClickListener negativeListener){
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positive,positiveListener);
        builder.setNegativeButton(negative,negativeListener);
        builder.show();
    }
}
