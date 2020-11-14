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
import androidx.appcompat.app.AppCompatActivity;
import com.app.domain.SinceCalculator;
import com.example.myapplication.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView showTextView;
    private TextView resultTextView;


    private static final DecimalFormat decimalFormat = new DecimalFormat("###################.###########"); //小数格式化
    private String sign = "+"; //符号控制
    private BigDecimal numSave = BigDecimal.ZERO;  //内部储存数字
    private String numInput = "0";  //输入框文本
    private String show = "";  //显示框文本
    private boolean signFlag = false;  //各种标志
    private String display = "";  //科学计算器文本
    private boolean equalsFlag = false;
    private boolean pointFlag = false;
    //科学计算器
    private Stack<String> saveStack;  //保存的元素
    private Stack<String> showStack;  //显示的元素
    private int leftBreakCount;  //左括号数量
    private int rightBreakCount;  //右括号数量

    public static long factorial(long number) {  //阶乘递归
        return number <= 1 ? 1 : number * factorial(number - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //关联任务栏
        setSupportActionBar(findViewById(R.id.toolbar));
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        showTextView = findViewById(R.id.input_textView);
        resultTextView = findViewById(R.id.output_textView);
        if (isOrientation()) { //竖屏模式
            for (int i = 0; i < gridLayout.getChildCount(); i++) { //循环绑定事件
                final Button button = (Button) gridLayout.getChildAt(i);
                button.setOnClickListener(v -> {
                    String s = button.getText().toString();
                    Log.v("chick", sign + "," + numInput + "," + numSave + "," + show);
                    try {
                        calculation(s);
                    } catch (ArithmeticException e) {
                        initialization();
                        showTextView.setText(e.getMessage());
                        Log.getStackTraceString(e);
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
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        Intent intent = new Intent(MainActivity.this, Conversion.class);
        intent.putExtra("itemID", item.getItemId()); //传递点击的菜单内容进行单位换算
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private void calculation(String str) { //标准计算器
        switch (str) {
            case "+":
            case "-":
            case "*":
            case "/":
                equalsFlag = false;
                pointFlag = false;
                numSave = result(str);  //四则运算
                sign = str;
                resultTextView.setText(decimalFormat.format(numSave));  //显示文本
                signFlag = true;
                break;
            case "=":
                signFlag = false;
                equalsFlag = true;
                pointFlag = false;
                numSave = result(str);  //结果计算
                show = decimalFormat.format(numSave);
                resultTextView.setText(show);
                break;
            case "C":
                initialization();  //初始化
                resultTextView.setText(numInput);
                break;
            case "CE":
                flagReset();  //标志初始化
                numInput = "0";
                resultTextView.setText(numInput);
                break;
            case "back":
                if (!numInput.equals("")) {  //退格键
                    numInput = numInput.length() == 1 ? "0" : numInput.substring(0, numInput.length() - 1);
                    resultTextView.setText(numInput);
                }
                break;
            case ".":
                if (!pointFlag) {  //小数点,禁止输入多次
                    pointFlag = true;
                    signFlag = false;
                    if (equalsFlag) initialization();
                    numInput += str;
                    resultTextView.setText(numInput);
                }
                break;
            default:  //数字
                signFlag = false;
                if (equalsFlag) initialization();
                numInput = numInput.equals("0") ? str : (numInput + str);
                resultTextView.setText(numInput);
                break;
        }
        showTextView.setText(show);  //显示文本
        Log.i("show", show);
        Log.i("result", numInput);
    }

    private boolean isOrientation() {
        return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    private BigDecimal result(String str) {
        if (signFlag) {
            show = show.substring(0, show.length() - 1) + str;  //重复输入符号进行更改
            return numSave;
        }
        if (show.equals("")) show = numInput + str;
        else show += numInput + str;
        BigDecimal num = new BigDecimal(numInput.equals(".") || numInput.equals("") ? "0" : numInput);  //文本转高精度数字
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

    private String getStr(View view) {  //获取按钮文字
        Button button = (Button) view;
        return button.getText().toString();
    }

    private void showText() {  //显示文本
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : showStack) stringBuilder.append(s);
        showTextView.setText(stringBuilder);
    }

    private void saveNumInput() {  //保存数字
        if (numInput.matches("\\d+\\.$")) numInput = numInput.substring(0, numInput.length() - 1);  //去除运算符,保存纯数字
        saves(numInput);
    }

    private void saves(String str) {  //保存符号
        if (display.equals("")) showStack.add(str);
        else {
            showStack.add(display);
            display = "";
        }
        saveStack.add(str);
    }

    private void landInitialization() {  //横屏科学计算器的初始化
        initialization();
        display = "";
        showTextView.setText("");
        saveStack.clear();
        showStack.clear();
        resultTextView.setText("0");
        leftBreakCount = rightBreakCount = 0;
    }

    public void piORe(View view) {  //Π或e的输入
        String str = getStr(view);
        if (!signFlag) {
            numInput = resultTextView.getText().toString();
            if (!numInput.equals("") && !numInput.equals("0")) saveNumInput();  //为空或为0替换文本
            if (!saveStack.isEmpty()) saves("*");  //计算如3Π的连续运算
        }
        if (equalsFlag) landInitialization();
        numInput = str;
        showText();
        resultTextView.setText(str);
        signFlag = false;
    }

    public void leftBreak(View view) {  //左括号
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

    public void clear(View view) {
        landInitialization();
    }

    public void clearErr(View view) {
        numInput = "0";
        resultTextView.setText("0");
    }

    public void rightBreak(View view) {  //右括号
        if (leftBreakCount > rightBreakCount) {
            if (signFlag) {  //前一输入为符号时替换符号
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

    public void equals(View view) {
        try {
            if (equalsFlag) landInitialization();
            else {
                if (signFlag) throw new RuntimeException("Illegal input");
                saveNumInput();
                numSave = SinceCalculator.calculate(saveStack);  //运算最终结果
                showText();
                showTextView.setText(showTextView.getText() + "=");
                resultTextView.setText(numSave.stripTrailingZeros().toPlainString());
            }
        } catch (Exception e) {
            landInitialization();  //非法计算后初始化计算器
            showTextView.setText(e.getMessage());
            Log.getStackTraceString(e);
        } finally {
            flagReset();
            equalsFlag = true;
        }
    }

    public void power(View view) {
        operation("^");
    }

    public void operation(View view) {
        operation(getStr(view));
    }

    public void mod(View view) {
        operation("%");
    }//运算符转化

    private void operation(String str) {  //高级运算(四则,幂,求余)
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

    public void digital(View view) {  //数字输入
        String str = getStr(view);
        if (equalsFlag) landInitialization();
        numInput = numInput.equals("0") ? str : (numInput + str);
        resultTextView.setText(numInput);
        signFlag = false;
    }

    public void point(View view) {  //小数点输入
        if (!pointFlag) {
            if (equalsFlag) landInitialization();
            numInput += ".";
            resultTextView.setText(numInput);
            pointFlag = true;
            signFlag = false;
        }
    }

    private double getNumValue() {  //文本转数字,解析e和Π
        String num = resultTextView.getText().toString();
        if (num.matches("[0-9]+\\.?[0-9]*")) return Double.parseDouble(num);
        else if (num.equals("e")) return Math.E;
        else if (num.equals("π")) return Math.PI;
        else throw new RuntimeException("Operator error");
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

    public void sin(View view) {  //其他函数运算
        try {
            display = "sin(" + decimalFormat.format(getNumValue()) + ")";  //显示状态
            numInput = decimalFormat.format(Math.sin(getNumValue()));  //保存运算后的数字
        } catch (RuntimeException e) {
            landInitialization();
            showTextView.setText(e.getMessage());
        } finally {
            signFlag = false;
            resultTextView.setText(numInput);
        }
    }

    public void factorial(View view) {  //阶乘运算
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

    public void back(View view) {  //退格键
        if (equalsFlag) {
            String numInput = resultTextView.getText().toString();
            landInitialization();
            this.numInput = numInput;
            resultTextView.setText(numInput);
        }
        show = resultTextView.getText().toString();
        if (show.equals("")) { //若显示框文本为空
            if (showStack.isEmpty()) return;  //列表为空,不进行操作
            if (!(show = showStack.pop()).matches("[0-9]+\\.?[0-9]*")) show = "#";  //令显示文本为栈顶弹出,若为不数字,显示文本保留一位而后置空
            saveStack.pop();
        }
        show = show.substring(0, show.length() - 1); //退格
        showText();
        resultTextView.setText(numInput = show);  //文本置入选择框
        if (!show.equals("") && show.charAt(show.length() - 1) == '.') pointFlag = true;  //遇到小数点还原标志
        else if (show.equals("") && !showStack.isEmpty()) {  //若显示框无文本
            if ("+-*/^%".contains(showStack.peek())) signFlag = true;  //符号判断
            else if (showStack.peek().matches("[0-9]+\\.?[0-9]*")) {  //数字置顶
                resultTextView.setText(showStack.pop());
                saveStack.pop();
            }
        }
    }
}