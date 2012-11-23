package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ru.fizteh.fivt.bind.AsXmlCdata;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;
import ru.fizteh.fivt.students.dmitriyBelyakov.shell.IoUtils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T> {
    private HashMap<Class, ArrayList<Pair<Method, Method>>> methodsForClasses;
    private HashMap<Class, ArrayList<Field>> fieldsForClasses;

    XmlBinder(Class<T> clazz) {
        super(clazz);
        methodsForClasses = new HashMap<>();
        fieldsForClasses = new HashMap<>();
        prepareToSerialization(clazz);
    }

    private void prepareToSerialization(Class clazz) {
        if (fieldsForClasses.containsKey(clazz) || methodsForClasses.containsKey(clazz) || isPrimitive(clazz)) {
            return;
        }
        BindingType annotation = (BindingType) clazz.getAnnotation(BindingType.class);
        boolean allFields = true;
        if (annotation != null && annotation.value().equals(MembersToBind.GETTERS_AND_SETTERS)) {
            allFields = false;
        }
        if (allFields) {
            ArrayList<Field> fields = getFields(clazz);
            fieldsForClasses.put(clazz, fields);
            for (Field field : fields) {
                prepareToSerialization(field.getType());
            }
        } else {
            ArrayList<Pair<Method, Method>> methods = getMethods(clazz);
            methodsForClasses.put(clazz, methods);
            for (Pair<Method, Method> pair : methods) {
                prepareToSerialization(pair.getKey().getReturnType());
            }
        }
    }

    private String firstCharToLowerCase(String str) {
        if (str.length() == 1) {
            return str.toLowerCase();
        } else {
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        }
    }

    private String firstCharToUpperCase(String str) {
        if (str.length() == 1) {
            return str.toLowerCase();
        } else {
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
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

    private ArrayList<Pair<Method, Method>> getMethods(Class clazz) {
        ArrayList<Pair<Method, Method>> methods = new ArrayList<>();
        Method[] tmpMethods = clazz.getMethods();
        for (Method method : tmpMethods) {
            if (method.getName().matches("set.+")) {
                if (!method.getReturnType().equals(void.class) || method.getParameterTypes().length != 1) {
                    continue;
                }
                String nameGet = method.getName().replaceFirst("set", "get");
                String nameIs = method.getName().replaceFirst("set", "is");
                try {
                    Method methodGet = clazz.getMethod(nameGet);
                    if (!method.getParameterTypes()[0].equals(methodGet.getReturnType())) {
                        continue;
                    }
                    methods.add(new Pair(methodGet, method));
                    continue;
                } catch (NoSuchMethodException e) {
                    /* nothing */
                }
                try {
                    Method methodIs = clazz.getMethod(nameIs);
                    if (!method.getParameterTypes()[0].equals(methodIs.getReturnType())) {
                        continue;
                    }
                    methods.add(new Pair(methodIs, method));
                } catch (NoSuchMethodException e) {
                    /* nothing */
                }
            }
        }
        return methods;
    }

    private void serializeObjectToWriter(Object value, XMLStreamWriter xmlWriter, int deep) {
        if (deep >= 100) {
            throw new RuntimeException("Cannot serialize.");
        }
        if (value == null) {
            return;
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
                ArrayList<Field> fields = fieldsForClasses.get(clazz);
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
                ArrayList<Pair<Method, Method>> methods = methodsForClasses.get(clazz);
                for (Pair<Method, Method> pair : methods) {
                    Method method = pair.getKey(); // get getter
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
            throw new RuntimeException("An exception occurred within serialization of " + value.getClass(), t);
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
            throw new RuntimeException("An exception occurred within serialization of " + value.getClass(), t);
        } finally {
            IoUtils.close(writer);
        }
        return serialized.getBytes();
    }

    private Object deserializeElement() {
        return null;
    }

    private Object getValueForPrimitiveType(String val, Class clazz) {
        if (!isPrimitive(clazz)) {
            throw new RuntimeException("Not primitive type.");
        }
        if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
            return Boolean.parseBoolean(val);
        } else if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
            return Byte.parseByte(val);
        } else if (clazz.equals(Character.class) || clazz.equals(char.class)) {
            if (val.length() != 1) {
                throw new RuntimeException("Incorrect value.");
            }
            return val.charAt(0);
        } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
            return Short.parseShort(val);
        } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return Integer.parseInt(val);
        } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            return Long.parseLong(val);
        } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
            return Float.parseFloat(val);
        } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            return Double.parseDouble(val);
        } else if (clazz.isEnum()) {
            return Enum.valueOf(clazz, val);
        } else if (clazz.equals(String.class)) {
            return val;
        }
        return null;
    }

    private Object deserializeToValue(Element element, Class clazz) {
        try {
            boolean allFields = true;
            if (!(getClazz().getAnnotation(BindingType.class) == null)
                    && getClazz().getAnnotation(BindingType.class).value() == MembersToBind.GETTERS_AND_SETTERS) {
                allFields = false;
            }
            if (allFields) {
                Class clazz = getClazz().getField(element.getTagName()).getType();
                Object returnObject = clazz.newInstance();
                if (isPrimitive(clazz)) {
                    return getValueForPrimitiveType(element.getTextContent(), clazz);
                } else {
                    NodeList children = element.getChildNodes();
                    for (int i = 0; i < children.getLength(); ++i) {
                        Node node = children.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Field field = clazz.getField(((Element) node).getTagName());
                            field.setAccessible(true);
                            field.set(returnObject, deserializeToValue((Element) node));
                        }
                    }
                }
            } else {
                // TODO
            }
            return null;
        } catch (Throwable t) {
            throw new RuntimeException("An exception occurred within serialization of " + element.getTagName(), t);
        }
    }

    private void iterate(Document document) {
        Element element = document.getDocumentElement();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node node = children.item(i);
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    // TODO
                    break;
                case Node.CDATA_SECTION_NODE:
                    // TODO
                    break;
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
            //iterate(document);
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        } finally {
            IoUtils.close(reader);
        }
        return null;
    }
}
