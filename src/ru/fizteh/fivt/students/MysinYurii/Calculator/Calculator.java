package ru.fizteh.fivt.students.mysinYurii.calculator;

/*
 * Author: Mysin Yurii
 * 
 * Group: 196
 *  
 */

import java.io.IOException;

public class Calculator {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out
                    .println("Эта программа вычисляет значение выражения, заданного в параметрах запуска");
            System.exit(1);
        }
        StringBuilder tempExpression = new StringBuilder();
        for (int j = 0; j < args.length; ++j) {
            tempExpression.append(args[j]);
            tempExpression.append(" ");
        }
        Expression answer = new Expression(tempExpression.toString());
        char c = answer.correctSymbols();
        if (c != ' ') {
            System.out.println("Incorrect input symbol: " + c);
            System.exit(1);
        }
        int res = 0;
        try {
            res = answer.result();
        } catch (CalculatorException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (ArithmeticException e) {
            System.out.println("Divide by zero");
            System.exit(1);
        }
        System.out.println(res);
    }
}
