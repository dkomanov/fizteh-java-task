package ru.fizteh.fivt.students.yuliaNikonova.userList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.misc.Unsafe;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

public class XmlBinder<T> {

    class FieldWithName {
	public Field field;
	public String name;

	public FieldWithName(Field f) {
	    this.field = f;
	    this.name = f.getName();
	}
    }

    private final Class<T> clazz;
    private Map<Class, List<FieldWithName>> fields = new HashMap<Class, List<FieldWithName>>();

    public XmlBinder(Class<T> clazz) {
	this.clazz = clazz;
	addForSerialization(clazz);
    }

    protected final Class<T> getClazz() {
	return clazz;
    }

    private void addForSerialization(Class clazz) {
	if (fields.containsKey(clazz) || isPrimitive(clazz)) {
	    return;
	}
	BindingType bt = (BindingType) clazz.getAnnotation(BindingType.class);
	if (bt == null || bt.value().equals(MembersToBind.FIELDS)) {
	    addToFields(clazz);
	}
    }

    private void addToFields(Class clazz) {
	if (!fields.containsKey(clazz)) {
	    List<Field> allFields = new ArrayList<Field>();
	    Class parent = clazz;
	    while (parent != null) {
		Field[] classFields = parent.getDeclaredFields();
		allFields.addAll(Arrays.asList(classFields));
		parent = parent.getSuperclass();
	    }
	    List<FieldWithName> allFieldWithName = new ArrayList<FieldWithName>();
	    for (Field field : allFields) {
		allFieldWithName.add(new FieldWithName(field));
	    }
	    fields.put(clazz, allFieldWithName);
	    allFieldWithName = fields.get(clazz);
	    for (FieldWithName fieldWithName : allFieldWithName) {
		fieldWithName.field.setAccessible(true);
		addForSerialization(fieldWithName.field.getType());
	    }
	}
    }

    private boolean isPrimitive(Class clazz) {
	return clazz.isEnum() || clazz.isPrimitive()
		|| clazz.equals(Boolean.class) || clazz.equals(Byte.class)
		|| clazz.equals(Character.class) || clazz.equals(Short.class)
		|| clazz.equals(Integer.class) || clazz.equals(Long.class)
		|| clazz.equals(Float.class) || clazz.equals(Double.class)
		|| clazz.equals(String.class);
    }

    public void writeObject(Object obj, XMLStreamWriter xmlsw) {
	if (obj == null) {
	    return;
	}
	Class clazz = obj.getClass();
	try {
	    if (isPrimitive(clazz)) {
		xmlsw.writeCharacters(obj.toString());
		return;
	    }
	    BindingType bt = (BindingType) clazz.getAnnotation(BindingType.class);
	    if (bt == null || bt.value().equals(MembersToBind.FIELDS)) {
		List<FieldWithName> allFieldWithName = fields.get(clazz);
		for (int i = 0; i < allFieldWithName.size(); ++i) {
		    Field f = allFieldWithName.get(i).field;
		    if (f.get(obj) != null) {
			String name = f.getName();
			allFieldWithName.get(i).name = name;

			xmlsw.writeStartElement(name);
			int k = i + 1;
			while (k < allFieldWithName.size()) {
			    Field field = allFieldWithName.get(k).field;
			    if (field.get(obj) != null) {
				break;
			    } else {
				++k;
			    }
			}
			i = --k;
			writeObject(f.get(obj), xmlsw);
			xmlsw.writeEndElement();
		    }
		}
	    }
	} catch (Throwable cause) {
	    throw new RuntimeException("Something bad occured during writing",
		    cause);
	}
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
	} catch (Exception e) {
	}
	try {
	    Field f = Unsafe.class.getDeclaredField("theUnsafe");
	    f.setAccessible(true);
	    Unsafe unsafe = (Unsafe) f.get(Unsafe.class);
	    return unsafe.allocateInstance(clazz);
	} catch (Throwable t) {
	    throw new RuntimeException("Cannot create new object");
	}
    }

    public Object readObject(Element e, Class clazz) {
	if (isPrimitive(clazz)) {
	    return getPrimitive(e.getTextContent(), clazz);
	}
	Object objectToReturn = getNewObject(clazz);
	try {
	    BindingType bt = (BindingType) clazz.getAnnotation(BindingType.class);
	    if (bt == null || bt.value().equals(MembersToBind.FIELDS)) {
		List<FieldWithName> allFieldWithName = fields.get(clazz);
		Set<FieldWithName> nullFields = new HashSet<FieldWithName>(
			allFieldWithName);
		if (allFieldWithName != null) {
		    NodeList nl = e.getChildNodes();
		    for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
			    Element el = (Element) n;
			    FieldWithName myFieldWithName = null;
			    for (FieldWithName fieldWithName : allFieldWithName) {
				if (fieldWithName.name.equals(el.getTagName())) {
				    myFieldWithName = fieldWithName;
				    break;
				}
			    }
			    if (myFieldWithName != null) {
				myFieldWithName.field.set(objectToReturn,readObject(el, myFieldWithName.field.getType()));
				nullFields.remove(myFieldWithName);
			    }
			}
		    }
		    NamedNodeMap nnm = e.getAttributes();
		    for (int i = 0; i < nnm.getLength(); ++i) {
			Node n = nnm.item(i);
			FieldWithName myFieldWithName = null;
			for (FieldWithName fieldWithName : allFieldWithName) {
			    if (fieldWithName.name.equals(n.getNodeName())) {
				myFieldWithName = fieldWithName;
				break;
			    }
			}
			if (myFieldWithName != null) {
			    myFieldWithName.field.set(objectToReturn, getPrimitive(n.getNodeValue(), myFieldWithName.field.getType()));
			    nullFields.remove(myFieldWithName);
			}
		    }
		}
		for (FieldWithName fieldWithName : nullFields) {
		    if (fieldWithName != null) {
			fieldWithName.field.set(objectToReturn, null);
		    }
		}
	    }
	} catch (Throwable cause) {
	    throw new RuntimeException(
		    "Something bad occured during deserialization", cause);
	}
	return objectToReturn;
    }

}