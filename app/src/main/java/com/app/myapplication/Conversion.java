package com.app.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.myapplication.R;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Objects;


enum Options {
    LENGTH, WEIGHT, ANGLE, TEMPERATURE, TIME, BASE
}

public class Conversion extends AppCompatActivity {
    GridLayout gridLayout;
    private Spinner inputSpinner;
    private Spinner outputSpinner;
    private EditText inputText;
    private EditText outputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        gridLayout = findViewById(R.id.gridLayout);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
        inputSpinner = findViewById(R.id.input_spinner);
        outputSpinner = findViewById(R.id.output_spinner);
        choseView(this.getIntent().getExtras().getInt("itemID"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        initialization();
        choseView(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    private ArrayAdapter<String> adapter;
    private Options options;

    private static final String[] lengthList = {"纳米", "微米", "厘米", "米", "千米", "英里"};
    private static final String[] weightList = {"克", "千克", "盎司", "磅"};
    private static final String[] angleList = {"角度", "弧度"};
    private static final String[] temperatureList = {"摄氏度", "华氏度", "开尔文"};
    private static final String[] timeList = {"秒", "分", "时", "日"};
    private static final String[] baseList = {"二进制", "八进制", "十进制", "十六进制"};


    private void choseView(int itemId) {
        GridLayout extraGridlayout = findViewById(R.id.extraGridlayout);
        extraGridlayout.setVisibility(View.INVISIBLE);
        adapter.clear();
        switch (itemId) {
            case R.id.length:
                options = Options.LENGTH;
                adapter.addAll(lengthList);
                break;
            case R.id.weight:
                options = Options.WEIGHT;
                adapter.addAll(weightList);
                break;
            case R.id.angle:
                options = Options.ANGLE;
                adapter.addAll(angleList);
                break;
            case R.id.temperature:
                options = Options.TEMPERATURE;
                adapter.addAll(temperatureList);
                break;
            case R.id.time:
                options = Options.TIME;
                adapter.addAll(timeList);
                break;
            case R.id.base:
                options = Options.BASE;
                adapter.addAll(baseList);
                extraGridlayout.setVisibility(View.VISIBLE);
                addButtonListener(extraGridlayout);
                break;
        }
        inputSpinner.setAdapter(adapter);
        outputSpinner.setAdapter(adapter);
        addButtonListener(gridLayout);
        inputText = findViewById(R.id.input_textView);
        outputText = findViewById(R.id.output_textView);
        inputText.setText(inputNum);
        outputText.setText(outputNum);
    }

    private void addButtonListener(GridLayout extraGridlayout) {
        for (int i = 0; i < extraGridlayout.getChildCount(); i++) {
            final Button button = (Button) extraGridlayout.getChildAt(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonListener(button.getText().toString());
                }
            });
        }
    }


    private Boolean pointFlag = false;
    private Boolean positiveFlag = false;
    private Boolean equalsFlag = false;
    private String inputNum = "0";
    private String outputNum = "0";

    private void flagReset() {
        equalsFlag = false;
        positiveFlag = false;
        pointFlag = false;
    }

    private void initialization() {
        flagReset();
        inputNum = "0";
        outputNum = "0";
    }

    private static final DecimalFormat decimalFormat = new DecimalFormat("###################.###########");

    private void buttonListener(String str) {
        switch (str) {
            case "CE":
                initialization();
                break;
            case "back":
                if (inputNum.charAt(inputNum.length() - 1) == '-' || inputNum.length() == 1) inputNum = "0";
                else inputNum = inputNum.substring(0, inputNum.length() - 1);
                if (inputNum.charAt(inputNum.length() - 1) == '.') pointFlag = false;
                break;
            case "=":
                equalsFlag = true;
                break;
            case "+/-":
                if (equalsFlag) initialization();
                if (!positiveFlag) inputNum = "-" + inputNum;
                else if (inputNum.charAt(0) == '-') inputNum = inputNum.substring(1);
                positiveFlag = !positiveFlag;
                break;
            case ".":
                if (equalsFlag) initialization();
                if (!pointFlag) inputNum = inputNum.equals("0") ? str : (inputNum + str);
                pointFlag = true;
                break;
            default:
                if (equalsFlag) initialization();
                inputNum = inputNum.equals("0") ? str : (inputNum + str);
                calculation();
                break;
        }
        calculation();
        inputText.setText(inputNum);
        try {
            outputText.setText(decimalFormat.format(outputNum));
        } catch (Exception e) {
            outputText.setText(outputNum);
        }
    }

    private void calculation() {
        outputNum = conversion(inputSpinner.getSelectedItem().toString(),
                outputSpinner.getSelectedItem().toString(), inputNum);
    }

    private String conversion(String a, String b, String c) {
        switch (options) {
            case LENGTH:
                return length(a, b, c);
            case WEIGHT:
                return weight(a, b, c);
            case TIME:
                return time(a, b, c);
            case TEMPERATURE:
                return temperature(a, b, c);
            case BASE:
                try {
                    return base(a, b, c);
                } catch (Exception e) {
                    return "Illegal input";
                }
            case ANGLE:
                return angle(a, b, c);
        }
        return null;
    }

    private static int indexOf(Object object) {
        int i = 0;
        for (Object o : Conversion.lengthList) {
            i++;
            if (object.equals(o)) return i;
        }
        return -1;
    }

    private static final double[] length = {1, 1000, 10000, 100, 1000, 1.609344};

    private static String length(String a, String b, String c) {
        return getString(a, b, c, length);
    }

    private static final double[] weight = {1, 1000, 0.02835, 16};

    private static String weight(String a, String b, String c) {
        return getString(a, b, c, weight);
    }

    private static String angle(String a, String b, String c) {
        double d = Double.parseDouble(c);
        if (a.equals("角度")) d = Math.toRadians(d);
        if (b.equals("角度")) d = Math.toDegrees(d);
        return String.valueOf(d);
    }

    private static String temperature(String a, String b, String c) {
        double d = Double.parseDouble(c);
        switch (a) {
            case "华氏度":
                d = (d - 32) / 1.8;
                break;
            case "开尔文":
                d -= 273.15;
                break;
        }
        switch (b) {
            case "华氏度":
                d = d * 1.8 + 32;
                break;
            case "开尔文":
                d += 273.15;
                break;
        }
        return String.valueOf(d);
    }

    private static final double[] time = {1, 60, 60, 24};

    private static String time(String a, String b, String c) {
        return getString(a, b, c, time);
    }

    @NotNull
    private static String getString(String a, String b, String c, double[] doubles) {
        double d = Double.parseDouble(c);
        for (int i = indexOf(a) - 1; i >= 0; i--) d /= doubles[i];
        for (int i = 0; i < indexOf(b); i++) d *= doubles[i];
        return String.valueOf(d);
    }

    private static String base(String a, String b, String c) {
        int d = 0;
        switch (a) {
            case "二进制":
                d = Integer.parseInt(c, 2);
                break;
            case "八进制":
                d = Integer.parseInt(c, 8);
                break;
            case "十六进制":
                d = Integer.parseInt(c, 16);
                break;
        }
        switch (b) {
            case "二进制":
                c = Integer.toBinaryString(d);
                break;
            case "八进制":
                c = Integer.toOctalString(d);
                break;
            case "十进制":
                c = String.valueOf(d);
                break;
            case "十六进制":
                c = Integer.toHexString(d);
                break;
        }
        return c;
    }
}