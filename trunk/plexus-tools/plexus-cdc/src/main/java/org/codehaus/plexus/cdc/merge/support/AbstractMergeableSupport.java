/**
 * 
 */
package org.codehaus.plexus.cdc.merge.support;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.cdc.merge.MergeException;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.filter.Filter;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id:$
 */
public abstract class AbstractMergeableSupport
    implements Mergeable
{

    /**
     * Wrapped JDOM element.
     */
    protected Element element;

    public AbstractMergeableSupport( Element element )
    {
        super();
        this.element = element;
    }

    /**
     * @see  Mergeable#merge(Mergeable)
     */
    public abstract void merge( Mergeable me )
        throws MergeException;

    /**
     * Determines if the passed in {@link Mergeable} was of same type as this
     * class.
     * 
     * @param me
     *            {@link Mergeable} instance to test.
     * @return <code>true</code> if the passed in Mergeable can be merged with
     *         the current Mergeable.
     */
    protected abstract boolean isExpectedElementType( Mergeable me );

    /**
     * Returns an array of tags/elements that are allowed under the current
     * element.
     * 
     * @return the allowedTags
     */
    protected abstract ComponentsXmlTag[] getAllowedTags();

    // 
    // Methods delegated on wrapped JDOM element.
    // 

    /**
     * @param collection
     * @return
     * @see org.jdom.Element#addContent(java.util.Collection)
     */
    public Element addContent( Collection collection )
    {
        return element.addContent( collection );
    }

    /**
     * @param child
     * @return
     * @see org.jdom.Element#addContent(org.jdom.Content)
     */
    public Element addContent( Content child )
    {
        return element.addContent( child );
    }

    /**
     * @param index
     * @param c
     * @return
     * @see org.jdom.Element#addContent(int, java.util.Collection)
     */
    public Element addContent( int index, Collection c )
    {
        return element.addContent( index, c );
    }

    /**
     * @param index
     * @param child
     * @return
     * @see org.jdom.Element#addContent(int, org.jdom.Content)
     */
    public Element addContent( int index, Content child )
    {
        return element.addContent( index, child );
    }

    /**
     * @param str
     * @return
     * @see org.jdom.Element#addContent(java.lang.String)
     */
    public Element addContent( String str )
    {
        return element.addContent( str );
    }

    /**
     * @param additional
     * @see org.jdom.Element#addNamespaceDeclaration(org.jdom.Namespace)
     */
    public void addNamespaceDeclaration( Namespace additional )
    {
        element.addNamespaceDeclaration( additional );
    }

    /**
     * @return
     * @see org.jdom.Element#clone()
     */
    public Object clone()
    {
        return element.clone();
    }

    /**
     * @return
     * @see org.jdom.Element#cloneContent()
     */
    public List cloneContent()
    {
        return element.cloneContent();
    }

    /**
     * @return
     * @see org.jdom.Content#detach()
     */
    public Content detach()
    {
        return element.detach();
    }

    /**
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        return element.equals( obj );
    }

    /**
     * @return
     * @see org.jdom.Element#getAdditionalNamespaces()
     */
    public List getAdditionalNamespaces()
    {
        return element.getAdditionalNamespaces();
    }

    /**
     * @param name
     * @param ns
     * @return
     * @see org.jdom.Element#getAttribute(java.lang.String, org.jdom.Namespace)
     */
    public Attribute getAttribute( String name, Namespace ns )
    {
        return element.getAttribute( name, ns );
    }

    /**
     * @param name
     * @return
     * @see org.jdom.Element#getAttribute(java.lang.String)
     */
    public Attribute getAttribute( String name )
    {
        return element.getAttribute( name );
    }

    /**
     * @return
     * @see org.jdom.Element#getAttributes()
     */
    public List getAttributes()
    {
        return element.getAttributes();
    }

    /**
     * @param name
     * @param ns
     * @param def
     * @return
     * @see org.jdom.Element#getAttributeValue(java.lang.String,
     *      org.jdom.Namespace, java.lang.String)
     */
    public String getAttributeValue( String name, Namespace ns, String def )
    {
        return element.getAttributeValue( name, ns, def );
    }

    /**
     * @param name
     * @param ns
     * @return
     * @see org.jdom.Element#getAttributeValue(java.lang.String,
     *      org.jdom.Namespace)
     */
    public String getAttributeValue( String name, Namespace ns )
    {
        return element.getAttributeValue( name, ns );
    }

    /**
     * @param name
     * @param def
     * @return
     * @see org.jdom.Element#getAttributeValue(java.lang.String,
     *      java.lang.String)
     */
    public String getAttributeValue( String name, String def )
    {
        return element.getAttributeValue( name, def );
    }

    /**
     * @param name
     * @return
     * @see org.jdom.Element#getAttributeValue(java.lang.String)
     */
    public String getAttributeValue( String name )
    {
        return element.getAttributeValue( name );
    }

    /**
     * @param name
     * @param ns
     * @return
     * @see org.jdom.Element#getChild(java.lang.String, org.jdom.Namespace)
     */
    public Element getChild( String name, Namespace ns )
    {
        return element.getChild( name, ns );
    }

    /**
     * @param name
     * @return
     * @see org.jdom.Element#getChild(java.lang.String)
     */
    public Element getChild( String name )
    {
        return element.getChild( name );
    }

    /**
     * @return
     * @see org.jdom.Element#getChildren()
     */
    public List getChildren()
    {
        return element.getChildren();
    }

    /**
     * @param name
     * @param ns
     * @return
     * @see org.jdom.Element#getChildren(java.lang.String, org.jdom.Namespace)
     */
    public List getChildren( String name, Namespace ns )
    {
        return element.getChildren( name, ns );
    }

    /**
     * @param name
     * @return
     * @see org.jdom.Element#getChildren(java.lang.String)
     */
    public List getChildren( String name )
    {
        return element.getChildren( name );
    }

    /**
     * @param name
     * @param ns
     * @return
     * @see org.jdom.Element#getChildText(java.lang.String, org.jdom.Namespace)
     */
    public String getChildText( String name, Namespace ns )
    {
        return element.getChildText( name, ns );
    }

    /**
     * @param name
     * @return
     * @see org.jdom.Element#getChildText(java.lang.String)
     */
    public String getChildText( String name )
    {
        return element.getChildText( name );
    }

    /**
     * @param name
     * @param ns
     * @return
     * @see org.jdom.Element#getChildTextNormalize(java.lang.String,
     *      org.jdom.Namespace)
     */
    public String getChildTextNormalize( String name, Namespace ns )
    {
        return element.getChildTextNormalize( name, ns );
    }

    /**
     * @param name
     * @return
     * @see org.jdom.Element#getChildTextNormalize(java.lang.String)
     */
    public String getChildTextNormalize( String name )
    {
        return element.getChildTextNormalize( name );
    }

    /**
     * @param name
     * @param ns
     * @return
     * @see org.jdom.Element#getChildTextTrim(java.lang.String,
     *      org.jdom.Namespace)
     */
    public String getChildTextTrim( String name, Namespace ns )
    {
        return element.getChildTextTrim( name, ns );
    }

    /**
     * @param name
     * @return
     * @see org.jdom.Element#getChildTextTrim(java.lang.String)
     */
    public String getChildTextTrim( String name )
    {
        return element.getChildTextTrim( name );
    }

    /**
     * @return
     * @see org.jdom.Element#getContent()
     */
    public List getContent()
    {
        return element.getContent();
    }

    /**
     * @param filter
     * @return
     * @see org.jdom.Element#getContent(org.jdom.filter.Filter)
     */
    public List getContent( Filter filter )
    {
        return element.getContent( filter );
    }

    /**
     * @param index
     * @return
     * @see org.jdom.Element#getContent(int)
     */
    public Content getContent( int index )
    {
        return element.getContent( index );
    }

    /**
     * @return
     * @see org.jdom.Element#getContentSize()
     */
    public int getContentSize()
    {
        return element.getContentSize();
    }

    /**
     * @return
     * @see org.jdom.Element#getDescendants()
     */
    public Iterator getDescendants()
    {
        return element.getDescendants();
    }

    /**
     * @param filter
     * @return
     * @see org.jdom.Element#getDescendants(org.jdom.filter.Filter)
     */
    public Iterator getDescendants( Filter filter )
    {
        return element.getDescendants( filter );
    }

    /**
     * @return
     * @see org.jdom.Content#getDocument()
     */
    public Document getDocument()
    {
        return element.getDocument();
    }

    /**
     * @return
     * @see org.jdom.Element#getName()
     */
    public String getName()
    {
        return element.getName();
    }

    /**
     * @return
     * @see org.jdom.Element#getNamespace()
     */
    public Namespace getNamespace()
    {
        return element.getNamespace();
    }

    /**
     * @param prefix
     * @return
     * @see org.jdom.Element#getNamespace(java.lang.String)
     */
    public Namespace getNamespace( String prefix )
    {
        return element.getNamespace( prefix );
    }

    /**
     * @return
     * @see org.jdom.Element#getNamespacePrefix()
     */
    public String getNamespacePrefix()
    {
        return element.getNamespacePrefix();
    }

    /**
     * @return
     * @see org.jdom.Element#getNamespaceURI()
     */
    public String getNamespaceURI()
    {
        return element.getNamespaceURI();
    }

    /**
     * @return
     * @see org.jdom.Content#getParent()
     */
    public Parent getParent()
    {
        return element.getParent();
    }

    /**
     * @return
     * @see org.jdom.Content#getParentElement()
     */
    public Element getParentElement()
    {
        return element.getParentElement();
    }

    /**
     * @return
     * @see org.jdom.Element#getQualifiedName()
     */
    public String getQualifiedName()
    {
        return element.getQualifiedName();
    }

    /**
     * @return
     * @see org.jdom.Element#getText()
     */
    public String getText()
    {
        return element.getText();
    }

    /**
     * @return
     * @see org.jdom.Element#getTextNormalize()
     */
    public String getTextNormalize()
    {
        return element.getTextNormalize();
    }

    /**
     * @return
     * @see org.jdom.Element#getTextTrim()
     */
    public String getTextTrim()
    {
        return element.getTextTrim();
    }

    /**
     * @return
     * @see org.jdom.Element#getValue()
     */
    public String getValue()
    {
        return element.getValue();
    }

    /**
     * @return
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return element.hashCode();
    }

    /**
     * @param child
     * @return
     * @see org.jdom.Element#indexOf(org.jdom.Content)
     */
    public int indexOf( Content child )
    {
        return element.indexOf( child );
    }

    /**
     * @param element
     * @return
     * @see org.jdom.Element#isAncestor(org.jdom.Element)
     */
    public boolean isAncestor( Element element )
    {
        return element.isAncestor( element );
    }

    /**
     * @return
     * @see org.jdom.Element#isRootElement()
     */
    public boolean isRootElement()
    {
        return element.isRootElement();
    }

    /**
     * @param attribute
     * @return
     * @see org.jdom.Element#removeAttribute(org.jdom.Attribute)
     */
    public boolean removeAttribute( Attribute attribute )
    {
        return element.removeAttribute( attribute );
    }

    /**
     * @param name
     * @param ns
     * @return
     * @see org.jdom.Element#removeAttribute(java.lang.String,
     *      org.jdom.Namespace)
     */
    public boolean removeAttribute( String name, Namespace ns )
    {
        return element.removeAttribute( name, ns );
    }

    /**
     * @param name
     * @return
     * @see org.jdom.Element#removeAttribute(java.lang.String)
     */
    public boolean removeAttribute( String name )
    {
        return element.removeAttribute( name );
    }

    /**
     * @param name
     * @param ns
     * @return
     * @see org.jdom.Element#removeChild(java.lang.String, org.jdom.Namespace)
     */
    public boolean removeChild( String name, Namespace ns )
    {
        return element.removeChild( name, ns );
    }

    /**
     * @param name
     * @return
     * @see org.jdom.Element#removeChild(java.lang.String)
     */
    public boolean removeChild( String name )
    {
        return element.removeChild( name );
    }

    /**
     * @param name
     * @param ns
     * @return
     * @see org.jdom.Element#removeChildren(java.lang.String,
     *      org.jdom.Namespace)
     */
    public boolean removeChildren( String name, Namespace ns )
    {
        return element.removeChildren( name, ns );
    }

    /**
     * @param name
     * @return
     * @see org.jdom.Element#removeChildren(java.lang.String)
     */
    public boolean removeChildren( String name )
    {
        return element.removeChildren( name );
    }

    /**
     * @return
     * @see org.jdom.Element#removeContent()
     */
    public List removeContent()
    {
        return element.removeContent();
    }

    /**
     * @param child
     * @return
     * @see org.jdom.Element#removeContent(org.jdom.Content)
     */
    public boolean removeContent( Content child )
    {
        return element.removeContent( child );
    }

    /**
     * @param filter
     * @return
     * @see org.jdom.Element#removeContent(org.jdom.filter.Filter)
     */
    public List removeContent( Filter filter )
    {
        return element.removeContent( filter );
    }

    /**
     * @param index
     * @return
     * @see org.jdom.Element#removeContent(int)
     */
    public Content removeContent( int index )
    {
        return element.removeContent( index );
    }

    /**
     * @param additionalNamespace
     * @see org.jdom.Element#removeNamespaceDeclaration(org.jdom.Namespace)
     */
    public void removeNamespaceDeclaration( Namespace additionalNamespace )
    {
        element.removeNamespaceDeclaration( additionalNamespace );
    }

    /**
     * @param attribute
     * @return
     * @see org.jdom.Element#setAttribute(org.jdom.Attribute)
     */
    public Element setAttribute( Attribute attribute )
    {
        return element.setAttribute( attribute );
    }

    /**
     * @param name
     * @param value
     * @param ns
     * @return
     * @see org.jdom.Element#setAttribute(java.lang.String, java.lang.String,
     *      org.jdom.Namespace)
     */
    public Element setAttribute( String name, String value, Namespace ns )
    {
        return element.setAttribute( name, value, ns );
    }

    /**
     * @param name
     * @param value
     * @return
     * @see org.jdom.Element#setAttribute(java.lang.String, java.lang.String)
     */
    public Element setAttribute( String name, String value )
    {
        return element.setAttribute( name, value );
    }

    /**
     * @param newAttributes
     * @return
     * @see org.jdom.Element#setAttributes(java.util.List)
     */
    public Element setAttributes( List newAttributes )
    {
        return element.setAttributes( newAttributes );
    }

    /**
     * @param newContent
     * @return
     * @see org.jdom.Element#setContent(java.util.Collection)
     */
    public Element setContent( Collection newContent )
    {
        return element.setContent( newContent );
    }

    /**
     * @param child
     * @return
     * @see org.jdom.Element#setContent(org.jdom.Content)
     */
    public Element setContent( Content child )
    {
        return element.setContent( child );
    }

    /**
     * @param index
     * @param collection
     * @return
     * @see org.jdom.Element#setContent(int, java.util.Collection)
     */
    public Parent setContent( int index, Collection collection )
    {
        return element.setContent( index, collection );
    }

    /**
     * @param index
     * @param child
     * @return
     * @see org.jdom.Element#setContent(int, org.jdom.Content)
     */
    public Element setContent( int index, Content child )
    {
        return element.setContent( index, child );
    }

    /**
     * @param name
     * @return
     * @see org.jdom.Element#setName(java.lang.String)
     */
    public Element setName( String name )
    {
        return element.setName( name );
    }

    /**
     * @param namespace
     * @return
     * @see org.jdom.Element#setNamespace(org.jdom.Namespace)
     */
    public Element setNamespace( Namespace namespace )
    {
        return element.setNamespace( namespace );
    }

    /**
     * @param text
     * @return
     * @see org.jdom.Element#setText(java.lang.String)
     */
    public Element setText( String text )
    {
        return element.setText( text );
    }

    /**
     * @return
     * @see org.jdom.Element#toString()
     */
    public String toString()
    {
        return element.toString();
    }

    /**
     * Returns the wrapped up JDom {@link Element} instance.
     * 
     * @return the wrapped up JDom {@link Element} instance.
     */
    public Element getElement()
    {
        return this.element;
    }

    /**
     * Sub classes should override if they wish to provide a different
     * combination of composite keys for determining conflicts.
     * 
     * @param defaultList
     * @return
     */
    protected List getElementNamesForConflictResolution( List defaultList )
    {
        return defaultList;
    }

}
