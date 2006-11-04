package org.codehaus.plexus.logging.jdk;

import org.codehaus.plexus.logging.BaseLoggerManager;
import org.codehaus.plexus.logging.Logger;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public abstract class AbstractJdkLoggerManager
    extends BaseLoggerManager
{
    public Logger createLogger( String key )
    {
        Logger logger = new JdkLogger( getThreshold(), java.util.logging.Logger.getLogger( key ) );

        return logger;
    }
}
