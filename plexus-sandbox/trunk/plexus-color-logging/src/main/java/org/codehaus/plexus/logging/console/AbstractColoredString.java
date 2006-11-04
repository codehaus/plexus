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
    private Color defaultColor = null;

    public AbstractColoredString()
    {
        this( "" );
    }

    public AbstractColoredString( Color color )
    {
        this( color, "" );
    }

    public AbstractColoredString( String start )
    {
        this( null, start );
    }

    public AbstractColoredString( Color color, String start )
    {
        defaultColor = color;
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
        if ( defaultColor == null )
            return internal;

        return convertColorStart( defaultColor ) + internal +
            convertColorEnd( defaultColor );
    }

    public Color getDefaultColor()
    {
        return defaultColor;
    }

    public abstract String convertColorStart( Color color );

    public abstract String convertColorEnd( Color color );
}
