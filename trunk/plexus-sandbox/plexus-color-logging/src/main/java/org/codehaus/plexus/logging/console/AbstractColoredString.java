package org.codehaus.plexus.logging.console;

import java.awt.Color;

/**
 * A generic coloured string. Base support for colouring portions of a string.
 * Should be overridden to provide the actual implementation of colour
 * conversion.
 *
 * @author Andrew Williams <andy@handyande.co.uk>
 * @since 22-Oct-2006
 */
public abstract class AbstractColoredString
{
    private String internal;

    public AbstractColoredString()
    {
        this( "" );
    }

    public AbstractColoredString( String start )
    {
        internal = start;
    }

    public void append( String text )
    {
        internal = internal.concat( text );
    }

    public void appendColored( Color color, String text )
    {
      internal = internal.concat( convertColorStart(color) + text +
          convertColorEnd( color ) );
    }

    public String toString()
    {
        return internal;
    }

    public abstract String convertColorStart( Color color );

    public abstract String convertColorEnd( Color color );
}
