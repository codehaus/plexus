package org.codehaus.plexus;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ApplicationTest
    extends PlexusTestCase
{
    public void testApplication()
        throws Exception
    {
        Application app = (Application) lookup( Application.ROLE );

        Populator populator = app.getPopulator();

        assertNotNull( populator );

        // Make sure all the lifecycle phases have been run through
        assertTrue( populator.isConfigured() );

        assertTrue( populator.isMonitored() );

        assertTrue( populator.isExecuted() );

        // Do some work
        app.doWork();

        // Make sure it was done.
        assertTrue( app.isWorkDone() );
    }
}
