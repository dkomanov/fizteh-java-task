package ru.fizteh.fivt.students.nikitaAntonov.calculator;

/**
 * Вспомогательный класс, занимающийся разбиением строки
 * на лексемы
 * 
 * @author Антонов Никита
 */
class Lexer {
	public Lexem lex;
	public String source;
	public int pos;
	
	public Lexer(String src) {
		source = src;
		pos = 0;
		lex = new Lexem(Lexem.Type.BEGIN);
	}
	
	public void nextLexem() throws Exception {
		skipWhitespaces();
		
		if (pos >= source.length()) {
			lex = new Lexem(Lexem.Type.END);
			return;
		}
			
		switch (source.charAt(pos)) {
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
				lex = new Lexem(source.charAt(pos));
			}
			break;
		case '*':
		case '/':
		case '%':
			lex = new Lexem(source.charAt(pos));
			break;
		default:
			if (Character.isDigit(source.charAt(pos))) {
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
		while ((pos < source.length()) && Character.isWhitespace(source.charAt(pos))) {
			++pos;
		}
	}
	
	private Lexem getNum() throws Exception {
		boolean negative = false;
		int number = 0;
		int count = 0;
		
		if (source.charAt(pos) == '-') {
			negative = true;
			++pos;
		} else if (source.charAt(pos) == '+') {
			++pos;
		}
		
		skipWhitespaces();
		
		while (pos < source.length() && Character.isDigit(source.charAt(pos))) {
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