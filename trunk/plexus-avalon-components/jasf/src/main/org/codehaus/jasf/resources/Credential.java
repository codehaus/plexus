package org.codehaus.jasf.resources;

/**
 * A Credential Attribute.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 11, 2003
 */
public class Credential
{
    private String name;

    public Credential( String name )
    {
        this.name = name;
    }
     
    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }
   
}
