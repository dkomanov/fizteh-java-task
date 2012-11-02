package ru.fizteh.fivt.students.alexanderKuzmin.calculator;

/**
 * @author Kuzmin A.
 *      group 196
 *      Class Calculator for calculate expression.
 * 
 */

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Iterator;

public class Calculator {

    private static void printErrAndExit(String message) {
        System.err.println(message);
        System.exit(1);
    }

    // The method converts the string in RPN
    public String toPolishNotation(String formula) {
        if (formula.isEmpty()) {
        	printErrAndExit("Nothing input.");
        }
        formula = "(" + formula + ")";

        final ArrayDeque<Character> stack = new ArrayDeque<Character>();
        final ArrayDeque<Character> outString = new ArrayDeque<Character>();
        int sum = 0;
        boolean number = false;
        for (int i = 0; i < formula.length(); ++i) {
            if (formula.charAt(i) == ')') {
                --sum;
                if (sum < 0) {
                	printErrAndExit("Error with parentheses");
                }
                number = false;
                try {
                    while (stack.peek().charValue() != '(') {
                        outString.push(stack.pop());
                    }
                    stack.pop(); // delete '('
                } catch (Exception e1) {
                	printErrAndExit("Error with stack(error with parentheses).");
                }
            } else if (formula.charAt(i) == '(') {
                ++sum;
                number = false;
                stack.push('(');
            } else if (Character.isDigit(formula.charAt(i))) {
                if (number) {
                	printErrAndExit("Error with order of numbers.");
                }
                outString.push(formula.charAt(i));
                if (!Character.isDigit(formula.charAt(i + 1))) {
                    outString.push(' ');
                    number = true;
                }
            } else if ((formula.charAt(i) == '+') || (formula.charAt(i) == '-')
                    || (formula.charAt(i) == '*') || (formula.charAt(i) == '/')) {
                number = false;
                if (stack.size() == 0) {
                    stack.push(formula.charAt(i));
                } else if (priority(formula.charAt(i)) > priority(String
                        .valueOf(stack.peek()).charAt(0))) {
                    stack.push(formula.charAt(i));
                } else {
                    while ((stack.size() != 0)
                            && (priority(String.valueOf(stack.peek()).charAt(0)) >= priority(formula
                                    .charAt(i)))) {
                        outString.push(stack.pop());
                    }
                    stack.push(formula.charAt(i));
                }
            } else if (!Character.isWhitespace(formula.charAt(i))) {
            	printErrAndExit("Error with order of symbols. Unrecognized character: "
                                + formula.charAt(i));
            }
        }
        if (sum != 0) {
        	printErrAndExit("Error with parentheses");
        }
        StringBuilder resultNotation = new StringBuilder();
        Iterator<Character> it = outString.descendingIterator();
        while (it.hasNext()) {
            resultNotation.append(it.next());
        }
        return resultNotation.toString();
    }

    // Method for priority operations
    private int priority(final char a) {
        switch (a) {
        case '/':
            return 3;
        case '*':
            return 3;
        case '-':
            return 2;
        case '+':
            return 2;
        case '(':
            return 1;
        }
        return 0;
    }

    // The method that calculate the expression by using Reverse Polish Notation
    private BigInteger solve(final String formula) {
        if (formula.isEmpty()) {
        	printErrAndExit("Empty input.");
        }
        ArrayDeque<BigInteger> stack = new ArrayDeque<BigInteger>();
        int begin = 0;
        boolean first = true;
        for (int i = 0; i < formula.length(); ++i) {
            if (Character.isDigit(formula.charAt(i))) {
                if (first) {
                    begin = i;
                    first = false;
                }
                if (!Character.isDigit(formula.charAt(i + 1))) {
                    stack.push(new BigInteger(formula.substring(begin, i + 1)));
                    first = true;
                }
            } else if (formula.charAt(i) == ' ') {
                continue;
            } else {
                try {
                    switch (formula.charAt(i)) {
                    case '/':
                        BigInteger a = stack.pop();
                        try {
                            stack.push(stack.pop().divide(a));
                        } catch (Exception e3) {
                        	printErrAndExit("Division by zero or another error with division!\n");
                        }
                        break;
                    case '*':
                        stack.push(stack.pop().multiply(stack.pop()));
                        break;
                    case '-':
                        BigInteger b = stack.pop();
                        stack.push(stack.pop().subtract(b));
                        break;
                    case '+':
                        stack.push(stack.pop().add(stack.pop()));
                        break;
                    }
                } catch (Exception e3) {
                	printErrAndExit("Error with operations.");
                }
            }
        }
        return stack.pop();
    }

    /**
     * @param args
     *            input expression, which the program should calculate
     *            (supported Operations: multiplication - "*", division - "/",
     *            subtraction - "-" Addition - "+").
     */
    public static void main(String[] args) throws Exception {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            str.append(args[i]).append(" ");
        }
        Calculator calc = new Calculator();
        System.out.print(calc.solve(calc.toPolishNotation(str.toString())));
    }
}