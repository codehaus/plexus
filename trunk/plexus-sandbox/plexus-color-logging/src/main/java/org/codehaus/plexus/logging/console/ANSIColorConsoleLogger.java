package org.codehaus.plexus.logging.console;

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

    public String color(int color, String text)
    {
        ANSIColoredString string = new ANSIColoredString();
        string.appendColored( color, text );

        return string.toString();
    }

}
