package org.codehaus.plexus.logging;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;

/**
 * Abtract base class for testing implementations of the {@link LoggerManager}
 * and {@link Logger} interfaces.
 *
 * @author Mark H. Wilkinson
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Revision$
 */
public abstract class AbstractLoggerManagerTest
    extends PlexusTestCase
{
    protected abstract LoggerManager createLoggerManager()
        throws Exception;

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

        manager = getManager( Logger.LEVEL_FATAL );
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

    public void testDebugLevelConfiguration()
        throws Exception
    {
        LoggerManager manager = getManager( Logger.LEVEL_DEBUG );

        Logger logger = extractLogger( manager );

        checkDebugLevel( logger );

        logger = extractLogger( manager );

        checkDebugLevel( logger );
    }

    public void testInfoLevelConfiguration()
        throws Exception
    {
        LoggerManager manager = getManager( Logger.LEVEL_INFO );

        Logger logger = extractLogger( manager );

        checkInfoLevel( logger );

        logger = extractLogger( manager );

        checkInfoLevel( logger );
    }

    public void testWarnLevelConfiguration()
        throws Exception
    {
        LoggerManager manager = getManager( Logger.LEVEL_WARN );

        Logger logger = extractLogger( manager );

        checkWarnLevel( logger );

        logger = extractLogger( manager );

        checkWarnLevel( logger );
    }

    public void testErrorLevelConfiguration()
        throws Exception
    {
        LoggerManager manager = getManager( Logger.LEVEL_ERROR );

        Logger logger = extractLogger( manager );

        checkErrorLevel( logger );

        logger = extractLogger( manager );

        checkErrorLevel( logger );
    }

    public void testFatalLevelConfiguration()
        throws Exception
    {
        LoggerManager manager = getManager( Logger.LEVEL_FATAL );

        Logger logger = extractLogger( manager );

        checkFatalLevel( logger );

        logger = extractLogger( manager );

        checkFatalLevel( logger );
    }

    private LoggerManager getManager( int threshold )
        throws Exception
    {
        LoggerManager manager = createLoggerManager();

        manager.setThreshold( threshold );

        assertNotNull( manager );

        return manager;
    }
    /*
     private Logger extractRootLogger( LoggerManager manager )
     {
     Logger logger = manager.getRootLogger();

     assertNotNull( logger );

     return logger;
     }
     */
    private Logger extractLogger( LoggerManager manager )
    {
        Logger logger = manager.getLoggerForComponent( "foo" );

        assertNotNull( logger );
        assertEquals( "foo", logger.getName() );

        return logger;
    }

    private void checkDebugLevel( Logger logger )
    {
        assertTrue( "debug enabled", logger.isDebugEnabled() );
        assertTrue( "info enabled", logger.isInfoEnabled() );
        assertTrue( "warn enabled", logger.isWarnEnabled() );
        assertTrue( "error enabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }

    private void checkInfoLevel( Logger logger )
    {
        assertFalse( "debug disabled", logger.isDebugEnabled() );
        assertTrue( "info enabled", logger.isInfoEnabled() );
        assertTrue( "warn enabled", logger.isWarnEnabled() );
        assertTrue( "error enabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }

    private void checkWarnLevel( Logger logger )
    {
        assertFalse( "debug disabled", logger.isDebugEnabled() );
        assertFalse( "info disabled", logger.isInfoEnabled() );
        assertTrue( "warn enabled", logger.isWarnEnabled() );
        assertTrue( "error enabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }

    private void checkErrorLevel( Logger logger )
    {
        assertFalse( "debug disabled", logger.isDebugEnabled() );
        assertFalse( "info disabled", logger.isInfoEnabled() );
        assertFalse( "warn disabled", logger.isWarnEnabled() );
        assertTrue( "error enabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }

    private void checkFatalLevel( Logger logger )
    {
        assertFalse( "debug disabled", logger.isDebugEnabled() );
        assertFalse( "info disabled", logger.isInfoEnabled() );
        assertFalse( "warn disabled", logger.isWarnEnabled() );
        assertFalse( "error disabled", logger.isErrorEnabled() );
        assertTrue( "fatal enabled", logger.isFatalErrorEnabled() );
    }
}
