package ru.fizteh.fivt.students.tolyapro.calculator;

import java.util.*;
import java.math.*;

public class Calc {

    public static boolean isNumber(String s) {
        if (s.length() != 0)
            if (Character.isDigit(s.charAt(0))) {
                return true;
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

    public static boolean checkOverflow(int a, int b, char operand) {
        String res_str = new String();
        if (operand == '+') {
            res_str = new String(Integer.toString(a + b));
        } else if (operand == '-') {
            res_str = new String(Integer.toString(b - a));
        } else if (operand == '*') {
            res_str = new String(Integer.toString(a * b));
        }
        String a_str = new String(Integer.toString(a));
        String b_str = new String(Integer.toString(b));
        BigInteger res = new BigInteger(res_str);
        BigInteger a_big = new BigInteger(a_str);
        BigInteger b_big = new BigInteger(b_str);
        if (operand == '+') {
            return !res.equals(a_big.add(b_big));

        } else if (operand == '-') {
            return !res.equals(b_big.subtract(a_big));

        } else if (operand == '*') {
            return !res.equals(a_big.multiply(b_big));

        }
        if (operand == '/')
            return false;
        return true;
    }

    public static void main(String[] args) throws Exception {
        String expr = new String();
        StringBuilder builder = new StringBuilder();
        boolean is_prev_num = false;
        for (String s : args) {
            if (is_prev_num) {
                if ((s.charAt(0) >= '0') && (s.charAt(0) <= '9')) {
                    System.err.println("Error: Incorrect Input");
                    System.exit(1);
                }
            }
            builder.append(s);
            if ((s.charAt(s.length() - 1) >= '0')
                    && (s.charAt(s.length() - 1) <= '9')) {
                is_prev_num = true;
            } else {
                is_prev_num = false;
            }

        }
        expr = builder.toString();
        expr.replaceAll("\"", "");
        expr.replaceAll(" ", "");
        if (!(checkBrackets(expr))) {
            System.err.println("Incorrect input: Brackets error");
            System.exit(1);
        }
        if (expr.isEmpty()) {
            System.err.println("Usage");
            System.exit(1);
        }
        ReversePolishNotation converter = new ReversePolishNotation();
        String output = converter.toPolish(expr);
        String[] tokens = output.split(" ");
        Stack<String> polish_stack = new Stack<String>();
        for (int i = 0; i < tokens.length; ++i) {
            if (isNumber(tokens[i])) {
                polish_stack.push(tokens[i]);
            } else {
                try {
                    if (polish_stack.empty()) {
                        throw new Exception("Incorrect Input");
                    }
                    int a = new Integer(polish_stack.peek());
                    polish_stack.pop();
                    if (polish_stack.empty()) {
                        throw new Exception("Incorrect Input");
                    }
                    int b = new Integer(polish_stack.peek());
                    polish_stack.pop();
                    char operand = tokens[i].charAt(0);
                    if (checkOverflow(a, b, operand)) {
                        throw new Exception("Overflow!");
                    } else {
                        if (operand == '+') {
                            polish_stack.push(Integer.toString(a + b));
                        } else if (operand == '-') {
                            polish_stack.push(Integer.toString(b - a));
                        } else if (operand == '*') {
                            polish_stack.push(Integer.toString(a * b));
                        } else if (operand == '/') {
                            polish_stack.push(Integer.toString(b / a));
                        }
                    }
                } catch (Exception e) {
                    System.err.print("Error: ");
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        }
        if (!polish_stack.empty()) {
            System.out.println(polish_stack.peek());
        } else {
            System.err.println("Error");
            System.exit(1);
        }
    }
}
