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
import org.codehaus.plexus.appserver.application.profile.AppRuntimeProfile;
import org.codehaus.plexus.appserver.service.AbstractPlexusService;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.jetty.ServletContainer;
import org.codehaus.plexus.jetty.ServletContainerException;
import org.codehaus.plexus.jetty.configuration.HttpListener;
import org.codehaus.plexus.jetty.configuration.ProxyHttpListener;
import org.codehaus.plexus.jetty.configuration.ServiceConfiguration;
import org.codehaus.plexus.jetty.configuration.ServletContext;
import org.codehaus.plexus.jetty.configuration.WebContext;
import org.codehaus.plexus.jetty.configuration.Webapp;
import org.codehaus.plexus.jetty.configuration.builder.ServiceConfigurationBuilder;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 * @todo how to make the servlet container here be the standard plexus-jetty, we shouldn't need to copy
 * the component here to make it work as a service.
 */
public class JettyPlexusService
    extends AbstractPlexusService
{
    /**
     * @plexus.requirement
     */
    private ServiceConfigurationBuilder configurationBuilder;

    /**
     * @plexus.requirement
     */
    private ServletContainer servletContainer;

    /**
     * Set of ports to be activated. The port can only be used once.
     */
    private Set activePorts = new HashSet();

    // ----------------------------------------------------------------------
    // PlexusService Implementation
    // ----------------------------------------------------------------------

    public void beforeApplicationStart( AppRuntimeProfile runtimeProfile, PlexusConfiguration serviceConfiguration )
        throws Exception
    {
        // We probably need to stop running contexts here ...

        ServiceConfiguration configuration = configurationBuilder.buildConfiguration( serviceConfiguration,
                                                                                      runtimeProfile.getApplicationServerContainer().getContainerRealm() );
        for ( Iterator it = configuration.getWebapps().iterator(); it.hasNext(); )
        {
            Webapp webapp = (Webapp) it.next();

            // Stop the webapp context if it is running.

            if ( servletContainer.hasContext( webapp.getContext() ) )
            {
                servletContainer.stopApplication( webapp.getContext() );
            }

            File webAppDir;

            if ( webapp.getPath() == null )
            {
                // ----------------------------------------------------------------------
                // Extract the jar
                // ----------------------------------------------------------------------

                expand( getFile( webapp.getFile() ), getFile( webapp.getExtractionPath() ), false );

                webAppDir = getFile( webapp.getExtractionPath() );
            }
            else
            {
                webAppDir = getFile( webapp.getPath() );
            }

            if ( !webAppDir.isDirectory() )
            {
                throw new Exception( "The webapp isn't a directory: '" + webAppDir.getAbsolutePath() + "'." );
            }

            try
            {
                getLogger().info( "Deploying " + webAppDir + " with context path of " + webapp.getContext() );

                servletContainer.deployWarDirectory( webAppDir, runtimeProfile.getApplicationContainer(), webapp );
            }
            catch ( ServletContainerException e )
            {
                getLogger().error( "Error while deploying WAR '" + webAppDir.getAbsolutePath() + "'.", e );
            }
        }

        // ----------------------------------------------------------------------------
        // Web contexts
        // ----------------------------------------------------------------------------

        for ( Iterator i = configuration.getWebContexts().iterator(); i.hasNext(); )
        {
            WebContext webContext = (WebContext) i.next();

            getLogger().info(
                "Deploying " + webContext.getPath() + " with context path of " + webContext.getContext() );

            servletContainer.deployContext( webContext );
        }

        // ----------------------------------------------------------------------------
        // Servlet contexts
        // ----------------------------------------------------------------------------

        for ( Iterator i = configuration.getServletContexts().iterator(); i.hasNext(); )
        {
            ServletContext servletContext = (ServletContext) i.next();

            getLogger().info( "Deploying servlet " + servletContext.getName() + " with context path of " +
                servletContext.getContext() );

            servletContainer.deployServletContext( servletContext );
        }
    }

    public void afterApplicationStart( AppRuntimeProfile appRuntimeProfile, PlexusConfiguration serviceConfiguration )
        throws Exception
    {
        ServiceConfiguration configuration = configurationBuilder.buildConfiguration( serviceConfiguration,
                                                                                      appRuntimeProfile.getApplicationServerContainer().getContainerRealm() );

        for ( Iterator it = configuration.getWebapps().iterator(); it.hasNext(); )
        {
            Webapp application = (Webapp) it.next();

            processWebContextConfiguration( application, appRuntimeProfile );

            //if ( !servletContainer.hasContext( application.getContext() ) )
            //{
            // ----------------------------------------------------------------------------
            // Now we need to find all the components that might be included in the webapp.
            // We have to do this here because now the container is initialized which
            // means discovery can occur.
            // ----------------------------------------------------------------------------

            DefaultPlexusContainer c = appRuntimeProfile.getApplicationContainer();

            ClassRealm realm = c.getContainerRealm();

            c.discoverComponents( realm );
            //}

            servletContainer.startApplication( application.getContext() );
        }

        for ( Iterator i = configuration.getWebContexts().iterator(); i.hasNext(); )
        {
            WebContext webContext = (WebContext) i.next();

            processWebContextConfiguration( webContext, appRuntimeProfile );

            servletContainer.startApplication( webContext.getContext() );
        }

        for ( Iterator i = configuration.getServletContexts().iterator(); i.hasNext(); )
        {
            ServletContext servletContext = (ServletContext) i.next();

            processWebContextConfiguration( servletContext, appRuntimeProfile );

            servletContainer.startApplication( servletContext.getContext() );
        }
    }

    // ----------------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------------

    private void processWebContextConfiguration( WebContext context, AppRuntimeProfile profile )
        throws Exception
    {
        if ( context.getVirtualHost() == null )
        {
            getLogger().info( "Deploying appserver '" + profile.getName() + "'." );
        }
        else
        {
            getLogger().info( "Deploying appserver '" + profile.getName() + "' " + "on virtual host '" +
                context.getVirtualHost() + "'." );
        }

        for ( Iterator j = context.getListeners().iterator(); j.hasNext(); )
        {
            HttpListener httpListener = (HttpListener) j.next();

            String port = Integer.toString( httpListener.getPort() );

            if ( activePorts.contains( port ) )
            {
                continue;
            }

            activePorts.add( port );

            String listener;

            if ( httpListener.getHost() != null )
            {
                listener = httpListener.getHost() + ":" + httpListener.getPort();
            }
            else
            {
                listener = "*:" + httpListener.getPort();
            }

            if ( httpListener instanceof ProxyHttpListener )
            {
                ProxyHttpListener proxyHttpListener = (ProxyHttpListener) httpListener;

                String proxyListener;

                if ( proxyHttpListener.getHost() != null )
                {
                    proxyListener = proxyHttpListener.getHost() + ":" + proxyHttpListener.getPort();
                }
                else
                {
                    proxyListener = "*:" + proxyHttpListener.getPort();
                }

                getLogger().info( "Adding HTTP proxy listener on " + listener + " for " + proxyListener );

                servletContainer.addProxyListener( proxyHttpListener );
            }
            else
            {
                getLogger().info( "Adding HTTP listener on " + listener );

                servletContainer.addListener( httpListener );
            }
        }
    }

    private File getFile( String path )
    {
        return FileUtils.resolveFile( new File( "." ), path );
    }
}
