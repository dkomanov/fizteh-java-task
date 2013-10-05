import java.io.PrintWriter;
import java.util.Scanner;

public class Calculator {
	class CalculationException extends Exception {
		private static final long serialVersionUID = -6424956785095571854L;

	}

	private Scanner in;
	private PrintWriter out;

	private enum LexType {
		PLS, MNS, MUL, DIV, OPN, CLS, NUM, END, NAN
	};

	private String[] value = { "PLS", "MNS", "MUL", "DIV", "OPN", "CLS", "NUM",
			"END", "NAN" };
	private String tSymbols = "+-*/()";

	public static void main(String[] s) {
		new Calculator().run();
	}

	private void run() {
		in = new Scanner(System.in);
		out = new PrintWriter(System.out);
		try {
			solve();
		} catch (Exception e) {
			e.printStackTrace(out);
		} finally {
			out.close();
		}
	}

	private String expStr;
	private int expPos = 0;
	private LexType curLex = LexType.NAN;
	private String lexValue;

	private boolean isNumber(String s) {

		try {
			Integer.parseInt(s, 18);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private LexType nextLex() throws CalculationException {
		if (expStr.length() == 0) {
			throw new CalculationException();
		}
		while (expPos < expStr.length() && expStr.charAt(expPos) == ' ') {
			expPos++;
		}
		if (expPos == expStr.length()) {
			return LexType.END;
		}
		char c = expStr.charAt(expPos);
		int pos = tSymbols.indexOf(c);
		if (pos != -1) {
			expPos++;
			return LexType.valueOf(value[pos]);
		}
		StringBuffer lexValueBuff = new StringBuffer("");

		while (expPos < expStr.length()
				&& !(tSymbols.indexOf(expStr.charAt(expPos)) != -1 || expStr
						.charAt(expPos) == ' ')) {
			lexValueBuff.append(expStr.charAt(expPos++));
		}

		lexValue = lexValueBuff.toString();

		if (isNumber(lexValue)) {
			return LexType.NUM;
		}
		return LexType.NAN;

	}

	private int calcMul() throws CalculationException {
		int res = 0;
		LexType sign = LexType.PLS;
		if (curLex.equals(LexType.MNS) || curLex.equals(LexType.PLS)) {
			sign = curLex;
			curLex = nextLex();
		}
		switch (curLex) {
		case OPN: {
			curLex = nextLex();
			res = calcExp();
			if (!curLex.equals(LexType.CLS)) {
				throw new CalculationException();
			}

			break;
		}
		case NUM: {
			res = Integer.valueOf(lexValue, 18);
			break;
		}
		default:
			throw new CalculationException();

		}
		curLex = nextLex();
		res *= sign.equals(LexType.MNS) ? -1 : 1;
		return res;
	}

	private int calcTerm() throws CalculationException {
		int res = calcMul();
		while (curLex.equals(LexType.DIV) || curLex.equals(LexType.MUL)) {
			LexType oper = curLex;
			curLex = nextLex();
			int next = calcTerm();
			if (oper.equals(LexType.DIV)) {
				try {
					res /= next;
				} catch (ArithmeticException e) {
					throw new CalculationException();
				}
			} else if (oper.equals(LexType.MUL)) {
				res *= next;
			} else {
				throw new CalculationException();
			}
		}
		return res;
	}

	private int calcExp() throws CalculationException {
		int res = calcTerm();
		while (curLex.equals(LexType.PLS) || curLex.equals(LexType.MNS)) {
			LexType oper = curLex;
			curLex = nextLex();
			int next = calcTerm();
			if (oper.equals(LexType.MNS)) {
				res -= next;
			} else if (oper.equals(LexType.PLS)) {
				res += next;
			}
		}
		if (!(curLex.equals(LexType.END) || curLex.equals(LexType.CLS))) {
			throw new CalculationException();
		}
		return res;

	}

	public void solve() {
		expStr = in.nextLine();
		try {
			curLex = nextLex();
			out.println(Integer.toString(calcExp(), 18).toUpperCase());
		} catch (CalculationException e) {
			out.println("bad input");
		}
	}
}