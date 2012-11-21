package ru.fizteh.fivt.students.fedyuninV.bind;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T>{


    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    TransformerFactory transformerFactory = TransformerFactory.newInstance();

    public XmlBinder(Class<T> clazz) {
        super(clazz);
    }


    private boolean checkPrefix(String prefix, String methodName) {
        return (methodName.length() >= prefix.length()  &&  methodName.substring(0, prefix.length()).equals(prefix));
    }

    private void writeToDocument(Document document, Object value, Element root) throws Exception{
        List<SerializeComponent> components = new ArrayList<>();
        BindingType bindingType = value.getClass().getAnnotation(BindingType.class);
        Method[] methods = null;
        if (bindingType == null  ||  bindingType.equals(MembersToBind.GETTERS_AND_SETTERS)) {
            methods = value.getClass().getDeclaredMethods();
        } else {
            methods = value.getClass().getDeclaredMethods();
        }

        //building components to Bind

        for (Method method: methods) {
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
                            System.err.println("Indefinite pair of methods found near " + method.getName() + " of " + value.getClass().getName()); //Cannot use exceptions because
                            System.exit(1);                                         //abstract method doesn't throw it
                        }
                    }
                }
                if (!componentFound) {
                    SerializeComponent newComponent = new SerializeComponent(name);
                    if (!newComponent.setMethod(method, mode)) {
                        System.err.println("Error while creating new component");
                        System.exit(1);
                    }
                    components.add(newComponent);
                }
            }
        }
        for (SerializeComponent component: components) {
            String name = component.getName();
            if (name.charAt(0) >= 'A'  &&  name.charAt(0) <= 'Z') {
                name = ((char)(name.charAt(0) + 'a' - 'A')) + name.substring(1);
            }
            Element child = document.createElement(name);

            Object newValue = component.getter().invoke(value);
            if (newValue != null) {
                root.appendChild(child);
                if (newValue.getClass().getName() == "java.lang.Integer"
                        ||  newValue.getClass().getName() == "java.lang.Boolean"
                        ||  newValue.getClass().getName() == "java.lang.String"
                        ||  newValue.getClass().getName() == "java.lang.Double"
                        ||  newValue.getClass().getName() == "java.lang.Float"
                        ||  newValue.getClass().getName() == "java.lang.BigInteger"
                        ||  newValue.getClass().isEnum()) {
                    child.setTextContent(newValue.toString());
                } else {
                    writeToDocument(document, newValue, child);
                }
            }
            //document.appendChild(child);
        }
    }

    @Override
    public byte[] serialize(Object value) {

        //Creating XML
        try {
            Document document = factory.newDocumentBuilder().newDocument();
            Element root = document.createElement(value.getClass().getName());
            writeToDocument(document, value, root);
            document.appendChild(root);
            StringWriter writer = new StringWriter();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount",
                    "2"
            );
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            System.out.println(writer.getBuffer().toString());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public T deserialize(byte[] bytes) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
