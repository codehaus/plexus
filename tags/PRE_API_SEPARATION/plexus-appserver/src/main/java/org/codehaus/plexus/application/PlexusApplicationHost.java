package org.codehaus.plexus.application;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.application.deploy.ApplicationDeployer;

import java.io.File;
import java.io.FileReader;

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

        applicationDeployer = (ApplicationDeployer) container.lookup( ApplicationDeployer.ROLE );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        Thread thread = new Thread( this );

        thread.setDaemon( false );

        Runtime.getRuntime().addShutdownHook( new Thread( new Runnable()
        {
            public void run()
            {
                try
                {
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
            System.err.println( "usage: plexus <plexus.conf>" );

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


