package org.codehaus.plexus.service.jetty;

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
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.application.profile.ApplicationRuntimeProfile;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.util.FileUtils;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.InetAddrPort;
import org.mortbay.util.MultiException;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

/**
 * 1
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
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

        HttpContext context = null;

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

    public void deployWarFile( File war,
                               boolean extractWar,
                               File extractionLocation,
                               ApplicationRuntimeProfile applicationProfile,
                               String context,
                               String virtualHost,
                               boolean standardWebappClassloading )
        throws ServletContainerException
    {
        deployWAR( war, extractWar, extractionLocation, applicationProfile, context, virtualHost, false );
    }

    public void deployWarDirectory( File directory,
                                    ApplicationRuntimeProfile applicationProfile,
                                    String context,
                                    String virtualHost,
                                    boolean standardWebappClassLoader )
        throws ServletContainerException
    {
        deployWAR( directory, false, null, applicationProfile, context, virtualHost, standardWebappClassLoader );
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
            throw new ServletContainerException( "Error while starting the web application.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private HttpContext getContext( String contextPath )
        throws ServletContainerException
    {
        HttpContext[] contexts = server.getContexts();

        HttpContext context = null;

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
                            ApplicationRuntimeProfile applicationProfile,
                            String context,
                            String virtualHost,
                            boolean standardWebappClassloader )
        throws ServletContainerException
    {
        if ( war == null )
        {
            throw new ServletContainerException( "Invalid parameter: 'war' cannot be null." );
        }

        if ( context == null )
        {
            throw new ServletContainerException( "Invalid parameter: 'context' cannot be null." );
        }

        // ----------------------------------------------------------------------
        // Create the web application
        // ----------------------------------------------------------------------

        WebApplicationContext applicationContext;

        try
        {
            if ( virtualHost != null )
            {
                applicationContext = server.addWebApplication( virtualHost, context, war.getAbsolutePath() );
            }
            else
            {
                applicationContext = server.addWebApplication( context, war.getAbsolutePath() );
            }
        }
        catch ( IOException e )
        {
            throw new ServletContainerException( "Error while deploying WAR.", e );
        }

        // ----------------------------------------------------------------------
        // Configure the application context
        // ----------------------------------------------------------------------

        applicationContext.setExtractWAR( extractWar );

        if ( extractionLocation != null )
        {
            applicationContext.setTempDirectory( extractionLocation );
        }

        PlexusContainer applicationContainer = applicationProfile.getApplicationContainer();

        DefaultPlexusContainer appserverContainer = (DefaultPlexusContainer) applicationProfile.getApplicationServerContainer();

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

                ClassRealm realm = applicationContainer.getContainerRealm();

                List jars = FileUtils.getFiles( war, "**/*.jar", null );

                for ( Iterator i = jars.iterator(); i.hasNext(); )
                {
                    File file = (File) i.next();

                    realm.addConstituent( file.toURL() );
                }

                File webInf = new File( war, "WEB-INF" );

                realm.addConstituent( webInf.toURL() );

                File classes = new File( war, "WEB-INF/classes" );

                realm.addConstituent( classes.toURL() );

                // ClassWorlds will not combine all the URLs from all the realms. The problem here
                // is that jasper will create a compile classpath from the contents of the URLs
                // that are in the classloader. The paths will only be taken from the container
                // realm and not any parent realms.

                applicationContext.setClassLoader( realm.getClassLoader() );

                // ----------------------------------------------------------------------------
                // Now we need to find all the components that might be included in the webapp.
                // ----------------------------------------------------------------------------

                ((DefaultPlexusContainer)applicationContainer).discoverComponents( applicationContainer.getContainerRealm() );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
        else
        {
            // Dirty hack, need better methods for classloaders because i can set the core realm but not get it,
            // or get the container realm but not set it. blah!
            applicationContext.setClassLoader( ((DefaultPlexusContainer)applicationContainer).getCoreRealm().getClassLoader() );
        }

        applicationContext.getServletContext().setAttribute( PlexusConstants.PLEXUS_KEY, applicationContainer );
    }
}
