package net.java.dev.openim.data.jabber;

import ctu.jabber.data.Packet;
import ctu.jabber.data.Element;
import net.java.dev.openim.data.Deferrable;
import net.java.dev.openim.data.Transitable;

import java.util.Iterator;
import java.util.Map;
import java.io.StringWriter;

/**
 * @author PV
 * @version 1.0
 */
public class DeferrableTransitable implements Deferrable, Transitable {

    String m_name;
    String m_to = "";
    String m_from = "";
    String m_type = "";
    String m_attributes = "";
    String m_content = "";
    String m_error = "";
    String m_errcode = "";

    public DeferrableTransitable(Packet packet) {
        m_name = packet.getName();
        Map attributes = packet.getAttributes();
        Iterator iAttributes = attributes.keySet().iterator();
        while (iAttributes.hasNext()) {
            String name = (String) iAttributes.next();
            if (name.equals("to")) setTo((String) attributes.get(name));
            else if (name.equals("from")) setFrom((String) attributes.get(name));
            else if (name.equals("type")) setType((String) attributes.get(name));
            else m_attributes += " " + name + "='" + (String) attributes.get(name) + "'";
        }
        Element error = packet.getFirstChild("error");
        if (error != null) {
            m_error = error.getText();
            m_errcode = error.getAttribute("code");
            packet.getChildren().remove(error);
        }
        StringWriter sw = new StringWriter();
        try {
            packet.writeInnerXML(sw, "");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        m_content = sw.toString();
    }

    public void setTo(String to) {
        m_to = to;
    }
    public String getTo() {
        return m_to;
    }

    public void setFrom(String from) {
        m_from = from;
    }
    public String getFrom() {
        return m_from;
    }

    public void setType(String type) {
        m_type = type;
    }
    public String getType() {
        return m_type;
    }

    public void setError(String errormsg) {
        m_error = errormsg;
    }

    public void setErrorCode(int errorCode) {
        m_errcode = Integer.toString(errorCode);
    }

    public String toString(String encoding) {
        Packet packet = new Packet(m_name);
        packet.setFrom(getFrom());
        packet.setTo(getTo());
        packet.setType(getType());
        packet.addSerializedAttribute(m_attributes);
        packet.addSerializedChild(m_content);
        if (m_errcode.length() > 0 || m_error.length() > 0) {
            packet.setChildText("error", m_error);
            packet.setChildAttribute("error", "code", m_errcode);
        }
        return packet.toString(encoding);
    }

    public String toString() {
        return toString("");
    }
}
