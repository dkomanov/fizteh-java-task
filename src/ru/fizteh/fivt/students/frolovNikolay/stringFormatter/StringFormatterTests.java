package ru.fizteh.fivt.students.frolovNikolay.stringFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.*;
import org.junit.rules.ExpectedException;

public class StringFormatterTests {
    
    public class TestClass {
        public int simpleValue = 27;
        public String noExtension = new String("I don't have extension.");
        public Integer notNullInteger = new Integer(10);
        public Date notNullDate = new Date();
        public Integer nullInteger = null;
        public Date nullDate = null; 
    }

    class ChildClass extends TestClass {
        public int childSimpleValue = 27;
        public Integer childNotNullInteger = new Integer(10);
        public Date childNotNullDate = new Date();
        public Integer childNullInteger = null;
        public Date childNullDate = null;
    }

    public static StringFormatter formatter;

    @Before
    public void init() {
        formatter = new StringFormatterFactory().create(DateExtension.class.getName(), IntegerExtension.class.getName());
    }

    @Rule
    public ExpectedException expt = ExpectedException.none();
    
    @Test
    public void duplicatesClasses() {
        expt.expectMessage("Have same classes: ru.fizteh.fivt.students.frolovNikolay.stringFormatter.DateExtension");
        StringFormatter incorrectFormatter = new StringFormatterFactory().create(DateExtension.class.getName()
                , IntegerExtension.class.getName(), DateExtension.class.getName());
    }
    
    @Test
    public void nullExtension() {
        expt.expectMessage("Can't create StringFormatter with these extensions");
        StringFormatter incorrectFormatter = new StringFormatterFactory().create(null);
    }

    @Test
    public void noExtension() {
        expt.expectMessage("No extension for: class java.lang.String");
        formatter.format("start {0.noExtension:d} end", new TestClass());
    }

    @Test
    public void wrongBrackets() {
        expt.expectMessage("Incorrect placement of the brackets");
        formatter.format("{{0}} {0 end", new TestClass());
    }

    @Test
    public void wrongBrackets2() {
        expt.expectMessage("Incorrect placement of the brackets");
        formatter.format("{{0}} {", new TestClass());
    }

    @Test
    public void wrongBracketsExpression() {
        expt.expectMessage("Incorrect expression in some brackets");
        formatter.format("{{{{{:bad} {0}", new TestClass());
    }

    @Test
    public void wrongBracketsExpression2() {
        expt.expectMessage("Incorrect expression in some brackets");
        formatter.format("{.firstfield.secondfield}}} {0}", new TestClass());
    }
    
    @Test
    public void wrongBracketsExpression3() {
        expt.expectMessage("Incorrect expression in some brackets");
        formatter.format("{-0}", new TestClass());
    }
    
    @Test
    public void wrongBracketsExpression4() {
        expt.expectMessage("Incorrect expression in some brackets");
        formatter.format("{0..field}", new TestClass());
    }
    
    
    @Test
    public void wrongBracketsExpression5() {
        expt.expectMessage("Incorrect expression in some brackets");
        formatter.format("{0.}", "1");
    }
 
    @Test
    public void badPattern() {
        expt.expectMessage("Bad pattern");
        formatter.format("start {{1}} {0:bad} end", new TestClass().notNullDate, new TestClass().notNullInteger);
    }
    
    @Test
    public void badPattern2() {
        expt.expectMessage("Bad pattern");
        formatter.format("{0.notNullDate:}", new TestClass());
    }

    @Test
    public void incorrectIndex() {
        expt.expectMessage("Incorrect object-index");
        formatter.format("{{1}} {2}", new ChildClass(), new TestClass());
    }
    
    @Test
    public void incorrectIndex2() {
        expt.expectMessage("Incorrect object-index");
        formatter.format("{0{1}}", new TestClass(), 2.0);
    }
    
    @Test
    public void correctFormattingTests() {
        String testString = null;
        
        testString = formatter.format("{0.simpleValue}", new TestClass());
        Assert.assertEquals(testString, "27");
    
        testString = formatter.format("succ{0.nullInteger}ess", new TestClass());
        Assert.assertEquals(testString, "success");

        testString = formatter.format("succ{0.nullDate}ess", new TestClass());
        Assert.assertEquals(testString, "success");

        testString = formatter.format("su{0.nullDate}cce{1.TestClass.nullDate}ss", new TestClass(), new ChildClass());
        Assert.assertEquals(testString, "success");
        
        testString = formatter.format("{0.noField}", new TestClass());
        Assert.assertEquals(testString, "");

        testString = formatter.format("{{{0.TestClass.nullDate}suc{0.TestClass.nullInteger}cess{0.childNullInteger}}}", new ChildClass());
        Assert.assertEquals(testString, "{success}");

        TestClass dateTest = new TestClass();
        testString = formatter.format("{0.notNullDate:yyyy-mm-dd}", dateTest);
        Assert.assertEquals(new SimpleDateFormat("yyyy-mm-dd").format(dateTest.notNullDate), testString);
    }
}