package org.codehaus.plexus.logging.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

class Log4JLogger
    implements org.codehaus.plexus.logging.Logger
{
    private Logger logger;

    public Log4JLogger( Logger logger )
    {
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

    public boolean isDebugEnabled()
    {
        return logger.isDebugEnabled();
    }

    public void info( String message )
    {
        logger.info( message );
    }

    public void info( String message, Throwable throwable )
    {
        logger.info( message, throwable );
    }

    public boolean isInfoEnabled()
    {
        return logger.isInfoEnabled();
    }

    public void warn( String message )
    {
        logger.warn( message );
    }

    public void warn( String message, Throwable throwable )
    {
        logger.warn( message, throwable );
    }

    public boolean isWarnEnabled()
    {
        return logger.isEnabledFor( Priority.WARN );
    }

    public void error( String message )
    {
        logger.error( message );
    }

    public void error( String message, Throwable throwable )
    {
        logger.error( message, throwable );
    }

    public boolean isErrorEnabled()
    {
        return logger.isEnabledFor( Priority.ERROR );
    }

    public void fatalError( String message )
    {
        logger.fatal( message );
    }

    public void fatalError( String message, Throwable throwable )
    {
        logger.fatal( message, throwable );
    }

    public boolean isFatalErrorEnabled()
    {
        return logger.isEnabledFor( Priority.FATAL );
    }

    public org.codehaus.plexus.logging.Logger getChildLogger( String name )
    {
        return new Log4JLogger( Logger.getLogger( logger.getName() + "." + name ) );
    }
}
