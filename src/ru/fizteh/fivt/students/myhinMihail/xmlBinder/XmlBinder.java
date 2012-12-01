package ru.fizteh.fivt.students.myhinMihail.xmlBinder;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import ru.fizteh.fivt.bind.*;
import sun.misc.Unsafe;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import ru.fizteh.fivt.students.myhinMihail.Utils;

class MethodData {
    public String name;
    public Method get;
    public Method set;
    public Class<?> clazz;
    public boolean asXmlCdata;
}

class FieldData {
    public String name;
    public Field field;
    public Class<?> clazz;
    public boolean asXmlData;
}

public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T> {
    private HashMap<Class<?>, HashMap<String, MethodData> > methods = new HashMap<>();
    private HashMap<Class<?>, HashMap<String, FieldData> > fields = new HashMap<>();
    private IdentityHashMap<Object, Object> serialised = new IdentityHashMap<>();
    private String type;

    public XmlBinder(Class<T> clazz) {
        super(clazz);
        type = firstCharToLowerCase(clazz.getSimpleName());
        initSerialization(clazz, new HashSet<Class<?> >());
    }

    private Object newObject(Class<?> clazz) {
         try {
             Constructor<?> constructor = clazz.getConstructor();
             constructor.setAccessible(true);
             return constructor.newInstance();
         } catch (Exception expt) {
         }
         
         try {
             Field f = Unsafe.class.getDeclaredField("theUnsafe");
             f.setAccessible(true);
             return ((Unsafe)f.get(Unsafe.class)).allocateInstance(clazz);
         } catch (Exception expt) {
             throw new RuntimeException("Can not create new " + clazz);
         }
    }

    private void initSerialization(Class<?> clazz, HashSet<Class<?> > readyClasses) {
        if (readyClasses.contains(clazz)) {
            return;
        }
        readyClasses.add(clazz);
        
        BindingType annotation = (BindingType) clazz.getAnnotation(BindingType.class);
        
        if (annotation == null || annotation.value().equals(MembersToBind.FIELDS)) {
            Class<?> tmtClazz = clazz;
            ArrayList<Field> classFields = new ArrayList<>();
            while (tmtClazz != null) {
                classFields.addAll(Arrays.asList(tmtClazz.getDeclaredFields()));
                tmtClazz = tmtClazz.getSuperclass();
            }
            
            HashMap<String, FieldData> map = new HashMap<>();
            for (Field field : classFields) {
                FieldData data = new FieldData();
                data.name = field.getName();
                data.field = field;
                data.field.setAccessible(true);
                data.clazz = field.getType();
                data.asXmlData = (field.getAnnotation(AsXmlCdata.class) != null);
                map.put(data.name, data);
            }
            fields.put(clazz, map);
            for (Field field : classFields) {
                initSerialization(field.getType(), readyClasses);
            }
        } else {
            HashMap<String, MethodData> map = new HashMap<>();
            for (MethodData gas : getMethods(clazz)) {
                map.put(gas.name, gas);
            }
            methods.put(clazz, map);
            for (MethodData gas : getMethods(clazz)) {
                initSerialization(gas.get.getReturnType(), readyClasses);
            }
        }
    }
    
    private void analyzeMetod(Class<?> clazz, String mthd, Method method, ArrayList<MethodData> methods) {
        String setter = method.getName().replaceFirst("set", mthd);
        
        try {
            Method methodGet = clazz.getMethod(setter);
            if (!method.getParameterTypes()[0].equals(methodGet.getReturnType())) {
                return;
            }
            MethodData gas = new MethodData();
            gas.get = methodGet;
            gas.get.setAccessible(true);
            gas.set = method;
            gas.set.setAccessible(true);
            gas.name = firstCharToLowerCase(setter.replaceFirst(mthd, ""));
            gas.clazz = methodGet.getReturnType();
            gas.asXmlCdata = methodGet.getAnnotation(AsXmlCdata.class) != null || method.getAnnotation(AsXmlCdata.class) != null;
            methods.add(gas);
        } catch (NoSuchMethodException expt) {
        }
    }

    private ArrayList<MethodData> getMethods(Class<?> clazz) {
        ArrayList<MethodData> methods = new ArrayList<>();
        for (Method method : clazz.getMethods()) {
            if (method.getName().subSequence(0, 3).equals("set")) {
                if (method.getReturnType().equals(void.class) && method.getParameterTypes().length == 1) {
                    analyzeMetod(clazz, "get", method, methods);
                    analyzeMetod(clazz, "is", method, methods);
                }
            }
        }
        
        return methods;
    }

    private void serializeToWriter(Object value, XMLStreamWriter xmlWriter) {
        if (serialised.containsKey(value)) {
            throw new RuntimeException("Can not serialize object");
        }
        
        if (value == null) {
            return;
        }
        serialised.put(value, null);
        
        try {
            Class<?> clazz = value.getClass();
            if (isPrimitiveType(clazz)) {
                xmlWriter.writeCharacters(value.toString());
                return;
            }
            BindingType annotation = (BindingType) clazz.getAnnotation(BindingType.class);

            if (annotation == null || annotation.value().equals(MembersToBind.FIELDS)) {
                for (FieldData data : fields.get(clazz).values()) {
                    Object val = data.field.get(value);
                    if (val != null) {
                        xmlWriter.writeStartElement(data.name);
                        if (!data.asXmlData || !isPrimitiveType(data.clazz)) {
                            serializeToWriter(val, xmlWriter);
                        } else {
                            xmlWriter.writeCData(val.toString());
                        }
                        xmlWriter.writeEndElement();
                    }
                }
            } else {
                for (MethodData pair : methods.get(clazz).values()) {
                    Object val = pair.get.invoke(value);
                    if (val != null) {
                        xmlWriter.writeStartElement(firstCharToLowerCase(pair.name));
                        if (!pair.asXmlCdata || !isPrimitiveType(pair.clazz)) {
                            serializeToWriter(val, xmlWriter);
                        } else {
                            xmlWriter.writeCData(val.toString());
                        }
                        xmlWriter.writeEndElement();
                    }
                }
            }
        } catch (Exception expt) {
            throw new RuntimeException("Error during serialization");
        }
    }

    @Override
    public byte[] serialize(T value) {
        if (value == null) {
            throw new RuntimeException("Null pointer");
        }
        if (!getClazz().equals(value.getClass())) {
            throw new RuntimeException("Bad class");
        }
        serialised.clear();

        StringWriter writer = null;
        try {
            writer = new StringWriter();
            XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
            xmlWriter.writeStartElement(type);
            serializeToWriter(value, xmlWriter);
            xmlWriter.writeEndElement();
        } catch (Exception expt) {
            throw new RuntimeException("Error during serialization");
        } finally {
            Utils.tryClose(writer);
        }
        
        return writer.getBuffer().toString().getBytes();
    }
    
    private String firstCharToLowerCase(String str) {
        if (str.length() > 1) {
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        } 
        
        return str.toLowerCase();
    }

    private boolean isPrimitiveType(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.equals(Boolean.class) || clazz.equals(Byte.class)
            || clazz.equals(Character.class) || clazz.equals(Short.class) || clazz.equals(Integer.class) 
            || clazz.equals(Long.class) || clazz.equals(Float.class) || clazz.equals(Double.class)
            || clazz.isEnum() || clazz.equals(String.class);
    }

    private Object getPrimitiveValue(String val, Class clazz) {
        if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
            return Boolean.parseBoolean(val);
        } 
        
        if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
            return Byte.parseByte(val);
        } 
        
        if (clazz.equals(Character.class) || clazz.equals(char.class)) {
            if (val.length() == 1) {
                return val.charAt(0);
            }
            throw new RuntimeException("Bad char");
        } 
        
        if (clazz.equals(Short.class) || clazz.equals(short.class)) {
            return Short.parseShort(val);
        } 
        
        if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return Integer.parseInt(val);
        } 
        
        if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            return Long.parseLong(val);
        } 
        
        if (clazz.equals(Float.class) || clazz.equals(float.class)) {
            return Float.parseFloat(val);
        } 
        
        if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            return Double.parseDouble(val);
        } 
        
        if (clazz.isEnum()) {
            return Enum.valueOf(clazz, val);
        } 
        
        if (clazz.equals(String.class)) {
            return val;
        }
        
        throw new RuntimeException("Not a primitive type");
    }

    public T deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException("Empty bytes");
        }
        
        StringReader reader = null;
        try {
            reader = new StringReader(new String(bytes));
            InputSource source = new InputSource(reader);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
            if (!document.getDocumentElement().getTagName().equals(type)) {
                throw new RuntimeException("Unsupported class");
            }
            return (T) getDeserializedValue(document.getDocumentElement(), getClazz());
        } catch (Exception excp) {
            throw new RuntimeException(excp);
        } finally {
            Utils.tryClose(reader);
        }
    }
    
    private Object getDeserializedValue(Element element, Class<?> clazz) {
        if (isPrimitiveType(clazz)) {
            return getPrimitiveValue(element.getTextContent(), clazz);
        }
        
        try {
            BindingType annotation = (BindingType) clazz.getAnnotation(BindingType.class);
            Object rtrn = newObject(clazz);
            NodeList child = element.getChildNodes();
            
            if (annotation == null || annotation.value().equals(MembersToBind.FIELDS)) {
                HashMap<String, FieldData> serializedFields = fields.get(clazz);
                if (serializedFields == null) {
                    throw new RuntimeException("Can not deserialize object");
                }
                HashSet<FieldData> unused = new HashSet<>(serializedFields.values());
                for (int i = 0; i < child.getLength(); ++i) {
                    Node node = child.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        FieldData data = serializedFields.get(((Element) node).getTagName());
                        if (data == null) {
                            continue;
                        }
                        data.field.set(rtrn, getDeserializedValue((Element) node, data.clazz));
                        unused.remove(data);
                    }
                }
                for (FieldData data : unused) {
                    if (!data.clazz.isPrimitive()) {
                        data.field.set(rtrn, null);
                    }
                }
                return rtrn;
            } else {
                if (methods.get(clazz) == null) {
                    throw new RuntimeException("Can not deserialize object");
                }
                for (int i = 0; i < child.getLength(); ++i) {
                    Node node = child.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        MethodData gas = methods.get(clazz).get(((Element) node).getTagName());
                        if (gas == null) {
                            continue;
                        }
                        gas.set.invoke(rtrn, getDeserializedValue((Element) node, gas.clazz));
                    }
                }
                return rtrn;
            }
        } catch (Exception expt) {
            throw new RuntimeException(expt);
        }
    }
}
