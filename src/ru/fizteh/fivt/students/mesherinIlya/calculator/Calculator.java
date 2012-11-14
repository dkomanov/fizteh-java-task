package ru.fizteh.fivt.students.mesherinIlya.calculator;

import java.util.*;
import java.math.BigInteger;

public class Calculator {

    private static boolean isOp(Character c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    private static int priority(Integer op) {
        if (op < 0) {
            return 4;
        }
        return
            op == '+' || op == '-' ? 1 :
            op == '*' || op == '/' || op == '%' ? 2 :
            -1;
        }

    private static void executeOperation(ArrayDeque<BigInteger> numbers, Integer op) {
        if (op < 0) {
            BigInteger operand = numbers.pop();
            switch (-op) {
            case (int)'+': 
                numbers.push(operand); 
                break;
            case (int)'-': 
                numbers.push(operand.negate()); 
                break;
            }
        }
        else {
            BigInteger right = numbers.pop();
            BigInteger left = numbers.pop();
            switch (op) {
            case (int)'+': 
                numbers.push(left.add(right)); 
                break;
            case (int)'-': 
                numbers.push(left.subtract(right)); 
                break;
            case (int)'*': 
                numbers.push(left.multiply(right)); 
                break;
            case (int)'/': 
                if (right.signum() == 0) {
                    System.err.println("Error: division by zero");
                    System.exit(1);
                }
                numbers.push(left.divide(right)); 
                break;
            case (int)'%': 
                if (right.signum() == 0) {
                    System.err.println("Error: division by zero");
                    System.exit(1);
                }
                numbers.push(left.mod(right)); 
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.err.println("Using: Calculator <expression>");
            System.exit(1);
        }
        
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s);
            builder.append(' ');
        }

        String str = builder.toString();

        //проверим корректность выражения
        boolean thereWasNumber = false;
        boolean thereWasOp = false;
        for (int i = 0; i < str.length(); i++) {
            char cur = str.charAt(i);
            char next = (i < str.length()-1 ? str.charAt(i+1) : 0);
            if (!Character.isWhitespace(cur) && !Character.isDigit(cur) && 
                    !isOp(cur) && str.charAt(i) != '(' && str.charAt(i) != ')') {
                System.err.println("Error: unknown symbol or unsupported operation");
                System.exit(1);
            }
            if (i < str.length()-1 &&
                    (Character.isWhitespace(cur) || cur == '(' || cur == ')') &&
                    (!Character.isWhitespace(next) && next != '(' && next != ')')) {
                if (isOp(next) && thereWasOp && (cur != '(' || next != '-')) {
                    System.err.println("Error: two operators in a row!");
                    System.exit(1);
                }
                if (Character.isDigit(next) && thereWasNumber) {
                    System.err.println("Error: two numbers in a row!");
                    System.exit(1);
                }		
            }
            if (i < str.length()-1 && isOp(cur) && isOp(next)) {
                System.err.println("Error: two operators in a row!");
                System.exit(1);
            }
            
            if (!thereWasNumber && !thereWasOp && isOp(str.charAt(i)) && str.charAt(i) != '-') {
                System.err.println("Error: a binary operator in the beginning of expression!");
                System.exit(1);
            }
            
            
            if (Character.isDigit(str.charAt(i))) {
                thereWasNumber = true;
                thereWasOp = false;
            }
            if (isOp(str.charAt(i))) {
                thereWasOp = true;
                thereWasNumber = false;
            }
            
            if (thereWasNumber && str.charAt(i) == '(') {
                System.err.println("Error: a number before an opening bracket!");
                System.exit(1);
            }
            if (thereWasOp && str.charAt(i) == ')') {
                System.err.println("Error: a closing bracket after an operation mark!");
                System.exit(1);
            }
            if (thereWasOp && i == str.length()-1) {
                System.err.println("Error: a binary operator in the end of expression!");
                System.exit(1);
            }
        }    
            
        
        int bracketsBalance = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(') {
                bracketsBalance++;
            } else if (str.charAt(i) == ')') {
                bracketsBalance--;
            }
            if (bracketsBalance < 0) {
                break;
            }
        }
        if (bracketsBalance != 0) {
            System.err.println("Error: uncorrect brackets arrangement");
            System.exit(1);
        }


        boolean mayUnary = true;
        ArrayDeque<BigInteger> numbers = new ArrayDeque<BigInteger>();
        ArrayDeque<Integer> operations = new ArrayDeque<Integer>();



        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                if (str.charAt(i) == '(') {
                    operations.push((int)'(');
                    mayUnary = true;
                }
                else if (str.charAt(i) == ')') {
                    while (operations.peek() != '(') {
                        executeOperation(numbers, operations.peek());
                        operations.pop();
                    }
                    operations.pop();
                    mayUnary = false;
                }
                else if (isOp(str.charAt(i))) {
                    char c = str.charAt(i);
                    int curOp = c;
                    if (mayUnary && (curOp == '+' || curOp == '-')) 
                    curOp = -curOp;
                    while (!operations.isEmpty() && priority(operations.peek()) >= priority(curOp)) {
                        executeOperation(numbers, operations.pop());
                    }
                    operations.push(curOp);
                    mayUnary = true;
                }
                else {
                    String operand = "";
                    while (i < str.length() && "0123456789".indexOf(str.charAt(i)) != -1) {
                        operand += str.charAt(i++);
                    }
                    i--;
                    numbers.push(new BigInteger(operand));
                    mayUnary = false;
                }
            }
        }
        while (!operations.isEmpty()) {
            executeOperation(numbers, operations.pop());
        }

        if (numbers.isEmpty()) {
            System.out.println(0);
        }
        else {
            System.out.println(numbers.pop());
        }

    }




}
