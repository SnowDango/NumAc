package com.snowdango.numac.NumAcMain;

import android.util.Log;
import android.widget.TextView;

import com.snowdango.numac.R;


public class ClickTextChanger{

    public void clickEvents(int button, Boolean b, TextView textView){

        if(b) {
            switch (button) {
                case R.id.button1:
                    textView.setText(textView.getText() + "1");
                    Log.d("E","1");
                    break;
                case R.id.button2:
                    textView.setText(textView.getText() + "2");
                    Log.d("E","2");
                    break;
                case R.id.button3:
                    textView.setText(textView.getText() + "3");
                    Log.d("E","3");
                    break;
                case R.id.button4:
                    textView.setText(textView.getText() + "4");
                    Log.d("E","4");
                    break;
                case R.id.button5:
                    textView.setText(textView.getText() + "5");
                    Log.d("E","5");
                    break;
                case R.id.button6:
                    textView.setText(textView.getText() + "6");
                    Log.d("E","6");
                    break;
                    case R.id.button7:
                    textView.setText(textView.getText() + "7");
                        Log.d("E","7");
                    break;
                case R.id.button8:
                    textView.setText(textView.getText() + "8");
                    Log.d("E","8");
                    break;
                case R.id.button9:
                    textView.setText(textView.getText() + "9");
                    Log.d("E","9");
                    break;
                case R.id.button10:
                    textView.setText("");
                    break;
                case R.id.button0:
                    textView.setText(textView.getText() + "0");
                    Log.d("E","0");
                    break;
                case R.id.button11:
                    textView.setText(textView.getText() + "#");
                    Log.d("E","#");
                    break;

            }
        } // button switch



    }

}
