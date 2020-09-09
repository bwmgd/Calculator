package com.example.myapplication;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private TextView showTextView;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        showTextView = findViewById(R.id.show_textView);
        resultTextView = findViewById(R.id.result_textView);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            final Button button = (Button) gridLayout.getChildAt(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = button.getText().toString();
                    Log.v("chick", sign + "," + numInput + "," + numSave + "," + show + "," + signFlag);
                    calculation(s);
                }
            });
        }
    }

    private String sign = "+";
    private double numSave = 0;
    private String numInput = "0";
    private String show = "";
    private boolean signFlag = false;
    private boolean equalsFlag = false;
    private boolean pointFlag = false;

    private void calculation(String str) {
        switch (str) {
            case "+":
            case "-":
            case "*":
            case "/":
                equalsFlag = false;
                pointFlag = false;
                numSave = result(str);
                sign = str;
                resultTextView.setText(numSave + "");
                signFlag = true;
                break;
            case "=":
                signFlag = false;
                equalsFlag = true;
                pointFlag = false;
                numSave = result(str);
                resultTextView.setText(numSave + "");
                show = numSave + "";
                break;
            case "C":
                initialization();
                resultTextView.setText(numInput);
                break;
            case "CE":
                equalsFlag = false;
                signFlag = false;
                pointFlag = false;
                numInput = "0";
                resultTextView.setText(numInput);
                break;
            case "back":
                if (!numInput.equals("")) {
                    equalsFlag = false;
                    signFlag = false;
                    pointFlag = false;
                    numInput = numInput.length() == 1 ? "0" : numInput.substring(0, numInput.length() - 1);
                    resultTextView.setText(numInput);
                }
                break;
            case ".":
                if (!pointFlag) {
                    pointFlag = true;
                    signFlag = false;
                    if (equalsFlag) initialization();
                    numInput += str;
                    resultTextView.setText(numInput);
                }
                break;
            default:
                pointFlag = false;
                signFlag = false;
                if (equalsFlag) initialization();
                numInput = numInput.equals("0") ? str : (numInput + str);
                resultTextView.setText(numInput);
                break;
        }
        showTextView.setText(show);
        Log.i("show", show);
        Log.i("result", numInput);
    }

    private void initialization() {
        equalsFlag = false;
        sign = "+";
        numSave = 0;
        numInput = "0";
        show = "";
        signFlag = false;
    }

    private double result(String str) {
        if (signFlag) {
            show = show.substring(0, show.length() - 1) + str;
            return numSave;
        }
        if (show.equals("")) show = numInput + str;
        else show += numInput + str;
        double num = numInput.equals("") ? 0 : Double.parseDouble(numInput);
        numInput = "";
        switch (sign) {
            case ("+"):
                return numSave + num;
            case ("-"):
                return numSave - num;
            case ("*"):
                return numSave * num;
            case ("/"):
                return numSave / num;
        }
        return 0;
    }
}