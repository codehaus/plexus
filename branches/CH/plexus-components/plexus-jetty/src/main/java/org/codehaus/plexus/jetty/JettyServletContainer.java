package org.codehaus.plexus.jetty;

import java.io.File;
import java.util.List;
import java.util.Iterator;
import java.net.UnknownHostException;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.mortbay.http.ajp.AJP13Listener;
import org.mortbay.util.Log;

public class JettyServletContainer
    extends AbstractLogEnabled
    implements Contextualizable, Initializable, Startable, ServletContainer
{
    private JettyServer server;

    private ClassLoader classLoader;

    private PlexusContainer plexusContainer;

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    private String webappsDirectory;

    private boolean extractWars;

    private boolean stopGracefully;

    private List socketListeners;

    private List ajpListeners;

    public void setWebappsDirectory( String webappsDirectory )
    {
        this.webappsDirectory = webappsDirectory;
    }

    public String getWebappsDirectory()
    {
        return webappsDirectory;
    }

    public void setExtractWars( boolean extractWars )
    {
        this.extractWars = extractWars;
    }

    public boolean getExtractWars()
    {
        return extractWars;
    }

    public void setStopGracefully( boolean stopGracefully )
    {
        this.stopGracefully = stopGracefully;
    }

    public boolean getStopGracefully()
    {
        return stopGracefully;
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        classLoader = (ClassLoader) context.get( "common.classloader" );

        plexusContainer = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void initialize()
        throws Exception
    {
        PlexusLogSink logSink = new PlexusLogSink();

        logSink.enableLogging( getLogger() );

        Log.instance().add( logSink );

        server = new JettyServer();

        // ----------------------------------------------------------------------
        // Socket listeners
        // ----------------------------------------------------------------------

        if ( socketListeners != null )
        {
            for ( Iterator i = socketListeners.iterator(); i.hasNext(); )
            {
                SocketListener listener = (SocketListener) i.next();

                configureSocketListener( server, listener );
            }
        }

        // ----------------------------------------------------------------------
        // Ajp listeners
        // ----------------------------------------------------------------------

        if ( ajpListeners != null )
        {
            for ( Iterator i = ajpListeners.iterator(); i.hasNext(); )
            {
                SocketListener listener = (SocketListener) i.next();

                configureAjpListener( server, listener );
            }
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        server.setPlexusContainer( plexusContainer );
        
        // This is wrong, but will suffice at the moment. jvz.
        server.setClassLoader( classLoader );

        File f = new File( webappsDirectory );

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        server.addWebApplications( "*", f.getCanonicalPath(), getExtractWars() );
    }

    public void start()
        throws Exception
    {
        server.start();
    }

    public void stop()
        throws Exception
    {
        server.stop( getStopGracefully() );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void configureSocketListener( JettyServer server, SocketListener listener )
        throws UnknownHostException
    {
        org.mortbay.http.SocketListener list = new org.mortbay.http.SocketListener();

        String host = listener.getHost();

        if ( !host.equals( "*" ) )
        {
            list.setHost( host );
        }

        list.setPort( listener.getPort() );

        server.addListener( list );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void configureAjpListener( JettyServer server, SocketListener listener )
        throws UnknownHostException
    {
        AJP13Listener list = new AJP13Listener();

        String host = listener.getHost();

        if ( !host.equals( "*" ) )
        {
            list.setHost( host );
        }

        list.setPort( listener.getPort() );

        server.addListener( list );
    }
}
