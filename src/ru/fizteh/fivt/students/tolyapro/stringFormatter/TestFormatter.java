package ru.fizteh.fivt.students.tolyapro.stringFormatter;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.fizteh.fivt.format.FormatterException;

public class TestFormatter {

	static ru.fizteh.fivt.students.tolyapro.stringFormatter.StringFormatter formatter = new StringFormatterFactory()
			.create(DoubleFormatter.class.getName(),
					BigIntegerFormat.class.getName());

	static public class TestDouble {
		private final double d1 = 1;
		protected double d2 = 2;
		public final double d3 = 3;
		public double d4 = 4;
	}

	static public class TestDoubleChild extends TestDouble {
		public final double d3 = -3;
		private final double d5 = 0.55555;
	}

	TestDouble testDouble;
	TestDoubleChild testDoubleChild;
	String result;

	@Before
	public void init() {
		testDouble = new TestDouble();
		testDoubleChild = new TestDoubleChild();
		String result = new String();
	}

	@Test(expected = FormatterException.class)
	public void testBadPatternDouble() {
		double d = 100500;
		formatter.format("{0:...}", d);
	}

	@Test(expected = FormatterException.class)
	public void testBadPatternBigInteger() {
		BigInteger b = new BigInteger("100500");
		formatter.format("{0:...}", b);
	}

	@Test(expected = FormatterException.class)
	public void testNonDouble() {
		String s = "Bad string";
		formatter.format("{0}", s);

	}

	@Test(expected = FormatterException.class)
	public void testBrackets1() {
		formatter.format("{0", 0);
	}

	@Test(expected = FormatterException.class)
	public void testBrackets2() {
		formatter.format("{}0", 0);
	}

	@Test(expected = FormatterException.class)
	public void testBrackets3() {
		formatter.format("0}", 0);
	}

	@Test(expected = FormatterException.class)
	public void testNonExistingFieldsParent() {
		formatter.format("{0}.d", testDouble);
	}

	@Test(expected = FormatterException.class)
	public void testNonExistingFieldsChild() {
		formatter.format("{0}.d", testDoubleChild);
	}

	@Test
	public void correctnessTests() {

		result = formatter.format("{0.d5}", testDoubleChild);
		Assert.assertEquals("0.55555", result);

		result = formatter.format("{0.d4}", testDoubleChild);
		Assert.assertEquals("4.0", result);

		result = formatter.format("{{", testDoubleChild);
		Assert.assertEquals("{", result);

	}

}
