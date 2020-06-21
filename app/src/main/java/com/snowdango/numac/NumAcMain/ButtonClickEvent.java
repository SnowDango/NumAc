package com.snowdango.numac.NumAcMain;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import static com.snowdango.numac.NumAcMain.NumAcActivity.dataBaseHelper;
import static com.snowdango.numac.NumAcMain.NumAcFragment.textPut;

public class ButtonClickEvent {

    private Handler handler = new Handler();

    public Intent onClickEvents(final TextView textView) {
        Intent intent = null;

        Runnable updateText = new Runnable() {
            @Override
            public void run() {
                textView.setText("");
                textPut = true;
            }
        };

        String command = (String) textView.getText();

        try {
            textView.setText("Load");
            textPut = false;
            String[] info = dataBaseHelper.getPackageAndClass(dataBaseHelper, command);

            if (info[0] != null) {
                intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClassName(info[0], info[1]);
            }

        } catch (Exception e) {
            Log.d("command", command);
            textView.setText("Error");
            textPut = false;
            intent = null;
            handler = new Handler();
            handler.postDelayed(updateText, 1000);
        } finally {
            return intent;
        }
    }
}
