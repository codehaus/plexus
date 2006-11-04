package org.codehaus.plexus.logging.log4j;

import java.util.Properties;

import org.codehaus.plexus.logging.AbstractLoggerManagerTest;
import org.codehaus.plexus.logging.LoggerManager;

public class Log4JLoggerManagerWithNormalConfigTest
    extends AbstractLoggerManagerTest
{
    protected LoggerManager createLoggerManager()
        throws Exception
    {
        return (LoggerManager)lookup( LoggerManager.ROLE );
    }
}
