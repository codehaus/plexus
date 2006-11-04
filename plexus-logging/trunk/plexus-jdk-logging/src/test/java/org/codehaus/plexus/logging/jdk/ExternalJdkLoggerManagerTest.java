package org.codehaus.plexus.logging.jdk;

import org.codehaus.plexus.logging.AbstractLoggerManagerTest;
import org.codehaus.plexus.logging.LoggerManager;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public class ExternalJdkLoggerManagerTest
    extends AbstractLoggerManagerTest
{
    protected LoggerManager createLoggerManager()
        throws Exception
    {
        return (LoggerManager) lookup( LoggerManager.ROLE );
    }
}