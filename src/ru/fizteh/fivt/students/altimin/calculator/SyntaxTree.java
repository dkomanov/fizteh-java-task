package ru.fizteh.fivt.students.altimin.calculator;

public class SyntaxTree {
    static public class SyntaxTreeNode {
        public Token token;
        public SyntaxTreeNode left;
        public SyntaxTreeNode right;

        SyntaxTreeNode(Token token) {
            this.token = token;
        }

        SyntaxTreeNode(Token token, SyntaxTreeNode left, SyntaxTreeNode right) {
            this.token = token;
            this.left = left;
            this.right = right;
        }
    }

    public SyntaxTreeNode rootNode;

    public SyntaxTree(SyntaxTreeNode rootNode) {
        this.rootNode = rootNode;
    }
}
