package ru.fizteh.fivt.students.altimin.calculator;

import java.util.Vector;

public class TokenParser {
    private String expression;
    private int currentPosition;
    TokenParser(String expression) throws ParseException {
        expression.replace(" ", "");
        if (expression.contains(".")) throw new ParseException("Found illegal symbol '.'");
        this.expression = expression + ".";
    }

    private Token getNextToken() throws ParseException {
        int oldPosition = currentPosition;
        switch (expression.charAt(currentPosition)) {
            case '+': currentPosition ++;
                        return new Token(TokenType.SIGN_PLUS, expression.substring(oldPosition, oldPosition + 1));
            case '-': currentPosition ++;
                        return new Token(TokenType.SIGN_MINUS, expression.substring(oldPosition, oldPosition + 1));
            case '*': currentPosition ++;
                        return new Token(TokenType.SIGN_MULTIPLY, expression.substring(oldPosition, oldPosition + 1));
            case '/': currentPosition ++;
                        return new Token(TokenType.SIGN_DIVIDE, expression.substring(oldPosition, oldPosition + 1));
            case '.': currentPosition ++;
                        return new Token(TokenType.END, expression.substring(oldPosition, oldPosition + 1));
            case '(': currentPosition ++;
                return new Token(TokenType.OPEN_BRACKET, expression.substring(oldPosition, oldPosition + 1));
            case ')': currentPosition ++;
                return new Token(TokenType.CLOSE_BRACKET, expression.substring(oldPosition, oldPosition + 1));
            default:
                if (!Character.isDigit(expression.charAt(currentPosition)))
                    throw new ParseException("Unknown symbol at position " + currentPosition);
                while (Character.isDigit(expression.charAt(currentPosition)))
                    currentPosition ++;
                return new Token(TokenType.INTEGER, expression.substring(oldPosition, currentPosition));
        }
    }

    public Vector<Token> parse() throws ParseException {
        currentPosition = 0;
        Vector<Token> parsedTokens = new Vector<Token>();
        Token currentToken;
        do
        {
            currentToken = getNextToken();
            parsedTokens.add(currentToken);
        }
        while (currentToken.tokenType != TokenType.END);
        return parsedTokens;
    }
}
