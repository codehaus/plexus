package org.codehaus.plexus.appserver;

/*
 * Copyright (c) 2004, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;

import java.io.File;
import java.io.FileReader;

// Container host plexus container is configured and initialized
// The appserver component is looked up

/**
 * A <code>ContainerHost</code>.
 *
 * @author Jason van Zyl
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusApplicationHost
    implements Runnable
{
    private DefaultPlexusContainer container;

    private boolean shouldStop;

    private boolean isStopped;

    private static final Object waitObj = new Object();

    private ApplicationServer applicationServer;

    private ClassWorld classWorld;

    private String configurationResource;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public PlexusContainer getContainer()
    {
        return container;
    }

    public ApplicationServer getApplicationServer()
    {
        return applicationServer;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void start( ClassWorld classWorld, String configurationResource )
        throws Exception
    {
        this.classWorld = classWorld;

        this.configurationResource = configurationResource;

        container = new DefaultPlexusContainer();

        container.setClassWorld( classWorld );

        container.setConfigurationResource( new FileReader( configurationResource ) );

        String plexusHome = System.getProperty( "plexus.home" );
        plexusHome = new File( plexusHome ).getAbsolutePath();

        container.addContextValue( "plexus.home", plexusHome );

        File appserverHome = new File( System.getProperty( "appserver.home", plexusHome ) );
        container.addContextValue( "appserver.home", appserverHome.getAbsolutePath() );

        File appserverBase = new File( System.getProperty( "appserver.base", appserverHome.getAbsolutePath() ) );
        container.addContextValue( "appserver.base", appserverBase.getAbsolutePath() );

        container.addContextValue( "plexus.work",
                                   new File( appserverBase, PlexusRuntimeConstants.WORK_DIRECTORY ).getAbsolutePath() );

        File plexusTemp = new File( appserverBase, PlexusRuntimeConstants.TEMP_DIRECTORY );
        container.addContextValue( "plexus.temp", plexusTemp.getAbsolutePath() );

        File plexusLogs = new File( appserverBase, PlexusRuntimeConstants.LOGS_DIRECTORY );
        container.addContextValue( "plexus.logs", plexusLogs.getAbsolutePath() );

        if ( !plexusLogs.exists() )
        {
            plexusLogs.mkdirs();
        }

        if ( !plexusTemp.exists() )
        {
            plexusTemp.mkdirs();
        }

        container.initialize();

        container.start();

        LoggerManager loggerManager = (LoggerManager) container.lookup( LoggerManager.ROLE );

        loggerManager.setThreshold( Logger.LEVEL_DEBUG );

        final Logger logger = loggerManager.getLoggerForComponent( this.getClass().getName() );

        // ----------------------------------------------------------------------
        // This lookup will start the appserver server
        // ----------------------------------------------------------------------

        applicationServer = (ApplicationServer) container.lookup( ApplicationServer.ROLE );
        applicationServer.setAppServerHome( appserverHome );
        applicationServer.setAppServerBase( appserverBase );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        // TODO: Add timing.
        logger.info( "The appserver server has started." );

        Thread thread = new Thread( this );

        thread.setDaemon( false );

        Runtime.getRuntime().addShutdownHook( new Thread( new Runnable()
        {
            public void run()
            {
                try
                {
                    logger.info( "Shutting down the appserver container." );

                    shutdown();
                }
                catch ( Exception e )
                {
                    // do nothing.
                }
            }
        } ) );

        thread.start();
    }

    /**
     * Asynchronous hosting component loop.
     */
    public void run()
    {
        synchronized ( this )
        {
            while ( !shouldStop )
            {
                try
                {
                    wait();
                }
                catch ( InterruptedException e )
                {
                    //ignore
                }
            }
        }

        synchronized ( this )
        {
            isStopped = true;

            notifyAll();
        }
    }

    public void restart()
        throws Exception
    {
        shutdown();

        start( classWorld, configurationResource );
    }

    // ----------------------------------------------------------------------
    //  Startup
    // ----------------------------------------------------------------------

    /**
     * Shutdown this container.
     *
     * @throws java.lang.Exception If an error occurs while shutting down the container.
     */
    public void shutdown()
        throws Exception
    {
        synchronized ( this )
        {
            shouldStop = true;

            container.release( applicationServer );

            container.dispose();

            notifyAll();
        }

        synchronized ( this )
        {
            while ( !isStopped )
            {
                try
                {
                    wait();
                }
                catch ( InterruptedException e )
                {
                    //ignore
                }
            }

            synchronized ( waitObj )
            {
                waitObj.notifyAll();
            }
        }
    }

    private boolean isStopped()
    {
        return isStopped;
    }

    // ----------------------------------------------------------------------
    // Main: used by org.codehaus.classworlds.Launcher
    // ----------------------------------------------------------------------

    public static void main( String[] args, ClassWorld classWorld )
    {
        if ( args.length != 1 )
        {
            System.err.println( "usage: plexus <plexus.xml>" );

            System.exit( 1 );
        }

        try
        {
            PlexusApplicationHost host = new PlexusApplicationHost();

            host.start( classWorld, args[0] );

            while ( !host.isStopped() )
            {
                try
                {
                    synchronized ( waitObj )
                    {
                        waitObj.wait();
                    }
                }
                catch ( InterruptedException e )
                {
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();

            System.exit( 2 );
        }
    }
}
