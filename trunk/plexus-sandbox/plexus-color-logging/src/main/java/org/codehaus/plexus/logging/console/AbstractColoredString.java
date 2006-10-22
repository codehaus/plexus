package org.codehaus.plexus.logging.console;

/**
 * TODO: Document Me
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

    public void appendColored( int color, String text )
    {
      internal = internal.concat( convertColorStart(color) + text +
          convertColorEnd( color ) );
    }

    public String toString()
    {
        return internal;
    }

    public abstract String convertColorStart( int color );

    public abstract String convertColorEnd( int color );
}
