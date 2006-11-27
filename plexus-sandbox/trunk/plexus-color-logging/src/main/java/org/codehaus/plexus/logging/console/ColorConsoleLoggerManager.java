package org.codehaus.plexus.logging.console;

import org.codehaus.plexus.logging.Logger;

/**
 * This is a simple logger manager - the work is all done by the
 * ConsoleLoggerManager :)
 *
 * Sample configuration:
 * <pre>
 * <logging>
 *   <implementation>org.codehaus.plexus.logging.ColorConsoleLoggerManager</implementation>
 *   <logger>
 *     <threshold>DEBUG</threshold>
 *   </logger>
 * </logging>
 * </pre>
 * 
 * @author <a href="mailto:andy@handyande.co.uk">Andrew Williams</a>
 * @version $Id:$
 */
public class ColorConsoleLoggerManager extends ConsoleLoggerManager
{
    private String format;

    /**
     */
    public ColorConsoleLoggerManager()
    {
        this( "debug" );
    }

    /**
     * This special constructor is called directly when the container is bootstrapping itself.
     *
     * @param threshold the logging threshold to use for all loggers
     */
    public ColorConsoleLoggerManager( String threshold )
    {
        super( threshold );
    }

    /**
     * Get the format of loggers being returned from createLogger.
     *
     * @return the current logger format
     */
    public String getFormat()
    {
        return format;
    }

    /**
     * Set the format of the color logger. "ansi" returns an ANSI colored logger, anything else
     * returns a standard colsole logger.
     *
     * @param format The output format to use
     */
    public void setFormat( String format )
    {
        this.format = format;
    }

    public Logger createLogger( int threshold, String name )
    {
        if ( format != null )
        {
            String lower = format.toLowerCase();

            if ( lower.equals("ansi") )
            {
                return new ANSIColorConsoleLogger( getThreshold(), name );
            }
            else if ( lower.equals("html") )
            {
                return new HTMLColorConsoleLogger( getThreshold(), name );
            }
        }
        return new ConsoleLogger( getThreshold(), name );
    }
}
