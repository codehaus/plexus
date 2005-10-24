package org.codehaus.plexus.logging.log4j;

import org.codehaus.plexus.logging.BaseLoggerManager;
import org.codehaus.plexus.logging.Logger;

/**
 * Base class for Logger Managers for Log4j logging system.
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractLog4JLoggerManager
        extends BaseLoggerManager
{

   /*
    * Implements a method in BaseLoggerManager.
    */
    public Logger createLogger( String key )
    {
        Logger logger = new Log4JLogger( getThreshold(), org.apache.log4j.Logger.getLogger( key ) );

        return logger;
    }

    /**
     * @todo Log4j supports following levels:
     *
     * OFF, FATAL, ERROR, WARN, INFO, DEBUG, ALL
     *
     * and ... custom levels.
     *
     * we can think bit longer what to do here to validate them.
     *
     * @return
     */
    public String getThresholdAsString()
    {
        String retValue = super.getThresholdAsString();

        if ( retValue != null )
        {
            retValue = retValue.toUpperCase();
        }

        return retValue;
    }

}
