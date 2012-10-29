package ru.fizteh.fivt.students.yuliaNikonova.calculator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    protected static ArrayList<String> myOperators = new ArrayList<String>();

    public static void main(String[] args) {

        myOperators.add("+");
        myOperators.add("-");
        myOperators.add("*");
        myOperators.add("/");

        StringBuilder myExpressionBuilder = new StringBuilder();

        for (String argument : args) {
            myExpressionBuilder.append(argument);
            myExpressionBuilder.append(" ");
        }
        String myExpression = myExpressionBuilder.toString();
        System.out.println(myExpression);
        Pattern p = Pattern.compile("(.*\\d+\\s+\\d+.*)");
        Matcher m = p.matcher(myExpression);
        if (m.matches()) {
            System.err.println("Invalid input");
            System.exit(1);
        }

        myExpression = myExpression.replaceAll("\\s+", "");
        System.out.println("Expression: " + myExpression);

        ArrayList<String> myExpressionOpn;
        try {
            myExpressionOpn = Calculator.toOpn(myExpression);
            StringBuilder myExpressionOpnStringBuilder = new StringBuilder("");
            for (String el : myExpressionOpn) {
                myExpressionOpnStringBuilder.append(el + " ");
            }
            String myExpressionOpnString = myExpressionOpnStringBuilder.toString();

            if (myExpressionOpnString.isEmpty()) {
                System.err.println("Error: empty expression, you should enter expression as argument");
            } else {
                System.out.println("OPN: " + myExpressionOpnString);
                try {
                    BigInteger result = calculateOpn(myExpressionOpn);
                    System.out.println("Result: " + result.toString());
                } catch (Exception e) {
                    System.err.println("Error: invalid expression");
                }
            }
        } catch (Exception e1) {
            System.err.println(e1.getMessage());
            System.exit(1);
        }
    }

    public static ArrayList<String> toOpn(String myExpression) throws Exception {
        ArrayList<String> myExpressionOpn = new ArrayList<String>();
        String number = "";
        boolean ifLastOperator = false;
        HashMap<String, Integer> operatorsPriority = new HashMap<String, Integer>();
        operatorsPriority.put("+", 1);
        operatorsPriority.put("-", 1);
        operatorsPriority.put("*", 2);
        operatorsPriority.put("/", 2);

        ArrayList<String> opnStack = new ArrayList<String>();
        for (int i = 0; i < myExpression.length(); i++) {
            char c = myExpression.charAt(i);
            if (Character.isDigit(c)) {
                number += c;
                ifLastOperator = false;
            } else {
                myExpressionOpn.add(number);
                number = "";
                if (operatorsPriority.containsKey(String.valueOf(c))) {
                    char c1 = myExpression.charAt(i + 1);
                    char c2 = myExpression.charAt(i - 1);
                    char c3 = '.';
                    if (i > 2) {
                        c3 = myExpression.charAt(i - 2);
                    }
                    if ((c == '+' || c == '-') && Character.isDigit(c1) && ((i == 0 || c2 == '(') || (i > 1 && myOperators.contains(String.valueOf(c2)) && (Character.isDigit(c3) || c3 == ')')))) {
                        number += c;
                        ifLastOperator = true;
                    } else {
                        if (ifLastOperator) {
                            throw new Exception("Error: too much operators");
                        }
                        ifLastOperator = true;
                        while (!opnStack.isEmpty() && myOperators.contains(opnStack.get(opnStack.size() - 1))
                                && (int) operatorsPriority.get(String.valueOf(c)) <= (int) operatorsPriority.get(opnStack.get(opnStack.size() - 1))) {
                            myExpressionOpn.add(opnStack.get(opnStack.size() - 1));
                            opnStack.remove(opnStack.size() - 1);
                        }
                        opnStack.add(String.valueOf(c));
                    }
                } else if (c == '(') {
                    ifLastOperator = false;
                    opnStack.add(String.valueOf(c));
                } else if (c == ')') {
                    if (ifLastOperator) {
                        throw new Exception("Error: too much operators");
                    }
                    int j = opnStack.size() - 1;
                    while (!opnStack.get(j).equals("(")) {
                        myExpressionOpn.add(opnStack.get(j));
                        opnStack.remove(j);
                        j--;
                    }
                    if (opnStack.isEmpty()) {
                        throw new Exception("Error: someting wrong with ( and )");
                    } else {
                        opnStack.remove(j);
                    }
                } else {
                    throw new Exception("Error: bad symbol");
                }
            }
        }

        if (number.length() > 0) {
            myExpressionOpn.add(number);
        }
        while (!opnStack.isEmpty()) {
            String op = opnStack.get(opnStack.size() - 1);
            if (!(op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/"))) {
                throw new Exception("Error: someting wrong with ( and )");
            }
            myExpressionOpn.add(op);
            opnStack.remove(opnStack.size() - 1);
        }
        if (ifLastOperator) {
            throw new Exception("Error: too much operators");
        }
        return myExpressionOpn;
    }

    public static BigInteger calculateOpn(ArrayList<String> myExpressionOPN) {
        ArrayList<BigInteger> integerStack = new ArrayList<BigInteger>();
        for (String op : myExpressionOPN) {
            if (op.length() > 0) {
                if (Character.isDigit(op.charAt(0)) || ((op.length() > 1) && Character.isDigit(op.charAt(1)))) {
                    integerStack.add(new BigInteger(op));
                } else {
                    if (integerStack.size() > 1) {
                        BigInteger op1 = integerStack.get(integerStack.size() - 1);
                        BigInteger op2 = integerStack.get(integerStack.size() - 2);
                        if (myOperators.contains(op)) {
                            integerStack.remove(integerStack.size() - 1);
                            integerStack.remove(integerStack.size() - 1);
                            if (op.equals("+")) {
                                integerStack.add(op1.add(op2));
                            } else if (op.equals("-")) {
                                integerStack.add(op2.subtract(op1));
                            } else if (op.equals("*")) {
                                integerStack.add(op1.multiply(op2));
                            } else if (op.equals("/")) {
                                integerStack.add(op2.divide(op1));
                            }
                        }
                    }
                }
            }
        }
        return integerStack.get(integerStack.size() - 1);
    }
}
