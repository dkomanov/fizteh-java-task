package ru.fizteh.fivt.students.verytable.calc;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: Arseny
 * Date: 22.09.12
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */


public class ConsoleCalculation {


    static boolean isOperation(char symbol) {
        return ((symbol == '+') || (symbol == '-')
                || (symbol == '*') || (symbol == '/'));
    }

    static boolean isUnary(char operation) {
        return operation == '-';
    }

    static int priority(char operation, boolean unaryFlag) {
        if (unaryFlag) {
            return 3;
        } else if ((operation == '+') || (operation == '-')) {
            return 1;
        } else if ((operation == '*') || (operation == '/')) {
            return 2;
        }
        return -1;
    }

    static void doOper(Stack<Integer> numStack, char operation,
                       boolean unaryFlag) throws Exception {
        if (unaryFlag) {
            int operand = numStack.pop();
            numStack.push(-operand);
        } else {
            int operand1 = numStack.pop();
            int operand2 = numStack.pop();
            switch (operation) {
                case '+':
                    if (Math.abs(operand1) > 10000 * 10000
                        || Math.abs(operand2) > 10000 * 10000) {
                        throw new Exception("Result of '+' > Integer.MaxValue");
                    }
                    numStack.push(operand1 + operand2);
                    break;
                case '-':
                    if (Math.abs(operand2) > 10000 * 10000
                        || Math.abs(operand1) > 10000 * 10000) {
                        throw new Exception("Result of '-' > Integer.MaxValue");
                    }
                    numStack.push(operand2 - operand1);
                    break;
                case '*':
                    if (Math.abs(operand1) > 10000
                        || Math.abs(operand2) > 10000) {
                        throw new Exception("Result of '*' > Integer.MaxValue");
                    }
                    numStack.push(operand1 * operand2);
                    break;
                case '/':
                    if (operand1 == 0) {
                        throw new Exception("Division by zero");
                    }
                    numStack.push(operand2 / operand1);
                    break;
            }
        }
    }

    static int calc(String s) throws Exception {
        int bracketBalance = 0;
        boolean mayUnary = true;
        Stack<Integer> numStack = new Stack<Integer>();
        Stack<Character> operations = new Stack<Character>();
        Stack<Boolean> unaryFlags = new Stack<Boolean>();
        char prevSymbol = ' ';
        char prevPrevSymbol = ' ';
        boolean wasNonSpaceCharacter = false;

        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isSpaceChar(s.charAt(i))) {
                wasNonSpaceCharacter = true;
                if (s.charAt(i) == '(') {
                    if (Character.isDigit(prevSymbol) || prevSymbol == ')') {
                        throw new Exception("Invalid symbol before (." +
                                            " In position " + i);
                    }
                    ++bracketBalance;
                    operations.push('(');
                    unaryFlags.push(false);
                    mayUnary = true;
                } else if (s.charAt(i) == ')') {
                    if (isOperation(prevSymbol) || prevSymbol == '('
                            || prevPrevSymbol == ' ') {
                        throw new Exception("Invalid symbol before )." +
                                            " In position " + i);
                    }
                    --bracketBalance;
                    if (bracketBalance < 0) {
                        throw new Exception("Wrong bracket balance");
                    }
                    while (operations.peek() != '(') {
                        doOper(numStack, operations.peek(), unaryFlags.peek());
                        operations.pop();
                        unaryFlags.pop();
                    }
                    operations.pop();
                    unaryFlags.pop();
                    mayUnary = false;
                } else if (isOperation(s.charAt(i))) {
                    if (prevSymbol == '(' && s.charAt(i) != '-') {
                        throw new Exception("Invalid symbol before operation "
                                            + s.charAt(i)
                                            + ". In position " + i);
                    }
                    if (isOperation(prevSymbol) && isOperation(prevPrevSymbol)) {
                        throw new Exception("Too many operation in a raw. "
                                            + "In position " + i);
                    }
                    if (isOperation(prevSymbol) && ((s.charAt(i) != '-')
                        || (prevSymbol == '-' && s.charAt(i) == '-'))) {
                        throw new Exception("Unacceptable pair "
                                            + "of sequent operations. "
                                            + "In position " + i);
                    }
                    if (s.charAt(i) != '-' && prevSymbol == ' ') {
                        throw new Exception("No operand before binary operator."
                                            + " In position " + i);
                    }
                    char curOp = s.charAt(i);
                    boolean curUnaryFlag = mayUnary && isUnary(curOp);
                    while (!operations.empty()
                            && (priority(operations.peek(), unaryFlags.peek())
                            >= priority(curOp, curUnaryFlag))) {
                        doOper(numStack, operations.peek(), unaryFlags.peek());
                        operations.pop();
                        unaryFlags.pop();
                    }
                    operations.push(curOp);
                    unaryFlags.push(curUnaryFlag);
                    mayUnary = true;
                } else if (Character.isDigit(s.charAt(i))) {
                    if (prevSymbol == ')') {
                        throw new Exception("Invalid symbol before a number "
                                            + s.charAt(i) + ". In position "
                                            + i);
                    }

                    int j = i;
                    while (i < s.length() && Character.isDigit(s.charAt(i))) {
                        ++i;
                    }
                    --i;
                    String operand = s.substring(j, i + 1);
                    if (operand.length() > 9) {
                        throw new Exception("Max int overflow.");
                    }
                    numStack.push(Integer.parseInt(operand));
                    mayUnary = false;
                } else {
                    throw new Exception("Unknown symbol "
                                        + s.charAt(i) +
                                        ". In position " + i);
                }
                prevPrevSymbol = prevSymbol;
                prevSymbol = s.charAt(i);
            }
        }

        if (bracketBalance != 0) {
            throw new Exception("Wrong bracket balance.");
        }

        while (!operations.empty()) {
            doOper(numStack, operations.peek(), unaryFlags.peek());
            operations.pop();
            unaryFlags.pop();
        }

        if (wasNonSpaceCharacter) {
            return numStack.peek();
        }
        throw new Exception("Empty expression. Usage: arguments are strings" +
                            " forming " + "mathematical expression, " +
                            "consisting of " + "'+', '-', '*', '/', ')', '('," +
                            " and integers " + "< 10^9. Brackets should be" +
                            " correct and " + "two operations in a raw " +
                            "are possible if only" + "of them is '-'. " +
                            "Expression analysis must be " +
                            "possible and unambiguous!");
    }


    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            System.out.println(args[i]);
            sb.append(args[i]);
        }
        System.out.println(sb);
        String s = sb.toString();
        try {
            System.out.println(calc(s));
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

}

