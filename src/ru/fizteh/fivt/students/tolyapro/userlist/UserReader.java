package ru.fizteh.fivt.students.tolyapro.userlist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.students.tolyapro.xmlBinder.*;

public class UserReader {
    File file;

    public UserReader(File file) {
        // TODO Auto-generated constructor stub
        this.file = file;

    }

    ArrayList<User> read() {
        ArrayList<User> result = new ArrayList<User>();
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(file);
            if (!doc.getDocumentElement().getTagName().equals("users")) {
                throw new RuntimeException("Incorrect root element");
            }
            NodeList nl = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i) {
                Node n = nl.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) n;
                    if (el.getTagName().equals("user")) {
                        result.add((User) binder.deserializeMe(el, User.class));
                    }
                }
            }
        } catch (Throwable cause) {
            throw new RuntimeException(cause);
        }
        return result;
    }

}
