package ru.fizteh.fivt.students.nikitaAntonov.stringformatter;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Formatter;

import org.junit.*;
import org.junit.rules.ExpectedException;

import ru.fizteh.fivt.format.FormatterException;

public class UnitTests {

    public class TestClass {
        public final double fieldDouble = 123.4;
        public BigInteger fieldBig = new BigInteger(
                "123456789987654321123456789");
        private final double fieldPrivateDouble = 567.8;
        protected final double fieldProtectedDouble = 9.1;
        public Integer fieldNullInteger = null;
    }

    class ChildClass extends TestClass {
        public int fieldPublicInt = 1234;
    }

    public static ru.fizteh.fivt.format.StringFormatter formatter;
    public static String testString;

    @Before
    public void init() {
        formatter = new StringFormatterFactory().create(
                StringFormatterDoubleExtension.class.getName(),
                StringFormatterBigIntegerExtension.class.getName());
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void factoryNullPointerException() {
        thrown.expectMessage("Name of the extension class can't be null");
        StringFormatterFactory f = new StringFormatterFactory();
        ru.fizteh.fivt.format.StringFormatter ff = f.create(
                StringFormatterDoubleExtension.class.getName(), null);
    }

    @Test
    public void factoryNullParamList() {
        thrown.expectMessage("Extensions list mustn't be null");
        StringFormatterFactory f = new StringFormatterFactory();
        ru.fizteh.fivt.format.StringFormatter ff = f.create(null);

    }
    
    @Test
    public void factoryGetFormatterFromHash() {
        StringFormatterFactory f = new StringFormatterFactory();
        StringFormatter a = (StringFormatter) f
                .create(StringFormatterDoubleExtension.class.getName());
        StringFormatter b = (StringFormatter) f
                .create(StringFormatterDoubleExtension.class.getName());

        assertEquals(a.extensions.get(0), b.extensions.get(0));
    }

    @Test
    public void factoryIncorrectNameOfFormatter() {
        thrown.expectMessage("Can't create instance of blahblah");

        StringFormatterFactory f = new StringFormatterFactory();
        StringFormatter a = (StringFormatter) f.create("blahblah");
    }

    @Test
    public void simpleFormat() {
        String result = formatter.format("Test {{ {0}, {1}, }} {{, {2} }}{{}}",
                1, 2, 3);
        Assert.assertEquals(result, "Test { 1, 2, } {, 3 }{}");
    }

    @Test
    public void formatterNullPointer() {
        thrown.expectMessage("exList == null");
        StringFormatter f = new StringFormatter(null);
    }

    @Test
    public void formatterNullFormat() {
        thrown.expectMessage("Format musn't be null");
        formatter.format(null, 1, 2, 3);
    }

    @Test
    public void formatterNothingToDoHere() {
        String result = formatter.format("Test", 1, 2, 3);
        Assert.assertEquals(result, "Test");
    }

    @Test
    public void formatterBracketTest1() {
        thrown.expectMessage("Unexpected opened bracket");
        String result = formatter.format("blah { Test", 1, 2, 3);
    }

    @Test
    public void formatterBracketTest2() {
        thrown.expectMessage("Unexpected opened bracket");
        String result = formatter.format("blah Test {", 1, 2, 3);
    }

    @Test
    public void formatterBracketTest3() {
        thrown.expectMessage("Unexpected closed bracket");
        String result = formatter.format("blah {0} } Test", 1, 2, 3);
    }

    @Test
    public void formatterBracketTest4() {
        thrown.expectMessage("Unexpected closed bracket");
        String result = formatter.format("blah Test}", 1, 2, 3);
    }

    @Test
    public void extractorTest1() {
        class TestClass {
            public int a = 10;
        }

        class TestClassSon extends TestClass {
            public int b = 20;
        }

        String result = formatter.format("Test {0.a}, {0.b}",
                new TestClassSon());
        assertEquals(result, "Test 10, 20");
    }

    @Test
    public void extractorFieldNotFound() {
        class TestClass {
            public int a = 10;
        }

        String result = formatter.format("Test {0.a}, {0.b}", new TestClass());
        Assert.assertEquals("Test 10, ", result);
    }

    @Test
    public void moreComplexExctractor() {
        class TestClassA {
            public int a = 10;
        }

        class TestClassB {
            TestClassA b = new TestClassA();
        }

        String result = formatter.format("Test {0.b.a}", new TestClassB());
        Assert.assertEquals("Test 10", result);

    }

    @Test
    public void otherExtractorError() {
        class TestClass {
            public Object a = null;
        }

        String result = formatter.format("Test {0.a.b}", new TestClass());
        Assert.assertEquals("Test ", result);
    }

    @Test
    public void formatTest1() {
        Formatter format = new Formatter();
        String result = formatter.format("{0:.2f}", 3.1415926);
        Assert.assertEquals(format.format("%.2f", 3.1415926).toString(), result);
        format.close();
    }

    @Test
    public void formatTest2() {
        String result = formatter.format("{0:04d}", new BigInteger("31"));
        Assert.assertEquals("0031", result);
    }

    @Test
    public void noFormatter() {
        Calendar c = Calendar.getInstance();
        thrown.expectMessage("There is no good formatter for class "
                + c.getClass().getName());
        String result = formatter.format("{0:04d}", c);
    }

    @Test
    public void formatterAndNull() {
        String result = formatter.format("test{0}test{1}test{2}", null, null,
                null);
        assertEquals("testtesttest", result);
    }

    @Test
    public void incorrectPattern() {
        thrown.expectMessage("Incorrect pattern: \".2f\"");
        String result = formatter.format("{0:.2f}", new BigInteger("31"));
    }

    @Test
    public void emptyPattern() {
        thrown.expectMessage("Pattern must be non empty string");
        String result = formatter.format("{0:}", new BigInteger("31"));
    }

    @Test
    public void nullPatternInExt() {
        
        thrown.expectMessage("Pattern must be non empty string");
        StringFormatterDoubleExtension ext = new StringFormatterDoubleExtension();
        Double a = 0.0;
        ext.format(new StringBuilder(), a, null);
    }
    
    @Test
    public void nullBufferInExt() {
        
        thrown.expectMessage("Buffer == null");
        StringFormatterDoubleExtension ext = new StringFormatterDoubleExtension();
        Double a = 0.0;
        ext.format(null, a, ".2");
    }
    
    @Test
    public void nullObjectInExt() {
        
        thrown.expectMessage("Null isn't correct object to format");
        StringFormatterDoubleExtension ext = new StringFormatterDoubleExtension();
        ext.format(new StringBuilder(), null, ".2");
    }
    
    @Test
    public void formatterNullBuffer() {
        thrown.expectMessage("Buffer musn't be null");
        formatter.format((StringBuilder) null, "Test {0}", 1);
    }
    
    @Test
    public void formatterNullArgs() {
        String result = formatter.format("test{0}test", null);
        Assert.assertEquals("testtest", result);
    }
    
    @Test 
    public void formatterIncorrectIndex1() {
        thrown.expectMessage("Incorrect number: blah");
        formatter.format("test{blah}", 1);
    }
    
    @Test 
    public void formatterIncorrectIndex2() {
        thrown.expectMessage("Incorrect number: blah");
        formatter.format("test{blah.blah}", 1);
    }
    
    @Test 
    public void formatterIncorrectIndex3() {
        thrown.expectMessage("Index 5 is out of range");
        formatter.format("test{5}", 1);
    }
    
    @Test 
    public void formatterIncorrectIndex4() {
        thrown.expectMessage("Index 5 is out of range");
        formatter.format("test{5.blah}", 1);
    }
    
    @Test 
    public void formatterIncorrectIndex5() {
        thrown.expectMessage("Index 2 is out of range");
        formatter.format("test{2}", 1);
    }
    
    @Test 
    public void formatterIncorrectIndex6() {
        thrown.expectMessage("Index 2 is out of range");
        formatter.format("test{2.blah}", 1);
    }
    
    @Test
    public void formatterIncorrectIndex7() {
        thrown.expectMessage("Incorrect number: +0");
        formatter.format("{+0}", "1");
    }
    
    @Test
    public void formatterFormatingOfNull() {
        thrown.expectMessage("Null can't be formatted");
        formatter.format("{0:f}", (Object) null);
    }
    
    @Test
    public void formatterEmptyFieldName() {
        thrown.expectMessage("Field of class can't have empty name");
        formatter.format("{0.}", 1);
    }
    

}

