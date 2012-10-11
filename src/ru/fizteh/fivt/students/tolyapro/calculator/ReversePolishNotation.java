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

    public String toPolish(String expr) throws Exception, RuntimeException {
        boolean inNumber = false;
        String number = new String();
        String output = new String();
        Stack<Character> st = new Stack<Character>();
        for (int i = 0; i < expr.length(); ++i) {
            char ch = expr.charAt(i);
            if (Character.isDigit(ch)) {
                if (!inNumber) {
                    inNumber = true;
                    number += ch;
                } else {
                    number += ch;
                }
            } else if ((ch == '+') || (ch == '-') || (ch == '*') || (ch == '/')) {
                if (inNumber) {
                    output += number;
                    output += " ";
                    number = "";
                    inNumber = false;
                }
                if (!(st.empty())) {
                    char tmp = st.peek();
                    while (((tmp == '+') || (tmp == '-') || (tmp == '*') || (tmp == '/'))
                            && (priority(ch) <= priority(tmp))) {
                        output += tmp;
                        output += " ";
                        st.pop();
                        if (st.empty())
                            break;
                        tmp = st.peek();
                    }
                }
                st.push(ch);
            } else if (ch == '(') {
                if (inNumber) {
                    output += number;
                    output += " ";
                    number = "";
                    inNumber = false;
                    st.push(ch);
                } else {
                    st.push(ch);

                }
            } else if (ch == ')') {
                if (inNumber) {
                    output += number;
                    output += " ";
                    number = "";
                    inNumber = false;
                }
                if (!st.empty()) {
                    char tmp = st.peek();
                    while (!(tmp == '(')) {
                        if (st.empty()) {
                            throw new Exception("Brackets Error");
                        }
                        output += tmp;
                        output += " ";
                        st.pop();
                        tmp = st.peek();
                    }
                    st.pop();
                }

            }
        }
        if (inNumber) {
            output += number;
            output += " ";
        }
        while (!(st.empty())) {
            output += st.peek();
            output += " ";
            st.pop();
        }
        return output;
    }
}