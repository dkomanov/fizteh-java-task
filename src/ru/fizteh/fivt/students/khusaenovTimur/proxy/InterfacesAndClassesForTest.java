package ru.fizteh.fivt.students.khusaenovTimur.proxy;

/**
 * Created with IntelliJ IDEA.
 * User: Timur
 * Date: 12/8/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
interface VoidInterface{}

class VoidInterfaceClass implements VoidInterface{
    public void func() {}
}

interface InnerInterface {
    public void go();
}

class ExternalInterface implements InnerInterface {
    @Override
    public void go() {
    }
}

interface SetArrays {
    public Double[] setArray(Double[] newArray);

    public void voidMethod(Double[] newArray);
}

class ArrayTest implements SetArrays{
    @Override
    public Double[] setArray(Double[] array) {
        return array;
    }

    @Override
    public void voidMethod(Double[] newArray) {}
}

interface SetObjectArrays {
    public Object[] setArray(Object[] newArray);
}

class ObjectArrayTest implements SetObjectArrays{
    @Override
    public Object[] setArray(Object[] array) {
        return array;
    }
}
