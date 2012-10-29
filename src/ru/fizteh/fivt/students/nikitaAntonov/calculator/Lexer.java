package ru.fizteh.fivt.kogemrka;

/*
 * Вспомогательный класс, занимающийся разбиением строки
 * на лексемы
 */
class Lexer {
	public Lexem lex;
	
	private char expr[];
	private String source;
	public int pos;
	public int endpos;
	
	public Lexer(String src) {
		source = src;
		expr = src.toCharArray();
		endpos = expr.length;
		pos = 0;
		lex = new Lexem(Lexem.Type.BEGIN);
	}
	
	public void nextLexem() throws Exception {
		skipWhitespaces();
		
		if (pos >= endpos) {
			lex = new Lexem(Lexem.Type.END);
			return;
		}
			
		switch (expr[pos]) {
		case '(':
			lex = new Lexem(Lexem.Type.BRACKET_OPEN);
			break;
		case ')':
			lex = new Lexem(Lexem.Type.BRACKET_CLOSE);
			break;
		case '+':
		case '-':
			if (lex.type == Lexem.Type.BEGIN
					|| lex.type == Lexem.Type.BRACKET_OPEN
					|| lex.type == Lexem.Type.OP_LEVEL2
					|| lex.type == Lexem.Type.OP_LEVEL1) {
				lex = getNum();
			} else {
				lex = new Lexem(expr[pos]);
			}
			break;
		case '*':
		case '/':
		case '%':
			lex = new Lexem(expr[pos]);
			break;
		default:
			if (Character.isDigit(expr[pos])) {
				lex = getNum();
			} else {
				throw new Exception("Incorrect symbol at position " + pos);
			}
			break;
		}
		
		if (lex.type != Lexem.Type.NUM) {
			++pos;
		}
	}
	
	private void skipWhitespaces() {
		while ((pos < endpos) && Character.isWhitespace(expr[pos])) {
			++pos;
		}
	}
	
	private Lexem getNum() throws Exception {
		boolean negative = false;
		int number = 0;
		int count = 0;
		
		if (expr[pos] == '-') {
			negative = true;
			++pos;
		} else if (expr[pos] == '+') {
			++pos;
		}
		
		skipWhitespaces();
		
		while (pos < endpos && Character.isDigit(expr[pos])) {
			++count;
			++pos;
		}
		
		if (count == 0) {
			throw new Exception("Incorrect number at position " + pos);
		} else {
			try
			{
				number = Integer.parseInt(source.substring(pos - count, pos));
			} catch (NumberFormatException e) {
				throw new Exception("Number at position " + (pos - count) + " is too large");
			}
			return new Lexem(negative ? -number : number);
		}
	}
}


class Lexem {
	/* XXX Очень не хватает union`ов из C/C++
	 * для реализации классов такого рода, приходится делать некрасиво,
	 * дабы не городить иерархию классов и приведение типов на каждом шагу.
	 */
	
	public enum Type {
		BRACKET_OPEN, BRACKET_CLOSE, OP_LEVEL1, OP_LEVEL2, NUM, END, BEGIN
	}
	
	public enum Op1Type	{
		PLUS, MINUS
	}
	
	public enum Op2Type {
		MULT, DIV, MOD
	}
		
	public Type type;
	public int number;
	public Op1Type type1;
	public Op2Type type2;
	
	public Lexem(int num) {
		type = Type.NUM;
		number = num;
	}
	
	public Lexem(Type t) {
		type = t;
	}
	
	public Lexem(char c) throws Exception {
		switch (c) {
		case '+':
			type = Type.OP_LEVEL1;
			type1 = Op1Type.PLUS;
			break;
		case '-':
			type = Type.OP_LEVEL1;
			type1 = Op1Type.MINUS;
			break;
		case '*':
			type = Type.OP_LEVEL2;
			type2 = Op2Type.MULT;
			break;
		case '/':
			type = Type.OP_LEVEL2;
			type2 = Op2Type.DIV;
			break;
		case '%':
			type = Type.OP_LEVEL2;
			type2 = Op2Type.MOD;
			break;
		default:
			throw new Exception("Incorrect symbol of operation: " + c);
		}
	}
}
