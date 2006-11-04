package ctu.tools.xml;

import ctu.jabber.data.Element;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * @author PV
 * @version 1.0
 */

public class XMLUtils {

    /**
     * Serializes the XML from parser.
     *
     * @param xpp        XML parser.
     * @param serializer XML serializer.
     * @throws XmlPullParserException
     * @throws java.io.IOException
     */
    public static void serialize(final XmlPullParser xpp, final XmlSerializer serializer, boolean ignoreDefaultNamespace) throws XmlPullParserException, java.io.IOException
    {
        if (xpp.getEventType() == XmlPullParser.START_TAG) {
           // serialize start tag
           ignoreDefaultNamespace = serializeToken(xpp, serializer, ignoreDefaultNamespace);
           // serialize tag content
           while (xpp.next() != XmlPullParser.END_TAG) {
               if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    serialize(xpp, serializer, ignoreDefaultNamespace);
               } else {
                   serializeToken(xpp, serializer, false);
               }
           }
           // serialize end tag
           serializeToken(xpp, serializer, ignoreDefaultNamespace);
        } else {
            serializeToken(xpp, serializer, false);
        }
    }

    /**
     * Serializes the currently parsed token.
     *
     * @param xpp        XML parser.
     * @param serializer XML serializer.
     * @throws XmlPullParserException
     * @throws java.io.IOException
     */
    private static boolean serializeToken(final XmlPullParser xpp, final XmlSerializer serializer, boolean ignoreDefaultNamespace) throws XmlPullParserException, java.io.IOException
    {
        switch (xpp.getEventType())
        {
            case XmlPullParser.START_TAG:
                if (xpp.getFeature(XmlPullParser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES) == false)
                {
                    for (int i = xpp.getNamespaceCount(xpp.getDepth() - 1);
                         i <= xpp.getNamespaceCount(xpp.getDepth()) - 1;
                         i++)
                    {
                        String prefix = xpp.getNamespacePrefix(i);
                        if (prefix == null) prefix = ""; // set default namespace
                        serializer.setPrefix(prefix, xpp.getNamespaceUri(i));
                        ignoreDefaultNamespace = false;
                    }

                    if (ignoreDefaultNamespace)
                        serializer.startTag(null, xpp.getName());
                    else
                        serializer.startTag(xpp.getNamespace(), xpp.getName());

                } else {
                    // TODO: Check functionality
                    serializer.startTag(xpp.getNamespace(), xpp.getName());
                }

                for (int i = 0; i < xpp.getAttributeCount(); i++)
                {
                    serializer.attribute(xpp.getAttributeNamespace(i), xpp.getAttributeName(i), xpp.getAttributeValue(i));
                }
                break;

            case XmlPullParser.END_TAG:
                if (ignoreDefaultNamespace)
                    serializer.endTag(null, xpp.getName());
                else
                    serializer.endTag(xpp.getNamespace(), xpp.getName());
                break;

            case XmlPullParser.TEXT:
                serializer.text(xpp.getText());
                break;

            default:
                break;
        }

        return ignoreDefaultNamespace;
    }

    /**
     * Creates an element and its subelements according to the parsed xml content.
     *
     * @return Element.
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static Element createElement(XmlPullParser xpp) throws XmlPullParserException, IOException {
        if (xpp.getEventType() != XmlPullParser.START_TAG) return null;

        Element element = new Element(xpp);
        while (xpp.next() != XmlPullParser.END_TAG) {
            switch (xpp.getEventType()) {
                case XmlPullParser.TEXT:
                    element.appendText(xpp.getText());
                    break;
                case XmlPullParser.START_TAG:
                    element.addChild(createElement(xpp));
                    break;
                default:
                    break;
            }
        }
        return element;
    }

    public static String s_DefaultEncoding = "UTF-8";

    /**
     * This will take the three pre-defined entities in XML 1.0
     * (used specifically in XML elements) and convert their character
     * representation to the appropriate entity reference, suitable for
     * XML element content.
     *
     * @param str <code>String</code> input to escape.
     * @return <code>String</code> with escaped content.
     */
    public static String escapeElementEntities(String str, String encoding) {

        StringBuffer buffer;
        char ch;
        String entity;
        if (encoding == null || encoding.length() < 1)
            encoding = s_DefaultEncoding;
        EscapeStrategy strategy = new EscapeStrategy(encoding);

        buffer = null;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            switch(ch) {
                case '<' :
                    entity = "&lt;";
                    break;
                case '>' :
                    entity = "&gt;";
                    break;
                case '&' :
                    entity = "&amp;";
                    break;
                case '\r' :
                    entity = "&#xD;";
                    break;
                case '\n' :
                    entity = "\r\n";
                    break;
                default :
                    if (strategy.shouldEscape(ch)) {
                        entity = "&#x" + Integer.toHexString(ch) + ";";
                    }
                    else {
                        entity = null;
                    }
                    break;
            }
            if (buffer == null) {
                if (entity != null) {
                    // An entity occurred, so we'll have to use StringBuffer
                    // (allocate room for it plus a few more entities).
                    buffer = new StringBuffer(str.length() + 20);
                    // Copy previous skipped characters and fall through
                    // to pickup current character
                    buffer.append(str.substring(0, i));
                    buffer.append(entity);
                }
            }
            else {
                if (entity == null) {
                    buffer.append(ch);
                }
                else {
                    buffer.append(entity);
                }
            }
        }

        // If there were any entities, return the escaped characters
        // that we put in the StringBuffer. Otherwise, just return
        // the unmodified input string.
        return (buffer == null) ? str : buffer.toString();
    }

}
