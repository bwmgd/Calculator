package com.app.myapplication;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.app.domain.SinceCalculator;
import com.example.myapplication.R;

import java.math.BigDecimal;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView showTextView;
    private TextView resultTextView;
    private Stack<String> saveStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        showTextView = findViewById(R.id.show_textView);
        resultTextView = findViewById(R.id.result_textView);
        if (isOrientation()) {
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                final Button button = (Button) gridLayout.getChildAt(i);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = button.getText().toString();
                        Log.v("chick", sign + "," + numInput + "," + numSave + "," + show);
                        calculation(s);
                    }
                });
            }
        }
        else {
            saveStack = new Stack<>();
        }
    }

    private String sign = "+";
    private BigDecimal numSave = new BigDecimal(0);
    private String numInput = "0";
    private String show = "";
    private boolean signFlag = false;
    private boolean equalsFlag = false;
    private boolean pointFlag = false;
    private boolean zeroFlag = false;

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
                resultTextView.setText(numSave.stripTrailingZeros().toString());
                signFlag = true;
                break;
            case "=":
                signFlag = false;
                equalsFlag = true;
                pointFlag = false;
                numSave = result(str);
                show = numSave.stripTrailingZeros().toString();
                resultTextView.setText(show);
                break;
            case "C":
                initialization();
                resultTextView.setText(numInput);
                break;
            case "CE":
                flagReset();
                numInput = "0";
                resultTextView.setText(numInput);
                break;
            case "back":
                if (!numInput.equals("")) {
                    flagReset();
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
        if (zeroFlag) {
            showTextView.setText("Division by zero");
            zeroFlag = false;
        }
        else showTextView.setText(show);
        Log.i("show", show);
        Log.i("result", numInput);
    }

    private void flagReset() {
        equalsFlag = false;
        signFlag = false;
        pointFlag = false;
        zeroFlag = false;
    }

    private void initialization() {
        flagReset();
        sign = "+";
        numSave = new BigDecimal(0);
        numInput = "0";
        show = "";
    }

    private void landInitialization() {
        initialization();
        showTextView.setText("");
        saveStack.clear();
        resultTextView.setText("0");
    }

    private BigDecimal result(String str) {
        if (signFlag) {
            show = show.substring(0, show.length() - 1) + str;
            return numSave;
        }
        if (show.equals("")) show = numInput + str;
        else show += numInput + str;
        BigDecimal num = new BigDecimal(numInput.equals(".") || numInput.equals("") ? "0" : numInput);
        numInput = "";
        switch (sign) {
            case ("+"):
                return numSave.add(num);
            case ("-"):
                return numSave.subtract(num);
            case ("*"):
                return numSave.multiply(num);
            case ("/"):
                if (num.equals(new BigDecimal(0))) zeroFlag = true;
                else return numSave.divide(num, 12, BigDecimal.ROUND_HALF_UP);
        }
        return new BigDecimal(0);
    }

    private boolean isOrientation() {
        return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }


    public void sin(View view) {

    }

    public void cos(View view) {

    }

    public void tan(View view) {

    }

    public void ln(View view) {

    }

    public void lg(View view) {

    }

    public void factorial(View view) {

    }

    public void sqrt(View view) {

    }

    public void piORe(View view) {

    }

    public void leftBreak(View view) {

    }

    public void rightBreak(View view) {

    }

    public void back(View view) {

    }

    public void clear(View view) {
        landInitialization();
    }

    public void clearErr(View view) {
        flagReset();
        numInput = "0";
        resultTextView.setText("0");
    }

    public void equals(View view) {
        equalsFlag = true;
        saveStack.add(numInput);
        numSave = SinceCalculator.calculate(saveStack);
        show();
        resultTextView.setText(numSave.stripTrailingZeros().toString());
    }

    public void mod(View view) {
        operation("%");
    }

    public void power(View view) {
        operation("^");
    }

    public void operation(View view) {
        operation(getStr(view));
    }

    private void operation(String str) {
        equalsFlag = false;
        pointFlag = false;
        if (signFlag) saveStack.pop();
        else saveStack.add(numInput);
        sign = str;
        saveStack.add(sign);
        signFlag = true;
        numInput = "0";
        show();
    }

    public void digital(View view) {
        pointFlag = false;
        signFlag = false;
        String str = getStr(view);
        if (equalsFlag) landInitialization();
        numInput = numInput.equals("0") ? str : (numInput + str);
        resultTextView.setText(numInput);
    }

    public void point(View view) {
        if (!pointFlag) {
            pointFlag = true;
            if (equalsFlag) landInitialization();
            numInput += ".";
            resultTextView.setText(numInput);
        }
    }

    private String getStr(View view) {
        Button button = (Button) view;
        return button.getText().toString();
    }

    private void show() {
        StringBuilder stringBuilder = new StringBuilder(show);
        for (String s : saveStack) stringBuilder.append(s);
        showTextView.setText(stringBuilder);
    }
}