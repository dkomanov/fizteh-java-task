package ru.fizteh.fivt.students.fedyuninV.bind;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.fizteh.fivt.bind.AsXmlElement;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T>{

    Map<Class, List<SerializeComponent>> methods;
    Map<Class, List<Field>> fields;
    IdentityHashMap<Object, Object> serialized;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Object CONTAINS = new Object();

    public XmlBinder(Class<T> clazz) {
        super(clazz);
        methods = new HashMap<>();
        fields = new HashMap<>();
        addToMap(clazz);
    }

    private void addToMethodMap(Class clazz) {
        if (methods.containsKey(clazz)) { //already in map
            return;
        }
        List<SerializeComponent> components = new ArrayList<>();
        Method[] methodList = clazz.getMethods();
        for (Method method: methodList) {
            String name = null;
            char mode = 'g';
            Class[] args = method.getParameterTypes();
            String methodName = method.getName();
            if (checkPrefix("get", methodName)  &&  args.length == 0) {
                name = methodName.substring(3);
            }
            if (checkPrefix("is", methodName)  &&  args.length == 0) {
                name = methodName.substring(2);
            }
            if (checkPrefix("set", methodName)  &&  args.length == 1) {
                name = methodName.substring(3);
                mode = 's';
            }
            if (name  != null) {
                boolean componentFound = false;
                for (SerializeComponent component: components) {
                    if (component.getName().equals(name)) {
                        componentFound = true;
                        if (!component.setMethod(method, mode)) {
                            throw new RuntimeException("Indefinite pair of methods found in " + clazz.getName());
                        }
                    }
                }
                if (!componentFound) {
                    SerializeComponent newComponent = new SerializeComponent(name);
                    if (!newComponent.setMethod(method, mode)) {
                        throw new RuntimeException("Error while creating new component");
                    }
                    components.add(newComponent);
                }
            }
        }
        List<SerializeComponent> result = new ArrayList<>();
        for (SerializeComponent component: components) {
            if (component.getter() != null  &&  component.setter() != null) {
                result.add(component);
                addToMap(component.getter().getReturnType());
            }
        }
        methods.put(clazz, result);
    }

    private void addToFieldMap(Class clazz) {
        if (fields.containsKey(clazz)) {
            return;
        }
        List<Field> result = new ArrayList<>();
        Class parent = clazz;
        while (parent != null) {
            Field[] fieldList = parent.getDeclaredFields();
            result.addAll(Arrays.asList(fieldList));
            parent = parent.getSuperclass();
        }
        fields.put(clazz, result);
        for (Field field: result) {
            addToMap(field.getType());
        }
    }

    private void addToMap(Class clazz) {
        if (possibleToString(clazz)) {
            return;
        }
        BindingType bindingType = (BindingType) clazz.getAnnotation(BindingType.class);
        if (bindingType == null  ||  bindingType.value().equals(MembersToBind.FIELDS)) {
            addToFieldMap(clazz);
        } else {
            addToMethodMap(clazz);
        }
    }

    private boolean checkPrefix(String prefix, String methodName) {
        return (methodName.length() >= prefix.length()  &&  methodName.substring(0, prefix.length()).equals(prefix));
    }



    private String firstCharToLowerCase(String str) {
        if (str.length() > 0) {
            str = Character.toLowerCase(str.charAt(0)) + str.substring(1);
        }
        return str;
    }

    private String getElementName(Object value, String defaultName) {
        AsXmlElement asXmlElement = value.getClass().getAnnotation(AsXmlElement.class);
        if (asXmlElement == null) {
            return firstCharToLowerCase(defaultName);
        } else {
            return asXmlElement.name();
        }
    }

    private String getElementName(Field value) {
        AsXmlElement asXmlElement = value.getAnnotation(AsXmlElement.class);
        if (asXmlElement == null) {
            return firstCharToLowerCase(value.getName());
        } else {
            return asXmlElement.name();
        }
    }

    private boolean possibleToString(Class classExample) {
        return (classExample.isPrimitive()
                ||  classExample.getName().equals("java.lang.Integer")
                ||  classExample.getName().equals("java.lang.Boolean")
                ||  classExample.getName().equals("java.lang.String")
                ||  classExample.getName().equals("java.lang.Double")
                ||  classExample.getName().equals("java.lang.Float")
                || classExample.getName().equals("java.lang.Byte")
                ||  classExample.getName().equals("java.lang.Long")
                ||  classExample.getName().equals("java.lang.Short")
                ||  classExample.getName().equals("java.lang.Character")
                ||  classExample.isEnum());
    }

    private void writeToDocumentByFields(Document document, Object value, Element root) throws Exception {
        for (Field field: fields.get(value.getClass())) {
            field.setAccessible(true);
            Object fieldValue = field.get(value);
            if (fieldValue != null) {
                Element child = document.createElement(getElementName(field));
                root.appendChild(child);
                if (possibleToString(fieldValue.getClass())) {
                    child.setTextContent(fieldValue.toString());
                } else {
                    writeToDocument(document, fieldValue, child);
                }
            }
        }
    }

    private void writeToDocumentByMethods(Document document, Object value, Element root) throws Exception {
        for (SerializeComponent component: methods.get(value.getClass())) {
            String name = firstCharToLowerCase(component.getName());
            Object newValue = component.getter().invoke(value);
            if (newValue != null) {
                Element child = document.createElement(name);
                root.appendChild(child);
                if (possibleToString(newValue.getClass())) {
                    child.setTextContent(newValue.toString());
                } else {
                    writeToDocument(document, newValue, child);
                }
            }
        }
    }


    private void writeToDocument(Document document, Object value, Element root) throws Exception{
        BindingType bindingType = value.getClass().getAnnotation(BindingType.class);
        if (serialized.put(value, CONTAINS) != null) {
            throw new RuntimeException("Object contains link to itself, cannot serailize");
        }
        if (bindingType == null  ||  bindingType.value().equals(MembersToBind.FIELDS)) {
            writeToDocumentByFields(document, value, root);
        } else {
            writeToDocumentByMethods(document, value, root);
        }
    }



    @Override
    public byte[] serialize(Object value) {
        if (value != null  &&  !value.getClass().equals(getClazz())) {
            throw new RuntimeException("This class is not supported by this binder!");
        }
        serialized = new IdentityHashMap<>();
        //Creating XML
        try {
            Document document = factory.newDocumentBuilder().newDocument();
            if (value != null) {
                Element root = document.createElement(getElementName(value, value.getClass().getName()));
                writeToDocument(document, value, root);
                document.appendChild(root);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Result result = new StreamResult(out);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount",
                    "2"
            );
            /*transformer.transform(new DOMSource(document), result);
            return out.toByteArray();*/
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            System.out.println(stringWriter.getBuffer().toString());
            return null;
        } catch (Exception ex) {
            throw new RuntimeException("Serializing error", ex);
        }
    }

    private Document bytesToXml(byte[] xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(xml));
        } catch (Exception ex) {
            throw new RuntimeException("Incorrect byte array", ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes) {
        Document document = bytesToXml(bytes);
        Element root = document.getDocumentElement();

        return null;
    }
}
