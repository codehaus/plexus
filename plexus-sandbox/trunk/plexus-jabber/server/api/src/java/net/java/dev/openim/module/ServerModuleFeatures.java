package net.java.dev.openim.module;

import ctu.jabber.data.Element;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.io.StringWriter;

/**
 * @author PV
 * @version 1.0
 */
public class ServerModuleFeatures {
    private Set m_features = new HashSet();

    public Set getFeatures() { return m_features; }

    public String toString(String encoding) {
        StringWriter sw = new StringWriter();
        Iterator i = getFeatures().iterator();
        while (i.hasNext()) {
            String feature = (String) i.next();
            Element e = new Element("feature");
            e.setAttribute("var", feature);
            try {
                e.writeXML(sw, "", encoding);
            } catch(Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
        return sw.toString();
    }

    public String toString() {
        return toString("");
    }
}
