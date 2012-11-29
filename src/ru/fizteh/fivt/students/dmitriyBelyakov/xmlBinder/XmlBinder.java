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
import sun.misc.Unsafe;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T> {
    private HashMap<Class, HashMap<String, GetterAndSetterPair>> methodsForClasses;
    private HashMap<Class, HashMap<String, FieldData>> fieldsForClasses;
    private IdentityHashMap<Object, Object> alreadySerialised;
    private HashMap<String, Constructor> constructors;
    private String className;
    private Unsafe unsafe;
    private HashSet<Class> usedClasses;

    public XmlBinder(Class<T> clazz) {
        super(clazz);
        className = firstCharToLowerCase(clazz.getSimpleName());
        methodsForClasses = new HashMap<>();
        fieldsForClasses = new HashMap<>();
        alreadySerialised = new IdentityHashMap<>();
        constructors = new HashMap<>();
        usedClasses = new HashSet<>();
        prepareToSerialization(clazz);
        usedClasses.clear();
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(Unsafe.class);
        } catch (Throwable t) {
            throw new RuntimeException("Cannot create binder.");
        }
    }

    private Constructor getConstructor(Class clazz) {
        try {
            Constructor constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            /* default constructor does not exist */
        }
        return null;
    }

    private Object newInstanceOfObject(Class clazz) throws Exception {
        if (constructors.containsKey(clazz.getName()) && constructors.get(clazz.getName()) != null) {
            return constructors.get(clazz.getName()).newInstance();
        }
        return unsafe.allocateInstance(clazz);
    }

    private void prepareToSerialization(Class clazz) {
        constructors.put(clazz.getName(), getConstructor(clazz));
        if (usedClasses.contains(clazz)) {
            return;
        }
        usedClasses.add(clazz);
        BindingType annotation = (BindingType) clazz.getAnnotation(BindingType.class);
        boolean allFields = true;
        if (annotation != null && annotation.value().equals(MembersToBind.GETTERS_AND_SETTERS)) {
            allFields = false;
        }
        if (allFields) {
            ArrayList<Field> fields = getFields(clazz);
            HashMap<String, FieldData> map = new HashMap<>();
            for (Field field : fields) {
                FieldData data = new FieldData();
                data.name = field.getName();
                data.field = field;
                data.field.setAccessible(true);
                data.type = field.getType();
                data.asXmlData = field.getAnnotation(AsXmlCdata.class) != null || field.getAnnotation(AsXmlCdata.class) != null;
                map.put(data.name, data);
            }
            fieldsForClasses.put(clazz, map);
            for (Field field : fields) {
                prepareToSerialization(field.getType());
            }
        } else {
            ArrayList<GetterAndSetterPair> methods = getMethods(clazz);
            HashMap<String, GetterAndSetterPair> map = new HashMap<>();
            for (GetterAndSetterPair pair : methods) {
                map.put(pair.name, pair);
            }
            methodsForClasses.put(clazz, map);
            for (GetterAndSetterPair pair : methods) {
                prepareToSerialization(pair.getter.getReturnType());
            }
        }
    }

    private String firstCharToLowerCase(String str) {
        if (str == null || str.length() == 0) {
            throw new RuntimeException("String not found.");
        }
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

    private ArrayList<GetterAndSetterPair> getMethods(Class clazz) {
        ArrayList<GetterAndSetterPair> methods = new ArrayList<>();
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
                    GetterAndSetterPair pair = new GetterAndSetterPair();
                    pair.getter = methodGet;
                    pair.setter = method;
                    pair.getter.setAccessible(true);
                    pair.setter.setAccessible(true);
                    pair.name = firstCharToLowerCase(nameGet.replaceFirst("get", ""));
                    pair.type = methodGet.getReturnType();
                    pair.asXmlCdata = methodGet.getAnnotation(AsXmlCdata.class) != null;
                    methods.add(pair);
                    continue;
                } catch (NoSuchMethodException e) {
                    /* nothing */
                }
                try {
                    Method methodIs = clazz.getMethod(nameIs);
                    if (!method.getParameterTypes()[0].equals(methodIs.getReturnType()) || !(methodIs.getReturnType().equals(boolean.class)
                            || methodIs.getReturnType().equals(Boolean.class))) {
                        continue;
                    }
                    GetterAndSetterPair pair = new GetterAndSetterPair();
                    pair.getter = methodIs;
                    pair.setter = method;
                    pair.name = firstCharToLowerCase(nameIs.replaceFirst("is", ""));
                    pair.type = methodIs.getReturnType();
                    pair.asXmlCdata = methodIs.getAnnotation(AsXmlCdata.class) != null;
                    methods.add(pair);
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
                HashMap<String, FieldData> fields = fieldsForClasses.get(clazz);
                for (FieldData data : fields.values()) {
                    Object val = data.field.get(value);
                    if (val != null) {
                        if (!data.asXmlData || !isPrimitive(data.type)) {
                            xmlWriter.writeStartElement(data.name);
                            serializeObjectToWriter(val, xmlWriter);
                            xmlWriter.writeEndElement();
                        } else {
                            xmlWriter.writeStartElement(data.name);
                            xmlWriter.writeCData(val.toString());
                            xmlWriter.writeEndElement();
                        }
                    }
                }
            } else {
                HashMap<String, GetterAndSetterPair> methods = methodsForClasses.get(clazz);
                for (GetterAndSetterPair pair : methods.values()) {
                    Object val = pair.getter.invoke(value);
                    if (val != null) {
                        if (!pair.asXmlCdata || !isPrimitive(pair.type)) {
                            xmlWriter.writeStartElement(firstCharToLowerCase(pair.name));
                            serializeObjectToWriter(val, xmlWriter);
                            xmlWriter.writeEndElement();
                        } else {
                            xmlWriter.writeStartElement(firstCharToLowerCase(pair.name));
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
            xmlWriter.writeStartElement(className);
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
                Object returnObject = newInstanceOfObject(clazz);
                NodeList children = element.getChildNodes();
                HashMap<String, FieldData> serializedFields = fieldsForClasses.get(clazz);
                if (serializedFields == null) {
                    throw new RuntimeException("Cannot deserialize object.");
                }
                HashSet<FieldData> unused = new HashSet<>(serializedFields.values());
                for (int i = 0; i < children.getLength(); ++i) {
                    Node node = children.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        FieldData data = serializedFields.get(((Element) node).getTagName());
                        if (data == null) {
                            continue;
                        }
                        data.field.set(returnObject, deserializeToValue((Element) node, data.type));
                        unused.remove(data);
                    }
                }
                for (FieldData data : unused) {
                    if (!data.type.isPrimitive()) {
                        data.field.set(returnObject, null);
                    }
                }
                return returnObject;
            } else {
                Object returnObject = newInstanceOfObject(clazz);
                NodeList children = element.getChildNodes();
                HashMap<String, GetterAndSetterPair> serializedMethods = methodsForClasses.get(clazz);
                if (serializedMethods == null) {
                    throw new RuntimeException("Cannot deserialize object.");
                }
                for (int i = 0; i < children.getLength(); ++i) {
                    Node node = children.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        GetterAndSetterPair pair = serializedMethods.get(((Element) node).getTagName());
                        if (pair == null) {
                            continue;
                        }
                        pair.setter.invoke(returnObject, deserializeToValue((Element) node, pair.type));
                    }
                }
                return returnObject;
            }
        } catch (Throwable t) {
            throw new RuntimeException("An exception occurred within deserialization of " + element.getTagName(), t);
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
            if (!document.getDocumentElement().getTagName().equals(className)) {
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
