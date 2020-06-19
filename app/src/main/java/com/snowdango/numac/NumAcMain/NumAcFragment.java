package com.snowdango.numac.NumAcMain;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.snowdango.numac.ListFormat.SharpCommandListFormat;
import com.snowdango.numac.R;

import static com.snowdango.numac.NumAcMain.NumAcActivity.metrics;
import static com.snowdango.numac.NumAcMain.NumAcActivity.sharpCommandList;

public class NumAcFragment extends Fragment implements View.OnClickListener {

    private Button[] buttons;
    private RelativeLayout relativeLayout;
    private LinearLayout buttonLinear, textLinear;
    private TextView textView;
    private SeekBar loadSeek;
    private Handler handler , upHandler;
    private Runnable updateText;
    public static Boolean textPut = true;

    /* first listen method
    return View */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        textPut = true;
        updateText = () ->{
            textView.setText("");
            textPut = true;
        };
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


    /* listener after on create view
       organize view and set button click events */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        //buttonの取得
        buttons = new Button[]{
                v.findViewById(R.id.button1),
                v.findViewById(R.id.button2),
                v.findViewById(R.id.button3),
                v.findViewById(R.id.button4),
                v.findViewById(R.id.button5),
                v.findViewById(R.id.button6),
                v.findViewById(R.id.button7),
                v.findViewById(R.id.button8),
                v.findViewById(R.id.button9),
                v.findViewById(R.id.button10),
                v.findViewById(R.id.button0),
                v.findViewById(R.id.button11)
        };

        //setting button[0] Margin
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) buttons[0].getLayoutParams();
        mlp.setMargins(metrics.widthPixels/4/12, 10,metrics.widthPixels/4/12,10);

        //setting button layout
        for(int i = 0; i < buttons.length; i++){
            buttons[i].setHeight(metrics.heightPixels/3/3);
            buttons[i].setWidth(metrics.widthPixels/4);
            buttons[i].setLayoutParams(mlp);
        }

        //setting message padding
        textView = v.findViewById(R.id.some_things_message);
        textView.setTextSize(metrics.heightPixels/70); // textの設定
        textView.setText("");

        loadSeek = v.findViewById(R.id.loading_seek_bar);
        loadSeek.setMax(100);
        loadSeek.setProgress(100);
        loadSeek.getThumb().mutate().setAlpha(0);
        loadSeek.setOnTouchListener((view , motionEvent) ->{ return true; });

        textLinear = v.findViewById(R.id.textLinear);
        textLinear.setPadding(0,metrics.heightPixels/5,0,metrics.heightPixels/5);

        // get Layout
        relativeLayout = v.findViewById(R.id.relative);
        buttonLinear =  v.findViewById(R.id.button_layout);

        //setting LinearLayout
        buttonLinear.setMinimumHeight(metrics.heightPixels/2/3);

        // set button Click Listener
        for(int i = 0; i < buttons.length; i++) buttons[i].setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ClickTextChanger c = new ClickTextChanger();
        c.clickEvents(v.getId(), textPut, textView);

        int updateLateTime = 1000;

        if (textView.getText().length() == 4) {
            for (SharpCommandListFormat s : sharpCommandList) {
                if (s.getCommand().equals(textView.getText())) {
                    textView.setText(s.getText());
                    textPut = false;
                    updateLateTime = s.getUpdateLateTime();
                    if(s.getText().equals("Loading Now")){
                        ObjectAnimator animation = ObjectAnimator.ofInt (loadSeek, "progress", 0, 100); // see this max value coming back here, we animale towards that value
                        animation.setDuration (4000); //in milliseconds
                        animation.setInterpolator (new DecelerateInterpolator());
                        animation.start ();
                        Log.d("animation","read");
                    }
                    handler = new Handler();
                    handler.postDelayed(s.getRunnable(), s.getLateTime());
                    upHandler = new Handler();
                    upHandler.postDelayed(updateText, updateLateTime);
                    break;
                }
            }
            if (handler == null) {
                ButtonClickEvent buttonClickEvent = new ButtonClickEvent();
                Intent intent = buttonClickEvent.onClickEvents(textView);
                if(intent != null) {
                    startActivity(intent);
                }else {
                    textView.setText(R.string.undefined_app);
                }
                upHandler = new Handler();
                upHandler.postDelayed(updateText, updateLateTime);
            }
        }
    }
}
