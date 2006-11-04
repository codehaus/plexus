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

    /**
     */
    public ColorConsoleLoggerManager()
    {
        this( "debug" );
    }

    /**
     * This special constructor is called directly when the container is bootstrapping itself.
     */
    public ColorConsoleLoggerManager( String threshold )
    {
        super( threshold );
    }

    public Logger createLogger( int threshold, String name )
    {
        return new ANSIColorConsoleLogger( getThreshold(), name );
    }
}
