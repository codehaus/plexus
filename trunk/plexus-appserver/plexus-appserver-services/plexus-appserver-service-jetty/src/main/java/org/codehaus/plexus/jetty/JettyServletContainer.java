package org.codehaus.plexus.jetty;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.jetty.configuration.InitParameter;
import org.codehaus.plexus.jetty.configuration.ServletContext;
import org.codehaus.plexus.jetty.configuration.WebContext;
import org.codehaus.plexus.jetty.configuration.Webapp;
import org.codehaus.plexus.util.FileUtils;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.InetAddrPort;
import org.mortbay.util.MultiException;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 * @todo need to be able to deploy individual servlets
 * @todo could we filter web.xml files via jetty somehow?
 */
public class JettyServletContainer
    extends AbstractLogEnabled
    implements ServletContainer,
    Startable
{
    private Server server;

    // ----------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------

    public void start()
        throws StartingException
    {
        // ----------------------------------------------------------------------
        // Start the server
        // ----------------------------------------------------------------------

        server = new Server();

        try
        {
            server.start();
        }
        catch ( MultiException e )
        {
            throw new StartingException( "Error while starting Jetty", e );
        }
    }

    public void stop()
    {
        if ( server.isStarted() )
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
                    continue;
                }
            }

            server.destroy();
        }
    }

    // ----------------------------------------------------------------------
    // ServletContainer Implementation
    // ----------------------------------------------------------------------

    public boolean hasContext( String contextPath )
    {
        HttpContext[] contexts = server.getContexts();

        HttpContext context;

        for ( int i = 0; i < contexts.length; i++ )
        {
            context = contexts[i];

            if ( context.getContextPath().equals( contextPath ) )
            {
                return true;
            }
        }

        return false;
    }

    public void addListener( String host,
                             int port )
        throws ServletContainerException, UnknownHostException
    {
        InetAddrPort addrPort = new InetAddrPort( host, port );

        HttpListener listener;

        try
        {
            listener = server.addListener( addrPort );
        }
        catch ( IOException e )
        {
            throw new ServletContainerException(
                "Error while adding listener on address: '" + host + "', port: " + port + ".", e );
        }

        try
        {
            listener.start();
        }
        catch ( Exception e )
        {
            throw new ServletContainerException(
                "Error while starting listener on address: '" + host + "', port: " + port + ".", e );
        }
    }

    public void addProxyListener( String host,
                                  int port,
                                  String proxyHost,
                                  int proxyPort )
        throws ServletContainerException, UnknownHostException
    {
        InetAddrPort addrPort = new InetAddrPort( host, port );

        JettyProxyHttpListener listener = new JettyProxyHttpListener( addrPort );

        listener.setForcedHost( proxyHost + ":" + proxyPort );

        server.addListener( listener );


        try
        {
            listener.start();
        }
        catch ( Exception e )
        {
            throw new ServletContainerException(
                "Error while starting listener on address: '" + host + "', port: " + port + ".", e );
        }
    }

    public void deployWarDirectory( File directory,
                                    DefaultPlexusContainer container,
                                    Webapp webapp )
        throws ServletContainerException
    {
        deployWAR( directory, false, null, container, webapp );
    }

    public void startApplication( String contextPath )
        throws ServletContainerException
    {
        try
        {
            getContext( contextPath ).start();
        }
        catch ( Exception e )
        {
            throw new ServletContainerException( "Error while starting the web appserver.", e );
        }
    }

    public void deployContext( WebContext webContext )
        throws ServletContainerException
    {
        HttpContext context = new HttpContext();

        context.setContextPath( webContext.getContext() );

        context.setResourceBase( webContext.getPath() );

        // This will setup a standard resource handler for document retrieval. If you want more
        // functionality like POST, or WebDAV type stuff that will need to be configurable.
        context.addHandler( new ResourceHandler() );

        server.addContext( context );
    }

    public void deployServletContext( ServletContext servletContext )
        throws ServletContainerException
    {
        ServletHttpContext context = new ServletHttpContext();

        context.setContextPath( servletContext.getContext() );

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
                        if ( directive.equals( "create-directory" ) )
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

        server.addContext( context );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private HttpContext getContext( String contextPath )
        throws ServletContainerException
    {
        HttpContext[] contexts = server.getContexts();

        HttpContext context;

        for ( int i = 0; i < contexts.length; i++ )
        {
            context = contexts[i];

            if ( context.getContextPath().equals( contextPath ) )
            {
                return context;
            }
        }

        throw new ServletContainerException( "No such context '" + contextPath + "'." );
    }

    private void deployWAR( File war,
                            boolean extractWar,
                            File extractionLocation,
                            DefaultPlexusContainer container,
                            Webapp webapp )
        throws ServletContainerException
    {
        String context = webapp.getContext();

        String virtualHost = webapp.getVirtualHost();

        boolean standardWebappClassloader = webapp.isStandardWebappClassloader();

        if ( war == null )
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

        try
        {
            if ( virtualHost != null )
            {
                webappContext = server.addWebApplication( virtualHost, context, war.getAbsolutePath() );
            }
            else
            {
                webappContext = server.addWebApplication( context, war.getAbsolutePath() );
            }
        }
        catch ( IOException e )
        {
            throw new ServletContainerException( "Error while deploying WAR.", e );
        }

        // ----------------------------------------------------------------------
        // Configure the appserver context
        // ----------------------------------------------------------------------

        webappContext.setExtractWAR( extractWar );

        if ( extractionLocation != null )
        {
            webappContext.setTempDirectory( extractionLocation );
        }

        // If it is a standard WAR file then use the standard classloading semantics. We don't want
        // to use the plexus container classloader for deploying third-party WARs.

        // webapp
        // app
        // core

        // align the webapp classload and what plexus uses

        if ( standardWebappClassloader )
        {
            getLogger().info( "Using standard webapp classloader for webapp." );

            try
            {
                // We need to start the context to trigger the unpacking so that we can
                // create a realm. We need to create a realm so that we can discover all
                // the components in the webapp.

                ClassRealm realm = container.getCoreRealm();

                List jars = FileUtils.getFiles( war, "**/*.jar", null );

                // The webapp directory needs to be unpacked before we can pick up the files

                for ( Iterator i = jars.iterator(); i.hasNext(); )
                {
                    File file = (File) i.next();

                    realm.addConstituent( file.toURL() );
                }

                File webInf = new File( war, "WEB-INF" );

                realm.addConstituent( webInf.toURL() );

                File classes = new File( war, "WEB-INF/classes" );

                realm.addConstituent( classes.toURL() );

                webappContext.setClassLoader( realm.getClassLoader() );
            }
            catch ( Exception e )
            {
                throw new ServletContainerException( "Error creating webapp classloader.", e );
            }
        }
        else
        {
            // Dirty hack, need better methods for classloaders because i can set the core realm but not get it,
            // or get the container realm but not set it. blah!
            webappContext.setClassLoader( container.getCoreRealm().getClassLoader() );
        }

        webappContext.getServletContext().setAttribute( PlexusConstants.PLEXUS_KEY, container );
    }
}
