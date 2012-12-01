package ru.fizteh.fivt.students.altimin.xmlbinder;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.fizteh.fivt.bind.AsXmlAttribute;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;
import sun.misc.Unsafe;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * User: altimin
 * Date: 12/1/12
 * Time: 8:19 AM
 */

public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T> {
    public XmlBinder(Class<T> clazz) {
        super(clazz);
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafeInstance = Unsafe.class.cast(field.get(null));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to create binder: failed to get unsafe instance", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to create binder: failed to obtain theUnsafe field", e);
        }
    }

    @Override
    public byte[] serialize(T value) {
        if (value == null) {
            throw new RuntimeException("Failed to serialize: impossible to serialize null");
        }
        if (!value.getClass().equals(getClazz())) {
            throw new RuntimeException("Failed to serialize: class do not match");
        }
        serializedObjects = new IdentityHashMap<Object, Boolean>();
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlWriter = null;
        try {
            xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
            xmlWriter.writeStartElement(value.getClass().getSimpleName());
            serialize(value, xmlWriter);
            xmlWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException("Failed to serialize: failed to create XMLStreamWriter");
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
            }
        }
        return writer.getBuffer().toString().getBytes();
    }


    private boolean isPrimitive(Class clazz) {
        return clazz.isPrimitive() || clazz.isEnum() || clazz.equals(String.class)
                || clazz.equals(Boolean.class)
                || clazz.equals(Byte.class)
                || clazz.equals(Character.class)
                || clazz.equals(Double.class)
                || clazz.equals(Float.class)
                || clazz.equals(Integer.class)
                || clazz.equals(Long.class)
                || clazz.equals(Short.class);
    }

    private Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            Field field;
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (NoSuchFieldException e) {
            }
            clazz = clazz.getSuperclass();
        }
        throw new NoSuchFieldException("No such field " + fieldName);
    }

    private Object primitiveValueToObject(Class clazz, String value) {
        if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
            return Boolean.parseBoolean(value);
        }
        if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
            return Byte.parseByte(value);
        }
        if (clazz.equals(Character.class) || clazz.equals(char.class)) {
            if (value.length() != 1) {
                throw new RuntimeException("Expected 1 chars, but " + value.length() + " got");
            }
            return value.charAt(0);
        }
        if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            return Double.parseDouble(value);
        }
        if (clazz.equals(Float.class) || clazz.equals(float.class)) {
            return Float.parseFloat(value);
        }
        if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return Integer.parseInt(value);
        }
        if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            return Long.parseLong(value);
        }
        if (clazz.equals(Short.class) || clazz.equals(short.class)) {
            return Short.parseShort(value);
        }
        if (clazz.isEnum()) {
            return Enum.valueOf(clazz, value);
        }
        if (clazz.equals(String.class)) {
            return value;
        }
        throw new RuntimeException("Unexpected primitive type " + clazz.getSimpleName());
    }

    Method getSetter(Class clazz, String name) {
        Method[] methods = clazz.getMethods();
        for (Method method: methods) {
            if (isSetter(method) && method.getName().equals("set" + name)) {
                return method;
            }
        }
        return null;
    }

    Method getGetter(Class clazz, String name) {
        Method[] methods = clazz.getMethods();
        for (Method method: methods) {
            if (isGetter(method) && (method.getName().equals("is" + name) || method.getName().equals("get" + name))) {
                return method;
            }
        }
        return null;
    }

    private Field obtainField(Class clazz, String fieldName) {
        Field field;
        try {
            field = getField(clazz, fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to deserialize: incorrect XML: no such field " + fieldName);
        }
        field.setAccessible(true);
        return field;
    }

    private void setField(Field field, Object object, Object value) {
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to deserialize: failed to set field " + field.getName());
        }
    }

    private Object deserialize(Class clazz, Element element) {
        if (isPrimitive(clazz)) {
            return primitiveValueToObject(clazz, element.getTextContent());
        }
        Object object = createObject(clazz);
        BindingType annotation = (BindingType) clazz.getAnnotation(BindingType.class);
        boolean serializeAll = (annotation == null || annotation.value() == MembersToBind.FIELDS);
        if (serializeAll) {
            NamedNodeMap attributes = element.getAttributes();
            for (int i = 0; i < attributes.getLength(); i ++) {
                Node node = attributes.item(i);
                Field field = obtainField(clazz, node.getNodeName());
                setField(field, object, primitiveValueToObject(field.getType(),node.getNodeValue()));
            }
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i ++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Field field = obtainField(clazz, ((Element) node).getTagName());
                    Object obj = deserialize(field.getType(), (Element) node);
                    setField(field, object, obj);
                }
            }
            return object;
        } else {
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i ++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String name = firstLetterToUpper(((Element) node).getTagName());
                    Method setter = getSetter(clazz, name);
                    if (setter == null) {
                        throw new RuntimeException("Failed to deserialize: no such method get" + name);
                    }
                    Class newClass = setter.getParameterTypes()[0];
                    try {
                        setter.invoke(object, deserialize(newClass, (Element) node));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to deserialize: failed to call settler");
                    }
                }
            }
            return object;
        }
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException("Failed to deserialize: array is empty");
        }
        StringReader reader = new StringReader(new String(bytes));
        InputSource inputSource = new InputSource(reader);
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
            if (!document.getDocumentElement().getTagName().equals(getClazz().getSimpleName())) {
                throw new RuntimeException("Failed to deserialize: class name doesn't match");
            }
            return (T) deserialize(getClazz(), document.getDocumentElement());
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Failed to deserialize: failed to parse XML");
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize: failed to parse XML");
        } catch (SAXException e) {
            throw new RuntimeException("Failed to deserialize: failed to parse XML");
        } finally {
            reader.close();
        }
    }

    private Unsafe unsafeInstance;
    private IdentityHashMap<Object, Boolean> serializedObjects;

    private Object createObject(Class clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            // cannot use default constructor, using black magic
            try {
                return unsafeInstance.allocateInstance(clazz);
            } catch (InstantiationException ex) {
                throw new RuntimeException("Failed to allocate class unsafely", ex);
            }
        }
    }

    private String firstLetterToLower(String string) {
        if (string == null && string.length() == 0) {
            return string;
        }
        return Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    private String firstLetterToUpper(String string) {
        if (string == null && string.length() == 0) {
            return string;
        }
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    private static class GetterAndSetter {
        String fieldName;
        Method getter;
        Method setter;

        private GetterAndSetter(String fieldName, Method getter, Method setter) {
            this.fieldName = fieldName;
            this.getter = getter;
            this.setter = setter;
        }
    }

    private boolean isGetter(Method method) {
        String name = method.getName();
        return (name.matches("get.+")
                    && method.getParameterTypes().length == 0)
                || (name.matches("is.+")
                    && method.getParameterTypes().length == 0
                    && method.getReturnType().equals(boolean.class));
    }

    private boolean isSetter(Method method) {
        String name = method.getName();
        return name.matches("set.+")
                && method.getParameterTypes().length == 1
                && method.getReturnType().equals(void.class);
    }

    private String updateName(String string) {
        if (string.matches("get.+")) {
            return firstLetterToLower(string.substring(3));
        }
        if (string.matches("is.+")) {
            return firstLetterToLower(string.substring(2));
        }
        if (string.matches("set.+")) {
            return firstLetterToLower(string.substring(3));
        }
        throw new RuntimeException("Unexpected string to update");
    }

    private List<GetterAndSetter> getGettersAndSetters(Object object) {
        List<GetterAndSetter> list = new LinkedList<GetterAndSetter>();
        Method[] methods = object.getClass().getMethods();
        Map<String, Method> getters = new HashMap<String, Method>();
        Map<String, Method> setters = new HashMap<String, Method>();
        for (Method method: methods) {
            if (isGetter(method)) {
                getters.put(updateName(method.getName()), method);
            }
            if (isSetter(method)) {
                setters.put(updateName(method.getName()), method);
            }
        }
        for (String name: getters.keySet()) {
            if (setters.containsKey(name)) {
                list.add(new GetterAndSetter(name, getters.get(name), setters.get(name)));
            }
        }
        return list;
    }

    private void getFields(Class clazz, Object object, Map<Field, Object> map) {
        Field[] fields = object.getClass().getDeclaredFields();
        try {
            for (Field field: fields) {
                field.setAccessible(true);
                map.put(field, field.get(object));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to serialize");
        }
    }

    private Map<Field, Object> getFields(Object object) {
        Map<Field, Object> map = new HashMap<Field, Object>();
        Class clazz = object.getClass();
        while (clazz != null) {
            getFields(clazz, object, map);
            clazz = clazz.getSuperclass();
        }
        return map;
    }

    private boolean valid(String string) {
        return string != null && string.length() > 0;
    }

    private void serialize(Object object, XMLStreamWriter xmlWriter) {
        if (serializedObjects.containsKey(object)) {
            throw new RuntimeException("Failed to serialize");
        }
        serializedObjects.put(object, null);
        Class clazz = object.getClass();
        try {
            if (isPrimitive(clazz)) {
                xmlWriter.writeCharacters(object.toString());
                return;
            }
            BindingType annotation = (BindingType) clazz.getAnnotation(BindingType.class);
            boolean serializeAll = (annotation == null || annotation.value() == MembersToBind.FIELDS);
            if (serializeAll) {
                Map<Field, Object> fields = getFields(object);
                for (Field field: fields.keySet()) {
                    String fieldName = field.getName();
                    Object obj = fields.get(field);
                    AsXmlAttribute XmlAttributeAnnotation = (AsXmlAttribute) field.getAnnotation(AsXmlAttribute.class);
                    if (XmlAttributeAnnotation != null) {
                        if (obj != null) {
                            if (isPrimitive(obj.getClass())) {
                                xmlWriter.writeAttribute(
                                    valid(XmlAttributeAnnotation.name()) ? XmlAttributeAnnotation.name() : fieldName,
                                        obj.toString());
                            } else {
                                throw new RuntimeException(
                                        "Failed to serialize: impossible to write non-primitive object as attribute");
                            }
                        }
                    }
                }
                for (Field field: fields.keySet()) {
                    String fieldName = field.getName();
                    Object obj = fields.get(field);
                    AsXmlAttribute XmlAttributeAnnotation = (AsXmlAttribute) field.getAnnotation(AsXmlAttribute.class);
                    if (XmlAttributeAnnotation == null) {
                        if (obj != null) {
                            xmlWriter.writeStartElement(fieldName);
                            serialize(obj, xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                }
            } else {
                List<GetterAndSetter> getterAndSetters = getGettersAndSetters(object);
                for (GetterAndSetter getterAndSetter: getterAndSetters) {
                    Object obj = getterAndSetter.getter.invoke(object);
                    if (obj != null) {
                        AsXmlAttribute XMLAnnotation = (AsXmlAttribute) getterAndSetter.getter.getAnnotation(
                                AsXmlAttribute.class);
                        if (XMLAnnotation != null) {
                            if (isPrimitive(obj.getClass())) {
                               String attribute = valid(XMLAnnotation.name()) ? XMLAnnotation.name() : getterAndSetter.fieldName;
                               xmlWriter.writeAttribute(attribute, obj.toString());
                            }
                        }
                    }
                }
                for (GetterAndSetter getterAndSetter: getterAndSetters) {
                    Object obj = getterAndSetter.getter.invoke(object);
                    if (obj != null) {
                        AsXmlAttribute XMLAnnotation = (AsXmlAttribute) getterAndSetter.getter.getAnnotation(
                                AsXmlAttribute.class);
                        if (XMLAnnotation == null) {
                            xmlWriter.writeStartElement(getterAndSetter.fieldName);
                            serialize(obj, xmlWriter);
                            xmlWriter.writeEndElement();
                        }
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException("Failed to serialize");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to serialize");
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Failed to serialize");
        }
    }
}
