package ru.fizteh.fivt.students.tolyapro.userlist;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamWriter;

import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.students.tolyapro.xmlBinder.XmlBinder;

public class UserWriter {
    File file;

    public UserWriter(File file) {
        this.file = file;
    }

    public void write(ArrayList<User> users) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.append("<users>\n");
            for (User user : users) {
                XmlBinder<User> binder = new XmlBinder<User>(User.class);
                fw.append(new String(binder.serialize(user)));
            }
            fw.append("\n</users>");
            fw.close();
        } catch (Throwable cause) {
            throw new RuntimeException(cause);
        }
    }
}
