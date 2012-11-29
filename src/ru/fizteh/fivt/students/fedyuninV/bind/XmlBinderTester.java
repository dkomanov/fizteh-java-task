package ru.fizteh.fivt.students.fedyuninV.bind;

import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;
import ru.fizteh.fivt.students.fedyuninV.bind.binderTestClasses.FieldsNameFail;
import ru.fizteh.fivt.students.fedyuninV.bind.binderTestClasses.LinkToItself;
import ru.fizteh.fivt.students.fedyuninV.bind.binderTestClasses.MethodsNameFail;
import ru.fizteh.fivt.students.fedyuninV.bind.binderTestClasses.PairsWithSameNameFail;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class XmlBinderTester {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void incorrectFieldsNames() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Two fields with the same names.");
        XmlBinder<FieldsNameFail> binder = new XmlBinder<FieldsNameFail>(FieldsNameFail.class);
        FieldsNameFail nameFail = new FieldsNameFail();
        byte[] bytes = binder.serialize(nameFail);
        FieldsNameFail deserialized = binder.deserialize(bytes);
    }

    @Test
    public void incorrectMethodsNames() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Incorrect annotations of methods.");
        XmlBinder<MethodsNameFail> binder = new XmlBinder<MethodsNameFail>(MethodsNameFail.class);
        MethodsNameFail nameFail = new MethodsNameFail();
        byte[] bytes = binder.serialize(nameFail);
        MethodsNameFail deserialized = binder.deserialize(bytes);
    }

    @Test
    public void incorrectPairOfMethodsNames() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Two pairs of methods with the same names.");
        XmlBinder<PairsWithSameNameFail> binder = new XmlBinder<PairsWithSameNameFail>(PairsWithSameNameFail.class);
        PairsWithSameNameFail nameFail = new PairsWithSameNameFail();
        byte[] bytes = binder.serialize(nameFail);
        PairsWithSameNameFail deserialized = binder.deserialize(bytes);
    }

    @Test
    public void linkToItselfTest() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Object contains link to itself, cannot serailize.");
        XmlBinder<LinkToItself> binder = new XmlBinder<LinkToItself>(LinkToItself.class);
        LinkToItself linkToItself = new LinkToItself();
        linkToItself.setX(1);
        linkToItself.setLink(linkToItself);
        byte[] bytes = binder.serialize(linkToItself);
        LinkToItself deserialized = binder.deserialize(bytes);
    }

    @Test
    public void userTest() {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] bytes = binder.serialize(user);
        User deserialized = binder.deserialize(bytes);
        Assert.assertEquals(user, deserialized);
        bytes = binder.serialize(deserialized);
        user = binder.deserialize(bytes);
        Assert.assertEquals(user, deserialized);
        /*
        * UserType changed to null
        * can't change
        * */
        user = new User(1, null, new UserName("first", "last"), permissions);
        bytes = binder.serialize(user);
        deserialized = binder.deserialize(bytes);
        Assert.assertEquals(user, deserialized);
        bytes = binder.serialize(deserialized);
        user = binder.deserialize(bytes);
        Assert.assertEquals(user, deserialized);
    }
}
