package org.codehaus.plexus.pipeline;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
* @version $Id$
*/
public class ValveReturnCode
{
    private String key;

    protected ValveReturnCode( String key )
    {
        this.key = key;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        ValveReturnCode that = (ValveReturnCode) o;

        if ( !key.equals( that.key ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return key.hashCode();
    }
}
