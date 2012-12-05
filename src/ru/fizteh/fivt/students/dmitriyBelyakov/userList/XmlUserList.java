package ru.fizteh.fivt.students.dmitriyBelyakov.userList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.students.dmitriyBelyakov.shell.IoUtils;
import ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.XmlBinder;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class XmlUserList {
    private XmlBinder<User> binder;

    public XmlUserList() {
        binder = new XmlBinder<User>(User.class);
    }

    public ArrayList<User> loadUsers(File file) {
        ArrayList<User> users = new ArrayList<User>();
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            Element root = document.getDocumentElement();
            if (!root.getTagName().equals("users")) {
                throw new RuntimeException("Incorrect xml file.");
            }
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE && ((Element) node).getTagName().equals("user")) {
                    Element childElement = (Element) node;
                    User user = (User) binder.deserializeToValue(childElement, User.class);
                    users.add(user);
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("Cannot load data.", t);
        }
        return users;
    }

    public void saveUsers(ArrayList<User> users, File file) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);
            xmlWriter.writeStartElement("users");
            for (User user : users) {
                xmlWriter.writeStartElement("user");
                binder.serializeObjectToWriter(user, xmlWriter);
                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        } catch (Throwable t) {
            throw new RuntimeException("Cannot save data.", t);
        } finally {
            IoUtils.close(writer);
        }
    }
}