package org.codehaus.plexus.logging.console;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

import java.awt.Color;

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

    public static Color COLOR_INFO = Color.BLUE;
    public static Color COLOR_WARNING = Color.YELLOW;
    public static Color COLOR_ERROR = Color.RED;
    public static Color COLOR_FATAL = Color.RED;

    public AbstractColorConsoleLogger( int threshold, String name )
    {
        super( threshold, name );
    }

    public abstract String color( Color color, String text );

    public void printColor( Color color, String text )
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
            printColor( COLOR_INFO, "[INFO] " );

            if (wholeLineColored)
                printColor( COLOR_INFO, message + "\n" );
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
            printColor( COLOR_WARNING, "[WARNING] " );

            if (wholeLineColored)
                printColor( COLOR_WARNING, message + "\n" );
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
            printColor( COLOR_ERROR, "[ERROR] " );

            if (wholeLineColored)
                printColor( COLOR_ERROR, message + "\n" );
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
            printColor( COLOR_FATAL, "[FATAL_ERROR] " );

            if (wholeLineColored)
                printColor( COLOR_FATAL, message + "\n" );
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

    public void setWholeLineColored( boolean wholeLineColored )
    {
        this.wholeLineColored = wholeLineColored;
    }
}
