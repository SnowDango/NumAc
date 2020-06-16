package com.snowdango.numac.AppListView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.snowdango.numac.ListFormat.AppListFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AppListCreate{

    public List<AppListFormat> appRead(Context context){

        List<AppListFormat> data = new ArrayList<>();

        PackageManager pm = context.getApplicationContext().getPackageManager();
        List<PackageInfo> pkgInfoList = pm.getInstalledPackages(0);

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        for (PackageInfo pckInfo : pkgInfoList) {
            if (pm.getLaunchIntentForPackage(pckInfo.packageName) != null) {
                AppListFormat appInfo = new AppListFormat();
                appInfo.setAppIcon(pckInfo.applicationInfo.loadIcon(pm));
                appInfo.setAppName((String) pckInfo.applicationInfo.loadLabel(pm));
                appInfo.setAppPackageName(pckInfo.packageName);
                appInfo.setAppClassName(pm.getLaunchIntentForPackage(pckInfo.packageName).getComponent().getClassName() + "");
                appInfo.setAppCommand(randomCommand());
                data.add(appInfo);
            }
        }

        return data;
    }


    public String randomCommand(){
        Random r = new Random();
        int num = 0;
        String appCommand = "";
        for(int i = 0; i < 4; i++){
            num = r.nextInt(10);
            appCommand += num;
        }
        if (appCommand.equals("0000")) {
            appCommand = randomCommand();
        }
        return  appCommand;
    }
}

