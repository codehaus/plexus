package net.java.dev.openim.module;

import ctu.jabber.data.Element;

/**
 * @author PV
 * @version 1.0
 */
public class ServerModuleIdentity {
    private String m_category;
    private String m_type;
    private String m_name;

    public ServerModuleIdentity(String category, String type, String name) {
        m_category = category;
        m_type = type;
        m_name = name;
    }

    public String getCategory() { return m_category; }
    public String getType() { return m_type; }
    public String getName() { return m_name; }

    public String toString(String encoding) {
        Element e = new Element("identity");
        e.setAttribute("category", getCategory());
        e.setAttribute("type", getType());
        e.setAttribute("name", getName());
        return e.toString(encoding);
    }

    public String toString() {
        return toString("");
    }
}
