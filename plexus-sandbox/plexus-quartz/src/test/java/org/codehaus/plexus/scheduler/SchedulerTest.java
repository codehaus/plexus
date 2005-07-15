package org.codehaus.plexus.scheduler;

import org.codehaus.plexus.PlexusTestCase;

import java.io.File;

public class SchedulerTest extends PlexusTestCase
{
    public void testCreation()
        throws Exception
    {
        Scheduler scheduler = (Scheduler) lookup( Scheduler.ROLE );

        assertNotNull( scheduler );
    }
}

