package ru.fizteh.fivt.students.altimin.calculator;

import java.util.Vector;

public class Calculator
{
    Calculator(String expression) {
        this.expression = expression;
    }
    private String expression;

    private void check(long value) throws ArithmeticException {
        if (!(Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE))
            throw new ArithmeticException("Integer overflow");
    }

    private int safeAddition(int lhs, int rhs) throws ArithmeticException {
        long result = (long) lhs + (long) rhs;
        check(result);
        return (int) result;
    }

    private int safeSubtraction(int lhs, int rhs) throws ArithmeticException {
        long result = (long) lhs - (long) rhs;
        check(result);
        return (int) result;
    }

    private int safeMultiplication(int lhs, int rhs) throws ArithmeticException {
        long result = (long) lhs * (long) rhs;
        check(result);
        return (int) result;
    }

    private int safeDivision(int lhs, int rhs) throws ArithmeticException {
        if (rhs == 0) throw new ArithmeticException("Division by zero");
        if (lhs == -Integer.MIN_VALUE && rhs == -1)
            throw new ArithmeticException("It's impossible to divide -2^31 by -1 and get integer result. You're unlucky");
        return lhs / rhs;
    }

    private int calculateSubtreeExpression(SyntaxTree.SyntaxTreeNode node) throws  ArithmeticException, ParseException {
        if (node == null) return 0;
        if (node.token.tokenType == TokenType.INTEGER) {
            return Integer.parseInt(node.token.value);
        }
        if (node.token.tokenType == TokenType.SIGN_PLUS)
            return safeAddition(calculateSubtreeExpression(node.left), calculateSubtreeExpression(node.right));
        if (node.token.tokenType == TokenType.SIGN_MINUS)
            return safeSubtraction(calculateSubtreeExpression(node.left), calculateSubtreeExpression(node.right));
        if (node.token.tokenType == TokenType.SIGN_MULTIPLY)
            return safeMultiplication(calculateSubtreeExpression(node.left), calculateSubtreeExpression(node.right));
        if (node.token.tokenType == TokenType.SIGN_DIVIDE)
            return safeDivision(calculateSubtreeExpression(node.left), calculateSubtreeExpression(node.right));
        throw new ParseException("Unexpected token type when calculating expression value");
    }

    public int calculate() throws ParseException, ArithmeticException {
        TokenParser tokenParser = new TokenParser(expression);
        Vector<Token> parsedTokens = tokenParser.parse();
        SyntaxTreeParser syntaxTreeParser = new SyntaxTreeParser(parsedTokens);
        SyntaxTree syntaxTree = syntaxTreeParser.parse();
        return calculateSubtreeExpression(syntaxTree.rootNode);
    }
}
