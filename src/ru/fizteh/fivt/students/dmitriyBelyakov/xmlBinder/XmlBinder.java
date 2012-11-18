package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T> {
    XmlBinder(Class<T> clazz) {
        super(clazz);
    }

    private String firstCharToLowerCase(String str) {
        if(str.length() == 1) {
            return str.toLowerCase();
        } else {
            return str.toLowerCase().charAt(0) + str.substring(1);
        }
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
            if (clazz.isPrimitive() || clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(Byte.class)
                    || clazz.isAssignableFrom(Character.class) || clazz.isAssignableFrom(Short.class)
                    || clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(Long.class)
                    || clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(Double.class)
                    || clazz.isEnum() || clazz.equals(String.class)) {
                xmlWriter.writeCharacters(value.toString());
                return;
            }
            BindingType annotation = (BindingType) clazz.getAnnotation(BindingType.class);
            boolean allFields = true;
            if (annotation != null && annotation.value().equals(MembersToBind.GETTERS_AND_SETTERS)) {
                allFields = false;
            }
            if (allFields) {
                ArrayList<Field> fields = new ArrayList<>();
                while (clazz != null) {
                    Field[] tmpFields = clazz.getDeclaredFields();
                    fields.addAll(Arrays.asList(tmpFields));
                    clazz = clazz.getSuperclass();
                }
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.get(value) != null) {
                        xmlWriter.writeStartElement(field.getName());
                        serializeObjectToWriter(field.get(value), xmlWriter, deep + 1);
                        xmlWriter.writeEndElement();
                    }
                }
            } else {
                ArrayList<Method> methods = new ArrayList<>();
                while (clazz != null) {
                    Method[] tmpMethods = clazz.getDeclaredMethods();
                    HashMap<String, Method> methodsNames = new HashMap<>();
                    for (Method method : tmpMethods) {
                        if (method.getName().matches("get.+")) {
                            String name = method.getName().replaceFirst("get", "set");
                            if (methodsNames.containsKey(name)) {
                                methods.add(method);
                            } else {
                                methodsNames.put(method.getName(), method);
                            }
                        } else if (method.getName().matches("is.+")) {
                            String name = method.getName().replaceFirst("is", "set");
                            if (methodsNames.containsKey(name)) {
                                methods.add(method);
                            } else {
                                methodsNames.put(method.getName(), method);
                            }
                        } else if (method.getName().matches("set.+")) {
                            String nameGet = method.getName().replaceFirst("set", "get");
                            String nameIs = method.getName().replaceFirst("set", "is");
                            if (methodsNames.containsKey(nameGet)) {
                                methods.add(methodsNames.get(nameGet));
                            } else if (methodsNames.containsKey(nameIs)) {
                                methods.add(methodsNames.get(nameIs));
                            } else {
                                methodsNames.put(method.getName(), method);
                            }
                        }
                    }
                    clazz = clazz.getSuperclass();
                }
                for (Method method : methods) {
                    method.setAccessible(true);
                    Object val = method.invoke(value);
                    if (val != null) {
                        xmlWriter.writeStartElement(firstCharToLowerCase(method.getName().replaceFirst("(get)|(is)", "")));
                        serializeObjectToWriter(val, xmlWriter, deep + 1);
                        xmlWriter.writeEndElement();
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
        try {
            String serialized;
            StringWriter writer = new StringWriter();
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);
            String className = value.getClass().getName();
            xmlWriter.writeStartElement(firstCharToLowerCase(className));
            serializeObjectToWriter(value, xmlWriter, 0);
            xmlWriter.writeEndElement();
            serialized = writer.getBuffer().toString();
            return serialized.getBytes();
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
    }

    @Override
    public T deserialize(byte[] bytes) {
        return null;
    }
}
