package org.codehaus.plexus.logging.log4j;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLoggerManager;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 *
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AbstractLog4JLoggerManager
        extends AbstractLoggerManager
{
    /** */
    private Map loggerCache = new HashMap();

    /** */
    private int currentThreshold;

    /**
     * The configuration. This field is set by the container.
     */
    private PlexusConfiguration logging;

    public AbstractLog4JLoggerManager()
    {
    }

    ///////////////////////////////////////////////////////////////////////////
    // LoggerManager implementation

    /**
     * Sets the threshold for all new loggers. It will NOT affect the existing loggers.
     * <p/>
     * This is usually only set once while the logger manager is configured.
     *
     * @param currentThreshold The new threshold.
     */
    public void setThreshold( int currentThreshold )
    {
        this.currentThreshold = currentThreshold;
    }

    /**
     * Returns the current threshold for all new loggers.
     *
     * @return Returns the current threshold for all new loggers.
     */
    public int getThreshold()
    {
        return currentThreshold;
    }

    public void setThreshold( String role, String roleHint, int threshold )
    {
        Log4JLogger logger;

        String name;

        name = toMapKey( role, roleHint );

        logger = ( Log4JLogger ) loggerCache.get( name );

        if ( logger == null )
        {
            debug( "Trying to set the threshold of a unknown logger '" + name + "'." );
            return; // nothing to do
        }

        logger.setThreshold( threshold );
    }

    public int getThreshold( String role, String roleHint )
    {
        Log4JLogger logger;

        String name;

        name = toMapKey( role, roleHint );

        logger = ( Log4JLogger ) loggerCache.get( name );

        if ( logger == null )
        {
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

        logger = ( Logger ) loggerCache.get( name );

        if ( logger != null )
        {
            return logger;
        }

        debug( "Creating logger '" + name + "' " + hashCode() + "." );

        logger = new Log4JLogger( getThreshold(), org.apache.log4j.Logger.getLogger( name ) );

        loggerCache.put( name, logger );

        return logger;
    }

    public void returnComponentLogger( String role, String roleHint )
    {
        Object obj;

        String name;

        name = toMapKey( role, roleHint );

        obj = loggerCache.remove( name );

        if ( obj == null )
        {
            System.err.println( "There was no such logger '" + name + "' " + hashCode() + "." );
        }
        else
        {
            debug( "Removed logger '" + name + "' " + hashCode() + "." );
        }
    }

    public int getActiveLoggerCount()
    {
        return loggerCache.size();
    }
    /**
     * @todo Remove this method and all references when this code is verified.
     *
     * @param msg
     */
    protected void debug( String msg )
    {
//        System.out.println( "[Log4j] " + msg );
    }
}
