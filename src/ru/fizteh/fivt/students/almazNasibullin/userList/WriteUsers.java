package ru.fizteh.fivt.students.almazNasibullin.userList;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import ru.fizteh.fivt.students.almazNasibullin.xmlBinder.XmlBinder;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.students.almazNasibullin.IOUtils;

/**
 * 9.12.12
 * @author almaz
 */

public class WriteUsers {
    public void writeUsers(List<User> users, File f) {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        FileWriter fw = null;
        try {
            fw = new FileWriter(f);
            XMLStreamWriter xmlsw = XMLOutputFactory.newInstance().createXMLStreamWriter(fw);
            xmlsw.writeStartElement("users");
            for (User u : users) {
                xmlsw.writeStartElement("user");
                binder.serializeObject(u, xmlsw);
                xmlsw.writeEndElement();
            }
            xmlsw.writeEndElement();
        } catch (Throwable cause) {
            throw new RuntimeException("Something bad occured during serialization",
                    cause);
        } finally {
            IOUtils.closeOrExit(fw);
        }
    }
}
