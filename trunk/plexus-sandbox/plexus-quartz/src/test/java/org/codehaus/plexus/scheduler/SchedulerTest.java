package org.codehaus.plexus.scheduler;

import org.codehaus.plexus.PlexusTestCase;

import java.io.File;

public class SchedulerTest extends PlexusTestCase
{
    public SchedulerTest(String name)
    {
        super( name );
    }

    public void testCreation()
        throws Exception
    {
        Scheduler scheduler = (Scheduler) getComponent( Scheduler.ROLE );

        assertNotNull( scheduler );
    }

    public void testAddJobFile()
        throws Exception
    {
        Scheduler scheduler = (Scheduler) getComponent( Scheduler.ROLE );

        File jobFile = new File( getTestInputDir(), "plexus-jobs-test-0.1.jar" );
        scheduler.addJobFile( jobFile );
        Thread.sleep( 7000 );
    }

    protected File getTestInputDir()
    {
        String baseDir = System.getProperty( "basedir" );
        File testInputDir = new File ( baseDir, "target/test-plexus/work/jobs" );
        return testInputDir;
    }
}

