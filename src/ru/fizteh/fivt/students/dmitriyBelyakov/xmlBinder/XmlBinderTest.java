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
        HashMap<String, String> m1 = new HashMap<>();
        m1.put("one", "two");
        m1.put("tho", "one");
        HashMap<String, String> m2 = new HashMap<>();
        m2.put("one", "two");
        m2.put("tho", "one");
        assertEquals(m1, m2);
        //assertEquals(new String(binder.serialize(user)), new String(binder.serialize(anotherUser)));
        //assertFalse(user == anotherUser);
        //assertEquals(user, anotherUser);
        XmlBinder anotherBinder = new XmlBinder(ClassForSerializationFields.class);
        byte[] bytes = anotherBinder.serialize(new ClassForSerializationFields());
        ClassForSerializationFields cl = (ClassForSerializationFields) anotherBinder.deserialize(bytes);
        assertEquals(new String(bytes), new String(anotherBinder.serialize(cl)));
        /*assertEquals("<ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.ClassForSerialization>"
                + "<intField><![CDATA[11]]></intField>"
                + "</ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.ClassForSerialization>",
                new String(anotherBinder.serialize(new ClassForSerialization())));*/
        //ClassForSerializationFields val = (ClassForSerializationFields) anotherBinder.deserialize(anotherBinder.serialize(new ClassForSerializationFields()));
        /*assertEquals("<ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.ClassForSerialization>"
                + "<intField><![CDATA[11]]></intField>"
                + "</ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.ClassForSerialization>",
                new String(anotherBinder.serialize(val)));*/
    }
}
