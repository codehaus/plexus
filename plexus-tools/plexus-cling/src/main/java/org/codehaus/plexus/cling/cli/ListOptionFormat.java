/*
 * Created on Apr 28, 2003
 */
package org.codehaus.plexus.cling.cli;

/**
 * @author jdcasey
 */
public class ListOptionFormat
    extends OptionFormat
{

    private String splitPattern;

    private OptionFormat subFormat;

    /**
     * @param description
     * @param ordinal
     */
    public ListOptionFormat( String splitPattern, OptionFormat subFormat )
    {
        super( "List Format" );
        this.splitPattern = splitPattern;
        this.subFormat = subFormat;
    }

    /**
     * Override of
     * 
     * @see org.codehaus.plexus.cling.cli.OptionFormat#isValid(java.lang.String)
     * @param value
     * @return @see org.codehaus.plexus.cling.cli.OptionFormat#isValid(java.lang.String)
     */
    public boolean isValid( String value )
    {
        String[] values = value.split( splitPattern );
        for ( int i = 0; i < values.length; i++ )
        {
            if ( !subFormat.isValid( values[i] ) )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Override of
     * 
     * @see org.codehaus.plexus.cling.cli.OptionFormat#getValue(java.lang.String)
     * @param value
     * @return @see org.codehaus.plexus.cling.cli.OptionFormat#getValue(java.lang.String)
     */
    public Object getValue( String value )
    {
        String[] temp = value.split( splitPattern );
        Object[] values = new Object[temp.length];

        for ( int i = 0; i < values.length; i++ )
        {
            values[i] = subFormat.getValue( temp[i] );
        }

        return values;
    }

}