package ru.fizteh.fivt.students.MuhinMihail.Calculator;

import java.util.*;
import java.math.BigInteger;

public class Calculator {

    private static int getPriority(char funct) {
        switch (funct) {
            case '(':
                return -1;

            case '*':
            case '/':
                return 1;

            case '+':
            case '-':
                return 2;

            default:
                System.err.println("Error: Unknown character \"" + funct + "\"");
                System.exit(1); 
                return 0;
        }
    }

    private static void popFunction(Stack<BigInteger> operands, Stack<Character> functions) throws Exception {
        if (operands.size() < 2) {
            System.err.println("Error: Number of functions and operands does not coincide");
            System.exit(1); 
        }

        BigInteger b = operands.pop();
        BigInteger a = operands.pop();

        char c = functions.pop();
        switch (c) {
            case '+':
                operands.push(a.add(b));
                break;

            case '-':
                operands.push(a.subtract(b));
                break;

            case '*':
                operands.push(a.multiply(b));
                break;

            case '/':
                operands.push(a.divide(b));
                break;

            default:
                System.err.println("Error: Unknown character \"" + c + "\"");
                System.exit(1); 
                break;
                
        }
    }

    private static boolean canPop(char op, Stack<Character> functions) throws Exception {
        if (functions.empty()) {
            return false;
        }

        if (functions.peek().equals('N')) {
            return false;
        }

        int prior1 = getPriority(op);
        int prior2 = getPriority(functions.peek());

        return prior1 >= 0 && prior2 >= 0 && prior1 >= prior2;
    }

    private static Integer readInt(String s, int[] ind) throws Exception {
        int begin = ind[0];
        int end = 0;
        
        while (ind[0] < s.length() && Character.isDigit(s.charAt(ind[0]))) {
            end = ind[0]++;
        } 
        
        try {
            return Integer.parseInt(s.substring(begin, end+1));
        } catch (Exception exp) { 
            System.err.println("Error: Number too long");
            System.exit(1); 
        }
        return null;
    }

    private static Object getToken(String s, int[] ind) throws Exception {
        if (ind[0] == s.length()) {
            return null;
        }

        if (Character.isDigit(s.charAt(ind[0]))) {
            return readInt(s, ind);
        } else {
            return new Character(s.charAt(ind[0]++));
        }
    }

    public static void main(String[] args) throws Exception {
        int ind[] = new int[1];
        Stack<Character> functions = new Stack<Character>();
        Stack<BigInteger> operands = new Stack<BigInteger>();
        StringBuilder sb = new StringBuilder("(");

        for (int r = 0; r < args.length; ++r) {
            sb.append(args[r]).append(" ");
        }

        ind[0] = 0;
        sb.append(")");
        
        String str = sb.toString();
        System.out.println(str);

        try {
            if ((args.length == 0) || str.replaceAll("\\s", "").equals("")) {
                System.err.println("Error: No arguments. Mathematical expression as argement is expected");
                System.exit(1); 
            }

            Object prevtoken = new Object();
            Object token = getToken(str, ind);

            while (token != null) {
                
                if (token instanceof Character && Character.isWhitespace((Character)token)) {
                    token = getToken(str, ind);
                    continue;
                }

                if (token instanceof Character && prevtoken instanceof Character 
                        && prevtoken.equals('(') && (token.equals('+') || token.equals('-'))) {
                    operands.push(BigInteger.valueOf(0));
                }

                if (token instanceof Integer) {
                    if (prevtoken.equals('N')) {
                        operands.push(BigInteger.valueOf((-1) * ((Integer) token)));
                        functions.pop();
                        prevtoken = functions.peek();
                    } else {
                        operands.push(BigInteger.valueOf((Integer) token));
                    }
                } else if (token instanceof Character) {
                    if (((Character) token).equals(')')) {
                        while (!functions.empty() && functions.peek() != '(') {
                            popFunction(operands, functions);
                        }
                        if (functions.size() == 0) {
                            System.err.println("Error: brackets don't coincide");
                            System.exit(1); 
                        }
                        functions.pop();

                        if (functions.size() > 0 && functions.peek().equals('N')) {
                            BigInteger temp = operands.pop();
                            operands.push(temp.multiply(BigInteger.valueOf(-1)));
                            functions.pop();
                            prevtoken = functions.peek();
                        }
                    } else {
                        boolean doPop = true;

                        boolean isMinus = token.equals('-');
                        boolean wasPlus = prevtoken.equals('+');

                        boolean wasDiv = prevtoken.equals('/');
                        boolean wasMul = prevtoken.equals('*');

                        if (wasPlus && isMinus) {
                            functions.pop();
                            prevtoken = functions.peek();
                            token = '-';
                            doPop = false;
                        }

                        if ((wasDiv || wasMul) && isMinus) {
                            token = 'N';
                            doPop = false;
                        }

                        if (doPop) {
                            while (canPop((Character) token, functions)) {
                                popFunction(operands, functions);
                            }
                        }
                        functions.push((Character) token);
                    }
                }

                prevtoken = token;
                token = getToken(str, ind);
            }

            if (operands.size() != 1 || !functions.empty()) {
                if (operands.size() != 1) {
                    System.err.println("Error: Number of functions and operands does not coincide");
                } else {
                    System.err.println("Error: brackets don't coincide");
                }
            } else {
                System.out.println("Result = " + operands.pop());
            }
        } catch (Exception expt) {
            System.err.println("Error: " + expt.getMessage());
        }

    }
}
