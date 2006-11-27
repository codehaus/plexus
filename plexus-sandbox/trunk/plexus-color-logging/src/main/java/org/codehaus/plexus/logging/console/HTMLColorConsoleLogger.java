package org.codehaus.plexus.logging.console;

import java.awt.*;

/**
 * TODO: Document Me
 *
 * @author Andrew Williams <andy@handyande.co.uk>
 * @since 24-Oct-2006
 */
public class HTMLColorConsoleLogger
    extends AbstractColorConsoleLogger
{

    public HTMLColorConsoleLogger( int threshold, String name )
    {
        super( threshold, name );
    }

    public String color( Color color, String text )
    {
        return new HTMLColoredString( color, text ).toString();
    }

}
