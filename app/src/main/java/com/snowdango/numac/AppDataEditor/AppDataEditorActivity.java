package com.snowdango.numac.AppDataEditor;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.snowdango.numac.AppListView.AppListViewActivity;
import com.snowdango.numac.R;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
import static com.snowdango.numac.NumAcMain.NumAcActivity.builder;
import static com.snowdango.numac.NumAcMain.NumAcActivity.list;
import static com.snowdango.numac.NumAcMain.NumAcActivity.metrics;
import static com.snowdango.numac.NumAcMain.NumAcActivity.windowManager;

public class AppDataEditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(metrics == null){
            windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
            metrics = new DisplayMetrics();
            Display display = windowManager.getDefaultDisplay();
            display.getMetrics(metrics);
        }

        builder = new AlertDialog.Builder(this);
        setContentView(R.layout.activity_appdata);
        setRequestedOrientation(SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, AppListViewActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public int searchAppFormList(String appName){
        int appPosition = -1;
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getAppName().equals(appName)){
                appPosition = i;
                break;
            }
        }
        return appPosition;
    }
}
