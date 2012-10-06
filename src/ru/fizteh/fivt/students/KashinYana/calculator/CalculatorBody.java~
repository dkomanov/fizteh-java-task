package fizteh.fivt.students.KashinYana.calculator;

import java.util.*;
import java.io.*;

public class CalculatorBody {
    private String inputString;
    private int[] stack;
    private char[] operation;
    private int operationSize, stackSize;

    CalculatorBody(String inputString) {
        this.inputString = inputString;
        stack = new int[inputString.length()];
        operation = new char[inputString.length()];
        operationSize = 0;
        stackSize = 0;
    }

    int recognaze() throws Exception {
        boolean beforeDigit = false;
        for (int i = 0; i < inputString.length(); i++) {
            try {
                char currentChar = inputString.charAt(i);
                if (!delim(currentChar)) {
                    if (currentChar == '(') {
                        if (beforeDigit) {
                            throw new Exception("You miss operand.");
                        }
                        operation[operationSize++] = '(';
                        beforeDigit = false;
                    } else if (currentChar == ')') {
                        try {
                            beforeDigit = false;
                            while (operation[operationSize - 1] != '(') {
                                processOperation(operation[operationSize - 1]);
                                operationSize--;
                            }
                            operationSize--;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            throw new Exception("You miss ( ");
                        }
                    } else if (isOperation(currentChar)) {
                        beforeDigit = false;
                        while ((operationSize > 0)
                                && (priority(operation[operationSize - 1]) >=
                                priority(currentChar))) {
                            processOperation(operation[operationSize - 1]);
                            operationSize--;
                        }
                        operation[operationSize++] = currentChar;
                    } else {
                        String operand = "";
                        while ((i < inputString.length()) && (isDigit(inputString.charAt(i)))) {
                            operand += inputString.charAt(i++);
                        }
                        --i;
                        if (beforeDigit) {
                            throw new Exception("You miss operand.");
                        }
                        if (isDigit(operand.charAt(0))) {
                            stack[stackSize++] = Integer.parseInt(operand);
                            beforeDigit = true;
                        }
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                throw new Exception("Out of range. Write letter to programmer.");
            } catch (Exception e) {
                throw new Exception(e.getMessage() + "Error in pos " + i);
            }
        }
        while (operationSize > 0) {
            processOperation(operation[operationSize - 1]);
            operationSize--;
        }
        if (stackSize > 1) {
            throw new Exception("You write unvalid formula.");
        }
        return stack[stackSize - 1];
    }

    private boolean delim(char c) {
        return (c == ' ') || (c == '\t');
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean isOperation(char c) {
        return (c == '+') || (c == '-') || (c == '*') || (c == '/') || (c == '%');
    }

    private int priority(char operation) {
        if ((operation == '+') || (operation == '-')) {
            return 1;
        } else if ((operation == '*') || (operation == '/') || (operation == '%')) {
            return 2;
        } else {
            return -1;
        }
    }

    private void processOperation(char operation) throws Exception {
        if (operation == '(') {
            throw new Exception("You miss ).");
        }
        if (stackSize < 2) {
            throw new Exception("You miss numbers near operand.");
        }

        int r = stack[stackSize - 1];
        stackSize--;
        int l = stack[stackSize - 1];
        stackSize--;
        if (operation == '+') {
            stack[stackSize++] = (l + r);
        } else if (operation == '-') {
            stack[stackSize++] = (l - r);
        } else if (operation == '*') {
            stack[stackSize++] = (l * r);
        } else if (operation == '/') {
            if (r == 0) {
                throw new Exception("Exists delete zero.");
            }
            stack[stackSize++] = (l / r);
        } else if (operation == '%') {
            stack[stackSize++] = (l % r);
        } else {
            throw new Exception("You use strange operand.");
        }
    }
}
