package org.codehaus.plexus.logging.jdk;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.AbstractLoggerManagerTest;

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

        System.out.println("Create logger manger called");
        return ( LoggerManager ) lookup( LoggerManager.ROLE );
    }

}