package org.codehaus.plexus.logging.commonslogging;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.logging.AbstractLoggerManager;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class CommonsLoggingLoggerManager
    extends AbstractLoggerManager
    implements LoggerManager, Initializable
{
    /**
     * Message of this level or higher will be logged. 
     * 
     * This field is set by the plexus container thus the name is 'threshold'. The field
     * currentThreshold contains the current setting of the threshold.
     */
    private String threshold = "info";

    private int currentThreshold;

    private Map loggers;

    /** The number of active loggers in use. */
    private int loggerCount;

    private boolean bootTimeLogger = false;

    public void initialize()
    {
        currentThreshold = parseThreshold( threshold );

        if ( currentThreshold == -1 )
        {
            currentThreshold = Logger.LEVEL_DEBUG;
        }

        loggers = new HashMap();
    }

    public void setThreshold( int currentThreshold )
    {
        this.currentThreshold = currentThreshold;
    }

    /**
     * @return Returns the threshold.
     */
    public int getThreshold()
    {
        return currentThreshold;
    }

    public void setThreshold( String role, String roleHint, int threshold )
    {
        CommonsLoggingLogger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (CommonsLoggingLogger)loggers.get( name );

        if(logger == null)
        {
            return; // nothing to do
        }

        logger.setThreshold( threshold );
    }

    public int getThreshold( String role, String roleHint )
    {
        CommonsLoggingLogger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (CommonsLoggingLogger)loggers.get( name );

        if(logger == null)
        {
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

        logger = new CommonsLoggingLogger( getThreshold(), name );
        loggers.put( name, logger );

        return logger;
    }

    public void returnComponentLogger( String role, String roleHint )
    {
        Object obj;
        String name;

        name = toMapKey( role, roleHint );
        obj = loggers.remove( name );
    }

    public int getActiveLoggerCount()
    {
        return loggers.size();
    }

    private int parseThreshold( String text )
    {
        text = text.trim().toLowerCase();

        if ( text.equals( "debug" ) )
        {
            return Logger.LEVEL_DEBUG;
        }
        else if ( text.equals( "info" ) )
        {
            return Logger.LEVEL_INFO;
        }
        else if ( text.equals( "warn" ) )
        {
            return Logger.LEVEL_WARN;
        }
        else if ( text.equals( "error" ) )
        {
            return Logger.LEVEL_ERROR;
        }
        else if ( text.equals( "fatal" ) )
        {
            return Logger.LEVEL_FATAL;
        }

        return -1;
    }

    private String decodeLogLevel( int logLevel )
    {
        switch(logLevel)
        {
            case Logger.LEVEL_DEBUG: return "debug";
            case Logger.LEVEL_INFO: return "info";
            case Logger.LEVEL_WARN: return "warn";
            case Logger.LEVEL_ERROR: return "error";
            case Logger.LEVEL_FATAL: return "fatal";
            case Logger.LEVEL_DISABLED: return "disabled";
            default: return "unknown";
        }
    }
}
