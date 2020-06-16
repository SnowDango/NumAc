package com.snowdango.numac.AppDataEditor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.snowdango.numac.ListFormat.AppListFormat;
import com.snowdango.numac.R;

import static com.snowdango.numac.NumAcMain.NumAcActivity.list;
import static com.snowdango.numac.NumAcMain.NumAcActivity.metrics;

public class AppDataEditorFragment extends Fragment implements View.OnClickListener {

    private Button[] buttons;
    public TextView appNameView;
    private EditText appCommand;
    public String appName = "Error";
    public String oldCommand = "0000";
    private int appPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        appName = getActivity().getIntent().getStringExtra("appName");
        AppDataEditorActivity appDataEditorActivity = new AppDataEditorActivity();
        appPosition = appDataEditorActivity.searchAppFormList(appName);
        if(appPosition != -1) {
            AppListFormat appData = list.get(appPosition);
            oldCommand = appData.getAppCommand();
        }
        return inflater.inflate(R.layout.fragment_appdata,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        InputFilter[] filters = new InputFilter[]{new InputFilter.LengthFilter(4)};
        appCommand = v.findViewById(R.id.target_app_command);
        appCommand.setFilters(filters);
        appCommand.setText(oldCommand);

        appNameView = v.findViewById(R.id.target_app_name);
        appNameView.setPadding(0,metrics.heightPixels/4,0,9*0);
        appNameView.setText(appName);

        buttons = new Button[]{v.findViewById(R.id.uninstall_button),v.findViewById(R.id.replace_button)};
        for(Button b : buttons) {
            b.setWidth(metrics.widthPixels / 3);
            b.setHeight(metrics.heightPixels / 14);
            b.setOnClickListener(this);
        }

        if(appPosition == -1){
            ClickButtonEvents c = new ClickButtonEvents();
            c.AlertCreate("Sorry","Sorry, Can't find this app.\n" +
                            "please push reload appList command(\"##00\")",
                    "OK",null,null,null);
        }
    }

    @Override
    public void onClick(View v) {
        ClickButtonEvents c = new ClickButtonEvents();
        if(appPosition != -1) {
            switch (v.getId()) {
                case R.id.uninstall_button:
                    c.AlertCreate("UnInstallApp","Really? Do you want yo uninstall this app?","OK"
                            ,(DialogInterface dialog, int which) -> {
                                Intent uninstallIntent = c.uninstallApp(oldCommand);
                                if (uninstallIntent != null) {
                                    startActivity(uninstallIntent);
                                    c.deleteApp(appPosition, appName, getActivity());
                                }else{
                                    c.AlertCreate("Sorry", "I can't  this app.", "OK"
                                            , null, null, null);
                                }
                            },"Cancel" , null);
                    break;
                case R.id.replace_button:
                    c.AlertCreate("Change Command", "Really? Do you want yo change this app's command ?" ," OK"
                            , (DialogInterface dialog, int which) -> {
                                String newCommand = appCommand.getText().toString();
                                if (c.checkCommandFormat(newCommand)) {
                                    if (c.checkNewCommandForList(newCommand)) {
                                        if (c.changeAppCommand(appPosition, appName, newCommand)) {
                                            c.AlertCreate("Finish", "Finish update!! \n I change this app's command"
                                                    , "OK", null, null, null);
                                        }
                                    }
                                }
                            }, "Cancel" , null ) ;
                    break;
            }
        }else{
            c.AlertCreate("Sorry","Sorry, Can't find this app.\n" +
                            "please push reload appList command(\"##00\")",
                    "OK",null,null,null);
        }
    }
}