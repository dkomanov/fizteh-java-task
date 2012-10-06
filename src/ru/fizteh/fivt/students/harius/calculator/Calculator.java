/*
 * Calculator.java
 * Sep 30, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.calculator;

import java.util.*;

/*
 * Calculates a mathematical expression
 */
public class Calculator {
    /* List of supported operators */
    private static final String OPERATORS = "+-*/"; 
    /* Splitted expression */
    private Queue<String> tokens = new LinkedList<String>();

    /* Calculates an expression taken as an argument from console */
    public static void main(String[] args) {
        StringBuilder concat = new StringBuilder();
        for(String arg : args) {
            concat.append(arg);
            concat.append(" ");
        }
        String expression = concat.toString().trim();
        if (expression.isEmpty()) {
            System.out.println("usage: java ru.fizteh.fivt.students.harius.calculator.Calculator expression");
            System.exit(1);
        }
        try {
            System.out.println(new Calculator(expression).eval());
        } catch (CalculatorException wrong) {
            System.out.println("error:\n" + wrong.getMessage());
            System.exit(1);
        }
    }

    /* Tokenizes the given expression, but the does not start evaluation */
    public Calculator(String expression) {
        StringTokenizer tok = new StringTokenizer(expression, OPERATORS + "()", true);
        while (tok.hasMoreTokens()) {
            String next = tok.nextToken().trim();
            if (!next.isEmpty()) {
                tokens.add(next);
            }
        }
    }

    /* Evaluates the expression */
    public int eval() throws CalculatorException {
        return evalPart(Scope.FULL);
    }

    /* Evaluates a part of expression */
    public int evalPart(Scope scope) throws CalculatorException {
        return evalPart(scope, null);
    }

    /* If prevOp is not null, evaluate until next operator has lower priority than prevOp */
    public int evalPart(Scope scope, String prevOp) throws CalculatorException {
        int result = loadArg();
        while (!tokens.isEmpty()) {
            if (prevOp != null) {
                String nextOp = tokens.peek();
                if (!inWrongOrder(prevOp, nextOp)) {
                    return result;
                }
            }
            String operator = tokens.poll();
            if (operator.equals(")")) {
                if (scope != Scope.BRACKET) {
                    throw new CalculatorException("Unexpected closing bracket");
                }
                return result;
            }
            if (!OPERATORS.contains(operator)) {
                throw new CalculatorException("Expected operator, got " + operator);
            }
            int arg = evalPart(Scope.FULL, operator);
            result = apply(result, operator, arg);
        }
        if (scope != Scope.FULL) {
            throw new CalculatorException("Unexpected end");
        }
        return result;
    }

    /* Load an argument: a single number or a complex expression */
    private int loadArg() throws CalculatorException {
        if (tokens.isEmpty()) {
            throw new CalculatorException("Unexpected end");
        }
        String next = tokens.poll();
        if (next.equals("-")) {
            return -loadArg();
        } else if (next.equals("+")) {
            return +loadArg();
        } else if (next.equals("(")) {
            return evalPart(Scope.BRACKET);
        } else {
            try {
                return Integer.parseInt(next);
            } catch (NumberFormatException notNumber) {
                throw new CalculatorException("Expected number, got " + next);
            }
        }
    }

    /* Apply operator to a pair of arguments */
    private static int apply(int arg1, String operator, int arg2) throws CalculatorException {
        long result;
        if (operator.equals("+")) {
            result = (long)arg1 + arg2;
        } else if (operator.equals("-")) {
            result = (long)arg1 - arg2;
        } else if (operator.equals("*")) {
            result = (long)arg1 * arg2;
        } else if (operator.equals("/")) {
            if (arg2 == 0) {
                throw new CalculatorException("Division by zero");
            }
            result = (long)arg1 / arg2;
        } else {
            throw new CalculatorException("Unimplemented operator: " + operator);
        }
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            throw new CalculatorException("Overflow occured");
        }
        return (int)result;
    }

    /* Return true if op1 has lower priority than op2 */
    private static boolean inWrongOrder(String op1, String op2) {
        return "+-".contains(op1) && "*/".contains(op2);
    }
}

/* An exception used by Calculator */
class CalculatorException extends Exception {
    public CalculatorException(String message) {
        super(message);
    }
}

/* A direction for Calculator.evalPart where to stop */
enum Scope {
    FULL, BRACKET
}
