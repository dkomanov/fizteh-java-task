package ru.fizteh.fivt.students.frolovNikolay.calculator;

/*
 * Данный класс отвечает за
 * получение значения выражения
 * из обратной польской записи.
 */
public class Calculator {
    public static int handle(String rpnExpression) throws Exception {
        IntStack numStack = new IntStack();
        int leftSide;
        long lhs;
        long rhs;
        long result = 0;
        for (int i = 0; i < rpnExpression.length(); ++i) {
            leftSide = i;
            while (rpnExpression.charAt(++i) != ' ');
            String current = rpnExpression.substring(leftSide, i);
            switch (current) {
            case "+":
                rhs = (long) numStack.top();
                numStack.pop();
                lhs = (long) numStack.top();
                numStack.pop();
                result = lhs + rhs;
                if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
                    throw (new Exception ("Error! Integer overflow."));
                } else {
                    numStack.push((int) result);
                }
                break;
            case "-":
                rhs = (long) numStack.top();
                numStack.pop();
                lhs = (long) numStack.top();
                numStack.pop();
                result = lhs - rhs;
                if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
                    throw (new Exception ("Error! Integer overflow."));
                } else {
                    numStack.push((int) result);
                }
                break;
            case "*":
                rhs = (long) numStack.top();
                numStack.pop();
                lhs = (long) numStack.top();
                numStack.pop();
                result = lhs * rhs;
                if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
                    throw (new Exception ("Error! Integer overflow."));
                } else {
                    numStack.push((int) result);
                }
                break;
            case "/":
                rhs = (long) numStack.top();
                numStack.pop();
                lhs = (long) numStack.top();
                numStack.pop();
                result = lhs / rhs;
                if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
                    throw (new Exception ("Error! Integer overflow."));
                } else {
                    numStack.push((int) result);
                }
                break;
            default:
                try {
                    result = (long) Integer.valueOf(current);
                } catch (Exception overflow) {
                    throw (new Exception ("Error! One of numbers is too big"));
                }
                if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
                    throw (new Exception ("Error! Integer overflow"));
                } else {
                    numStack.push((int) result);
                }
            }
            
        }
        return numStack.top();
    }
}