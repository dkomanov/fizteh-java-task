package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import com.sun.tools.corba.se.idl.toJavaPortable.StringGen;
import org.junit.Assert;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import org.junit.Test;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

import java.util.HashMap;

public class XmlBinderTest extends Assert {
    @Test(expected = RuntimeException.class)
    public void testValueNullPointer() {
        new XmlBinder(User.class).serialize(null);
    }

    @Test(expected = RuntimeException.class)
    public void testIncorrectTypeOfValue() {
        new XmlBinder(User.class).serialize(new String("Hello, world!"));
    }

    @Test(expected = RuntimeException.class)
    public void testRecursiveClass() {
        new XmlBinder(BadClassForSerialization.class).serialize(new BadClassForSerialization());
    }

    @Test
    public void testXmlBuilder() {
        XmlBinder binder = new XmlBinder(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] btes = binder.serialize(user);
        User anotherUser = (User) binder.deserialize(btes);
        assertFalse(user == anotherUser);
        assertEquals(user, anotherUser);
        XmlBinder anotherBinder = new XmlBinder(ClassForSerializationFields.class);
        ClassForSerializationFields classObject = new ClassForSerializationFields();
        byte[] bytes = anotherBinder.serialize(classObject);
        ClassForSerializationFields anotherClass = (ClassForSerializationFields) anotherBinder.deserialize(bytes);
        assertFalse(anotherClass == classObject);
        assertEquals(classObject, anotherClass);
        XmlBinder yetAnotherBinder = new XmlBinder(ClassForSerializationMethods.class);
        ClassForSerializationMethods classMethods = new ClassForSerializationMethods();
        classMethods.setSomething(true);
        byte[] b = yetAnotherBinder.serialize(classMethods);
        ClassForSerializationMethods clMethods = (ClassForSerializationMethods) yetAnotherBinder.deserialize(b);
    }
}
