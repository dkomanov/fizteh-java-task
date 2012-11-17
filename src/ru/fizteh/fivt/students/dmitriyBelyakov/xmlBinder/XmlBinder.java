package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T> {
    XmlBinder(Class<T> clazz) {
        super(clazz);
    }

    private void serializeFieldsToWriter(XMLStreamWriter writer, int deep) {
        if(deep >= 100) {
            throw new RuntimeException("Cannot serialize.");
        }
        // TODO insert code there
    }

    @Override
    public byte[] serialize(T value) {
        String serialized;
        if (value == null) {
            throw new RuntimeException("Null pointer for serialization.");
        }
        if (!getClazz().equals(value.getClass())) {
            throw new RuntimeException("Incorrect type.");
        }
        try {
            Class clazz = value.getClass();
            BindingType annotation = (BindingType) clazz.getAnnotation(MembersToBind.class);
            boolean allFields = true;
            if(annotation != null && annotation.value() == MembersToBind.GETTERS_AND_SETTERS) {
                allFields = false;
            }
            ArrayList<Field> fields = new ArrayList<>();
            while (clazz != null) {
                Field[] tmpFields = clazz.getDeclaredFields();
                fields.addAll(Arrays.asList(tmpFields));
                clazz = clazz.getSuperclass();
            }
            StringWriter writer = new StringWriter();
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);
            String className = value.getClass().getName();
            if (className.length() == 1) {
                className = className.toLowerCase();
            } else {
                className = className.toLowerCase().charAt(0) + className.substring(1);
            }
            xmlWriter.writeStartElement(className);
            xmlWriter.writeCharacters(System.lineSeparator()); // INFO
            for (Field field : fields) {
                xmlWriter.writeStartElement(field.getName());
                field.setAccessible(true);
                if (field.get(value) != null) {
                    xmlWriter.writeCharacters(field.get(value).toString());
                } else {
                    xmlWriter.writeCharacters("null");
                }
                xmlWriter.writeEndElement();
                xmlWriter.writeCharacters(System.lineSeparator()); // INFO
            }
            xmlWriter.writeEndElement();
            serialized = writer.getBuffer().toString();
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
        System.out.println(serialized); // INFO
        return serialized.getBytes();
    }

    @Override
    public T deserialize(byte[] bytes) {
        return null;
    }
}
