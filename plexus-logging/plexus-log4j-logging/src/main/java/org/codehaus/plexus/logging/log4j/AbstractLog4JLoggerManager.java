package org.codehaus.plexus.logging.log4j;

import org.codehaus.plexus.logging.BaseLoggerManager;
import org.codehaus.plexus.logging.Logger;

/**
 *
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractLog4JLoggerManager
        extends BaseLoggerManager
{

    public Logger createLogger( String key )
    {
        Logger logger = new Log4JLogger( getThreshold(), org.apache.log4j.Logger.getLogger( key ) );

        return logger;
    }
}
