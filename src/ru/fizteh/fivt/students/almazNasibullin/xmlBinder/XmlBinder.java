package ru.fizteh.fivt.students.almazNasibullin.xmlBinder;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.misc.Unsafe;
import ru.fizteh.fivt.bind.AsXmlAttribute;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;
import ru.fizteh.fivt.students.almazNasibullin.IOUtils;
import ru.fizteh.fivt.students.almazNasibullin.xmlBinder.FieldWithName;
/**
 * 25.11.12
 * @author almaz
 */

public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T> {
    private Map<Class, List<PairMethodsToSerialization> > methods = new 
            HashMap<Class, List<PairMethodsToSerialization> >();
    private Map<Class, List<FieldWithName> > fields = new HashMap<Class, List<FieldWithName> >();
    private String className;

    public XmlBinder(Class<T> clazz) {
        super(clazz);
        className = firstCharToLowerCase(clazz.getSimpleName());
        addForSerialization(clazz);
    }

    private void addForSerialization(Class clazz) {
        if (methods.containsKey(clazz) || fields.containsKey(clazz) ||
                isPrimitive(clazz)) {
            return;
        }
        BindingType bt = (BindingType)clazz.getAnnotation(BindingType.class);
        if (bt == null || bt.value().equals(MembersToBind.FIELDS)) {
            addToFields(clazz);
        } else {
            addToMethods(clazz);
        }
    }

    private void addToFields(Class clazz) {
        if (!fields.containsKey(clazz)) {
            List<Field> allFields = new ArrayList<Field>();
            Class parent = clazz;
            while (parent != null) {
                Field[] f = parent.getDeclaredFields();
                allFields.addAll(Arrays.asList(f));
                parent = parent.getSuperclass();
            }
            List<FieldWithName> allFieldWithName = new ArrayList<FieldWithName>();
            for (Field f : allFields) {
                allFieldWithName.add(new FieldWithName(f));
            }
            fields.put(clazz, allFieldWithName);
            allFieldWithName = fields.get(clazz);
            for (FieldWithName f : allFieldWithName) {
                f.f.setAccessible(true);
                addForSerialization(f.f.getType());
            }
        }
    }

    private void addToMethods(Class clazz) {
        if (!methods.containsKey(clazz)) {
            List<PairMethodsToSerialization> pairMethods = new ArrayList
                    <PairMethodsToSerialization>();
            Method[] allMethods = clazz.getMethods();
            for (Method m : allMethods) {
                String name = "";
                String getterOrSetter = "getter";
                if (m.getParameterTypes().length == 0) {
                    if (m.getName().matches("get.+")) {
                        name = m.getName().substring(3);
                    } else if (m.getName().matches("is.+") && (
                            m.getReturnType().equals(Boolean.class) ||
                            m.getReturnType().equals(boolean.class))) {
                        name = m.getName().substring(2);
                    }
                }
                if (m.getParameterTypes().length == 1 && m.getName().matches("set.+")) {
                    getterOrSetter = "setter";
                    name = m.getName().substring(3);
                }
                if (!name.equals("")) {
                    boolean exist = false;
                    for (PairMethodsToSerialization pm : pairMethods) {
                        if (pm.name.equals(name)) {
                            exist = true;
                            pm.setMethod(m, getterOrSetter);
                        }
                    }
                    if (!exist) {
                        PairMethodsToSerialization pm = new PairMethodsToSerialization(name);
                        pm.setMethod(m, getterOrSetter);
                        pairMethods.add(pm);
                    }
                }
            }
            List<PairMethodsToSerialization> toSerialization = new ArrayList
                    <PairMethodsToSerialization>();
            for (PairMethodsToSerialization pm : pairMethods) {
                if (pm.getter != null && pm.setter != null) {
                    AsXmlAttribute attributeGetter = pm.getter.getAnnotation(AsXmlAttribute.class);
                    AsXmlAttribute attributeSetter = pm.setter.getAnnotation(AsXmlAttribute.class);
                    if (attributeGetter != null && attributeSetter != null &&
                            !attributeGetter.name().equals(attributeSetter.name())){
                        throw new RuntimeException("Different names of AsXmlAttribute"
                                + " for getter and setter");
                    }
                    toSerialization.add(pm);
                    addForSerialization(pm.getter.getReturnType());
                }
            }
            methods.put(clazz, toSerialization);
            List<PairMethodsToSerialization> l = methods.get(clazz);
            for (PairMethodsToSerialization pm : l) {
                pm.getter.setAccessible(true);
                pm.setter.setAccessible(true);
            }
        }
    }

    private boolean isPrimitive(Class clazz) {
        return clazz.isEnum() || clazz.isPrimitive() || clazz.equals(Boolean.class)
                || clazz.equals(Byte.class) || clazz.equals(Character.class) ||
                clazz.equals(Short.class) || clazz.equals(Integer.class) ||
                clazz.equals(Long.class) || clazz.equals(Float.class) ||
                clazz.equals(Double.class) || clazz.equals(String.class);
    }

    private String firstCharToLowerCase(String s) {
        if (s.length() > 1) {
            return (Character.toLowerCase(s.charAt(0)) + s.substring(1));
        }
        return s.toLowerCase();
    }

    private String getName(Field f) {
        AsXmlAttribute attribute = f.getAnnotation(AsXmlAttribute.class);
        if (attribute != null) {
            return attribute.name();
        }
        return f.getName();
    }

    private String getName(Method m, String curName) {
        AsXmlAttribute attribute = m.getAnnotation(AsXmlAttribute.class);
        if (attribute != null) {
            return attribute.name();
        }
        return firstCharToLowerCase(curName);
    }

    private void writeFieldAsAttribute(Field f, XMLStreamWriter xmlsw, Object o, String name) {
        try {
            xmlsw.writeAttribute(name, f.get(o).toString());
        } catch (Throwable cause) {
            throw new RuntimeException("Something bad occured during field serialization",
                    cause);
        }
    }

    private void serializeObject(Object o, XMLStreamWriter xmlsw,
            Set<Object> serialized) {
        if (serialized.contains(o)) {
            throw new RuntimeException("It is impossible to serialize this object");
        }
        if (o == null) {
            return;
        }
        Class clazz = o.getClass();
        try {
            if (isPrimitive(clazz)) {
                xmlsw.writeCharacters(o.toString());
                return;
            }
            serialized.add(o);
            BindingType bt = (BindingType)clazz.getAnnotation(BindingType.class);
            if (bt == null || bt.value().equals(MembersToBind.FIELDS)) {
                List<FieldWithName> allFieldWithName = fields.get(clazz);
                for (int i = 0; i < allFieldWithName.size(); ++i) {
                    Field f = allFieldWithName.get(i).f;
                    if (f.get(o) != null) {
                        String name = getName(f);
                        allFieldWithName.get(i).name = name;
                        if (f.getAnnotation(AsXmlAttribute.class) == null) {
                            xmlsw.writeStartElement(name);
                            int k = i + 1;
                            while (k < allFieldWithName.size()) {
                                Field field = allFieldWithName.get(k).f;
                                if (field.get(o) != null) {
                                    if (field.getAnnotation(AsXmlAttribute.class) != null) {
                                        String s = getName(field);
                                        allFieldWithName.get(k).name = name;
                                        writeFieldAsAttribute(field, xmlsw, o, s);
                                        ++k;
                                    } else {
                                        break;
                                    }
                                } else {
                                    ++k;
                                }
                            }
                            i = --k;
                            serializeObject(f.get(o), xmlsw, serialized);
                            xmlsw.writeEndElement();
                        } else {
                            writeFieldAsAttribute(f, xmlsw, o, name);
                        }
                    }
                }
            } else {
                 List<PairMethodsToSerialization> pairMethods = methods.get(clazz);
                 for (int i = 0; i < pairMethods.size(); ++i) {
                     PairMethodsToSerialization pm = pairMethods.get(i);
                     String name = getName(pm.getter, pm.name);
                     name = getName(pm.setter, name);
                     Object newObject = pm.getter.invoke(o);
                     if (newObject != null) {
                         if (pm.getter.getAnnotation(AsXmlAttribute.class) == null
                                  && pm.setter.getAnnotation(AsXmlAttribute.class) == null) {
                            xmlsw.writeStartElement(name);
                            int k = i + 1;
                            while (k < pairMethods.size()) {
                                PairMethodsToSerialization m = pairMethods.get(k);
                                String s = getName(m.getter, m.name);
                                Object obj = m.getter.invoke(o);
                                if (obj != null) {
                                    if (m.getter.getAnnotation(AsXmlAttribute.class) != null) {
                                        xmlsw.writeAttribute(s, obj.toString());
                                        ++k;
                                    } else {
                                        break;
                                    }
                                } else {
                                    ++k;
                                }
                            }
                            i = --k;
                            serializeObject(newObject, xmlsw, serialized);
                            xmlsw.writeEndElement();
                         } else {
                             xmlsw.writeAttribute(name, newObject.toString());
                         }
                     }
                 }
            }
        } catch (Throwable cause) {
            throw new RuntimeException("Something bad occured during serialization",
                    cause);
        }
    }

    public String mySerialize(T value) {
        if (value == null) {
            throw new RuntimeException("Bad value: null pointer");
        }
        if (!getClazz().equals(value.getClass())) {
            throw new RuntimeException("Incompatible types");
        }
        Set<Object> serialized = new HashSet<Object>();
        StringWriter sw = null;
        String result = "";
        try {
            sw = new StringWriter();
            XMLStreamWriter xmlsw = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
            xmlsw.writeStartElement(className);
            serializeObject(value, xmlsw, serialized);
            xmlsw.writeEndElement();
            result = sw.getBuffer().toString();
        } catch (Throwable cause) {
            throw new RuntimeException("Something bad occured during serialization",
                    cause);
        } finally {
            IOUtils.closeOrExit(sw);
        }
        return result;
    }

    @Override
    public byte[] serialize(T value) {
        return mySerialize(value).getBytes();
    }

    private Object getPrimitive(String value, Class clazz) {
        if (clazz.isEnum()) {
            return Enum.valueOf(clazz, value);
        }
        if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
            return Boolean.parseBoolean(value);
        }
        if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
            return Byte.parseByte(value);
        }
        if (clazz.equals(Character.class) || clazz.equals(char.class)) {
            if (value.length() == 1) {
                return value.charAt(0);
            }
            return new RuntimeException("Bad value of char");
        }
        if (clazz.equals(Short.class) || clazz.equals(short.class)) {
            return Short.parseShort(value);
        }
        if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return Integer.parseInt(value);
        }
        if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            return Long.parseLong(value);
        }
        if (clazz.equals(Float.class) || clazz.equals(float.class)) {
            return Float.parseFloat(value);
        }
        if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            return Double.parseDouble(value);
        }
        return value;
    }

    private Object getNewObject(Class clazz) {
        try {
            Constructor c = clazz.getConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (Exception e) {}
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(Unsafe.class);
            return unsafe.allocateInstance(clazz);
        } catch (Throwable t) {
            throw new RuntimeException("Cannot create new object");
        }
    }

    private Object deserializeObject(Element e, Class clazz) {
        if (isPrimitive(clazz)) {
           return getPrimitive(e.getTextContent(), clazz);
        }
        Object o = getNewObject(clazz);
        try {
            BindingType bt = (BindingType)clazz.getAnnotation(BindingType.class);
            if (bt == null || bt.value().equals(MembersToBind.FIELDS)) {
                 List<FieldWithName> allFieldWithName = fields.get(clazz);
                 Set<FieldWithName> nullFields = new HashSet<FieldWithName>(allFieldWithName);
                 if (allFieldWithName != null) {
                     NodeList nl = e.getChildNodes();
                     for (int i = 0; i < nl.getLength(); ++i) {
                         Node n = nl.item(i);
                         if (n.getNodeType() == Node.ELEMENT_NODE) {
                             Element el = (Element)n;
                             FieldWithName f = null;
                             for (FieldWithName fi : allFieldWithName) {
                                 if (fi.name.equals(el.getTagName())) {
                                     f = fi;
                                     break;
                                 }
                             }
                             if (f != null) {
                                 f.f.set(o, deserializeObject(el, f.f.getType()));
                                 nullFields.remove(f);
                             }
                         }
                     }
                     NamedNodeMap nnm = e.getAttributes();
                     for (int i = 0; i < nnm.getLength(); ++i) {
                         Node n = nnm.item(i);
                         FieldWithName f = null;
                         for (FieldWithName fi : allFieldWithName) {
                             if (fi.name.equals(n.getNodeName())) {
                                 f = fi;
                                 break;
                             }
                         }
                         if (f != null) {
                            f.f.set(o, getPrimitive(n.getNodeValue(), f.f.getType()));
                            nullFields.remove(f);
                         }
                     }
                 }
                 for (FieldWithName f : nullFields) {
                     if (f != null) {
                         f.f.set(o, null);
                     }
                 }
            } else {
                List<PairMethodsToSerialization> pairMethods = methods.get(clazz);
                if (pairMethods != null) {
                    NodeList nl = e.getChildNodes();
                    for (int i = 0; i < nl.getLength(); ++i) {
                        Node n = nl.item(i);
                        if (n.getNodeType() == Node.ELEMENT_NODE) {
                            Element el = (Element)n;
                            PairMethodsToSerialization pm = null;
                            for (PairMethodsToSerialization pmm : pairMethods) {
                                if (firstCharToLowerCase(pmm.name).equals(el.getTagName())) {
                                    pm = pmm;
                                    break;
                                }
                            }
                            if (pm != null) {
                                pm.setter.invoke(o, deserializeObject(el,
                                            pm.getter.getReturnType()));
                            }
                        }
                    }
                }
            }
        } catch(Throwable cause) {
            throw new RuntimeException("Something bad occured during deserialization",
                    cause);
        }
        return o;
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException("Bad bytes for serialization");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        T result = null;
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bais);
            if (!doc.getDocumentElement().getTagName().equals(className)) {
                throw new RuntimeException("Incompatible types");
            }
            result = (T)deserializeObject(doc.getDocumentElement(), getClazz());
        } catch (Throwable cause) {
            throw new RuntimeException("Something bad occured during deserialization1",
                    cause);
        } finally {
            IOUtils.closeOrExit(bais);
        }
        return result;
    }
}
