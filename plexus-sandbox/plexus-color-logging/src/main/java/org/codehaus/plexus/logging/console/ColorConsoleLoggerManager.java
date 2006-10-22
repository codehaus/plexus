package org.codehaus.plexus.logging.console;

import org.codehaus.plexus.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * This is a simple logger manager that will only write the logging statements to the console.
 *
 * Sample configuration:
 * <pre>
 * <logging>
 *   <implementation>org.codehaus.plexus.logging.ConsoleLoggerManager</implementation>
 *   <logger>
 *     <threshold>DEBUG</threshold>
 *   </logger>
 * </logging>
 * </pre>
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:andy@handyande.co.uk">Andrew Williams</a>
 * @version $Id:$
 */
public class ColorConsoleLoggerManager extends ConsoleLoggerManager
{
    private Map loggers;

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
        loggers = new HashMap();
    }

    public void setThresholds( int currentThreshold )
    {
        setThreshold( currentThreshold );

        for ( Iterator logs = loggers.values().iterator(); logs.hasNext(); )
        {
            ColorConsoleLogger logger = (ColorConsoleLogger) logs.next();
            logger.setThreshold( currentThreshold );
        }
    }

    public void setThreshold( String role, String roleHint, int threshold ) {
        ColorConsoleLogger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (ColorConsoleLogger)loggers.get( name );

        if(logger == null) {
            debug( "Trying to set the threshold of a unknown logger '" + name + "'." );
            return; // nothing to do
        }

        logger.setThreshold( threshold );
    }

    public int getThreshold( String role, String roleHint ) {
        ColorConsoleLogger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (ColorConsoleLogger)loggers.get( name );

        if(logger == null) {
            debug( "Trying to get the threshold of a unknown logger '" + name + "'." );
            return Logger.LEVEL_DEBUG; // does not return null because that could create a NPE
        }

        return logger.getThreshold();
    }

    public Logger getLoggerForComponent( String role, String roleHint )
    {
        Logger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (Logger)loggers.get( name );

        if ( logger != null )
            return logger;

        debug( "Creating logger '" + name + "' " + this.hashCode() + "." );
        logger = new ColorConsoleLogger( getThreshold(), name );
        loggers.put( name, logger );

        return logger;
    }

    public void returnComponentLogger( String role, String roleHint )
    {
        Object obj;
        String name;

        name = toMapKey( role, roleHint );
        obj = loggers.remove( name );

        if ( obj == null )
        {
            debug( "There was no such logger '" + name + "' " + this.hashCode() + ".");
        }
        else
        {
            debug( "Removed logger '" + name + "' " + this.hashCode() + ".");
        }
    }

    public int getActiveLoggerCount()
    {
        return loggers.size();
    }

    /**
     * Remove this method and all references when this code is verified.
     *
     * @param msg
     */
    private void debug( String msg )
    {
//        if ( !bootTimeLogger )
//            System.out.println( "[Console] " + msg );
    }
}
