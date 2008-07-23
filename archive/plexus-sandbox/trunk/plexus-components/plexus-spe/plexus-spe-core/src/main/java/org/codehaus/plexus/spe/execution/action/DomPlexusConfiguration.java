package org.codehaus.plexus.spe.execution.action;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DomPlexusConfiguration
    implements PlexusConfiguration
{
    private Element element;

    public DomPlexusConfiguration( Element element )
    {
        this.element = element;
    }

    // -----------------------------------------------------------------------
    // PlexusConfiguration Implementation
    // -----------------------------------------------------------------------

    public String getName()
    {
//        System.out.println( "DomPlexusConfiguration.getName: " + element.getTagName() );

        return element.getTagName();
    }

    public String getValue()
    {
        NodeList children = element.getChildNodes();

        if  ( children.getLength() == 0 )
        {
            return null;
        }

        Node child = children.item( 0 );

        if ( child.getNodeType() == Node.TEXT_NODE )
        {
            return ( (Text) child ).getWholeText();
        }

        return null;
    }

    public String getValue( String defaultValue )
    {
        String value = getValue();

        if ( value != null )
        {
//            System.out.println( "DomPlexusConfiguration.getValue: " + value );
            return value;
        }

//        System.out.println( "DomPlexusConfiguration.getValue: default=" + defaultValue );
        return defaultValue;
    }

    public String[] getAttributeNames()
    {
        String[] names = new String[element.getAttributes().getLength()];

        for ( int i = 0; i < names.length; i++ )
        {
            names[i] = ( (Attr) element.getAttributes().item( i ) ).getName();
        }

        return names;
    }

    public String getAttribute( String name )
    {
//        System.out.println( "DomPlexusConfiguration.getAttribute: " + element.getAttribute( name ) );
        return element.getAttribute( name );
    }

    public String getAttribute( String name, String defaultValue )
    {
        Attr attr = element.getAttributeNode( name );

        if ( attr == null )
        {
            return null;
        }

        String value = attr.getValue();

        if ( value != null )
        {
            System.out.println( "DomPlexusConfiguration.getAttribute(name, default): name=" + name + ", default= " + defaultValue );
            return value;
        }

        System.out.println( "DomPlexusConfiguration.getAttribute(name, default): name=" + name + ", " + element.getAttribute( name ) );
        return defaultValue;
    }

    public PlexusConfiguration getChild( String child )
    {
//        System.out.println( "DomPlexusConfiguration.getChild: " + child );

        return getChild( child, true );
    }

    public PlexusConfiguration getChild( int i )
    {
//        System.out.println( "DomPlexusConfiguration.getChild(int i)" );
//        System.out.println( "element.getParentNode().getNodeName() = " + element.getParentNode().getNodeName() );
        PlexusConfiguration child = getChildren()[i];
//        System.out.println( "child.tagName = " + ( (DomPlexusConfiguration) child ).element.getTagName() );
        return child;
    }

    public PlexusConfiguration getChild( String childName, boolean createChild )
    {
//        System.out.println( "DomPlexusConfiguration.getChild: " + childName + ", create: " + createChild );
        NodeList nodes = element.getElementsByTagName( childName );

        PlexusConfiguration child = null;

        if ( nodes.getLength() == 0 )
        {
            if ( createChild )
            {
                Element childElement = element.getOwnerDocument().createElement( childName );
                element.appendChild( childElement );

                child = new DomPlexusConfiguration( childElement );
            }

            return child;
        }

        return new DomPlexusConfiguration( (Element) nodes.item( 0 ) );
    }

    public PlexusConfiguration[] getChildren()
    {
        List<DomPlexusConfiguration> children = new ArrayList<DomPlexusConfiguration>();

        NodeList childNodes = element.getChildNodes();

//        System.out.println( "DomPlexusConfiguration.getChildren()" );

        for ( int i = 0; i < childNodes.getLength(); i++ )
        {
            Node child = childNodes.item( i );

            if ( child.getNodeType() == Node.ELEMENT_NODE )
            {
//                System.out.println( "(Element) child.getNodeName() = " + ((Element) child).getTagName() );
                children.add( new DomPlexusConfiguration( (Element) child ) );
            }
        }

        return children.toArray( new PlexusConfiguration[children.size()] );
    }

    public PlexusConfiguration[] getChildren( String name )
    {
        NodeList nodes = element.getElementsByTagName( name );

        PlexusConfiguration[] children = new PlexusConfiguration[nodes.getLength()];

        for ( int i = 0; i < children.length; i++ )
        {
            children[i] = new DomPlexusConfiguration( (Element) nodes.item( i ) );
        }

//        System.out.println( "DomPlexusConfiguration.getChildren: " + children.length );
        return children;
    }

    public void addChild( PlexusConfiguration configuration )
    {
        throw new RuntimeException( "Not implemented" );
    }

    public int getChildCount()
    {
        NodeList childNodes = element.getChildNodes();

        int count = 0;

        for ( int i = 0; i < childNodes.getLength(); i++ )
        {
            if ( childNodes.item( i ).getNodeType() == Node.ELEMENT_NODE )
            {
                count++;
            }
        }

//        System.out.println( "DomPlexusConfiguration.getChildCount: " + count );
        return count;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public void dump()
    {
        dump( 0 );
    }

    private void dump( int indent )
    {
        String s = "";

        for ( int i = 0; i < indent; i++ )
        {
            s += " ";
        }

        PlexusConfiguration[] children = getChildren();

        if ( children.length > 0)
        {
            System.out.println( s + "<" + getName() + ">" );
            for ( PlexusConfiguration configuration : children )
            {
                ( (DomPlexusConfiguration) configuration ).dump( indent + 1 );
            }
            System.out.println( s + "</" + getName() + ">" );
        }
        else
        {
            System.out.println( s + "<" + getName() + ">" + getValue() + "</" + getName() + ">" );
        }
    }
}
