package ru.fizteh.fivt.students.frolovNikolay.calculator;

import java.util.LinkedList;

/*
 * Данный класс отвечает за
 * получение значения выражения
 * из обратной польской записи.
 */
public class Calculator {
    public static int calculate(String rpnExpression) throws Exception {
        LinkedList<Integer> numStack = new LinkedList<Integer>();
        int leftSide;
        long lhs;
        long rhs;
        long result = 0;
        for (int i = 0; i < rpnExpression.length(); ++i) {
            leftSide = i;
            do {
                ++i;
            } while (!Character.isWhitespace(rpnExpression.charAt(i)));
            String current = rpnExpression.substring(leftSide, i);
            switch (current) {
            case "+":
                rhs = (long) numStack.getFirst();
                numStack.pop();
                lhs = (long) numStack.getFirst();
                numStack.pop();
                result = lhs + rhs;
                if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
                    throw new Exception ("Error! Integer overflow.");
                } else {
                    numStack.push((int) result);
                }
                break;
            case "-":
                rhs = (long) numStack.getFirst();
                numStack.pop();
                lhs = (long) numStack.getFirst();
                numStack.pop();
                result = lhs - rhs;
                if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
                    throw new Exception ("Error! Integer overflow.");
                } else {
                    numStack.push((int) result);
                }
                break;
            case "*":
                rhs = (long) numStack.getFirst();
                numStack.pop();
                lhs = (long) numStack.getFirst();
                numStack.pop();
                result = lhs * rhs;
                if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
                    throw new Exception ("Error! Integer overflow.");
                } else {
                    numStack.push((int) result);
                }
                break;
            case "/":
                rhs = (long) numStack.getFirst();
                numStack.pop();
                lhs = (long) numStack.getFirst();
                numStack.pop();
                result = lhs / rhs;
                if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
                    throw new Exception ("Error! Integer overflow.");
                } else {
                    numStack.push((int) result);
                }
                break;
            default:
                try {
                    result = (long) Integer.valueOf(current);
                } catch (Exception overflow) {
                    throw new Exception ("Error! One of numbers is too big");
                }
                numStack.push((int) result);
            }
            
        }
        return numStack.getFirst();
    }
}