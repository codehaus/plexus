package org.codehaus.plexus.logging.console;

import java.awt.Color;

/**
 * TODO: Document Me
 *
 * @author Andrew Williams
 * @since 22-Oct-2006
 */
public class HTMLColoredString
    extends AbstractColoredString
{
    public HTMLColoredString()
    {
        super();
    }

    public HTMLColoredString( Color color )
    {
        super( color );
    }

    public HTMLColoredString( String start )
    {
        super( start );
    }

    public HTMLColoredString( Color color, String start )
    {
        super( color, start );
    }

    /**
     * Convert the given color to a hex string of the form #rrggbb
     *
     * @param color the color to convert
     * @return a hex string representing the color passed
     */
    public String getColorString( Color color )
    {
        return '#' + getColorDigit( color.getRed() ) +
            getColorDigit( color.getGreen() ) +
            getColorDigit( color.getBlue() );
    }

    private String getColorDigit( int digit )
    {
        String ret = Integer.toHexString( digit );
        if ( ret.length() == 1 )
        {
            ret = '0' + ret;
        }

        return ret;
    }

    public String convertColorStart( Color color )
    {
        return "<span style=\"color: " + getColorString( color ) + "\">";
    }

    public String convertColorEnd( Color color )
    {
        return "</span>";
    }
}
