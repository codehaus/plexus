package org.codehaus.plexus.spe.services.io;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.codehaus.plexus.spe.utils.DocumentUtils;
import org.codehaus.plexus.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.util.Iterator;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DocumentConverter
    implements Converter
{
    // -----------------------------------------------------------------------
    // Converter Implementation
    // -----------------------------------------------------------------------

    public void marshal( Object o, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        Document document = (Document) o;

//        System.out.println( "-----------------------------------------------------" );
//        System.out.println( "DocumentConverter.marshal" );
//        System.out.println( "-----------------------------------------------------" );
        marshal( document.getDocumentElement(), writer );
//        System.out.println( "-----------------------------------------------------" );
    }

    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context )
    {
//        System.out.println( "-----------------------------------------------------" );
//        System.out.println( "DocumentConverter.unmarshal" );
//        System.out.println( "-----------------------------------------------------" );
        Document document = DocumentUtils.getEmptyDocument( reader.getNodeName() );

        unmarshal( document.getDocumentElement(), reader );

//        System.out.println( "-----------------------------------------------------" );
        return document;
    }

    public boolean canConvert( Class aClass )
    {
        return Document.class.isAssignableFrom( aClass );
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    private void marshal( Element element, HierarchicalStreamWriter writer )
    {
//        System.out.println( "new node: " + element.getTagName() );

        element.normalize();

//        if ( element.getChildNodes().getLength() == 0 )
//        {
//            System.out.println( "element.getTextContent() = " + element.getTextContent() );
//        }

        // -----------------------------------------------------------------------
        // Attributes
        // -----------------------------------------------------------------------

        NamedNodeMap attributes = element.getAttributes();

//        System.out.println( "attributes.getLength() = " + attributes.getLength() );
        int length = attributes.getLength();

        for ( int i = 0; i < length; i++ )
        {
            String key = attributes.item( i ).getNodeName();
            String value = attributes.item( i ).getNodeValue();
//            System.out.println( "marshall: attribute: " + key + " = " + value );
            writer.addAttribute( key, value );
        }

        // -----------------------------------------------------------------------
        // Elements
        // -----------------------------------------------------------------------

        NodeList nodes = element.getChildNodes();

//        System.out.println( "nodes.getLength() = " + nodes.getLength() );
        length = nodes.getLength();

        for ( int i = 0; i < length; i++ )
        {
            Node node = nodes.item( i );

            if ( node.getNodeType() == Node.ELEMENT_NODE )
            {
                Element child = (Element) node;

                writer.startNode( child.getTagName() );
                marshal( child, writer );
                writer.endNode();
            }
            else if ( node.getNodeType() == Node.TEXT_NODE )
            {
                Text text = ( (Text) node );
//                System.out.println( "text.getWholeText() = " + text.getWholeText() );
//                System.out.println( "text.getTextContent() = " + text.getTextContent() );
                writer.setValue( text.getWholeText() );
            }
//            else
//            {
//                System.out.println( "node.getNodeType() = " + node.getNodeType() );
//            }
        }
    }

    private void unmarshal( Element element, HierarchicalStreamReader reader )
    {
        // -----------------------------------------------------------------------
        // Copy all attributes
        // -----------------------------------------------------------------------

        Iterator attributeNames = reader.getAttributeNames();
        while ( attributeNames.hasNext() )
        {
            String name = (String) attributeNames.next();

//            System.out.println( "attr: " + name + "=" + reader.getAttribute( name ) );

            element.setAttribute( name, reader.getAttribute( name ) );
        }

        // -----------------------------------------------------------------------
        // Copy all elements
        // -----------------------------------------------------------------------

        if ( !reader.hasMoreChildren() )
        {
            String value = reader.getValue();
//            System.out.println( "element '" + element.getTagName() + "' is empty, setting text content: " + value );

            if ( StringUtils.isNotEmpty( value ) )
            {
                Text text = element.getOwnerDocument().createTextNode( value );
                element.appendChild( text );
            }
        }
        else
        {
            while( reader.hasMoreChildren() )
            {
                reader.moveDown();
//                System.out.println( "new child '" + reader.getNodeName() + "' of " + element.getTagName() );

                Element child = element.getOwnerDocument().createElement( reader.getNodeName() );
                element.appendChild( child );

                unmarshal( child, reader );
                reader.moveUp();
            }
        }
    }
}
