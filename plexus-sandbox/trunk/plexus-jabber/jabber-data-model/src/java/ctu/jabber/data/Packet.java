package ctu.jabber.data;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author PV
 * @version 1.0
 */
public class Packet extends Element {

    // Constructors
    public Packet(String name) { super(name); }
    public Packet(XmlPullParser xpp) { super(xpp); setNamespace("");}

    // Transitable interface implementation
    public String getTo() { return getAttribute("to"); }
    public void setTo(String recipient) { setAttribute("to", recipient); }
    public String getFrom() { return getAttribute("from"); }
    public void setFrom(String sender) { setAttribute("from", sender); }
    public String getType() { return getAttribute("type"); }
    public void setType(String type) { setAttribute("type", type); }
    public String getId() { return getAttribute("id"); }
    public void setId(String ID) { setAttribute("id", ID); }
    public void setError(String errormsg) {
        this.setChildText("error", errormsg);
    }
    public void setErrorCode(int errorCode) {
        setChildAttribute("error", "code", Integer.toString(errorCode));
    }
}
