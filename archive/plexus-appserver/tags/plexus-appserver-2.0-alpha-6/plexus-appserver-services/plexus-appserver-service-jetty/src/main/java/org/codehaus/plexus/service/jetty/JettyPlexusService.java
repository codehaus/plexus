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

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.appserver.application.profile.AppRuntimeProfile;
import org.codehaus.plexus.appserver.deploy.DeploymentException;
import org.codehaus.plexus.appserver.service.AbstractPlexusService;
import org.codehaus.plexus.appserver.service.PlexusServiceException;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
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
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
     *
     * @deprecated
     */
    private Set activePorts = new HashSet();

    // ----------------------------------------------------------------------
    // PlexusService Implementation
    // ----------------------------------------------------------------------

    public void beforeApplicationStart( AppRuntimeProfile appRuntimeProfile, PlexusConfiguration serviceConfiguration )
        throws PlexusServiceException
    {
        // We probably need to stop running contexts here ...

        ServiceConfiguration configuration;
        try
        {
            configuration = configurationBuilder.buildConfiguration( serviceConfiguration,
                                                                     appRuntimeProfile.getApplicationServerContainer().getContainerRealm() );
        }
        catch ( ComponentConfigurationException e )
        {
            throw new PlexusServiceException( e.getMessage(), e );
        }

        appRuntimeProfile.addServiceConfiguration( this, configuration );

        for ( Iterator it = configuration.getWebapps().iterator(); it.hasNext(); )
        {
            Webapp webapp = (Webapp) it.next();

            // Stop the webapp context if it is running.

            if ( servletContainer.hasContext( webapp.getContext() ) )
            {
                try
                {
                    servletContainer.stopApplication( webapp.getContext() );
                }
                catch ( ServletContainerException e )
                {
                    throw new PlexusServiceException( e.getMessage(), e );
                }
            }

            File webAppDir;

            if ( webapp.getPath() == null )
            {
                // ----------------------------------------------------------------------
                // Extract the jar
                // ----------------------------------------------------------------------

                try
                {
                    expand( getFile( webapp.getFile() ), getFile( webapp.getExtractionPath() ), false );
                }
                catch ( DeploymentException e )
                {
                    throw new PlexusServiceException( e.getMessage(), e );
                }

                webAppDir = getFile( webapp.getExtractionPath() );
            }
            else
            {
                webAppDir = getFile( webapp.getPath() );
            }

            if ( !webAppDir.isDirectory() )
            {
                throw new PlexusServiceException(
                    "The webapp isn't a directory: '" + webAppDir.getAbsolutePath() + "'." );
            }

            try
            {
                getLogger().info( "Deploying " + webAppDir + " with context path of " + webapp.getContext() );

                servletContainer.deployWarDirectory( webAppDir, appRuntimeProfile.getApplicationContainer(), webapp );
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

            try
            {
                servletContainer.deployContext( webContext );
            }
            catch ( ServletContainerException e )
            {
                throw new PlexusServiceException( e.getMessage(), e );
            }
        }

        // ----------------------------------------------------------------------------
        // Servlet contexts
        // ----------------------------------------------------------------------------

        for ( Iterator i = configuration.getServletContexts().iterator(); i.hasNext(); )
        {
            ServletContext servletContext = (ServletContext) i.next();

            getLogger().info( "Deploying servlet " + servletContext.getName() + " with context path of " +
                servletContext.getContext() );

            try
            {
                servletContainer.deployServletContext( servletContext );
            }
            catch ( ServletContainerException e )
            {
                throw new PlexusServiceException( e.getMessage(), e );
            }
        }
    }

    public void afterApplicationStart( AppRuntimeProfile appRuntimeProfile, PlexusConfiguration serviceConfiguration )
        throws PlexusServiceException
    {
        ServiceConfiguration configuration;
        try
        {
            configuration = configurationBuilder.buildConfiguration( serviceConfiguration,
                                                                     appRuntimeProfile.getApplicationServerContainer().getContainerRealm() );
        }
        catch ( ComponentConfigurationException e )
        {
            throw new PlexusServiceException( e.getMessage(), e );
        }

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

            try
            {
                c.discoverComponents( realm );
            }
            catch ( PlexusConfigurationException e )
            {
                throw new PlexusServiceException( e.getMessage(), e );
            }
            catch ( ComponentRepositoryException e )
            {
                throw new PlexusServiceException( e.getMessage(), e );
            }
            //}

            try
            {
                servletContainer.startApplication( application.getContext() );
            }
            catch ( ServletContainerException e )
            {
                throw new PlexusServiceException( e.getMessage(), e );
            }
        }

        for ( Iterator i = configuration.getWebContexts().iterator(); i.hasNext(); )
        {
            WebContext webContext = (WebContext) i.next();

            processWebContextConfiguration( webContext, appRuntimeProfile );

            try
            {
                servletContainer.startApplication( webContext.getContext() );
            }
            catch ( ServletContainerException e )
            {
                throw new PlexusServiceException( e.getMessage(), e );
            }
        }

        for ( Iterator i = configuration.getServletContexts().iterator(); i.hasNext(); )
        {
            ServletContext servletContext = (ServletContext) i.next();

            processWebContextConfiguration( servletContext, appRuntimeProfile );

            try
            {
                servletContainer.startApplication( servletContext.getContext() );
            }
            catch ( ServletContainerException e )
            {
                throw new PlexusServiceException( e.getMessage(), e );
            }
        }
    }

    // ----------------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------------

    private void processWebContextConfiguration( WebContext context, AppRuntimeProfile profile )
        throws PlexusServiceException
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

            String listener = httpListener.getHost() != null ? httpListener.getHost() : "*";

            listener += port;

            if ( httpListener instanceof ProxyHttpListener )
            {
                ProxyHttpListener proxyHttpListener = (ProxyHttpListener) httpListener;

                String proxyListener = proxyHttpListener.getHost() != null ? proxyHttpListener.getHost() : "*";

                proxyListener += port;

                getLogger().info( "Adding HTTP proxy listener on " + listener + " for " + proxyListener );

                try
                {
                    servletContainer.addProxyListener( proxyHttpListener );
                }
                catch ( ServletContainerException e )
                {
                    throw new PlexusServiceException( e.getMessage(), e );
                }
                catch ( UnknownHostException e )
                {
                    throw new PlexusServiceException( e.getMessage(), e );
                }
            }
            else
            {
                getLogger().info( "Adding HTTP listener on " + listener );

                try
                {
                    servletContainer.addListener( httpListener );
                }
                catch ( ServletContainerException e )
                {
                    throw new PlexusServiceException( e.getMessage(), e );
                }
                catch ( UnknownHostException e )
                {
                    throw new PlexusServiceException( e.getMessage(), e );
                }
            }
        }
    }

    public void applicationStop( AppRuntimeProfile runtimeProfile )
        throws PlexusServiceException
    {
        ServiceConfiguration configuration = (ServiceConfiguration) runtimeProfile.getServiceConfiguration( this );

        removeContexts( configuration.getWebContexts() );

        removeContexts( configuration.getServletContexts() );

        for ( Iterator iterator = configuration.getWebapps().iterator(); iterator.hasNext(); )
        {
            Webapp webapp = (Webapp) iterator.next();

            if ( servletContainer.hasContext( webapp.getContext() ) )
            {
                removeListeners( webapp.getListeners() );
                try
                {
                    servletContainer.stopApplication( webapp.getContext() );
                }
                catch ( ServletContainerException e )
                {
                    throw new PlexusServiceException( e.getMessage(), e );
                }
            }
        }
    }


    private void removeContexts( List contexts )
        throws PlexusServiceException
    {
        for ( Iterator iterator = contexts.iterator(); iterator.hasNext(); )
        {
            WebContext context = (WebContext) iterator.next();
            removeListeners( context.getListeners() );
        }
    }

    private void removeListeners( List listeners )
        throws PlexusServiceException
    {
        for ( Iterator listenerIterator = listeners.iterator(); listenerIterator.hasNext(); )
        {
            HttpListener listener = (HttpListener) listenerIterator.next();

            try
            {
                servletContainer.removeListener( listener );
            }
            catch ( ServletContainerException e )
            {
                throw new PlexusServiceException( e.getMessage(), e );
            }
        }
    }

    private File getFile( String path )
    {
        return FileUtils.resolveFile( new File( "." ), path );
    }
}
