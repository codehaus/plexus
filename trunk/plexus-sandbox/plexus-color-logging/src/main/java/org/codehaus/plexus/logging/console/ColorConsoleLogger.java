package org.codehaus.plexus.logging.console;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

/**
 * A color console logger. Extends the default console logger to use ANSI colour
 * codes.
 *
 * @author <a href="mailto:andy@handyande.co.uk">Andrew Williams</a>
 * @version $Id:$
 */
public final class ColorConsoleLogger
    extends AbstractLogger
{
    boolean colorWholeLine = false;

    public ColorConsoleLogger( int threshold, String name )
    {
        super( threshold, name );
    }

    public void printColor(int color, String text)
    {
        ANSIColoredString string = new ANSIColoredString();
        string.appendColored(color, text);

        System.out.print( string.toString() );
    }

    public void debug( String message, Throwable throwable )
    {
        if ( isDebugEnabled() )
        {
            System.out.print( "[DEBUG] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public void info( String message, Throwable throwable )
    {
        if ( isInfoEnabled() )
        {
            printColor( ANSIColoredString.COLOR_BLUE, "[INFO] " );

            if (colorWholeLine)
                printColor( ANSIColoredString.COLOR_BLUE, message + "\n" );
            else
                System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public void warn( String message, Throwable throwable )
    {
        if ( isWarnEnabled() )
        {
            printColor( ANSIColoredString.COLOR_YELLOW, "[WARNING] " );

            if (colorWholeLine)
                printColor( ANSIColoredString.COLOR_YELLOW, message + "\n" );
            else
                System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public void error( String message, Throwable throwable )
    {
        if ( isErrorEnabled() )
        {
            printColor( ANSIColoredString.COLOR_RED, "[ERROR] " );

            if (colorWholeLine)
                printColor( ANSIColoredString.COLOR_RED, message + "\n" );
            else
                System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public void fatalError( String message, Throwable throwable )
    {
        if ( isFatalErrorEnabled() )
        {
            printColor( ANSIColoredString.COLOR_RED, "[FATAL_ERROR] " );

            if (colorWholeLine)
                printColor( ANSIColoredString.COLOR_RED, message + "\n" );
            else
                System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public Logger getChildLogger( String name )
    {
        return this;
    }
}
