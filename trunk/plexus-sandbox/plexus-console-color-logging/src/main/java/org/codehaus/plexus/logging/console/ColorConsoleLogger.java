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
    public static final int COLOR_NONE = 0;
    public static final int COLOR_BLACK = 30;
    public static final int COLOR_RED = 31;
    public static final int COLOR_GREEN = 32;
    public static final int COLOR_YELLOW = 33;
    public static final int COLOR_BLUE = 34;

    boolean colorWholeLine = false;

    public ColorConsoleLogger( int threshold, String name )
    {
        super( threshold, name );
    }

    public static String setColor(int color)
    {
        return "\u001b[" + color + "m";
    }

    public void printColor(int color)
    {
        System.out.print( setColor( color ) );
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
            printColor( COLOR_BLUE );
            System.out.print( "[INFO] " );
            if (!colorWholeLine)
                printColor( COLOR_NONE );

            System.out.println( message );
            if (colorWholeLine)
                printColor( COLOR_NONE );

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
            printColor(COLOR_YELLOW);
            System.out.print( "[WARNING] " );
            if (!colorWholeLine)
                printColor( COLOR_NONE );

            System.out.println( message );
            if (colorWholeLine)
                printColor( COLOR_NONE );

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
            printColor(COLOR_RED);
            System.out.print( "[ERROR] " );
            if (!colorWholeLine)
                printColor( COLOR_NONE );

            System.out.println( message );
            if (colorWholeLine)
                printColor( COLOR_NONE );

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
            printColor(COLOR_RED);
            System.out.print( "[FATAL ERROR] " );
            if (!colorWholeLine)
                printColor( COLOR_NONE );

            System.out.println( message );
            if (colorWholeLine)
                printColor( COLOR_NONE );

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
