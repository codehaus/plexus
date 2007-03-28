package org.codehaus.plexus.logging.commonslogging;

import org.codehaus.plexus.logging.BaseLoggerManager;
import org.codehaus.plexus.logging.Logger;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public class CommonsLoggingLoggerManager
    extends BaseLoggerManager
{

    public Logger createLogger( String key )
    {
        Logger logger = new CommonsLoggingLogger( getThreshold(), key );

        return logger;
    }

}
