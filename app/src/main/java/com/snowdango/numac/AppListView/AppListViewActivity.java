package com.snowdango.numac.AppListView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.snowdango.numac.NumAcMain.NumAcActivity;
import com.snowdango.numac.R;
import com.snowdango.numac.NumAcMain.WaitTimeFragment;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static com.snowdango.numac.NumAcMain.NumAcActivity.builder;
import static com.snowdango.numac.NumAcMain.NumAcActivity.list;
import static com.snowdango.numac.NumAcMain.NumAcActivity.metrics;
import static com.snowdango.numac.NumAcMain.NumAcActivity.windowManager;

public class AppListViewActivity extends AppCompatActivity {


    @SuppressLint("SourceLockedOrientationActivity")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(windowManager == null ){
            windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
            metrics = new DisplayMetrics();
            Display display = windowManager.getDefaultDisplay();
            display.getMetrics(metrics);
        }

        builder = new AlertDialog.Builder(this);
        setContentView(R.layout.activity_app_list);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
    }


    public void onResume() {
        super.onResume();
        loadAppList();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), NumAcActivity.class);
        startActivity(intent);
    }

    public void loadAppList(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        if (list == null) {
            fragmentTransaction.replace(R.id.fragment_app_list, new WaitTimeFragment());
        } else {
            fragmentTransaction.replace(R.id.fragment_app_list, new AppListViewFragment());
        }
        fragmentTransaction.commit();
    }

}
