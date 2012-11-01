package ru.fizteh.fivt.students.mysinYurii.calculator;

/*
 * Author: Mysin Yurii
 *
 * Group: 196
 * 
 */

public class Expression {
    String s;

    Expression(String expression) {
        s = expression;
    }

    // если есть некорректные символы то возвращает первый из них
    // иначе возвращает пробел
    public char correctSymbols() {
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            boolean digC = Character.isDigit(c);
            boolean spC = Character.isWhitespace(c);
            boolean opC = (c == '+') || (c == '-') || (c == '*') || (c == '/');
            boolean brC = (c == '(') || (c == ')');
            if (!digC && !opC && !brC && !spC) {
                return c;
            }
        }
        return ' ';
    }

    public int giveMeArgument(int operandIndex) {
        int i = operandIndex;
        ++i;
        int depth = 0;
        boolean noNumbers = true;
        while ((i < s.length())
                && (((s.charAt(i) != '+') && (s.charAt(i) != '-'))
                        || (depth != 0) || noNumbers)) {
            if (s.charAt(i) == '(') {
                ++depth;
                ++i;
                continue;
            }
            if (s.charAt(i) == ')') {
                --depth;
                ++i;
                continue;
            }
            if (noNumbers)
                noNumbers = !(Character.isDigit(s.charAt(i)));
            ++i;
        }
        return i;
    }

    public int result() throws CalculatorException {
        s = s.trim();
        if (s.isEmpty()) {
            throw new CalculatorException("Incorrect input data");
        }
        int depth = 0;
        int prevOperatorPos = -1;
        long res = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '(') {
                ++depth;
                continue;
            }
            if (s.charAt(i) == ')') {
                --depth;
                continue;
            }
            if ((depth == 0) && (s.charAt(i) == '+')) {
                int j = i - 1;
                boolean unary = true;
                while (j >= 0) {
                    if (s.charAt(j) != ' ') {
                        if ((Character.isDigit(s.charAt(j)))
                                || s.charAt(j) == ')') {
                            unary = false;
                        }
                        break;
                    }
                    --j;
                }
                if (unary) {
                    continue;
                }
                j = i;
                ++i;
                int end = giveMeArgument(j);
                i = end;
                if (prevOperatorPos == -1 && j != 0) {
                    String newSummand = s.substring(prevOperatorPos + 1, j);
                    Expression exp = new Expression(newSummand);
                    res += exp.result();
                    if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
                        throw new CalculatorException("Overflow");
                    }
                }
                if (prevOperatorPos == -1 && j == 0) {
                    int endPosition = giveMeArgument(0);
                    String newSummand = s.substring(prevOperatorPos + 2,
                            endPosition);
                    Expression exp = new Expression(newSummand);
                    prevOperatorPos = j;
                    res += exp.result();
                    if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
                        throw new CalculatorException("Overflow");
                    }
                    --i;
                    continue;
                }
                prevOperatorPos = j;
                String newSummand = s.substring(prevOperatorPos + 1, i);
                Expression newExp = new Expression(newSummand);
                res += newExp.result();
                if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
                    throw new CalculatorException("Overflow");
                }
                --i;
                continue;
            }

            if ((depth == 0) && (s.charAt(i) == '-')) {
                int j = i - 1;
                boolean unary = true;
                while (j >= 0) {
                    if (s.charAt(j) != ' ') {
                        if (Character.isDigit(s.charAt(j))
                                || s.charAt(j) == ')') {
                            unary = false;
                        }
                        break;
                    }
                    --j;
                }
                if (unary) {
                    continue;
                }
                j = i;
                ++i;
                int endPosition = giveMeArgument(j);
                i = endPosition;
                if (prevOperatorPos == -1 && j != 0) {
                    String newSummand = s.substring(prevOperatorPos + 1, j);
                    Expression exp = new Expression(newSummand);
                    prevOperatorPos = j;
                    res += exp.result();
                }
                if (prevOperatorPos == -1 && j == 0) {
                    String newSummand = s.substring(prevOperatorPos + 2,
                            endPosition);
                    Expression exp = new Expression(newSummand);
                    prevOperatorPos = j;
                    res -= exp.result();
                    --i;
                    continue;
                }
                prevOperatorPos = j;
                String newSummand = s.substring(prevOperatorPos + 1,
                        endPosition);
                Expression newExp = new Expression(newSummand);
                res -= newExp.result();
                if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
                    throw new CalculatorException("Overflow");
                }
                --i;
            }
        }

        if (prevOperatorPos != -1)
            return (int) res;

        depth = 0;
        for (int i = s.length() - 1; i >= 0; --i) {
            if (s.charAt(i) == '(') {
                ++depth;
                continue;
            }
            if (s.charAt(i) == ')') {
                --depth;
                continue;
            }
            if ((depth == 0) && (s.charAt(i) == '*')) {
                String left = s.substring(0, i);
                String right = s.substring(i + 1);
                Expression leftExpression = new Expression(left);
                Expression rightExpression = new Expression(right);
                res = leftExpression.result() * rightExpression.result();
                if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
                    throw new CalculatorException("Overflow");
                }
                return (int) res;
            }

            if ((depth == 0) && (s.charAt(i) == '/')) {
                String left = s.substring(0, i);
                String right = s.substring(i + 1);
                Expression leftExpression = new Expression(left);
                Expression rightExpression = new Expression(right);
                res = leftExpression.result() / rightExpression.result();
                if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
                    throw new CalculatorException("Overflow");
                }
                return (int) res;
            }
        }
        int bracketPosRight = s.lastIndexOf(')');
        int bracketPosLeft = s.indexOf('(');
        if (s.charAt(0) == '+') {
            String newArg = s.substring(1);
            Expression number = new Expression(newArg);
            return number.result();
        }
        if (s.charAt(0) == '-') {
            String newArg = s.substring(1);
            Expression number = new Expression(newArg);
            return -number.result();
        }
        if ((bracketPosRight == -1) && (bracketPosLeft == -1)) {
            int i = 0;
            while ((i < s.length()) && (!Character.isDigit(s.charAt(i)))) {
                ++i;
            }
            int j = i;
            while ((j < s.length()) && Character.isDigit(s.charAt(j))) {
                ++j;
            }
            for (int z = j; z < s.length(); ++z) {
                if (Character.isDigit(s.charAt(z))) {
                    throw new CalculatorException("Incorrect input data");
                }
            }
            String number = s.substring(i, j);
            try {
                res = Integer.parseInt(number);
            } catch (IllegalArgumentException e) {
                throw new CalculatorException("Overflow");
            }
            if (res < Integer.MIN_VALUE || res > Integer.MAX_VALUE) {
                throw new CalculatorException("Overflow");
            }
            return (int) res;
        }
        if ((bracketPosRight != -1) && (bracketPosLeft != -1)) {
            char[] temp = s.toCharArray();
            temp[bracketPosRight] = ' ';
            temp[bracketPosLeft] = ' ';
            s = String.valueOf(temp);
            return result();
        }

        throw new CalculatorException("Incorrect input data");
    }
}
