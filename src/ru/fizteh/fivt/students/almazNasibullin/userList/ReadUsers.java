package ru.fizteh.fivt.students.almazNasibullin.userList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.fizteh.fivt.students.almazNasibullin.xmlBinder.XmlBinder;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.students.almazNasibullin.IOUtils;

/**
 * 9.12.12
 * @author almaz
 */

public class ReadUsers {

    public List<User> readUsers(File f) {
        List<User> result = new ArrayList<User>();
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
            if (!doc.getDocumentElement().getTagName().equals("users")) {
                throw new RuntimeException("Incorrect root element");
            }
            NodeList nl = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i) {
                Node n = nl.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element)n;
                    if (el.getTagName().equals("user")) {
                        result.add((User)binder.deserializeObject(el, User.class));
                    }
                }
            }
        } catch (Throwable cause) {
            throw new RuntimeException("Something bad occured during deserialization",
                    cause);
        }
        return result;
    }
}
