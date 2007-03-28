package org.codehaus.plexus.logging.commonslogging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.logging.AbstractLogger;

public class CommonsLoggingLogger
    extends AbstractLogger
{
    private Log logger;

    public CommonsLoggingLogger( int threshold, String name )
    {
        super( threshold, name );

        logger = LogFactory.getLog( name );

        if ( null == logger )
        {
            throw new IllegalArgumentException( "Logger cannot be null" );
        }
    }

    public boolean isDebugEnabled()
    {
        if ( super.isDebugEnabled() )
        {
            return logger.isDebugEnabled();
        }
        else
        {
            return false;
        }
    }

    public void debug( String message )
    {
        logger.debug( message );
    }

    public void debug( String message, Throwable throwable )
    {
        logger.debug( message, throwable );
    }

    public boolean isInfoEnabled()
    {
        if( super.isInfoEnabled() )
        {
            return logger.isInfoEnabled();
        }
        else
        {
            return false;
        }
    }

    public void info( String message )
    {
        logger.info( message );
    }

    public void info( String message, Throwable throwable )
    {
        logger.info( message, throwable );
    }

    public boolean isWarnEnabled()
    {
        if( super.isWarnEnabled() )
        {
            return logger.isWarnEnabled();
        }
        else
        {
            return false;
        }
    }

    public void warn( String message )
    {
        logger.warn( message );
    }

    public void warn( String message, Throwable throwable )
    {
        logger.warn( message, throwable );
    }

    public boolean isErrorEnabled()
    {
        if( super.isErrorEnabled() )
        {
            return logger.isErrorEnabled();
        }
        else
        {
            return false;
        }
    }

    public void error( String message )
    {
        logger.error( message );
    }

    public void error( String message, Throwable throwable )
    {
        logger.error( message, throwable );
    }

    public boolean isFatalErrorEnabled()
    {
        if( super.isFatalErrorEnabled() )
        {
            return logger.isFatalEnabled();
        }
        else
        {
            return false;
        }
    }

    public void fatalError( String message )
    {
        logger.fatal( message );
    }

    public void fatalError( String message, Throwable throwable )
    {
        logger.fatal( message, throwable );
    }

    public org.codehaus.plexus.logging.Logger getChildLogger( String name )
    {
        return new CommonsLoggingLogger( getThreshold(), getName() + "." + name );
    }
}
