import java.util.ArrayList;
import java.util.Stack;

public class Calculator {
    public static void main(String[] args) {

        // compiling input expression
        String str = new String();
        for (int i = 0; i < args.length; ++i) {
            str += args[i] + " ";
        }

        str = str.trim();

        // checking if input expression is empty
        if (str.isEmpty()) {
            System.err.println("Error: the expression is empty. No arguments.");
            return;
        }

        // checking brackets
        int closedBrakets = 0, openedBrakets = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == '(')
                ++openedBrakets;
            else if (str.charAt(i) == ')')
                --openedBrakets;
            if (openedBrakets < 0) {
                closedBrakets = 0;
                break;
            }
        }
        if (closedBrakets != 0 || openedBrakets != 0) {
            System.err
                    .println("Error: wrong input. Something wrong with brakets.");
            return;
        }

        ArrayList<String> parsedInput = Parse(ToRPN(str));

        if (!CheckRPN(parsedInput)) {
            System.err.println("Error: wrong input");
            return;
        }

        System.out.println("Input: " + str);
        System.out.println("Answer: " + Calculate(parsedInput));
    }

    private static boolean CheckRPN(ArrayList<String> array) {
        int countOfDigs = 0;
        int countOfOperands = 0;
        for (int i = 0; i < array.size(); ++i) {
            if (array.get(i).equals("+") || array.get(i).equals("-")
                    || array.get(i).equals("/") || array.get(i).equals("*")) {
                ++countOfOperands;
            } else {
                ++countOfDigs;
            }
        }
        if ((countOfDigs - countOfOperands) == 1) {
            return true;
        } else {
            return false;
        }
    }

    private static String ToRPN(String str) {
        Stack<Character> s = new Stack<Character>();
        String resStr = new String();
        char strArray[] = str.toCharArray();
        for (int i = 0; i < str.length(); ++i) {
            char cur;
            if (Character.isDigit(strArray[i])) {
                resStr += strArray[i];
                if (i != str.length() - 1
                        && !Character.isDigit(strArray[i + 1]))
                    resStr += ",";
                else if (i == str.length() - 1)
                    resStr += ",";
            } else {
                if (strArray[i] == '(') {
                    s.push(strArray[i]);
                } else {
                    if (strArray[i] == '+' || strArray[i] == '-'
                            || strArray[i] == '/' || strArray[i] == '*') {
                        while (!s.isEmpty()) {
                            if (s.peek() == '/'
                                    || s.peek() == '*'
                                    || ((s.peek() == '-' || s.peek() == '+') && (strArray[i] == '-' || strArray[i] == '+'))) {
                                resStr += s.pop().charValue() + ",";
                            } else
                                break;
                            if (s.isEmpty() || s.peek() == '(') {
                                break;
                            }
                        }
                        s.push(strArray[i]);
                    } else {
                        if (strArray[i] == ')') {
                            while (!s.empty() && s.peek() != '(') {
                                resStr += s.pop().charValue() + ",";
                            }
                        } else {
                            if (strArray[i] != ' ' && strArray[i] != '\n'
                                    && strArray[i] != '\t') {
                                return "";
                            }
                        }
                    }
                }
            }
            if (i == str.length() - 1) {
                if (!s.isEmpty()) {
                    do {
                        if (!s.isEmpty()) {
                            cur = s.pop().charValue();
                            if (cur != '(')
                                resStr += cur + ",";
                        }
                    } while (!s.isEmpty());
                }
            }
        }
        return resStr;
    }

    private static ArrayList<String> Parse(String str) {
        ArrayList<String> res_array = new ArrayList<String>();
        char str_array[] = str.toCharArray();
        String cur = new String();
        for (int i = 0; i < str.length(); ++i) {
            if (str_array[i] == ',') {
                res_array.add(cur);
                cur = "";
            } else {
                cur += str_array[i];
            }
        }
        return res_array;
    }

    private static int Calculate(ArrayList<String> array)
            throws RuntimeException {
        int res;
        Stack<String> s = new Stack<String>();
        s.push(array.get(0));
        for (int i = 1; i < array.size(); ++i) {
            if (array.get(i).equals("+") || array.get(i).equals("-")
                    || array.get(i).equals("/") || array.get(i).equals("*")) {
                if (s.size() >= 2) {
                    int b = Integer.parseInt(s.pop());
                    int a = Integer.parseInt(s.pop());
                    int cur = 0;
                    if (array.get(i).equals("+")) {
                        cur = a + b;
                        if ((a > 0 && b > 0 && cur < 0)
                                || (a < 0 && b < 0 && cur > 0)) {
                            throw new RuntimeException("Integer overflow");
                        }
                        s.push(Integer.toString(cur));
                    }
                    if (array.get(i).equals("-")) {
                        cur = a - b;
                        if ((a > 0 && b < 0 && cur < 0)
                                || (a < 0 && b > 0 && cur > 0)) {
                            throw new RuntimeException("Integer overflow");
                        }
                        s.push(Integer.toString(cur));
                    }
                    if (array.get(i).equals("/")) {
                        if (b == 0) {
                            throw new RuntimeException("Division by 0");
                        }
                        cur = a / b;
                        s.push(Integer.toString(cur));
                    }
                    if (array.get(i).equals("*")) {
                        cur = a * b;
                        if (cur / a != b) {
                            throw new RuntimeException("Integer overflow");
                        }
                        s.push(Integer.toString(cur));
                    }
                }
            } else {
                s.push(array.get(i));
            }
        }
        res = Integer.parseInt(s.pop());
        return res;
    }
}
