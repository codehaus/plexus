package org.codehaus.plexus.application;

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

import java.io.File;
import java.io.FileReader;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.application.deploy.ApplicationDeployer;
import org.codehaus.plexus.application.service.ServiceDiscoverer;
import org.codehaus.plexus.application.supervisor.Supervisor;
import org.codehaus.plexus.application.supervisor.SupervisorListener;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;

/**
 * A <code>ContainerHost</code>.
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @version $Id$
 */
public class PlexusApplicationHost
    implements Runnable
{
    private DefaultPlexusContainer container;

    private boolean shouldStop;

    private boolean isStopped;

    private static Object waitObj;

    private ApplicationDeployer applicationDeployer;

    private Supervisor supervisor;

    private ServiceDiscoverer serviceDiscoverer;

    // ----------------------------------------------------------------------
    //  Implementation
    // ----------------------------------------------------------------------

    public void start( ClassWorld classWorld, String configurationResource )
        throws Exception
    {
        container = new DefaultPlexusContainer();

        container.setClassWorld( classWorld );

        container.setConfigurationResource( new FileReader( configurationResource ) );

        container.addContextValue( "plexus.home", System.getProperty( "plexus.home" ) );

        container.addContextValue( "plexus.work", System.getProperty( "plexus.home" ) + "/work" );

        container.addContextValue( "plexus.logs", System.getProperty( "plexus.home" ) + "/logs" );

        File plexusLogs = new File( System.getProperty( "plexus.home" ) + "/logs" );

        if ( plexusLogs.exists() == false )
        {
            plexusLogs.mkdirs();
        }

        container.initialize();

        container.start();

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        LoggerManager loggerManager = (LoggerManager) container.lookup( LoggerManager.ROLE );

        loggerManager.setThreshold( Logger.LEVEL_DEBUG );

        serviceDiscoverer = (ServiceDiscoverer) container.lookup( ServiceDiscoverer.ROLE );

        applicationDeployer = (ApplicationDeployer) container.lookup( ApplicationDeployer.ROLE );

        supervisor = (Supervisor) container.lookup( Supervisor.ROLE );

        final Logger logger = loggerManager.getLoggerForComponent( this.getClass().getName() );

        // ----------------------------------------------------------------------
        // Register the deployers inside the directory supervisor so applications
        // and services will be deployed.
        // ----------------------------------------------------------------------

        File home = new File( System.getProperty( "plexus.home" ) );

        supervisor.addDirectory( new File( home, "services" ), new SupervisorListener()
        {
            public void onJarDiscovered( File jar )
            {
                String name = jar.getName();

                try
                {
                    serviceDiscoverer.deploy( name.substring( 0, name.length() - 4 ), jar.getAbsolutePath() );
                }
                catch ( Exception e )
                {
                    System.err.println( "Error while deploying service '" + name + "'." );
                    e.printStackTrace();
                }
            }
        } );

        supervisor.addDirectory( new File( home, "apps" ), new SupervisorListener()
        {
            public void onJarDiscovered( File jar )
            {
                String name = jar.getName();

                try
                {
                    applicationDeployer.deploy( name.substring( 0, name.length() - 4 ), jar.toURL().toExternalForm() );
                }
                catch ( Exception e )
                {
                    System.err.println( "Error while deploying application '" + name + "'." );

                    e.printStackTrace();
                }
            }
        } );

        logger.info( "The application server has been initialized." );

        // ----------------------------------------------------------------------
        // Now start the supervisor which will deploy all services and applications
        // ----------------------------------------------------------------------

        supervisor.scan();

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        // TODO: Add timing.
        logger.info( "The application server has started." );

        Thread thread = new Thread( this );

        thread.setDaemon( false );

        Runtime.getRuntime().addShutdownHook( new Thread( new Runnable()
        {
            public void run()
            {
                try
                {
                    logger.info( "Shutting down the application container." );

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
            waitObj = new Object();

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


