package org.codehaus.plexus.naming;

/**
 * @since 10 janv. 07
 * @version $Id$
 * @author <a href="mailto:Olivier.LAMY@accor.com">Olivier Lamy</a>
 */
public class Environment
{
    private String name;

    private String type;

    private String value;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String toString()
    {
        StringBuffer humanReadable = new StringBuffer( "name " );
        return humanReadable.toString();
    }
}
