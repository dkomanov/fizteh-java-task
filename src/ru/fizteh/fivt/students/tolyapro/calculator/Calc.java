package ru.fizteh.fivt.students.tolyapro.calculator;

import java.util.*;
import java.math.*;

public class Calc {

    public static boolean isNumber(String s) {
        return ((s.length() > 0) && (Character.isDigit(s.charAt(0))));
    }

    public static boolean checkBrackets(String s) {
        int depth = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '(') {
                depth++;
            } else if (s.charAt(i) == ')') {
                depth--;
            }
            if (depth < 0) {
                return false;
            }
        }
        return depth == 0;
    }

    public static boolean checkCorrectness(String s) {
        boolean prevNumber = false;
        for (int i = 0; i < s.length(); ++i) {
            if ((prevNumber) && ((s.charAt(i) == '('))) {
                return false;
            }
            if (Character.isDigit(s.charAt(i))) {
                prevNumber = true;
            } else {
                prevNumber = false;
            }
        }
        return true;

    }

    public static boolean checkSpaces(String s) {
        boolean isPrevNum = false;
        boolean isPrevSpace = false;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if ((isPrevNum) && (isPrevSpace) && (Character.isDigit(c))) {
                return false;
            } else {
                if ((isPrevNum) && (Character.isWhitespace(c))) {
                    isPrevSpace = true;
                } else {
                    if ((!isPrevNum) && (Character.isDigit(c))) {
                        isPrevNum = true;
                        isPrevSpace = false;
                    } else {
                        if (!Character.isWhitespace(c)) {
                            isPrevSpace = false;
                        }
                    }
                }
            }
            if ((!Character.isDigit(c)) && (!Character.isWhitespace(c))) {
                isPrevNum = false;
            }

        }
        return true;
    }

    public static String operationToString(int a, int b, char operand)
            throws RuntimeException {
        if (operand == '+') {
            return Integer.toString(a + b);
        } else if (operand == '-') {
            return Integer.toString(b - a);
        } else if (operand == '*') {
            return Integer.toString(a * b);
        } else if (operand == '/') {
            return Integer.toString(b / a);
        }
        throw new RuntimeException("Uknown operand");
    }

    public static boolean checkOverflow(int a, int b, char operand)
            throws RuntimeException {
        if (operand == '/') {
            return false;
        }
        String resStr = operationToString(a, b, operand);
        String aStr = Integer.toString(a);
        String bStr = Integer.toString(b);
        BigInteger res = new BigInteger(resStr);
        BigInteger aBig = new BigInteger(aStr);
        BigInteger bBig = new BigInteger(bStr);
        if (operand == '+') {
            return !res.equals(aBig.add(bBig));
        } else if (operand == '-') {
            return !res.equals(bBig.subtract(aBig));
        } else if (operand == '*') {
            return !res.equals(aBig.multiply(bBig));
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        String expr;
        StringBuilder builder = new StringBuilder();
        boolean isPrevNum = false;
        for (String s : args) {
            if (isPrevNum) {
                if (Character.isDigit(s.charAt(0))) {
                    System.err
                            .println("Incorrect Input: New argument starts with a number with no previous operand");
                    System.exit(1);
                }
            }
            builder.append(s);
            if (Character.isDigit(s.charAt(s.length() - 1))) {
                isPrevNum = true;
            } else {
                isPrevNum = false;
            }
        }
        expr = builder.toString();
        expr = expr.replaceAll("\'|\"", "");
        System.out.println(expr);
        if (!checkSpaces(expr)) {
            System.err.println("Incorrect input: Space between digits");
            System.exit(1);
        }
        expr = expr.replaceAll("\\s+", "");
        if (!(checkBrackets(expr))) {
            System.err.println("Incorrect input: Brackets error");
            System.exit(1);
        }
        if (expr.isEmpty()) {
            System.err
                    .println("Usage: calc (expression with numbers, (), +, - , *, /)");
            System.exit(1);
        }
        if (!(checkCorrectness(expr))) {
            System.err
                    .println("Incorrect input: No operand between number and bracket");
            System.exit(1);
        }
        ReversePolishNotation converter = new ReversePolishNotation();
        String output = converter.toPolish(expr);
        String[] tokens = output.split(" ");
        Stack<String> polishStack = new Stack<String>();
        if (tokens.length == 1) {
            try {
                int result = Integer.parseInt(tokens[0]);
                System.out.println(result);
                System.exit(0);
            } catch (NumberFormatException e) {
                System.err
                        .println("Error: incorrect mathematical expression(expression should not contain nonmath symbols or large integers)");
                System.exit(1);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        for (int i = 0; i < tokens.length; ++i) {
            if (isNumber(tokens[i])) {
                polishStack.push(tokens[i]);
            } else {
                try {
                    if (polishStack.empty()) {
                        throw new Exception(
                                "Incorrect Input: incorrect mathematical expression");
                    }
                    int a = new Integer(polishStack.peek());
                    polishStack.pop();
                    if (polishStack.empty()) {
                        throw new Exception(
                                "Incorrect Input: incorrect mathematical expression");
                    }
                    int b = new Integer(polishStack.peek());
                    polishStack.pop();
                    char operand = tokens[i].charAt(0);
                    if (checkOverflow(a, b, operand)) {
                        throw new Exception("Overflow!");
                    } else {
                        polishStack.push(operationToString(a, b, operand));
                    }
                } catch (Exception e) {
                    System.err.print("Error: ");
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        }
        if (!polishStack.empty()) {
            System.out.println(polishStack.peek());
        } else {
            System.err
                    .println("Incorrect Input: incorrect mathematical expression");
            System.exit(1);
        }
    }
}
