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
public class Child
    implements java.io.Serializable
{

    /**
     * Field id
     */
    private int id = 0;

    /**
     * Field name
     */
    private String name;

    /**
     * Field description
     */
    private String description;

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

        if ( !( other instanceof Child ) )
        {
            return false;
        }

        Child that = (Child) other;
        boolean result = true;
        result = result && id == that.id;
        return result;
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
    public int getId()
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
     * Method hashCode
     */
    public int hashCode()
    {
        int result = 17;
        result = 37 * result + (int) ( id ^ ( id >>> 32 ) );
        return result;
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
    public void setId( int id )
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
