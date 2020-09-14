package com.app.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


public class SinceCalculator {
    private static List<String> Conversion(Stack<String> stack) {
        //创建一个栈用于保存操作符
        Stack<String> opStack = new Stack<>();
        //创建一个list用于保存后缀表达式
        List<String> suffixList = new ArrayList<>();
        for (String item : stack) {
            if (item.equals("e")) item = String.valueOf(Math.E);
            else if (item.equals("π")) item = String.valueOf(Math.PI);
            if (item.matches("-?[0-9]+\\.?[0-9]*")) suffixList.add(item); //是数字则直接入队
            else if ("(".equals(item)) opStack.push(item); //是左括号，压栈
            else if (")".equals(item)) {
                //是右括号 ，将栈中元素弹出入队，直到遇到左括号，左括号出栈，但不入队
                while (!opStack.isEmpty()) {
                    if ("(".equals(opStack.peek())) {
                        opStack.pop();
                        break;
                    }
                    suffixList.add(opStack.pop());
                }
            }
            else if ("+-*/^%".contains(item)) {
                //是操作符 判断操作符栈是否为空
                //否则将栈中元素出栈如队，直到遇到大于当前操作符或者遇到左括号时
                //当前操作符压栈
                if (!opStack.isEmpty() && !"(".equals(opStack.peek()) && priority(item) <= priority(opStack.peek()))
                    while (!opStack.isEmpty() && !"(".equals(opStack.peek()) && priority(item) <= priority(opStack.peek()))
                        suffixList.add(opStack.pop());
                //为空或者栈顶元素为左括号或者当前操作符大于栈顶操作符直接压栈
                opStack.push(item);
            }
            else {
                throw new RuntimeException("Illegal input");
            }
        }
        //循环完毕，如果操作符栈中元素不为空，将栈中元素出栈入队
        while (!opStack.isEmpty()) suffixList.add(opStack.pop());
        return suffixList;
    }


    /**
     * 获取操作符的优先级
     */
    public static int priority(String op) {
        switch (op) {
            case "^":
                return 2;
            case "%":
            case "*":
            case "/":
                return 1;
            case "+":
            case "-":
                return 0;
            default:
                break;
        }
        return -1;
    }

    /**
     * 根据后缀表达式list计算结果
     */
    public static BigDecimal calculate(Stack<String> suffixList) throws RuntimeException {
        List<String> list = Conversion(suffixList);
        Stack<BigDecimal> stack = new Stack<>();
        for (String item : list) {
            if (item.matches("-?[0-9]+\\.?[0-9]*")) stack.push(new BigDecimal(item)); //是数字
            else {
                //是操作符，取出栈顶两个元素
                BigDecimal num2 = stack.pop();
                BigDecimal num1 = stack.pop();
                BigDecimal res;
                switch (item) {
                    case "^":
                        res = BigDecimal.valueOf(Math.pow(num1.doubleValue(), num2.doubleValue()));
                        break;
                    case "%":
                        res = num1.divideAndRemainder(num2)[1];
                        break;
                    case "+":
                        res = num1.add(num2);
                        break;
                    case "-":
                        res = num1.subtract(num2);
                        break;
                    case "*":
                        res = num1.multiply(num2);
                        break;
                    case "/":
                        res = num1.divide(num2, 12, RoundingMode.HALF_UP);
                        break;
                    default:
                        throw new RuntimeException("Operator error");
                }
                stack.push(res);
            }
        }
        return stack.pop();
    }
}