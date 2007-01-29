package org.codehaus.plexus.jetty;

/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.appserver.application.profile.AppRuntimeProfile;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.codehaus.plexus.jetty.configuration.InitParameter;
import org.codehaus.plexus.jetty.configuration.ProxyHttpListener;
import org.codehaus.plexus.jetty.configuration.ServletContext;
import org.codehaus.plexus.jetty.configuration.WebContext;
import org.codehaus.plexus.jetty.configuration.Webapp;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.util.FileUtils;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.InetAddrPort;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id: JettyServletContainer.java 4167 2006-09-20 20:31:27Z evenisse $
 * @todo need to be able to deploy individual servlets
 * @todo could we filter web.xml files via jetty somehow?
 */
public abstract class AbstractJettyServletContainer
    extends AbstractLogEnabled
    implements ServletContainer, Startable
{
    protected Server server;

    protected Map httpListeners = new HashMap();

    private Map classLoaders = new HashMap();

    private Map classRealms = new HashMap();

    private ListenerStopThread stopThread;

    public boolean hasContext( String contextPath )
    {
        HttpContext[] contexts = server.getContexts();

        for ( int i = 0; i < contexts.length; i++ )
        {
            HttpContext context = contexts[i];

            if ( context.getContextPath().equals( contextPath ) )
            {
                return true;
            }
        }

        return false;
    }

    public void addListener( org.codehaus.plexus.jetty.configuration.HttpListener listener )
        throws ServletContainerException, UnknownHostException
    {
        if ( isPortRegistered( listener ) )
        {
            updatePort( listener );
        }
        else
        {
            InetAddrPort addrPort = new InetAddrPort( listener.getHost(), listener.getPort() );

            try
            {
                HttpListener httpListener = server.addListener( addrPort );

                registerPort( listener, httpListener );

                httpListener.start();
            }
            catch ( IOException e )
            {
                throw new ServletContainerException( "Error while adding httpListener on address: '" +
                    listener.getHost() + "', port: " + listener.getPort() + ".", e );
            }
            catch ( Exception e )
            {
                throw new ServletContainerException( "Error while starting httpListener on address: '" +
                    listener.getHost() + "', port: " + listener.getPort() + ".", e );
            }
        }
    }

    public void addProxyListener( ProxyHttpListener listener )
        throws ServletContainerException, UnknownHostException
    {
        if ( isPortRegistered( listener ) )
        {
            updatePort( listener );
        }
        else
        {
            InetAddrPort addrPort = new InetAddrPort( listener.getHost(), listener.getPort() );

            JettyProxyHttpListener proxyListener = new JettyProxyHttpListener( addrPort );

            proxyListener.setForcedHost( listener.getProxyHost() + ":" + listener.getProxyPort() );

            server.addListener( proxyListener );

            registerPort( listener, proxyListener );

            try
            {
                proxyListener.start();
            }
            catch ( Exception e )
            {
                throw new ServletContainerException( "Error while starting proxyListener on address: '" +
                    listener.getHost() + "', port: " + listener.getPort() + ".", e );
            }
        }
    }

    public void removeListener( org.codehaus.plexus.jetty.configuration.HttpListener listener )
        throws ServletContainerException
    {
        String port = Integer.toString( listener.getPort() );

        if ( httpListeners.containsKey( port ) )
        {
            HttpListenerReference httpListenerReference = (HttpListenerReference) httpListeners.get( port );

            if ( httpListenerReference.decrement().getRefCount() <= 0 )
            {
                if ( stopThread == null || !stopThread.isWaiting() )
                {
                    stopThread = new ListenerStopThread( server, httpListenerReference.getListener(), port );

                    //noinspection CatchGenericClass
                    try
                    {
                        stopThread.start();
                    }
                    catch ( Throwable t )
                    {
                        getLogger().info( "Error Stopping Http Listener", t );
                    }
                }
            }
        }
    }

    public void deployWarDirectory( File directory, AppRuntimeProfile profile, Webapp webapp )
        throws ServletContainerException
    {
        String context = webapp.getContext();

        String virtualHost = webapp.getVirtualHost();

        boolean standardWebappClassloader = webapp.isStandardWebappClassloader();

        if ( directory == null )
        {
            throw new ServletContainerException( "Invalid parameter: 'war' cannot be null." );
        }

        if ( context == null )
        {
            throw new ServletContainerException( "Invalid parameter: 'context' cannot be null." );
        }

        // ----------------------------------------------------------------------
        // Create the web appserver
        // ----------------------------------------------------------------------

        WebApplicationContext webappContext;

        if ( !hasContext( context ) )
        {
            try
            {
                if ( virtualHost != null )
                {
                    webappContext = server.addWebApplication( virtualHost, context, directory.getAbsolutePath() );
                }
                else
                {
                    webappContext = server.addWebApplication( context, directory.getAbsolutePath() );
                }
            }
            catch ( IOException e )
            {
                throw new ServletContainerException( "Error while deploying WAR.", e );
            }

            // ----------------------------------------------------------------------
            // Configure the appserver context
            // ----------------------------------------------------------------------

            webappContext.setExtractWAR( false );

            if ( standardWebappClassloader )
            {
                getLogger().info( "Using standard webapp classloader for webapp." );
                try
                {
                    // We need to start the context to trigger the unpacking so that we can
                    // create a realm. We need to create a realm so that we can discover all
                    // the components in the webapp.

                    // The webapp directory needs to be unpacked before we can pick up the files
                    ClassRealm realm = profile.getApplicationRealm();

                    List jars = FileUtils.getFiles( directory, "WEB-INF/lib/*.jar", null );

                    for ( Iterator i = jars.iterator(); i.hasNext(); )
                    {
                        File file = (File) i.next();

                        realm.addURL( file.toURL() );
                    }

                    File classes = new File( directory, "WEB-INF/classes" );

                    realm.addURL( classes.toURL() );

                    webappContext.setClassLoader( realm );

                    classRealms.put( webapp.getContext(), realm );
                }
                catch ( MalformedURLException e )
                {
                    throw new ServletContainerException( "Error creating webapp classloader.", e );
                }
                catch ( IOException e )
                {
                    throw new ServletContainerException( "Error creating webapp classloader.", e );
                }
            }
            else
            {
                // Dirty hack, need better methods for classloaders because i can set the core realm but not get it,
                // or get the container realm but not set it. blah!
                webappContext.setClassLoader( profile.getApplicationRealm() );
            }

            // Save the classloader for reloads
            classLoaders.put( webapp.getContext(), webappContext.getClassLoader() );
        }
        else
        {
            webappContext = (WebApplicationContext) getContext( webapp.getContext() );

            // TODO: determine if this is really needed now

            // We only need to reset the classloader if we're doing standard webapp loading. The stopping
            // of the Jetty context seems to whack the classloader so we need to reset it. If we are
            // using Plexus classloading then the classloader appears to resist the whacking. Not sure
            // what's happening here but classworlds is going to take a bath shortly anyway.
            if ( standardWebappClassloader )
            {

                webappContext.setClassLoader( (ClassLoader) classLoaders.get( webapp.getContext() ) );
                ClassRealm cr = (ClassRealm) classRealms.get( webapp.getContext() );

                Enumeration realms = new Vector( profile.getApplicationWorld().getRealms() ).elements();
                while ( realms.hasMoreElements() )
                {
                    ClassRealm next = (ClassRealm) realms.nextElement();

                    try
                    {
                        profile.getApplicationWorld().disposeRealm( next.getId() );
                    }
                    catch ( NoSuchRealmException e )
                    {
                        // no problems if it can ever happen
                    }
                }

                try
                {
                    profile.getApplicationWorld().newRealm( cr.getId(), cr );
                }
                catch ( DuplicateRealmException e )
                {
                    // won't happen
                }
            }
        }

        // This is used by the xwork integration to pass some knowledge of the application server to the web applications
        webappContext.getServletContext().setAttribute( PlexusConstants.PLEXUS_KEY,
                                                        profile.getApplicationServerContainer() );
    }

    public void startApplication( String contextPath )
        throws ServletContainerException
    {
        //noinspection OverlyBroadCatchBlock
        try
        {
            HttpContext context = getContext( contextPath );

            getLogger().info( "Starting Jetty Context " + contextPath );

            context.start();
        }
        catch ( Exception e )
        {
            throw new ServletContainerException( "Error while starting the context " + contextPath, e );
        }
    }

    public void stopApplication( String contextPath )
        throws ServletContainerException
    {
        try
        {
            HttpContext context = getContext( contextPath );

            getLogger().info( "Starting Jetty Context " + contextPath );

            context.stop( true );
        }
        catch ( InterruptedException e )
        {
            throw new ServletContainerException( "Error while stopping the context " + contextPath, e );
        }
    }

    public void deployContext( WebContext webContext )
        throws ServletContainerException
    {
        if ( hasContext( webContext.getContext() ) )
        {
            return;
        }

        HttpContext context = new HttpContext();

        context.setContextPath( webContext.getContext() );

        context.setResourceBase( webContext.getPath() );

        // This will setup a standard resource handler for document retrieval. If you want more
        // functionality like POST, or WebDAV type stuff that will need to be configurable.
        context.addHandler( new ResourceHandler() );

        addContext( context );
    }

    public void deployServletContext( ServletContext servletContext, AppRuntimeProfile appRuntimeProfile )
        throws ServletContainerException
    {
        if ( hasContext( servletContext.getContext() ) )
        {
            return;
        }

        ServletHttpContext context = new ServletHttpContext();

        context.setContextPath( servletContext.getContext() );

        context.setClassLoader( appRuntimeProfile.getApplicationRealm() );

        try
        {
            ServletHolder servletHolder =
                context.addServlet( servletContext.getName(), servletContext.getPath(), servletContext.getServlet() );

            // ----------------------------------------------------------------------------
            // Setup any init parameters
            // ----------------------------------------------------------------------------

            if ( servletContext.getInitParameters() != null )
            {
                for ( Iterator i = servletContext.getInitParameters().iterator(); i.hasNext(); )
                {
                    InitParameter param = (InitParameter) i.next();

                    String name = param.getName();

                    String value = param.getValue();

                    getLogger().info( "Setting init-param [" + name + " = " + value + "]" );

                    String directive = param.getDirective();

                    if ( directive != null )
                    {
                        if ( "create-directory".equals( directive ) )
                        {
                            FileUtils.mkdir( value );
                        }
                    }

                    servletHolder.setInitParameter( name, value );
                }
            }
        }
        catch ( ClassNotFoundException e )
        {
            throw new ServletContainerException( "Cannot find the servlet " + servletContext.getServlet(), e );
        }
        catch ( InstantiationException e )
        {
            throw new ServletContainerException( "Cannot instantiate the servlet " + servletContext.getServlet(), e );
        }
        catch ( IllegalAccessException e )
        {
            throw new ServletContainerException(
                "Illegal access trying to use the servlet " + servletContext.getServlet(), e );
        }

        addContext( context );
    }

    private HttpContext getContext( String contextPath )
        throws ServletContainerException
    {
        HttpContext[] contexts = server.getContexts();

        for ( int i = 0; i < contexts.length; i++ )
        {
            HttpContext context = contexts[i];

            if ( context.getContextPath().equals( contextPath ) )
            {
                return context;
            }
        }

        throw new ServletContainerException( "No such context '" + contextPath + "'." );
    }

    protected HttpContext addContext( HttpContext context )
    {
        return server.addContext( context );
    }

    public void clearContexts()
    {
        HttpContext[] contexts = server.getContexts();

        for ( int i = 0; i < contexts.length; i++ )
        {
            HttpContext context = contexts[i];

            getLogger().info( "Removing context " + context.getContextPath() );

            server.removeContext( context );
        }
    }

    public void stop()
    {
        if ( server != null && server.isStarted() )
        {
            while ( true )
            {
                try
                {
                    server.stop( true );

                    break;
                }
                catch ( InterruptedException e )
                {
                    // ignore, try again
                }
            }

            server.destroy();
        }
    }

    public boolean isPortRegistered( org.codehaus.plexus.jetty.configuration.HttpListener listener )
    {
        return httpListeners.containsKey( Integer.toString( listener.getPort() ) );
    }

    private void registerPort( org.codehaus.plexus.jetty.configuration.HttpListener config, HttpListener httpListener )
    {
        String port = Integer.toString( config.getPort() );

        if ( !httpListeners.containsKey( port ) )
        {
            HttpListenerReference httpRef = new HttpListenerReference( httpListener );

            httpListeners.put( port, httpRef );
        }
    }

    private void updatePort( org.codehaus.plexus.jetty.configuration.HttpListener config )
    {
        String port = Integer.toString( config.getPort() );

        if ( httpListeners.containsKey( port ) )
        {
            HttpListenerReference httpRef = (HttpListenerReference) httpListeners.get( port );

            httpRef.increment();
        }
    }

    private class ListenerStopThread
        extends Thread
    {
        private Server server;

        private HttpListener listener;

        private boolean waiting;

        private String port;

        private ListenerStopThread( Server server, HttpListener listener, String port )
        {
            this.server = server;
            this.listener = listener;
            this.port = port;
        }

        public void run()
        {
            try
            {
                waiting = true;
                Thread.sleep( 20000 );
                waiting = false;
                stopListener();
            }
            catch ( Exception ex )
            {
                //TODO:
            }
        }

        private void stopListener()
        {
            if ( httpListeners.containsKey( port ) )
            {
                HttpListenerReference httpListenerReference = (HttpListenerReference) httpListeners.get( port );

                if ( httpListenerReference.getRefCount() <= 0 )
                {
                    httpListeners.remove( port );

                    server.removeListener( listener );
                }
            }
        }

        public boolean isWaiting()
        {
            return waiting;
        }
    }
}
