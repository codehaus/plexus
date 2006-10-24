package org.codehaus.plexus.logging.console;

import java.util.Map;
import java.util.HashMap;
import java.awt.Color;

/**
 * TODO: Document Me
 *
 * @author Andrew Williams <andy@handyande.co.uk>
 * @since 22-Oct-2006
 */
public class ANSIColoredString extends AbstractColoredString
{
    public static final int ANSI_RESET = 0;

    public static final int ANSI_COLOR_DEFAULT = 39;

    public static final Color COLOR_BLACK = Color.BLACK;
    public static final Color COLOR_RED = Color.RED;
    public static final Color COLOR_GREEN = Color.GREEN;
    public static final Color COLOR_YELLOW = Color.YELLOW;
    public static final Color COLOR_BLUE = Color.BLUE;
    public static final Color COLOR_MAGENTA = Color.MAGENTA;
    public static final Color COLOR_CYAN = Color.CYAN;
    public static final Color COLOR_WHITE = Color.WHITE;

    public static final char ESCAPE = '\u001b';

    private static Map colors;
    static
    {
        colors = new HashMap();
        colors.put( COLOR_BLACK, new Integer( 30 ) );
        colors.put( COLOR_RED, new Integer( 31 ) );
        colors.put( COLOR_GREEN, new Integer( 32 ) );
        colors.put( COLOR_YELLOW, new Integer( 33 ) );
        colors.put( COLOR_BLUE, new Integer( 34 ) );
        colors.put( COLOR_MAGENTA, new Integer( 35 ) );
        colors.put( COLOR_CYAN, new Integer( 36 ) );
        colors.put( COLOR_WHITE, new Integer( 37 ) );
    }

    public ANSIColoredString()
    {
        super();
    }

    public ANSIColoredString( Color color )
    {
        super( color );
    }

    public ANSIColoredString( String start )
    {
        super( start );
    }

    public ANSIColoredString( Color color, String start )
    {
        super( color, start );
    }

    public String convertColorStart( Color color )
    {
        if ( !colors.containsKey( color ) )
            return "";
        return ESCAPE + "[" + ( (Integer) colors.get( color ) ).intValue() + "m";
    }

    public String convertColorEnd( Color color )
    {
        if ( !colors.containsKey( color ) )
            return "";

        return ESCAPE + "[" + ANSI_COLOR_DEFAULT + "m";
    }
}
