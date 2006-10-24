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
public abstract class AbstractColorConsoleLogger extends AbstractLogger
{
    boolean wholeLineColored = false;

    public AbstractColorConsoleLogger( int threshold, String name )
    {
        super( threshold, name );
    }

    public abstract String color( int color, String text );

    public void printColor( int color, String text )
    {
        System.out.print( color( color, text ) );
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

            if (wholeLineColored)
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

            if (wholeLineColored)
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

            if (wholeLineColored)
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

            if (wholeLineColored)
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


    public boolean isWholeLineColored()
    {
        return wholeLineColored;
    }

    public void setWholeLineColored(boolean wholeLineColored)
    {
        this.wholeLineColored = wholeLineColored;
    }
}
