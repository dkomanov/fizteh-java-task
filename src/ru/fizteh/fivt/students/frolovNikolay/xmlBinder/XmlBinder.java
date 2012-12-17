package ru.fizteh.fivt.students.frolovNikolay.xmlBinder;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;

import sun.misc.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.fizteh.fivt.bind.MembersToBind;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.students.frolovNikolay.Closer;

public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T> {
    
    private class FieldMeta {
        public String name;
        public Field field;
        public Class<?> type;
    }
    
    private class MethodMeta {
        public String name;
        public Method setter;
        public Method getter;
        public Class<?> type;
    }

    private HashMap<Class<?>, ArrayList<FieldMeta>> fields;
    private HashMap<Class<?>, ArrayList<MethodMeta>> methods;
    
    private Object createNewObject(Class<?> clazz) {
        try {
            Constructor<?> ctor = clazz.getConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Throwable ignoringException) {
        }
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return ((Unsafe) field.get(Unsafe.class)).allocateInstance(clazz);
        } catch (Throwable ignoringException) {
            throw new RuntimeException("can't construct class: " + clazz.getSimpleName());
        }
    }
    
    private Object getWriteableValue(String value, Class clazz) {
        if (clazz.isEnum()) {
            return Enum.valueOf(clazz, value);
        } else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
            return Byte.parseByte(value);
        } else if (clazz.equals(Character.class) || clazz.equals(char.class)) {
            if (value.length() == 1) {
                return value.charAt(0);
            } else {
                throw new RuntimeException("string isn't character");
            }
        } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
            return Short.parseShort(value);
        } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return Integer.parseInt(value);
        } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
            return Float.parseFloat(value);
        } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            return Long.parseLong(value);
        } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            return Double.parseDouble(value);
        } else if (clazz.equals(String.class)){
            return value;
        } else {
            throw new RuntimeException("isn't writeable value");
        }
    }
    
    public XmlBinder(Class<T> clazz) {
        super(clazz);
        if (clazz == null) {
            throw new RuntimeException("null pointer");
        }
        fields = new HashMap<Class<?>, ArrayList<FieldMeta>>();
        methods = new HashMap<Class<?>, ArrayList<MethodMeta>>();
        try {
            recursiveBuildXmlBinder(clazz, new HashSet<Class<?>>());
        } catch (Throwable ignoringException) {
            
        }
    }
    
    private void recursiveBuildXmlBinder(Class<?> clazz, HashSet<Class<?>> alreadyAdded) throws Throwable {
        if (!alreadyAdded.contains(clazz)) {
            alreadyAdded.add(clazz);
            BindingType serializeFields = clazz.getAnnotation(BindingType.class);
            if (serializeFields == null || serializeFields.value().equals(MembersToBind.FIELDS)) {
                Class<?> tempClazz = clazz;
                ArrayList<Field> tempClassFields = new ArrayList<Field>();
                while (tempClazz != null) {
                    tempClassFields.addAll(Arrays.asList(tempClazz.getDeclaredFields()));
                    tempClazz = tempClazz.getSuperclass();
                }
                ArrayList<FieldMeta> classFields = new ArrayList<FieldMeta>();
                for (Field iter : tempClassFields) {
                    recursiveBuildXmlBinder(iter.getType(), alreadyAdded);
                    FieldMeta fieldInfo = new FieldMeta();
                    fieldInfo.name = lowerFirstCharacter(iter.getName());
                    fieldInfo.field = iter;
                    fieldInfo.field.setAccessible(true);
                    fieldInfo.type = iter.getType();
                    classFields.add(fieldInfo);
                }
                fields.put(clazz, classFields);
                for (FieldMeta iter : classFields) {
                    recursiveBuildXmlBinder(iter.type, alreadyAdded);
                }
            } else {
                ArrayList<Method> tempClassMethods = new ArrayList<Method>();
                ArrayList<MethodMeta> classMethods = new ArrayList<MethodMeta>();
                tempClassMethods.addAll(Arrays.asList(clazz.getMethods()));
                ArrayList<Class<?>> needToAddClasses = new ArrayList<Class<?>>();
                for (Method iter : tempClassMethods) {
                    if (iter.getName().length() >= 3 && iter.getName().substring(0, 3).equals("set")
                       && iter.getReturnType().equals(void.class) && iter.getParameterTypes().length == 1) {
                        Class<?> tempClass = null;
                        tempClass = tryToAddMethods(clazz, iter, classMethods, "get");
                        if (tempClass != null) {
                            needToAddClasses.add(tempClass);
                        }
                        tempClass = tryToAddMethods(clazz, iter, classMethods, "is");
                        if (tempClass != null) {
                            needToAddClasses.add(tempClass);
                        }
                    }
                }
                methods.put(clazz, classMethods);
                for (Class<?> iter : needToAddClasses) {
                    recursiveBuildXmlBinder(iter, alreadyAdded);
                }
            }
        }
    }
    
    private Class<?> tryToAddMethods(Class<?> clazz, Method setter, ArrayList<MethodMeta> classMethods, String getterPrefix) {
        Class<?> returnStatement = null;
        try {
            String getterName = getterPrefix + setter.getName().substring(3);
            Method getter = clazz.getMethod(getterName);
            if (getter.getReturnType().equals(setter.getParameterTypes()[0])) {
                MethodMeta added = new MethodMeta();
                setter.setAccessible(true);
                getter.setAccessible(true);
                added.name = lowerFirstCharacter(setter.getName().substring(3));
                added.getter = getter;
                added.setter = setter;
                added.type = getter.getReturnType();
                classMethods.add(added);
                returnStatement =  getter.getReturnType();
            }
        } catch (Throwable ignoringException) { 
        }
        return returnStatement;
    }
    
    @Override
    public byte[] serialize(T value) {
        if (value == null) {
            throw new RuntimeException("null pointer");
        } else if (!getClazz().equals(value.getClass())) {
            throw new RuntimeException("incorrect input type: " + value.getClass().getName());
        } else {
            try {
                IdentityHashMap<Object, Object> cycleLinkInterrupter = new IdentityHashMap<Object, Object>();
                StringWriter sWriter = new StringWriter();
                XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sWriter);
                writer.writeStartElement(lowerFirstCharacter(getClazz().getSimpleName()));
                serializeToStream(value, writer, cycleLinkInterrupter);
                writer.writeEndElement();
                return sWriter.getBuffer().toString().getBytes();
            } catch (Throwable exception) {
                throw new RuntimeException("serialize crush for unknown reason", exception);
            }
        }
    }
    
    static public String lowerFirstCharacter(String text) throws Exception {
        if (text == null || text.isEmpty()) {
            throw new Exception("this String don't have first character");
        } else {
            return text.substring(0, 1).toLowerCase() + text.substring(1);
        }
    }
    
    static boolean isWriteable(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.isEnum() || clazz.equals(String.class)
               || clazz.equals(Character.class) || clazz.equals(Short.class)
               || clazz.equals(Integer.class) || clazz.equals(Long.class)
               || clazz.equals(Float.class) || clazz.equals(Double.class)
               || clazz.equals(Boolean.class) || clazz.equals(Byte.class);
    }
    
    public void serializeToStream(Object value, XMLStreamWriter writer, IdentityHashMap<Object, Object> cycleLinkInterrupter) throws Throwable {
        if (cycleLinkInterrupter.containsKey(value)) {
            throw new Exception("Can't serialize");
        }
        if (value == null) {
            return;
        }
        cycleLinkInterrupter.put(value, null);
        if (isWriteable(value.getClass())) {
            writer.writeCharacters(value.toString());
        } else {
            BindingType serializeFields = value.getClass().getAnnotation(BindingType.class);
            if (serializeFields == null || serializeFields.value().equals(MembersToBind.FIELDS)) {
                ArrayList<FieldMeta> classFields = fields.get(value.getClass());
                for (int i = 0; i < classFields.size(); ++i) {
                    FieldMeta fieldInfo = classFields.get(i);
                    Object tempValue = fieldInfo.field.get(value);
                    if (tempValue != null) {
                        writer.writeStartElement(fieldInfo.name);
                        serializeToStream(fieldInfo.field.get(value), writer, cycleLinkInterrupter);
                        writer.writeEndElement();
                    }
                }
            } else {
                ArrayList<MethodMeta> classMethods = methods.get(value.getClass());
                for (int i = 0; i < classMethods.size(); ++i) {
                    MethodMeta methodInfo = classMethods.get(i);
                    Object tempValue = methodInfo.getter.invoke(value);
                    if (tempValue != null) {
                        writer.writeStartElement(methodInfo.name);
                        serializeToStream(tempValue, writer, cycleLinkInterrupter);
                        writer.writeEndElement();
                    }
                }
            }
        }
    }

    public Object objectDeserialize(Element element, Class<?> clazz) throws Throwable {
        if (isWriteable(clazz)) {
            return getWriteableValue(element.getTextContent(), clazz);
        }
        Object object = createNewObject(clazz);
        BindingType serializeFields = clazz.getAnnotation(BindingType.class);
        if (serializeFields != null && serializeFields.value().equals(MembersToBind.GETTERS_AND_SETTERS)) {
            ArrayList<MethodMeta> classMethods = methods.get(clazz);
            if (classMethods != null) {
                NodeList childNodes = element.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    Node node = childNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element newElement = (Element) node;
                        for (MethodMeta method : classMethods) {
                            if (method.name.equals(newElement.getTagName())) {
                                method.setter.invoke(object, objectDeserialize(newElement, method.type));
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            ArrayList<FieldMeta> classFields = fields.get(clazz);
            HashSet<FieldMeta> nullTestFields = new HashSet<FieldMeta>(classFields);
            if (classFields != null) {
                NodeList childNodes = element.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    Node node = childNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element newElement = (Element) node;
                        for (FieldMeta field : classFields) {
                            if (field.name.equals(newElement.getTagName())) {
                                field.field.set(object, objectDeserialize(newElement, field.type));
                                nullTestFields.remove(field);
                                break;
                            }
                        }
                    }
                }
            }
            for (FieldMeta field : nullTestFields) {
                if (field != null && !isWriteable(field.type)) {
                    field.field.set(object, null);
                }
            }
        }
        return object;
    }
    
    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null) {
            throw new RuntimeException("null pointer");
        }
        if (bytes.length == 0) {
            throw new RuntimeException("empty array");
        }
        ByteArrayInputStream reader = null;
        try {
            reader = new ByteArrayInputStream(bytes);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(reader);
            if (!document.getDocumentElement().getTagName().equals(lowerFirstCharacter(getClazz().getSimpleName()))) {
                throw new Exception("incorrect class for deserialize: " + document.getDocumentElement().getTagName());
            }
            return (T) objectDeserialize(document.getDocumentElement(), getClazz());
        } catch (Throwable exception) {
            throw new RuntimeException("deserialize crush for unknown reason", exception);
        } finally {
            Closer.close(reader);
        }
    }
}