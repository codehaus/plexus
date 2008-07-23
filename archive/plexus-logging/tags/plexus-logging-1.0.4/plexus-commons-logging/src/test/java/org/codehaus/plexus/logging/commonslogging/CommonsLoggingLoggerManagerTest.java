package org.codehaus.plexus.logging.commonslogging;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.AbstractLoggerManagerTest;

public class CommonsLoggingLoggerManagerTest
    extends AbstractLoggerManagerTest
{

    protected LoggerManager createLoggerManager()
        throws Exception
    {
        return (LoggerManager)lookup( LoggerManager.ROLE );
    }


}