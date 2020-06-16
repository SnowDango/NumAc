package com.snowdango.numac.NumAcMain;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.AppLaunchChecker;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.snowdango.numac.AppListView.AppListViewActivity;
import com.snowdango.numac.DBControl.DataBaseHelper;
import com.snowdango.numac.DBControl.FirstLoadAppDb;
import com.snowdango.numac.ListFormat.AppListFormat;
import com.snowdango.numac.ListFormat.SharpCommandListFormat;
import com.snowdango.numac.R;
import com.snowdango.numac.DBControl.*;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class NumAcActivity extends AppCompatActivity {

    public static ArrayList<AppListFormat> list;
    public static DataBaseHelper dataBaseHelper;
    public static String modeThemeNight = "daylight";
    public static WindowManager windowManager;
    public static DisplayMetrics metrics;
    public static AlertDialog.Builder builder;

    private HomeKeyReceiver mHomeKeyReceiver;
    public static ArrayList<SharpCommandListFormat> sharpCommandList;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataBaseHelper = new DataBaseHelper(this);

        if(NumAcActivity.metrics == null){
            windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
            metrics = new DisplayMetrics();
            Display display = windowManager.getDefaultDisplay();
            display.getMetrics(metrics);
        }

        try {
            if (!AppLaunchChecker.hasStartedFromLauncher(this))
                modeThemeNight = dataBaseHelper.getThemeColor(dataBaseHelper);
        }catch (Exception e){

        }
        if(modeThemeNight.equals("dark")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        setSharpCommandList();
        setContentView(R.layout.activity_fullscreen);

        mHomeKeyReceiver = new HomeKeyReceiver();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadAppList();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mHomeKeyReceiver.getDebugUnregister()) unregisterReceiver(mHomeKeyReceiver);
    }

    @Override
    public void onBackPressed(){ }

    @Override
    protected void onPause() {
        super.onPause();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        if(mHomeKeyReceiver.getDebugUnregister()) registerReceiver(mHomeKeyReceiver, filter);
    }

    public void setSharpCommandList(){
        sharpCommandList = new ArrayList<SharpCommandListFormat>(Arrays.asList(
                new SharpCommandListFormat(
                        "####", "Change Mode",
                        () ->  {
                            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                dataBaseHelper.updateColor(dataBaseHelper,"dark");
                                modeThemeNight = "dark";
                            }else {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                dataBaseHelper.updateColor(dataBaseHelper,"daylight");
                                modeThemeNight = "daylight";
                            }
                            recreate();
                        },500,1500),
                new SharpCommandListFormat(
                        "0000", "Open List",
                        ()-> {
                            Intent intent = new Intent(this, AppListViewActivity.class);
                            startActivity(intent);
                        },500,2000),
                new SharpCommandListFormat(
                        "##00", "Loading Now",
                        ()->{
                            appListUpdate();
                        },0,4000)
        ));
    }

    private void appListUpdate() {
        FirstLoadAppDb firstLoadAppDb = new FirstLoadAppDb();
        firstLoadAppDb.updateDbList(dataBaseHelper,this);
    }

    public void loadAppList(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        if (list == null) {
            fragmentTransaction.replace(R.id.fragment_main, new WaitTimeFragment());
        } else {
            fragmentTransaction.replace(R.id.fragment_main, new NumAcFragment());
        }
        fragmentTransaction.commit();
    }

    private class HomeKeyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            Intent intent2 = new Intent(getApplication(),NumAcActivity.class);
            startActivity(intent2);
            finish();
        }
    }
}