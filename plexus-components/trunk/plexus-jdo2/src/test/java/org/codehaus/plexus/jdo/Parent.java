package org.codehaus.plexus.jdo;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * modello:java generated file.
 * 
 * @author <a href="mailto:mattis@inamo.no">Mathias Bjerke</a> 
 * @version $Id$
 */
public class Parent
    implements java.io.Serializable
{

    /**
     * Field id
     */
    private long id = 0;

    /**
     * Field price
     */
    private double price = 0.0;

    /**
     * Field name
     */
    private String name;

    /**
     * Field description
     */
    private String description;

    /**
     * Field children
     */
    private java.util.List children;

    /**
     * Method addChildren
     * 
     * @param child
     */
    public void addChildren( Child child )
    {
        getChildren().add( child );
    }

    /**
     * Method equals
     * 
     * @param other
     */
    public boolean equals( Object other )
    {
        if ( this == other )
        {
            return true;
        }

        if ( !( other instanceof Parent ) )
        {
            return false;
        }

        Parent that = (Parent) other;
        boolean result = true;
        result = result && id == that.id;
        return result;
    }

    /**
     * Method getChildren
     */
    public java.util.List getChildren()
    {
        if ( this.children == null )
        {
            this.children = new java.util.ArrayList();
        }

        return this.children;
    }

    /**
     * Get description
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Get id
     */
    public long getId()
    {
        return this.id;
    }

    /**
     * Get name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Get price
     */
    public double getPrice()
    {
        return this.price;
    }

    /**
     * Method hashCode
     */
    public int hashCode()
    {
        int result = 17;
        result = 37 * result + (int) ( id ^ ( id >>> 32 ) );
        return result;
    }

    /**
     * Method removeChildren
     * 
     * @param child
     */
    public void removeChildren( Child child )
    {
        getChildren().remove( child );
    }

    /**
     * Set children
     * 
     * @param children
     */
    public void setChildren( java.util.List children )
    {
        this.children = children;
    }

    /**
     * Set description
     * 
     * @param description
     */
    public void setDescription( String description )
    {
        this.description = description;
    }

    /**
     * Set id
     * 
     * @param id
     */
    public void setId( long id )
    {
        this.id = id;
    }

    /**
     * Set name
     * 
     * @param name
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * Set price
     * 
     * @param price
     */
    public void setPrice( double price )
    {
        this.price = price;
    }

    /**
     * Method toString
     */
    public java.lang.String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "id = '" );
        buf.append( getId() + "'" );
        return buf.toString();
    }
}
