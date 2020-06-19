package com.snowdango.numac.AppDataEditor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.snowdango.numac.ListFormat.AppListFormat;
import com.snowdango.numac.DBControl.FirstLoadAppDb;
import com.snowdango.numac.NumAcMain.NumAcActivity;

import static com.snowdango.numac.NumAcMain.NumAcActivity.builder;
import static com.snowdango.numac.NumAcMain.NumAcActivity.dataBaseHelper;

public class ClickButtonEvents {

    public Intent uninstallApp(String oldCommand){
        Intent intent = null;
        try {
            String[] info = dataBaseHelper.getPackageAndClass(dataBaseHelper, oldCommand);
            intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.fromParts("package", info[0], null));
        }catch (Exception e){
            ClickButtonEvents clickButtonEvents = new ClickButtonEvents();
            clickButtonEvents.AlertCreate("Error","can\'t uninstall app",
                    "OK",null,null,null);
        }finally {
            return intent;
        }
    }

    public boolean deleteApp(int appId, String appName, Context context){ //delete app for list
        boolean errorChecker = true;
        try {
            dataBaseHelper.deleteApp(dataBaseHelper, appName);
            NumAcActivity.list.remove(appId);
            FirstLoadAppDb firstLoadAppDb = new FirstLoadAppDb();
            firstLoadAppDb.updateDbList(dataBaseHelper, context);
        }catch (Exception e){
            errorChecker = false;
        }finally {
            return errorChecker;
        }
    }

    public boolean checkCommandFormat(String newCommand){
        boolean matchNumber = true;
        if (newCommand.length() != 4) {
            matchNumber = false;
            AlertCreate("Error code3-2", "This command doesn't follow the format.\n" +
                                    "You should choose 4 numbers.",
                            "OK", null, null, null);
        }
        for (int i = 0; i < newCommand.length(); i++) {
            if (Character.isDigit(newCommand.charAt(i))) {
                continue;
            } else {
                matchNumber = false;
                AlertCreate("Error 3-3","This command doesn't follow the format.\n" +
                                "You should choose 4 numbers.",
                        "OK",null,null,null);
                break;
            }
        }
        return matchNumber;
    }

    public boolean checkNewCommandForList(String newCommand){
        boolean checkCommandExist = true;
        for(AppListFormat a: NumAcActivity.list){
            if(a.getAppCommand().equals(newCommand)){
                AlertCreate("Error code 3-4","This command already exist in list. \n" +
                                "Please choose deference 4 numbers.",
                        "OK",null,null,null);
                checkCommandExist = false;
                break;
            }
        }
        return checkCommandExist;
    }

    public boolean changeAppCommand(int appPosition,String appName,String newCommand){
        boolean errorChecker = true;
        try{
            NumAcActivity.list.get(appPosition).setAppCommand(newCommand);
            dataBaseHelper.updateCommandWhereName(dataBaseHelper,appName,newCommand);
        }catch (Exception e){
            AlertCreate("Error code 3-5","Sorry, I missed change command. \n Couldn't change this app's command",
                    "OK",null ,null,null);
            errorChecker = false;
        }finally {
            return errorChecker;
        }
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
