package ru.fizteh.fivt.students.yuliaNikonova.userList;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import ru.fizteh.fivt.bind.test.User;

public class UsersReader {

    public ArrayList<User> readUsers(File f) {
	ArrayList<User> result = new ArrayList<User>();
	XmlBinder<User> binder = new XmlBinder<User>(User.class);
	try {
	    Document doc = DocumentBuilderFactory.newInstance()
		    .newDocumentBuilder().parse(f);
	    if (!doc.getDocumentElement().getTagName().equals("users")) {
		throw new RuntimeException("Incorrect root element");
	    }
	    NodeList nl = doc.getDocumentElement().getChildNodes();
	    for (int i = 0; i < nl.getLength(); ++i) {
		Node n = nl.item(i);
		if (n.getNodeType() == Node.ELEMENT_NODE) {
		    Element el = (Element) n;
		    if (el.getTagName().equals("user")) {
			result.add((User) binder.readObject(el, User.class));
		    }
		}
	    }
	} catch (Throwable cause) {
	    throw new RuntimeException("Something bad occured during reading",
		    cause);
	}
	return result;
    }
}
