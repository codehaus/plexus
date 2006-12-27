package org.codehaus.plexus.jdo;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Basic Object (post-processed from modello:java)
 * 
 * @version $Id$
 */
public class Basic implements java.io.Serializable {

    /**
     * Field id
     */
    private long id = 0;

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
    public boolean equals(Object other)
    {
        if ( this == other)
        {
            return true;
        }
        
        if ( !(other instanceof Basic) )
        {
            return false;
        }
        
        Basic that = (Basic) other;
        boolean result = true;
        result = result && id== that.id;
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
     * Method hashCode
     */
    public int hashCode()
    {
        int result = 17;
        result = 37 * result + (int) id;
        return result;
    } 

    /**
     * Set description
     * 
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    } 

    /**
     * Set id
     * 
     * @param id
     */
    public void setId(long id)
    {
        this.id = id;
    }  

    /**
     * Set name
     * 
     * @param name
     */
    public void setName(String name)
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
