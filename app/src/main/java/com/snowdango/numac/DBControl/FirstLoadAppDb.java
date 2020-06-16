package com.snowdango.numac.DBControl;

import android.content.Context;

import com.snowdango.numac.ListFormat.AppListFormat;
import com.snowdango.numac.AppListView.AppListCreate;
import com.snowdango.numac.NumAcMain.NumAcActivity;

import java.util.ArrayList;

public class FirstLoadAppDb {

    public void firstCreateDb(DataBaseHelper dataBaseHelper, Context context){
        AppListCreate appListCreate = new AppListCreate();
        NumAcActivity.list = new ArrayList<>();
        NumAcActivity.list = (ArrayList<AppListFormat>) appListCreate.appRead(context);

        for (AppListFormat app: NumAcActivity.list) {
            dataBaseHelper.insertData(dataBaseHelper, app.getAppName(),
                    app.getAppPackageName(),app.getAppClassName(),app.getAppCommand());
        };

        dataBaseHelper.insertColor(dataBaseHelper,"daylight");
    }

    public void updateDbList(DataBaseHelper dataBaseHelper, Context context ){

        AppListCreate appListCreate = new AppListCreate();
        NumAcActivity.list = (ArrayList<AppListFormat>) appListCreate.appRead(context);

        ArrayList<String> appNameList = dataBaseHelper.getAppNameList(dataBaseHelper);
        ArrayList<String> appCommandList = dataBaseHelper.getAppCommandList(dataBaseHelper);
        ArrayList<AppListFormat> queryList = new ArrayList<AppListFormat>();

        dataBaseHelper.clearTable(dataBaseHelper);

        for(int i = 0; i < NumAcActivity.list.size(); i++){
            if(NumAcActivity.list.get(i).getAppName().equals(appNameList.get(i))){
                AppListFormat appListFormat = new AppListFormat();
                appListFormat = NumAcActivity.list.get(i);
                appListFormat.setAppCommand(appCommandList.get(i));
                queryList.add(appListFormat);
            }else{
                queryList.add(NumAcActivity.list.get(i));
            }
            dataBaseHelper.insertData(dataBaseHelper,queryList.get(i).getAppName(),queryList.get(i).getAppPackageName(),
                    queryList.get(i).getAppClassName(),queryList.get(i).getAppCommand());
        }
        NumAcActivity.list.clear();
        NumAcActivity.list = queryList;
    }

    public void checkAppList(DataBaseHelper dataBaseHelper,Context context){
        AppListCreate appListCreate = new AppListCreate();
        NumAcActivity.list = new ArrayList<>();
        NumAcActivity.list = (ArrayList<AppListFormat>) appListCreate.appRead(context);

        ArrayList<String> appNameList = dataBaseHelper.getAppNameList(dataBaseHelper);

        for(int i = 0; i < NumAcActivity.list.size(); i++){
            if(!appNameList.contains(NumAcActivity.list.get(i).getAppName())){
                    dataBaseHelper.insertData(dataBaseHelper, NumAcActivity.list.get(i).getAppName(),
                            NumAcActivity.list.get(i).getAppPackageName(),NumAcActivity.list.get(i).getAppClassName(),NumAcActivity.list.get(i).getAppCommand());
            }
        }
    }

    public void setCommandList(DataBaseHelper dataBaseHelper){
        ArrayList<String> appCommandList = dataBaseHelper.getAppCommandList(dataBaseHelper);
        ArrayList<AppListFormat> queryList = new ArrayList<>();

        for(int i = 0; i < NumAcActivity.list.size(); i++){
            AppListFormat appListFormat = new AppListFormat();
            appListFormat = NumAcActivity.list.get(i);
            appListFormat.setAppCommand(appCommandList.get(i));
            queryList.add(appListFormat);
        }

        NumAcActivity.list.clear();
        NumAcActivity.list = queryList;
    }

}
