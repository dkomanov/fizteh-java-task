package ru.fizteh.fivt.students.yuliaNikonova.calculator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Calculator {
    protected static ArrayList<String> myOperators = new ArrayList<String>();

    public static void main(String[] args) {

        myOperators.add("+");
        myOperators.add("-");
        myOperators.add("*");
        myOperators.add("/");

        StringBuilder myExpressionBuilder = new StringBuilder("");

        for (String argument : args) {
            myExpressionBuilder.append(argument);
        }
        String myExpression = myExpressionBuilder.toString();
        // System.out.println(myExpression);
        myExpression = myExpression.replace("\\s", "");
        System.out.println("Expression: " + myExpression);

        ArrayList<String> myExpressionOPN;
        try {
            myExpressionOPN = Calculator.toOPN(myExpression);
            StringBuilder myExpressionOPNStringBuilder = new StringBuilder("");
            for (String el : myExpressionOPN) {
                myExpressionOPNStringBuilder.append(el + " ");
            }
            String myExpressionOPNString = myExpressionOPNStringBuilder
                    .toString();

            if (myExpressionOPNString.equals("")) {
                System.out
                        .println("Error: empty expression, you should enter expression as argument");
            } else {
                System.out.println("OPN: " + myExpressionOPNString);
                try {
                    BigInteger result = calculateOPN(myExpressionOPN);
                    System.out.println("Result: " + result.toString());
                } catch (Exception e) {
                    System.out.println("Error: invalid expression");
                }
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            System.out.println(e1.getMessage());
            System.exit(0);
        }
    }

    public static ArrayList<String> toOPN(String myExpression) throws Exception {
        ArrayList<String> myExpressionOPN = new ArrayList<String>();
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
                myExpressionOPN.add(number);
                // System.out.println(number);
                number = "";
                if (c == '+' || c == '-' || c == '*' || c == '/') {
                    if ((c == '+' || c == '-')
                            && Character.isDigit(myExpression.charAt(i + 1))
                            && ((i == 0 || myExpression.charAt(i - 1) == '(') || (i > 1
                                    && myOperators
                                            .contains(String
                                                    .valueOf(myExpression
                                                            .charAt(i - 1))) && (Character
                                    .isDigit(myExpression.charAt(i - 2)) || myExpression
                                    .charAt(i - 2) == ')')))) {
                        number += c;
                        // System.out.println("jhvhjg");
                        ifLastOperator = true;
                    } else {
                        if (ifLastOperator) {
                            throw new Exception("Error: too much operators");
                        }
                        ifLastOperator = true;
                        while (!opnStack.isEmpty()
                                && myOperators.contains(opnStack.get(opnStack
                                        .size() - 1))
                                && (int) operatorsPriority.get(String
                                        .valueOf(c)) <= (int) operatorsPriority
                                        .get(opnStack.get(opnStack.size() - 1))) {
                            myExpressionOPN
                                    .add(opnStack.get(opnStack.size() - 1));
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
                        myExpressionOPN.add(opnStack.get(j));
                        opnStack.remove(j);
                        j--;
                    }
                    if (opnStack.isEmpty()) {
                        throw new Exception(
                                "Error: someting wrong with ( and )");
                    } else {
                        opnStack.remove(j);
                    }
                } else {
                    throw new Exception("Error: bad symbol");
                }
            }
        }

        if (number.length() > 0) {
            myExpressionOPN.add(number);
        }
        while (!opnStack.isEmpty()) {
            String op = opnStack.get(opnStack.size() - 1);
            if (!(op.equals("+") || op.equals("-") || op.equals("*") || op
                    .equals("/"))) {
                throw new Exception("Error: someting wrong with ( and )");
            }
            myExpressionOPN.add(op);
            opnStack.remove(opnStack.size() - 1);
        }
        if (ifLastOperator) {
            throw new Exception("Error: too much operators");
        }
        return myExpressionOPN;
    }

    public static BigInteger calculateOPN(ArrayList<String> myExpressionOPN) {
        ArrayList<BigInteger> integerStack = new ArrayList<BigInteger>();
        for (String op : myExpressionOPN) {
            if (op.length() > 0) {
                if (Character.isDigit(op.charAt(0))
                        || ((op.length() > 1) && Character
                                .isDigit(op.charAt(1)))) {
                    integerStack.add(new BigInteger(op));
                } else {
                    if (integerStack.size() > 1) {
                        BigInteger op1 = integerStack
                                .get(integerStack.size() - 1);
                        BigInteger op2 = integerStack
                                .get(integerStack.size() - 2);
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
