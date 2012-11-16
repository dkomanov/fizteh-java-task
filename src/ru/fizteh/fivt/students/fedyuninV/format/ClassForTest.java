package ru.fizteh.fivt.students.fedyuninV.format;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class ClassForTest {
    public final float height = 100f;
    public byte[] params = {12, 13, 14, 15};
    private final int deep = 100;
    protected final double dispersion = 0.12;
    public Integer x = null;
    private Integer y = null;
}

class ChildForTest extends ClassForTest{
    public int width = 1000;
}
