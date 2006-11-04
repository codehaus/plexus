package org.codehaus.plexus.logging.log4j;

import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.Logger;

/**
 * Logger Manager for Log4j logging system
 * supposed to be used in situtation when Plexus is
 * embedded in larger application which have already log4j system configured.
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public class EmbeddedLog4JLoggerManager
    implements LoggerManager
{
    public Logger getLoggerForComponent( String role )
    {
        return createLogger( role );
    }

    public Logger getLoggerForComponent( String role,
                                         String roleHint )
    {
        return createLogger( role );
    }

    public Logger createLogger( String category )
    {
        return new Log4JLogger( getThreshold(), org.apache.log4j.Logger.getLogger( category ) );
    }

    public void setThreshold( int threshold )
    {
    }

    public void setThresholds( int threshold )
    {
    }

    public int getThreshold()
    {
        return 0;
    }

    public void setThreshold( String role,
                              int threshold )
    {
    }

    public void setThreshold( String role,
                              String roleHint,
                              int threshold )
    {
    }

    public int getThreshold( String role )
    {
        return 0;
    }

    public int getThreshold( String role,
                             String roleHint )
    {
        return 0;
    }


    public void returnComponentLogger( String role )
    {
    }

    public void returnComponentLogger( String role,
                                       String hint )
    {
    }

    public int getActiveLoggerCount()
    {
        return 0;
    }
}
