package org.codehaus.plexus.logging.commonslogging;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;

public class CommonsLoggingLoggerManagerTest
    extends PlexusTestCase
{
    /**
     * Creates the <code>${plexus.home}/logs</code> directory.
     */
    public final void setUp()
        throws Exception
    {
        File f;
        String plexusHome;

        super.setUp();

        plexusHome = getContainer().getContext().get( "plexus.home" ).toString();

        f = getTestFile( plexusHome, "logs" );

        if ( !f.isDirectory() )
        {
            f.mkdir();
        }
    }

    protected LoggerManager createLoggerManager()
        throws Exception
    {
        return (LoggerManager)lookup( LoggerManager.ROLE );
    }

    public void testSetThreshold()
        throws Exception
    {
        LoggerManager manager;
        Logger logger1, logger2;

        manager = createLoggerManager();

        manager.setThreshold( Logger.LEVEL_FATAL );
        logger1 = manager.getLoggerForComponent( "role1", "roleHint1" );
        assertEquals( Logger.LEVEL_FATAL, logger1.getThreshold() );

        manager.setThreshold( Logger.LEVEL_DEBUG );
        logger2 = manager.getLoggerForComponent( "role2", "roleHint2" );
        assertEquals( Logger.LEVEL_FATAL, logger1.getThreshold() );
        assertEquals( Logger.LEVEL_DEBUG, logger2.getThreshold() );
    }

    /**
     * There is only one logger instance pr component even if looked up more that once.
     */
    public void testActiveLoggerCount()
        throws Exception
    {
        LoggerManager manager;
        Logger b, c1, c2;
        Logger root;

        manager = createLoggerManager();
        assertEquals(0, manager.getActiveLoggerCount());

        b = manager.getLoggerForComponent( "b" );
        assertEquals(1, manager.getActiveLoggerCount());

        c1 = manager.getLoggerForComponent( "c", "1" );
        c1 = manager.getLoggerForComponent( "c", "1" );
        assertEquals(2, manager.getActiveLoggerCount());

        c2 = manager.getLoggerForComponent( "c", "2" );
        assertEquals(3, manager.getActiveLoggerCount());

        manager.returnComponentLogger( "c", "1" );
        assertEquals(2, manager.getActiveLoggerCount());

        manager.returnComponentLogger( "c", "2" );
        manager.returnComponentLogger( "c", "2" );
        manager.returnComponentLogger( "c", "1" );
        assertEquals(1, manager.getActiveLoggerCount());

        manager.returnComponentLogger( "b" );
        assertEquals(0, manager.getActiveLoggerCount());
    }

    public void testCreateLoggerManager()
        throws Exception
    {
        LoggerManager manager = createLoggerManager();
        assertNotNull( manager );
        assertEquals( "org.codehaus.plexus.logging.commonslogging.CommonsLoggingLoggerManager", manager.getClass().getName() );
    }

    public void testGetLoggerForComponent()
        throws Exception
    {
        LoggerManager manager = createLoggerManager();
        Logger logger = manager.getLoggerForComponent( "role1" );
        assertNotNull( logger );
        assertEquals( "org.codehaus.plexus.logging.commonslogging.CommonsLoggingLogger", logger.getClass().getName() );
        logger.info( "My message" );
    }
}