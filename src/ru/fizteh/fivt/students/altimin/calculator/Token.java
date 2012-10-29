package ru.fizteh.fivt.students.altimin.calculator;

public class Token {
    TokenType tokenType;
    String value;

    public Token(TokenType tokenType, String value) {
        this.tokenType = tokenType;
        this.value = value;
    }

    boolean isSign() {
        return tokenType.equals(TokenType.SIGN_DIVIDE)
                || tokenType.equals(TokenType.SIGN_MULTIPLY)
                || tokenType.equals(TokenType.SIGN_PLUS)
                || tokenType.equals(TokenType.SIGN_MINUS);
    }

    boolean isAdditionSign() {
        return tokenType.equals(TokenType.SIGN_PLUS)
                || tokenType.equals(TokenType.SIGN_MINUS);
    }

    boolean isMultiplicationSign() {
        return tokenType.equals(TokenType.SIGN_MULTIPLY)
                || tokenType.equals(TokenType.SIGN_DIVIDE);
    }
}
