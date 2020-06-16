package com.snowdango.numac.NumAcMain;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.AppLaunchChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.snowdango.numac.AppListView.AppListViewFragment;
import com.snowdango.numac.DBControl.FirstLoadAppDb;
import com.snowdango.numac.R;

import static com.snowdango.numac.NumAcMain.NumAcActivity.dataBaseHelper;
import static com.snowdango.numac.NumAcMain.NumAcActivity.list;

public class WaitTimeFragment extends Fragment {

    private ProgressBar progressBar;
    private Runnable firstAppLoading , appChecker;
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wait,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        progressBar = v.findViewById(R.id.wait_progress);

        fragmentManager = getFragmentManager();

        firstAppLoading = () -> {
            FirstLoadAppDb firstLoadAppDb = new FirstLoadAppDb();
            firstLoadAppDb.firstCreateDb(dataBaseHelper,getActivity());
            changeFragment();
        };
        appChecker = () -> {
            FirstLoadAppDb firstLoadAppDb = new FirstLoadAppDb();
            firstLoadAppDb.updateDbList(dataBaseHelper,getActivity());
            Log.d("appCheckFinish" ,String.valueOf(list.size()));
            changeFragment();
        };

        if(!AppLaunchChecker.hasStartedFromLauncher(getActivity())){
            new Thread(firstAppLoading).start();
        }else{
            if(list == null) {
                new Thread(appChecker).start();
            }else {
                changeFragment();
            }
        }
        AppLaunchChecker.onActivityCreate(getActivity());
    }

    public void changeFragment(){

        Activity activity = getActivity();
        if (activity != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            if(activity.getLocalClassName().equals("NumAcMain.NumAcActivity")) {
                fragmentTransaction.replace(R.id.fragment_main, new NumAcFragment());
            }else if(activity.getLocalClassName().equals("AppListView.AppListActivity")) {
                fragmentTransaction.replace(R.id.fragment_main, new AppListViewFragment());
            }
            fragmentTransaction.commitAllowingStateLoss();
        }
    }
}
