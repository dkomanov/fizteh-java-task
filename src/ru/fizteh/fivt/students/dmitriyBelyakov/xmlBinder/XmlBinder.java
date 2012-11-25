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
import sun.misc.Unsafe;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T> {
    private HashMap<Class, HashMap<String, Pair<Method, Method>>> methodsForClasses;
    private HashMap<Class, HashMap<String, Field>> fieldsForClasses;
    private IdentityHashMap<Object, Object> alreadySerialised;

    XmlBinder(Class<T> clazz) {
        super(clazz);
        methodsForClasses = new HashMap<>();
        fieldsForClasses = new HashMap<>();
        alreadySerialised = new IdentityHashMap<>();
        prepareToSerialization(clazz);
    }

    private Object newInstance(Class clazz) {
        try {
            Constructor constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            /* default constructor does not exist */
        } catch (InstantiationException e) {
            /* nothing */
        } catch (InvocationTargetException e) {
            /* nothing */
        } catch (IllegalAccessException e) {
            /* nothing */
        }
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(Unsafe.class);
            return unsafe.allocateInstance(clazz);
        } catch (Throwable t) {
            throw new RuntimeException("Cannot create new instance of object.");
        }
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
            HashMap<String, Field> map = new HashMap<>();
            for (Field field : fields) {
                map.put(field.getName(), field);
            }
            fieldsForClasses.put(clazz, map);
            for (Field field : fields) {
                prepareToSerialization(field.getType());
            }
        } else {
            ArrayList<Pair<Method, Method>> methods = getMethods(clazz);
            HashMap<String, Pair<Method, Method>> map = new HashMap<>();
            for (Pair pair : methods) {
                String name = firstCharToLowerCase(((Method) pair.getValue()).getName().replace("set", ""));
                map.put(name, pair);
            }
            methodsForClasses.put(clazz, map);
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

    // Unused
    /* private String firstCharToUpperCase(String str) {
        if (str.length() == 1) {
            return str.toLowerCase();
        } else {
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }
    } */

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
                        throw new NoSuchMethodException();
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

    private void serializeObjectToWriter(Object value, XMLStreamWriter xmlWriter) {
        if (alreadySerialised.containsKey(value)) {
            throw new RuntimeException("Cannot serialize this object.");
        }
        if (value == null) {
            return;
        }
        alreadySerialised.put(value, null);
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
                HashMap<String, Field> fields = fieldsForClasses.get(clazz);
                for (Field field : fields.values()) {
                    field.setAccessible(true);
                    if (field.get(value) != null) {
                        if (field.getAnnotation(AsXmlCdata.class) == null || !isPrimitive(field.get(value).getClass())) {
                            xmlWriter.writeStartElement(field.getName());
                            serializeObjectToWriter(field.get(value), xmlWriter);
                            xmlWriter.writeEndElement();
                        } else {
                            xmlWriter.writeStartElement(field.getName());
                            xmlWriter.writeCData(field.get(value).toString());
                            xmlWriter.writeEndElement();
                        }
                    }
                }
            } else {
                HashMap<String, Pair<Method, Method>> methods = methodsForClasses.get(clazz);
                for (Pair<Method, Method> pair : methods.values()) {
                    Method method = pair.getKey(); // get getter
                    Object val = method.invoke(value);
                    if (val != null) {
                        if (method.getAnnotation(AsXmlCdata.class) == null || !isPrimitive(method.getReturnType())) {
                            xmlWriter.writeStartElement(firstCharToLowerCase(method.getName().replaceFirst("(get)|(is)", "")));
                            serializeObjectToWriter(val, xmlWriter);
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
        alreadySerialised.clear();
        String serialized;
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);
            String className = value.getClass().getName();
            xmlWriter.writeStartElement(firstCharToLowerCase(className));
            serializeObjectToWriter(value, xmlWriter);
            xmlWriter.writeEndElement();
            serialized = writer.getBuffer().toString();
        } catch (Throwable t) {
            throw new RuntimeException("An exception occurred within serialization of " + value.getClass(), t);
        } finally {
            IoUtils.close(writer);
        }
        return serialized.getBytes();
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
        return null; // never used
    }

    private Object deserializeToValue(Element element, Class clazz) {
        if (isPrimitive(clazz)) {
            return getValueForPrimitiveType(element.getTextContent(), clazz);
        }
        try {
            boolean allFields = true;
            BindingType annotation = (BindingType) clazz.getAnnotation(BindingType.class);
            if (annotation != null
                    && annotation.value() == MembersToBind.GETTERS_AND_SETTERS) {
                allFields = false;
            }
            if (allFields) {
                Object returnObject = newInstance(clazz);
                NodeList children = element.getChildNodes();
                HashMap<String, Field> serializedFields = fieldsForClasses.get(clazz);
                if (serializedFields == null) {
                    throw new RuntimeException("Cannot serialize object.");
                }
                HashSet<Field> unused = new HashSet<>(serializedFields.values());
                for (int i = 0; i < children.getLength(); ++i) {
                    Node node = children.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Field field = serializedFields.get(((Element) node).getTagName());
                        if (field == null) {
                            continue;
                        }
                        field.setAccessible(true);
                        field.set(returnObject, deserializeToValue((Element) node, field.getType()));
                        unused.remove(field);
                    }
                }
                for (Field field : unused) {
                    if (!field.getType().isPrimitive()) {
                        field.setAccessible(true);
                        field.set(returnObject, null);
                    }
                }
                return returnObject;
            } else {
                Object returnObject = newInstance(clazz);
                NodeList children = element.getChildNodes();
                HashMap<String, Pair<Method, Method>> serializedMethods = methodsForClasses.get(clazz);
                if (serializedMethods == null) {
                    throw new RuntimeException("Cannot serialize object.");
                }
                for (int i = 0; i < children.getLength(); ++i) {
                    Node node = children.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Pair<Method, Method> method = serializedMethods.get(((Element) node).getTagName());
                        if (method == null) {
                            continue;
                        }
                        method.getValue().invoke(returnObject, deserializeToValue((Element) node, method.getKey().getReturnType()));
                    }
                }
                //Object returnObject = clazz.newInstance();
                return returnObject;
            }
        } catch (Throwable t) {
            throw new RuntimeException("An exception occurred within serialization of " + element.getTagName(), t);
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
            if (!document.getDocumentElement().getTagName().equals(getClazz().getName())) {
                throw new RuntimeException("Unsupported class.");
            }
            return (T) deserializeToValue(document.getDocumentElement(), getClazz());
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        } finally {
            IoUtils.close(reader);
        }
    }
}
