package org.codehaus.plexus.logging.console;

import java.awt.Color;

/**
 * TODO: Document Me
 *
 * @author Andrew Williams <andy@handyande.co.uk>
 * @since 24-Oct-2006
 */
public class ANSIColorConsoleLogger extends AbstractColorConsoleLogger
{

    public ANSIColorConsoleLogger( int threshold, String name )
    {
        super( threshold, name );
    }

    public String color( Color color, String text )
    {
        return new ANSIColoredString( color, text ).toString();
    }

}
