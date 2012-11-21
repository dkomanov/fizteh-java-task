package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ru.fizteh.fivt.bind.AsXmlCdata;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;
import ru.fizteh.fivt.students.dmitriyBelyakov.shell.IoUtils;

import javax.swing.text.html.parser.DocumentParser;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.util.StreamReaderDelegate;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T> {
    XmlBinder(Class<T> clazz) {
        super(clazz);
    }

    private String firstCharToLowerCase(String str) {
        if (str.length() == 1) {
            return str.toLowerCase();
        } else {
            return str.toLowerCase().charAt(0) + str.substring(1);
        }
    }

    private String firstCharToUpperCase(String str) {
        if (str.length() == 1) {
            return str.toLowerCase();
        } else {
            return str.toUpperCase().charAt(0) + str.substring(1);
        }
    }

    private boolean isPrimitive(Class clazz) {
        return clazz.isPrimitive() || clazz.equals(Boolean.class) || clazz.equals(Byte.class)
                || clazz.equals(Character.class) || clazz.equals(Short.class)
                || clazz.equals(Integer.class) || clazz.equals(Long.class)
                || clazz.equals(Float.class) || clazz.equals(Double.class)
                || clazz.isEnum() || clazz.equals(String.class);
    }

    private ArrayList<Field> getFields(Class clazz) {
        ArrayList<Field> fields = new ArrayList<>();
        while (clazz != null) {
            Field[] tmpFields = clazz.getDeclaredFields();
            fields.addAll(Arrays.asList(tmpFields));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private ArrayList<Method> getMethods(Class clazz) {
        ArrayList<Method> methods = new ArrayList<>();
        while (clazz != null) {
            Method[] tmpMethods = clazz.getDeclaredMethods();
            HashMap<String, Method> methodsNames = new HashMap<>();
            for (Method method : tmpMethods) {
                if (method.getName().matches("get.+")) {
                    if (method.getParameterTypes().length != 0) {
                        continue;
                    }
                    String name = method.getName().replaceFirst("get", "set");
                    if (methodsNames.containsKey(name)) {
                        if (!method.getReturnType().equals(methodsNames.get(name).getParameterTypes()[0])) {
                            continue;
                        }
                        methods.add(method);
                    } else {
                        methodsNames.put(method.getName(), method);
                    }
                } else if (method.getName().matches("is.+")) {
                    if (method.getParameterTypes().length != 0 || !method.getReturnType().equals(boolean.class)) {
                        continue;
                    }
                    String name = method.getName().replaceFirst("is", "set");
                    if (methodsNames.containsKey(name)) {
                        if (!methodsNames.get(name).getParameterTypes()[0].equals(boolean.class)) {
                            continue;
                        }
                        methods.add(method);
                    } else {
                        methodsNames.put(method.getName(), method);
                    }
                } else if (method.getName().matches("set.+")) {
                    if (!method.getReturnType().equals(void.class) || method.getParameterTypes().length != 1) {
                        continue;
                    }
                    String nameGet = method.getName().replaceFirst("set", "get");
                    String nameIs = method.getName().replaceFirst("set", "is");
                    if (methodsNames.containsKey(nameGet)) {
                        if (!method.getParameterTypes()[0].equals(methodsNames.get(nameGet).getReturnType())) {
                            continue;
                        }
                        methods.add(methodsNames.get(nameGet));
                    } else if (methodsNames.containsKey(nameIs)) {
                        if (!method.getParameterTypes()[0].equals(methodsNames.get(nameIs).getReturnType())) {
                            continue;
                        }
                        methods.add(methodsNames.get(nameIs));
                    } else {
                        methodsNames.put(method.getName(), method);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    private void serializeObjectToWriter(Object value, XMLStreamWriter xmlWriter, int deep) {
        if (deep >= 100) {
            throw new RuntimeException("Cannot serialize.");
        }
        if (value == null) {
            throw new RuntimeException("Null pointer for serialization.");
        }
        try {
            Class clazz = value.getClass();
            if (isPrimitive(clazz)) {
                xmlWriter.writeCharacters(value.toString());
                return;
            }
            BindingType annotation = (BindingType) clazz.getAnnotation(BindingType.class);
            boolean allFields = true;
            if (annotation != null && annotation.value().equals(MembersToBind.GETTERS_AND_SETTERS)) {
                allFields = false;
            }
            if (allFields) {
                ArrayList<Field> fields = getFields(clazz);
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.get(value) != null) {
                        if (field.getAnnotation(AsXmlCdata.class) == null || !isPrimitive(field.get(value).getClass())) {
                            xmlWriter.writeStartElement(field.getName());
                            serializeObjectToWriter(field.get(value), xmlWriter, deep + 1);
                            xmlWriter.writeEndElement();
                        } else {
                            xmlWriter.writeCData(field.get(value).toString());
                        }
                    }
                }
            } else {
                ArrayList<Method> methods = getMethods(clazz);
                for (Method method : methods) {
                    method.setAccessible(true);
                    Object val = method.invoke(value);
                    if (val != null) {
                        if (method.getAnnotation(AsXmlCdata.class) == null || !isPrimitive(method.getReturnType())) {
                            xmlWriter.writeStartElement(firstCharToLowerCase(method.getName().replaceFirst("(get)|(is)", "")));
                            serializeObjectToWriter(val, xmlWriter, deep + 1);
                            xmlWriter.writeEndElement();
                        } else {
                            xmlWriter.writeStartElement(firstCharToLowerCase(method.getName().replaceFirst("(get)|(is)", "")));
                            xmlWriter.writeCData(val.toString());
                            xmlWriter.writeEndElement();
                        }
                    }
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
    }

    @Override
    public byte[] serialize(T value) {
        if (value == null) {
            throw new RuntimeException("Null pointer for serialization.");
        }
        if (!getClazz().equals(value.getClass())) {
            throw new RuntimeException("Incorrect type.");
        }
        String serialized;
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);
            String className = value.getClass().getName();
            xmlWriter.writeStartElement(firstCharToLowerCase(className));
            serializeObjectToWriter(value, xmlWriter, 0);
            xmlWriter.writeEndElement();
            serialized = writer.getBuffer().toString();
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        } finally {
            IoUtils.close(writer);
        }
        return serialized.getBytes();
    }

    private Object deserializeElement() {
        return null;
    }

    private void iterate(Document document) {
        Element element = document.getDocumentElement();
        NodeList children = element.getChildNodes();
        for(int i = 0; i < children.getLength(); ++i) {
            Node node = children.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE :
                    // TODO
                    break;
                case Node.TEXT_NODE :
                    // TODO
                    break;
                case Node.CDATA_SECTION_NODE :
                    // TODO
                    break;
                case Node.COMMENT_NODE :
                    // Nothing
                    break;
                default:
                    throw new RuntimeException("Incorrect document.");
            }
        }
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException("Nothing found.");
        }
        StringReader reader = null;
        try {
            String data = new String(bytes);
            reader = new StringReader(data);
            InputSource source = new InputSource(reader);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
            iterate(document);
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        } finally {
            IoUtils.close(reader);
        }
        return null;
    }
}
