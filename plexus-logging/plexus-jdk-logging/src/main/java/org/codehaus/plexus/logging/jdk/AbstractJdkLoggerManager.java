package org.codehaus.plexus.logging.jdk;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.logging.AbstractLoggerManager;
import org.codehaus.plexus.logging.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public abstract class AbstractJdkLoggerManager
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
        JdkLogger logger;

        String key = toMapKey( role, roleHint );

        logger = ( JdkLogger ) loggerCache.get( key );

        if ( logger == null )
        {
            debug( "Trying to set the threshold of a unknown logger '" + key + "'." );

            return; // nothing to do
        }

        logger.setThreshold( threshold );
    }

    public int getThreshold( String role, String roleHint )
    {
        JdkLogger logger;

        String key = toMapKey( role, roleHint );

        logger = ( JdkLogger ) loggerCache.get( key );

        if ( logger == null )
        {
            debug( "Trying to get the threshold of a unknown logger '" + key + "'." );

            return Logger.LEVEL_DEBUG; // does not return null because that could create a NPE
        }

        return logger.getThreshold();
    }

    public Logger getLoggerForComponent( String role, String roleHint )
    {
        Logger logger;

        String key = toMapKey( role, roleHint );

        logger = ( Logger ) loggerCache.get( key );

        if ( logger != null )
        {
            return logger;
        }

        debug( "Creating logger '" + key + "' " + hashCode() + "." );

        logger = new JdkLogger( getThreshold(), java.util.logging.Logger.getLogger( key ) );

        loggerCache.put( key, logger );

        return logger;
    }

    public void returnComponentLogger( String role, String roleHint )
    {
        Object obj;

        String key = toMapKey( role, roleHint );

        obj = loggerCache.remove( key );

        if ( obj == null )
        {
            System.err.println( "There was no such logger '" + key + "' " + hashCode() + "." );
        }
        else
        {
            debug( "Removed logger '" + key + "' " + hashCode() + "." );
        }
    }

    public int getActiveLoggerCount()
    {
        System.out.println( "loggerCache:" + loggerCache );

        return loggerCache.size();
    }

    /**
     * @param msg
     * @todo Remove this method and all references when this code is verified.
     */
    protected void debug( String msg )
    {
        System.out.println( "[jdk] " + msg );
    }
}
