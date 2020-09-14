package com.app.myapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import com.app.domain.SinceCalculator;
import com.example.myapplication.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView showTextView;
    private TextView resultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        showTextView = findViewById(R.id.input_textView);
        resultTextView = findViewById(R.id.output_textView);
        if (isOrientation()) {
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                final Button button = (Button) gridLayout.getChildAt(i);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = button.getText().toString();
                        Log.v("chick", sign + "," + numInput + "," + numSave + "," + show);
                        try {
                            calculation(s);
                        } catch (ArithmeticException e) {
                            initialization();
                            showTextView.setText(e.getMessage());
                            Log.getStackTraceString(e);
                        }
                    }
                });
            }
        }
        else {
            saveStack = new Stack<>();
            showStack = new Stack<>();
            leftBreakCount = 0;
            rightBreakCount = 0;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        Intent intent = new Intent(MainActivity.this, Conversion.class);
        intent.putExtra("itemID", item.getItemId());
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private String sign = "+";
    private BigDecimal numSave = BigDecimal.ZERO;
    private String numInput = "0";
    private String show = "";
    private boolean signFlag = false;
    private boolean equalsFlag = false;
    private boolean pointFlag = false;
    private String display = "";
    private static final DecimalFormat decimalFormat = new DecimalFormat("###################.###########");

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

    private void flagReset() {
        equalsFlag = false;
        signFlag = false;
        pointFlag = false;
    }

    private void initialization() {
        flagReset();
        sign = "+";
        numSave = BigDecimal.ZERO;
        numInput = "0";
        show = "";
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
                return numSave.divide(num, 12, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }


    private Stack<String> saveStack;
    private Stack<String> showStack;
    private int leftBreakCount;
    private int rightBreakCount;

    private boolean isOrientation() {
        return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    private String getStr(View view) {
        Button button = (Button) view;
        return button.getText().toString();
    }

    private void showText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : showStack) stringBuilder.append(s);
        showTextView.setText(stringBuilder);
    }

    private void saveNumInput() {
        if (numInput.matches("\\d+\\.$")) numInput = numInput.substring(0, numInput.length() - 1);
        saves(numInput);
    }

    private void saves(String str) {
        if (display.equals("")) showStack.add(str);
        else {
            showStack.add(display);
            display = "";
        }
        saveStack.add(str);
    }

    private void landInitialization() {
        initialization();
        display = "";
        showTextView.setText("");
        saveStack.clear();
        showStack.clear();
        resultTextView.setText("0");
        leftBreakCount = rightBreakCount = 0;
    }

    public void piORe(View view) {
        String str = getStr(view);
        if (!signFlag) {
            numInput = resultTextView.getText().toString();
            if (!numInput.equals("") && !numInput.equals("0")) saveNumInput();
            if (!saveStack.isEmpty()) saves("*");
        }
        if (equalsFlag) landInitialization();
        numInput = str;
        showText();
        resultTextView.setText(str);
        signFlag = false;
    }

    public void leftBreak(View view) {
        if (!signFlag) {
            numInput = resultTextView.getText().toString();
            if (!numInput.equals("") && !numInput.equals("0")) saveNumInput();
            if (!saveStack.isEmpty()) saves("*");
        }
        if (equalsFlag) landInitialization();
        saves("(");
        showText();
        resultTextView.setText("(");
        flagReset();
        leftBreakCount++;
        numInput = "0";
    }

    public void rightBreak(View view) {
        if (leftBreakCount > rightBreakCount) {
            if (signFlag) {
                saveStack.pop();
                showStack.pop();
            }
            saveNumInput();
            numInput = ")";
            showText();
            resultTextView.setText(")");
            flagReset();
            rightBreakCount++;
        }

    }

    public void clear(View view) {
        landInitialization();
    }

    public void clearErr(View view) {
        numInput = "0";
        resultTextView.setText("0");
    }

    public void equals(View view) {
        try {
            if (equalsFlag) landInitialization();
            else {
                if (signFlag) throw new RuntimeException("Illegal input");
                saveNumInput();
                numSave = SinceCalculator.calculate(saveStack);
                showText();
                showTextView.setText(showTextView.getText() + "=");
                resultTextView.setText(numSave.stripTrailingZeros().toPlainString());
            }
        } catch (Exception e) {
            landInitialization();
            showTextView.setText(e.getMessage());
            Log.getStackTraceString(e);
        } finally {
            flagReset();
            equalsFlag = true;
        }
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
        if (signFlag) {
            saveStack.pop();
            showStack.pop();
        }
        else {
            if (equalsFlag) {
                String numInput = resultTextView.getText().toString();
                landInitialization();
                this.numInput = numInput;
            }
            saveNumInput();
        }
        sign = str;
        saves(sign);
        numInput = "0";
        showText();
        signFlag = true;
        equalsFlag = false;
        pointFlag = false;
    }

    public void digital(View view) {
        String str = getStr(view);
        if (equalsFlag) landInitialization();
        numInput = numInput.equals("0") ? str : (numInput + str);
        resultTextView.setText(numInput);
        signFlag = false;
    }

    public void point(View view) {
        if (!pointFlag) {
            if (equalsFlag) landInitialization();
            numInput += ".";
            resultTextView.setText(numInput);
            pointFlag = true;
            signFlag = false;
        }
    }

    private double getNumValue() {
        String num = resultTextView.getText().toString();
        if (num.matches("[0-9]+\\.?[0-9]*")) return Double.parseDouble(num);
        else if (num.equals("e")) return Math.E;
        else if (num.equals("π")) return Math.PI;
        else throw new RuntimeException("Operator error");
    }

    public void sin(View view) {
        try {
            display = "sin(" + decimalFormat.format(getNumValue()) + ")";
            numInput = decimalFormat.format(Math.sin(getNumValue()));
        } catch (RuntimeException e) {
            landInitialization();
            showTextView.setText(e.getMessage());
        } finally {
            signFlag = false;
            resultTextView.setText(numInput);
        }
    }

    public void cos(View view) {
        try {
            display = "cos(" + decimalFormat.format(getNumValue()) + ")";
            numInput = decimalFormat.format(Math.cos(getNumValue()));
        } catch (RuntimeException e) {
            landInitialization();
            showTextView.setText(e.getMessage());
        } finally {
            signFlag = false;
            resultTextView.setText(numInput);
        }
    }

    public void tan(View view) {
        try {
            display = "tan(" + decimalFormat.format(getNumValue()) + ")";
            numInput = decimalFormat.format(Math.tan(getNumValue()));
        } catch (RuntimeException e) {
            landInitialization();
            showTextView.setText(e.getMessage());
        } finally {
            signFlag = false;
            resultTextView.setText(numInput);
        }
    }

    public void ln(View view) {
        try {
            display = "ln(" + decimalFormat.format(getNumValue()) + ")";
            numInput = decimalFormat.format(Math.log(getNumValue()) / Math.log(Math.E));
        } catch (RuntimeException e) {
            landInitialization();
            showTextView.setText(e.getMessage());
        } finally {
            signFlag = false;
            resultTextView.setText(numInput);
        }
    }

    public void lg(View view) {
        try {
            display = "lg(" + decimalFormat.format(getNumValue()) + ")";
            numInput = decimalFormat.format(Math.log(getNumValue()));
        } catch (RuntimeException e) {
            landInitialization();
            showTextView.setText(e.getMessage());
        } finally {
            signFlag = false;
            resultTextView.setText(numInput);
        }
    }

    public void factorial(View view) {
        try {
            display = "(" + decimalFormat.format(getNumValue()) + ")!";
            numInput = String.valueOf(factorial((long) getNumValue()));
        } catch (RuntimeException e) {
            landInitialization();
            showTextView.setText(e.getMessage());
        } finally {
            signFlag = false;
            resultTextView.setText(numInput);
        }
    }

    public static long factorial(long number) {
        return number <= 1 ? 1 : number * factorial(number - 1);
    }

    public void sqrt(View view) {
        try {
            display = "sqrt(" + decimalFormat.format(getNumValue()) + ")";
            numInput = decimalFormat.format(Math.sqrt(getNumValue()));
        } catch (RuntimeException e) {
            landInitialization();
            showTextView.setText(e.getMessage());
        } finally {
            signFlag = false;
            resultTextView.setText(numInput);
        }
    }

    public void back(View view) {
        if (equalsFlag) {
            String numInput = resultTextView.getText().toString();
            landInitialization();
            this.numInput = numInput;
            resultTextView.setText(numInput);
        }
        show = resultTextView.getText().toString();
        if (show.equals("")) {
            if (showStack.isEmpty()) return;
            if (!(show = showStack.pop()).matches("[0-9]+\\.?[0-9]*")) show = "#";
            saveStack.pop();
        }
        show = show.substring(0, show.length() - 1);
        showText();
        resultTextView.setText(numInput = show);
        if (!show.equals("") && show.charAt(show.length() - 1) == '.') pointFlag = true;
        else if (show.equals("") && !showStack.isEmpty()) {
            if ("+-*/^%".contains(showStack.peek())) signFlag = true;
            else if (showStack.peek().matches("[0-9]+\\.?[0-9]*")) {
                resultTextView.setText(showStack.pop());
                saveStack.pop();
            }
        }
    }
}