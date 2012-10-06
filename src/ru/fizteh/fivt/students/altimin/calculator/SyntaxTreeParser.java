package ru.fizteh.fivt.students.altimin.calculator;

import java.util.Vector;

/**
 * User: altimin
 * Date: 10/6/12
 * Time: 3:06 PM
 */
public class SyntaxTreeParser {
    private Vector<Token> parsedTokenList;
    private int currentPosition;

    public SyntaxTreeParser(Vector<Token> parsedTokenList) {
        this.parsedTokenList = parsedTokenList;
    }

    private SyntaxTree.SyntaxTreeNode parseFormula() throws ParseException {
        SyntaxTree.SyntaxTreeNode result = parseSummand();
        while (parsedTokenList.get(currentPosition).isAdditionSign()) {
            Token currentToken = parsedTokenList.get(currentPosition);
            currentPosition++;
            result = new SyntaxTree.SyntaxTreeNode(
                    currentToken,
                    result,
                    parseSummand());
        }
        return result;
    }

    private SyntaxTree.SyntaxTreeNode parseSummand() throws ParseException {
        SyntaxTree.SyntaxTreeNode result = parseFactor();
        while (parsedTokenList.get(currentPosition).isMultiplicationSign()) {
            Token currentToken = parsedTokenList.get(currentPosition);
            currentPosition++;
            result = new SyntaxTree.SyntaxTreeNode(currentToken, result, parseFactor());
        }
        return result;
    }

    private SyntaxTree.SyntaxTreeNode parseFactor() throws ParseException {
        Token currentToken = parsedTokenList.get(currentPosition);
        currentPosition++;
        if (currentToken.tokenType == TokenType.INTEGER) {
            return new SyntaxTree.SyntaxTreeNode(currentToken);
        }
        if (currentToken.tokenType == TokenType.OPEN_BRACKET) {
            int oldPosition = currentPosition;
            SyntaxTree.SyntaxTreeNode result = parseFormula();
            if (parsedTokenList.get(currentPosition).tokenType != TokenType.CLOSE_BRACKET) {
                throw new ParseException("No match for opening bracket at position " + oldPosition);
            }
            currentPosition++;
            return result;
        }
        if (currentToken.isAdditionSign()) { // is unary plus possible?
            return new SyntaxTree.SyntaxTreeNode(
                    currentToken,
                    new SyntaxTree.SyntaxTreeNode(new Token(TokenType.INTEGER, "0")),
                    parseFactor());
        }
        throw new ParseException("Unexpected symbol at position " + (currentPosition - 1));
    }

    public SyntaxTree parse() throws ParseException {
        currentPosition = 0;
        return new SyntaxTree(parseFormula());
    }
}
