package org.codehaus.plexus.spe.utils;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DocumentWrapper
{
    private Document document;

    private Element element;

    public DocumentWrapper( Document document )
    {
        if ( document == null )
        {
            throw new IllegalArgumentException( "argument cannot be null" );
        }

        Element documentElement = document.getDocumentElement();

        this.document = document;
        this.element = documentElement;
    }

    public DocumentWrapper( Element element )
    {
        if ( element == null )
        {
            throw new IllegalArgumentException( "argument cannot be null" );
        }

        this.document = element.getOwnerDocument();
        this.element = element;
    }

    public DocumentWrapper( String rootTag )
    {
        this( DocumentUtils.getEmptyDocument( rootTag ) );
    }

    public String getChildText( String tag )
        throws ProcessException
    {
        return getChild( tag ).getText();
    }

    public String getChildText( String tag, String defaultText )
        throws ProcessException
    {
        NodeList elements = element.getElementsByTagName( tag );

        if ( elements.getLength() == 0 )
        {
            return defaultText;
        }

        if ( elements.getLength() > 1 )
        {
            throw new ProcessException( "Invalid configuration: Duplicate values of child element '" + tag + "' of '" +
                element.getTagName() + "'." );
        }

        Node node = elements.item( 0 );

        System.err.println( "DocumentWrapper.getChildText" );
        System.err.println( "node.getNodeType() = " + node.getNodeType() );

        String text = node.getTextContent();

        if ( text == null || StringUtils.isEmpty( text ) )
        {
            throw new ProcessException( "Invalid configuration: Empty child '" + tag + "'." );
        }

        return text;
    }

    public DocumentWrapper getChild( String tag )
        throws ProcessException
    {
        NodeList elements = element.getElementsByTagName( tag );

        if ( elements.getLength() == 0 )
        {
            throw new ProcessException( "Invalid configuration: Missing required child element '" + tag + "' of '" +
                element.getTagName() + "'." );
        }

        if ( elements.getLength() > 1 )
        {
            throw new ProcessException( "Invalid configuration: Duplicate values of child element '" + tag + "' of '" +
                element.getTagName() + "'." );
        }

        return new DocumentWrapper( (Element) elements.item( 0 ) );
    }

    public DocumentWrapper getOptionalChild( String tag )
        throws ProcessException
    {
        NodeList elements = element.getElementsByTagName( tag );

        if ( elements.getLength() == 0 )
        {
            return null;
        }

        if ( elements.getLength() > 1 )
        {
            throw new ProcessException( "Invalid configuration: Duplicate values of child element '" + tag + "' of '" +
                element.getTagName() + "'." );
        }

        return new DocumentWrapper( (Element) elements.item( 0 ) );
    }

    public DocumentWrapper[] getChildren()
        throws ProcessException
    {
        NodeList nodes = element.getChildNodes();

        DocumentWrapper[] children = new DocumentWrapper[ nodes.getLength() ];

        for ( int i = 0; i < nodes.getLength(); i++ )
        {
            Node node = nodes.item( i );

            if ( node.getNodeType() != Node.ELEMENT_NODE )
            {
                throw new ProcessException( "All children of '" + element.getTagName() + "' must be elements." );
            }

            children[ i ] = new DocumentWrapper( (Element) node );
        }

        return children;
    }

    public String getText()
    {
        return element.getTextContent();
    }

    public Document getDocument()
    {
        if ( document == null )
        {
            throw new RuntimeException( "document == null" );
        }

        return document;
    }

    public void addChildText( String tag, String text )
    {
        Element element = document.createElement( tag );

        element.setTextContent( text );

        this.element.appendChild( element );
    }
}
