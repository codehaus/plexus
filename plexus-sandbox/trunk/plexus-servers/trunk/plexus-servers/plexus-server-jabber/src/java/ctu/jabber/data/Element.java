package ctu.jabber.data;

import java.util.*;
import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import ctu.tools.xml.XMLUtils;

/**
 * @author PV
 * @version 1.0
 * TODO: prefix & attribute namespace support
 * TODO: escape attribute values
 */
public class Element {

    private List m_children = new LinkedList();
    private String m_namespace = "";
    private String m_name;
    private Map m_attributes = new HashMap();
    private String m_serializedAtts = "";

    // Constructors
    public Element(String name) { setName(name); }
    public Element(String name, String text) {
        setName(name);
        setText(text);
    }
    public Element(XmlPullParser xpp) {
        setName(xpp.getName());
        setNamespace(xpp.getNamespace());
        // Copy attributes into hashtable
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            m_attributes.put(xpp.getAttributeName(i), xpp.getAttributeValue(i));
        }
    }

    // name
    public void setName(String name) { this.m_name = name; }
    public String getName() { return m_name; }

    // namespace
    public void setNamespace(String name) {
        if (name == null) m_namespace = "";
        else m_namespace = name;
    }
    public void replaceNamespaceRecursive(String oldname, String name) {
        if (getNamespace().equals(oldname)) {
            setNamespace(name);
        }
        Iterator childIterator = m_children.iterator();
        while (childIterator.hasNext()) {
            Object child = childIterator.next();
            if (child instanceof Element) {
                ((Element)child).replaceNamespaceRecursive(oldname, name);
            }
        }
    }
    public String getNamespace() { return m_namespace; }

    // methods for manipulating with children elements
    public List getChildren() { return m_children; }
    public List getChildren(String childname) {
        List children = new LinkedList();
        Iterator childIterator = m_children.iterator();
        while (childIterator.hasNext()) {
            Object child = childIterator.next();
            if (child instanceof Element) {
                Element childElement = (Element) child;
                if (childElement.getName().equals(childname)) {
                    children.add(childElement);
                }
            }
        }
        return children;
    }
    public void addChild(Object child) { if (child != null) getChildren().add(child); }
    public void addSerializedChild(String content) {
        if (content.length() > 0)
            getChildren().add(new SerializedElement(content));
    }
    public void addChildTexts(final Collection collection, String childname) {
        Iterator i = collection.iterator();
        while (i.hasNext()) {
            String text = i.next().toString();
            Element child = new Element(childname);
            child.setText(text);
            addChild(child);
        }
    }
    public List getChildTexts(String childname) {
        List result = new LinkedList();
        Iterator i = getChildren(childname).iterator();
        while (i.hasNext()) {
            Object next = i.next();
            if (next != null && next instanceof Element) {
                Element child = (Element) next;
                String text = child.getText();
                if (text.length() > 0) {
                    result.add(text);
                }
            }
        }
        return result;
    }
    public Element getFirstChild(String childname) {
        Iterator childIterator = m_children.iterator();
        while (childIterator.hasNext()) {
            Object child = childIterator.next();
            if (child != null && child instanceof Element) {
                Element childElement = (Element) child;
                if (childElement.getName().equals(childname)) {
                    return childElement;
                }
            }
        }
        return null;
    }
    public String getChildText(String childname) {
        Element child = getFirstChild(childname);
        if (child == null) return "";
        return child.getText();
    }
    public void setChildText(String childname, String text) {
        Element child = this.getFirstChild(childname);
        if (child == null) {
            child = new Element(childname, text);
            child.setNamespace(this.getNamespace());
            this.addChild(child);
        }
        else child.setText(text);
    }
    public String getChildAttribute(String childname, String attribute) {
        Element child = getFirstChild(childname);
        if (child == null) return "";
        return child.getAttribute(attribute);
    }
    public void setChildAttribute(String childname, String attribute, String value) {
        Element child = this.getFirstChild(childname);
        if (child == null) {
            child = new Element(childname);
            child.setNamespace(this.getNamespace());
            child.setAttribute(attribute, value);
            this.addChild(child);
        }
        else child.setAttribute(attribute, value);
    }

    // methods for getting and setting element content
    public String getText() {
        String text = "";
        Iterator iChildren = getChildren().iterator();
        while (iChildren.hasNext()) {
            Object o = iChildren.next();
            if (o instanceof String) {
                text += (String) o;
            }
        }
        return text;
    }
    public void setText(String text) {
        text = text.trim();
        getChildren().clear();
        if (text.length() > 0) addChild(text);
    }
    public void appendText(String text) {
        text = text.trim();
        if (text.length() > 0) addChild(text);
    }

    // methods for manipulating with attributes
    public String getAttribute(String attribute) {
        String attrib = (String) m_attributes.get(attribute);
        return (attrib == null)? "": attrib;
    }
    public void setAttribute(String attribute, String value) {
        if (value == null || value.length() == 0) {
            removeAttribute(attribute);
        } else {
            m_attributes.put(attribute, value);
        }
    }
    public void removeAttribute(String attribute) {
        m_attributes.remove(attribute);
    }
    public void clearAttributes() {
        m_attributes.clear();
    }
    public Map getAttributes() { return m_attributes; }
    public void addSerializedAttribute(String attribute) {
        if (attribute.length() > 0) {
            m_serializedAtts += attribute + " ";
        }
    }

    public void writeInnerXML(Writer out, String encoding) throws IOException {
        Iterator childIterator = m_children.iterator();
        while (childIterator.hasNext()) {
            Object child = childIterator.next();
            if (child instanceof Element) {
               ((Element)child).writeXML(out, getNamespace(), encoding);
            } else if (child instanceof String){
                String s = (String) child;
                out.write(XMLUtils.escapeElementEntities(s, encoding));
            } else {
                out.write(child.toString());
            }
        }
    }
    public void writeXML(Writer out, String defaultNamespace, String encoding) throws IOException {
        out.write("<");
        out.write(m_name);

        if (defaultNamespace != null && this.getNamespace() != ""
            && !this.getNamespace().equals(defaultNamespace)) {

            out.write(" xmlns='" + this.getNamespace() + "'");
        }

        Iterator keys = m_attributes.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            out.write(" ");
            out.write(key);
            out.write("='");
            out.write((String) m_attributes.get(key));
            out.write("'");
        }
        if (m_serializedAtts.length() > 0) {
            out.write(" ");
            out.write(m_serializedAtts.trim());
        }

        if (m_children.size() == 0 && getText().length() == 0) {
            out.write("/>");
            out.flush();
            return;
        }

        out.write(">");

        writeInnerXML(out, encoding);

        out.write("</");
        out.write(m_name);
        out.write(">");
        out.flush();
    }

    public String toString(String encoding) {
        try {
            StringWriter sw = new StringWriter();
            writeXML(sw, "", encoding);
            return sw.toString();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return "<" + m_name + ">";
    }

    public String toString() {
        return this.toString("");
    }

    class SerializedElement {
        String m_content = "";

        SerializedElement(String s) {
            if (s != null) m_content = s;
        }

        public String toString() {
            return m_content;
        }
    }
}