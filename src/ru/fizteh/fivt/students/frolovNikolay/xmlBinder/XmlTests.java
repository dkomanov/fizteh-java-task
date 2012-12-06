package ru.fizteh.fivt.students.frolovNikolay.xmlBinder;

import org.junit.*;
import org.junit.rules.ExpectedException;

import ru.fizteh.fivt.bind.AsXmlAttribute;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

public class XmlTests {
    
    private static XmlBinder<User> binder = new XmlBinder<User>(User.class);;
    
    @Rule
    public ExpectedException expt = ExpectedException.none();
    
    @Test
    public void nullPointer() {
        expt.expectMessage("null pointer");
        binder.serialize(null);
    }
    
    @BindingType(MembersToBind.GETTERS_AND_SETTERS)
    private static class asXmlAttributeTest {
        private String field;

        public String getField() {
            return field;
        }

        @AsXmlAttribute(name = "anotherFieldName")
        public void setField(String field) {
            this.field = field;
        }

        @Override
        public boolean equals(Object object) {
            return ((asXmlAttributeTest) object).getField().equals(field);
        }

        @Override
        public int hashCode() {
            return 27 * 02 * 1993 + (field == null
                                    ? 0
                                    : field.hashCode());
        }
    }

    @Test
    public void testWithAttributeMethods() {
        XmlBinder<asXmlAttributeTest> binder = new XmlBinder<asXmlAttributeTest>
                (asXmlAttributeTest.class);
        asXmlAttributeTest original = new asXmlAttributeTest();
        original.setField("need success");
        asXmlAttributeTest deserialized = binder.deserialize(binder.serialize(original));
        Assert.assertEquals(deserialized, original);
        Assert.assertTrue(original != deserialized);
    }
    
    @Test
    public void emptyString() throws Throwable {
        expt.expectMessage("this String don't have first character");
        XmlBinder.lowerFirstCharacter(new String(""));
    }
    
    @Test
    public void usersTest() {
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] bytes = binder.serialize(user);
        User deserialized = binder.deserialize(bytes);
        Assert.assertEquals(user, deserialized);
        Assert.assertTrue(user != deserialized);
    }
    
}
