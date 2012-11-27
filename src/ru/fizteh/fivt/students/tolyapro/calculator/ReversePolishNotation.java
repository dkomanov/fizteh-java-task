package ru.fizteh.fivt.students.tolyapro.calculator;

import java.util.Stack;

public class ReversePolishNotation {

    public static int priority(char ch) {
        if ((ch == '+') || (ch == '-')) {
            return 1;
        } else if ((ch == '*') || (ch == '/')) {
            return 2;
        }
        return -1;
    }

    public static boolean isOperand(char ch) {
        return ((ch == '+') || (ch == '-') || (ch == '*') || (ch == '/'));
    }

    public String toPolish(String expr) throws Exception, RuntimeException {
        boolean inNumber = false;
        StringBuilder builderNumber = new StringBuilder();
        StringBuilder builderOutput = new StringBuilder();
        Stack<Character> st = new Stack<Character>();
        for (int i = 0; i < expr.length(); ++i) {
            char ch = expr.charAt(i);
            if (Character.isDigit(ch)) {
                if (!inNumber) {
                    inNumber = true;
                    builderNumber.append(ch);
                } else {
                    builderNumber.append(ch);
                }
            } else if (isOperand(ch)) {
                if (inNumber) {
                    builderOutput.append(builderNumber);
                    builderOutput.append(" ");
                    builderNumber.delete(0, builderNumber.length());
                    inNumber = false;
                }
                if (!(st.empty())) {
                    char tmp = st.peek();
                    while ((isOperand(ch)) && (priority(ch) <= priority(tmp))) {
                        builderOutput.append(tmp);
                        builderOutput.append(" ");
                        st.pop();
                        if (st.empty()) {
                            break;
                        }
                        tmp = st.peek();
                    }
                }
                st.push(ch);
            } else if (ch == '(') {
                if (inNumber) {
                    builderOutput.append(builderNumber);
                    builderOutput.append(" ");
                    builderNumber.delete(0, builderNumber.length());
                    inNumber = false;
                    st.push(ch);
                } else {
                    st.push(ch);
                }
            } else if (ch == ')') {
                if (inNumber) {
                    builderOutput.append(builderNumber);
                    builderOutput.append(" ");
                    builderNumber.delete(0, builderNumber.length());
                    inNumber = false;
                }
                if (!st.empty()) {
                    char tmp = st.peek();
                    while (!(tmp == '(')) {
                        if (st.empty()) {
                            throw new Exception("Brackets Error");
                        }
                        builderOutput.append(tmp);
                        builderOutput.append(" ");
                        st.pop();
                        tmp = st.peek();
                    }
                    st.pop();
                }
            }
        }
        if (inNumber) {
            builderOutput.append(builderNumber);
            builderOutput.append(" ");
        }
        while (!(st.empty())) {
            builderOutput.append(st.peek());
            builderOutput.append(" ");
            st.pop();
        }
        return builderOutput.toString();
    }
}