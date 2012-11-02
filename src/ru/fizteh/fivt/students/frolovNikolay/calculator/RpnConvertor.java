package ru.fizteh.fivt.students.frolovNikolay.calculator;

import java.util.LinkedList;

/*
 * Данный класс отвечает
 * за перевод инфиксной записи
 * выражения в постфиксную.
 * Попутно класс выполняет
 * проверку на корректность
 * введенного выражения.
 */
public class RpnConvertor {
    public static String convert(String expression) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean haveOp = true;
        boolean haveNumb = false;
        LinkedList<Character> stack = new LinkedList<Character>();
        for (int i = 0; i < expression.length(); ++i) {
            if (Character.isDigit(expression.charAt(i))) {
                if (!haveNumb && !haveOp) {
                    throw new Exception("Error! Incorrect arithmetic exception.");
                }
                haveNumb = true;
                haveOp = false;
                result.append(expression.charAt(i));
                continue;
            }
            if (haveNumb) {
                haveNumb = false;
                result.append(' ');
            }
            if (Character.isWhitespace(expression.charAt(i))) {
                continue;
            }
            if (!haveOp && (expression.charAt(i) == '+' || expression.charAt(i) == '-')) {
                haveOp = true;
                while (!stack.isEmpty() && stack.getFirst() != '(') {
                    result.append(stack.getFirst());
                    result.append(' ');
                    stack.pop();
                }
                stack.push(expression.charAt(i));
                continue;
            }
            if (!haveOp && (expression.charAt(i) == '*' || expression.charAt(i) == '/')) {
                haveOp = true;
                while (!stack.isEmpty() && (stack.getFirst() == '*'
                                           || stack.getFirst() == '/')) {
                    result.append(stack.getFirst());
                    result.append(' ');
                    stack.pop();
                }
                stack.push(expression.charAt(i));
                continue;
            }
            if (haveOp && expression.charAt(i) == '-') {
                if (i + 1 < expression.length()) {
                    if ('0' <= expression.charAt(i + 1) 
                       && expression.charAt(i + 1) <= '9') {
                        haveOp = false;
                        haveNumb = true;
                        result.append('-');
                        continue;
                    } else {
                        throw new Exception("Error! Incorrect arithmetic expression.");
                    }
                } else {
                    throw new Exception("Error! Incorrect arithmetic expression.");
                }       
            }
            if (expression.charAt(i) == '(') {
                stack.push('(');
                continue;
            }
            if (expression.charAt(i) == ')') {
                if (stack.isEmpty()) {
                    throw new Exception("Error! Brackets placed incorrectly.");
                }
                while (stack.getFirst() != '(') {
                    result.append(stack.getFirst());
                    result.append(' ');
                    stack.pop();
                    if (stack.isEmpty()) {
                        throw new Exception("Error! Brackets placed incorrectly.");
                    }
                }
                stack.pop();
                continue;
            }
            throw new Exception("Error! Incorrect arithmetic expression.");
        }
        if (haveOp) {
            throw new Exception("Error! Incorrect arithmetic expression");
        }
        if (haveNumb) {
            result.append(' ');
        }
        while (!stack.isEmpty()) {
            if (stack.getFirst() == '(') {
                throw new Exception("Error! Brackets placed incorrectly.");
            }
            result.append(stack.getFirst());
            result.append(' ');
            stack.pop();
        }
        return result.toString();
    }
}