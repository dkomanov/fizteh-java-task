package ru.fizteh.fivt.students.tolyapro.calculator;

import java.util.*;
import java.math.*;

public class Calc {

    public static boolean isNumber(String s) {
        if (s.length() != 0) {
            if (Character.isDigit(s.charAt(0))) {
                return true;
            }
        }
        return false;

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

    public static boolean checkOverflow(int a, int b, char operand) {
        if (operand == '/') {
            return false;
        }
        String resStr = new String();
        if (operand == '+') {
            resStr = Integer.toString(a + b);
        } else if (operand == '-') {
            resStr = Integer.toString(b - a);
        } else if (operand == '*') {
            resStr = Integer.toString(a * b);
        }
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
                    System.err.println("Error: Incorrect Input");
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
        expr = expr.replaceAll("\'|\"|\t|\\s", "");
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
            System.err.println("Incorrect input");
            System.exit(1);
        }
        ReversePolishNotation converter = new ReversePolishNotation();
        String output = converter.toPolish(expr);
        String[] tokens = output.split(" ");
        Stack<String> polishStack = new Stack<String>();
        for (int i = 0; i < tokens.length; ++i) {
            if (isNumber(tokens[i])) {
                polishStack.push(tokens[i]);
            } else {
                try {
                    if (polishStack.empty()) {
                        throw new Exception("Incorrect Input");
                    }
                    int a = new Integer(polishStack.peek());
                    polishStack.pop();
                    if (polishStack.empty()) {
                        throw new Exception("Incorrect Input");
                    }
                    int b = new Integer(polishStack.peek());
                    polishStack.pop();
                    char operand = tokens[i].charAt(0);
                    if (checkOverflow(a, b, operand)) {
                        throw new Exception("Overflow!");
                    } else {
                        if (operand == '+') {
                            polishStack.push(Integer.toString(a + b));
                        } else if (operand == '-') {
                            polishStack.push(Integer.toString(b - a));
                        } else if (operand == '*') {
                            polishStack.push(Integer.toString(a * b));
                        } else if (operand == '/') {
                            polishStack.push(Integer.toString(b / a));
                        }
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
            System.err.println("Incorrect Input");
            System.exit(1);
        }
    }
}
