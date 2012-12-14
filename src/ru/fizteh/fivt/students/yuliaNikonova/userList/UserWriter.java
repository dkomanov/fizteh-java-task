package ru.fizteh.fivt.students.yuliaNikonova.userList;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.students.yuliaNikonova.common.Utils;

public class UserWriter {

    public static void writeUsers(ArrayList<User> usersList, File xmlFile) {
	XmlBinder<User> binder = new XmlBinder<User>(User.class);
	FileWriter fileWriter = null;
	try {
	    fileWriter = new FileWriter(xmlFile);
	    XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance()
		    .createXMLStreamWriter(fileWriter);
	    xmlWriter.writeStartElement("users");
	    for (User u : usersList) {
		xmlWriter.writeStartElement("user");
		binder.writeObject(u, xmlWriter);
		xmlWriter.writeEndElement();
	    }
	    xmlWriter.writeEndElement();
	} catch (Throwable cause) {
	    throw new RuntimeException("Something bad occured during writing",
		    cause);
	} finally {
	    Utils.close(fileWriter);
	}
    }
}
