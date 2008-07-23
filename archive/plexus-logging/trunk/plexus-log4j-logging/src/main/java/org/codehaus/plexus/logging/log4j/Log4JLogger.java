package org.codehaus.plexus.logging.log4j;

import org.apache.log4j.Logger;
import org.codehaus.plexus.logging.AbstractLogger;

/**
 * Logger which delgates to logger from Log4j framwork.
 *  
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
class Log4JLogger
    extends AbstractLogger
{
    private Logger logger;

    public Log4JLogger( int threshold, Logger logger )
    {
        super( threshold, getLoggerName(logger) );

        if ( null == logger )
        {
            throw new IllegalArgumentException( "Logger cannot be null" );
        }

        this.logger = logger;
    }

    public void debug( String message )
    {
        logger.debug( message );
    }

    public void debug( String message, Throwable throwable )
    {
        logger.debug( message, throwable );
    }

    public void info( String message )
    {
        logger.info( message );
    }

    public void info( String message, Throwable throwable )
    {
        logger.info( message, throwable );
    }

    public void warn( String message )
    {
        logger.warn( message );
    }

    public void warn( String message, Throwable throwable )
    {
        logger.warn( message, throwable );
    }

    public void error( String message )
    {
        logger.error( message );
    }

    public void error( String message, Throwable throwable )
    {
        logger.error( message, throwable );
    }

    public void fatalError( String message )
    {
        logger.fatal( message );
    }

    public void fatalError( String message, Throwable throwable )
    {
        logger.fatal( message, throwable );
    }

    public String getName()
    {
        return logger.getName();
    }

    public org.codehaus.plexus.logging.Logger getChildLogger( String name )
    {
        return new Log4JLogger( getThreshold(), Logger.getLogger( logger.getName() + "." + name ) );
    }

    private static String getLoggerName( Logger logger)
    {
        if( logger == null )
        {
            return "";
        }

        return logger.getName();
    }
}
