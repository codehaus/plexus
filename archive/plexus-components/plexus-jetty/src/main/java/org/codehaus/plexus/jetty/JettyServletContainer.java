package org.codehaus.plexus.jetty;

import java.io.File;
import java.io.IOException;
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
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.mortbay.http.ajp.AJP13Listener;
import org.mortbay.util.Log;
import org.mortbay.util.MultiException;

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

    private List proxyListeners;

    private String DEFAULT_WEBAPP_CONFIGURATION = "org/codehaus/plexus/jetty/webdefault.xml";

    private String webappConfiguration;

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
        throws InitializationException
    {
        PlexusLogSink logSink = new PlexusLogSink();

        logSink.enableLogging( getLogger() );

        Log.instance().add( logSink );

        server = new JettyServer();

        // ----------------------------------------------------------------------
        // Setting of the default webapp configuration. By default we will use
        // our own descriptor which doesn't require the use of JSP.
        // ----------------------------------------------------------------------

        if ( webappConfiguration == null )
        {
            webappConfiguration = DEFAULT_WEBAPP_CONFIGURATION;
        }

        server.setWebappConfiguration( webappConfiguration );

        try
        {

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
                    AjpListener listener = (AjpListener) i.next();

                    configureAjpListener( server, listener );
                }
            }

            // ----------------------------------------------------------------------
            // Proxy listeners
            // ----------------------------------------------------------------------

            if ( proxyListeners != null )
            {
                for ( Iterator i = proxyListeners.iterator(); i.hasNext(); )
                {
                    ProxyListener listener = (ProxyListener) i.next();

                    configureProxyListener( server, listener );
                }
            }
        }
        catch ( UnknownHostException e )
        {
            throw new InitializationException( "Error initializing listeners: ", e );
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

        try
        {
            server.addWebApplications( "*", f.getCanonicalPath(), getExtractWars() );
        }
        catch ( IOException e )
        {
            throw new InitializationException( "Error trying to add webapps: ", e );
        }
    }

    public void start()
        throws StartingException
    {
        try
        {
            server.start();
        }
        catch ( MultiException e )
        {
            throw new StartingException( "Error starting the jetty servlet container: ", e );
        }
    }

    public void stop()
        throws StoppingException
    {
        try
        {
            server.stop( getStopGracefully() );
        }
        catch ( InterruptedException e )
        {
            throw new StoppingException( "Error stopping the jetty servlet container: ", e );
        }
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

    private void configureProxyListener( JettyServer server, ProxyListener listener )
        throws UnknownHostException
    {
        org.codehaus.plexus.jetty.http.ProxyListener list = new org.codehaus.plexus.jetty.http.ProxyListener();

        String host = listener.getHost();

        if ( !host.equals( "*" ) )
        {
            list.setHost( host );
        }

        list.setPort( listener.getPort() );

        list.setForcedHost( listener.getProxyHost() + ":" + listener.getProxyPort() );

        server.addListener( list );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void configureAjpListener( JettyServer server, AjpListener listener )
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
