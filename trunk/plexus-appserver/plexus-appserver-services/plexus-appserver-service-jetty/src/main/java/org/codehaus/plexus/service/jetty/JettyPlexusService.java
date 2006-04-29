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
import org.codehaus.plexus.service.jetty.configuration.HttpListener;
import org.codehaus.plexus.service.jetty.configuration.ProxyHttpListener;
import org.codehaus.plexus.service.jetty.configuration.ServiceConfiguration;
import org.codehaus.plexus.service.jetty.configuration.WebApplication;
import org.codehaus.plexus.service.jetty.configuration.builder.ServiceConfigurationBuilder;

import java.io.File;
import java.util.Iterator;

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

    // ----------------------------------------------------------------------
    // PlexusService Implementation
    // ----------------------------------------------------------------------

    public void beforeApplicationStart( AppRuntimeProfile runtimeProfile,
                                        PlexusConfiguration serviceConfiguration )
        throws Exception
    {
        ServiceConfiguration configuration = configurationBuilder.buildConfiguration( serviceConfiguration );

        for ( Iterator it = configuration.getWebapps().iterator(); it.hasNext(); )
        {
            WebApplication application = (WebApplication) it.next();

            File webAppDir;

            if ( application.getPath() == null )
            {
                // ----------------------------------------------------------------------
                // Extract the jar
                // ----------------------------------------------------------------------

                expand( new File( application.getFile() ), new File( application.getExtractionPath() ), false );

                webAppDir = new File( application.getExtractionPath() );
            }
            else
            {
                webAppDir = new File( application.getPath() );
            }

            if ( !webAppDir.isDirectory() )
            {
                throw new Exception( "The webapp isn't a directory: '" + webAppDir.getAbsolutePath() + "'." );
            }

            try
            {
                servletContainer.deployWarDirectory( webAppDir, runtimeProfile.getApplicationContainer(),
                                                     application.getContext(), application.getVirtualHost(),
                                                     application.isStandardWebappClassloader() );
            }
            catch ( ServletContainerException e )
            {
                getLogger().error( "Error while deploying WAR '" + webAppDir.getAbsolutePath() + "'.", e );
            }
        }
    }

    public void afterApplicationStart( AppRuntimeProfile appRuntimeProfile,
                                       PlexusConfiguration serviceConfiguration )
        throws Exception
    {
        ServiceConfiguration configuration = configurationBuilder.buildConfiguration( serviceConfiguration );

        for ( Iterator it = configuration.getWebapps().iterator(); it.hasNext(); )
        {
            WebApplication application = (WebApplication) it.next();

            if ( !servletContainer.hasContext( application.getContext() ) )
            {
                continue;
            }

            if ( application.getVirtualHost() == null )
            {
                getLogger().info( "Deploying appserver '" + appRuntimeProfile.getName() + "'." );
            }
            else
            {
                getLogger().info( "Deploying appserver '" + appRuntimeProfile.getName() + "' " + "on virtual host '" +
                    application.getVirtualHost() + "'." );
            }

            for ( Iterator j = application.getListeners().iterator(); j.hasNext(); )
            {
                HttpListener httpListener = (HttpListener) j.next();

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

                    servletContainer.addProxyListener( proxyHttpListener.getHost(), proxyHttpListener.getPort(),
                                                       proxyHttpListener.getProxyHost(),
                                                       proxyHttpListener.getProxyPort() );
                }
                else
                {
                    getLogger().info( "Adding HTTP listener on " + listener );

                    servletContainer.addListener( httpListener.getHost(), httpListener.getPort() );
                }
            }

            // ----------------------------------------------------------------------------
            // Now we need to find all the components that might be included in the webapp.
            // We have to do this here because now the container is initialized which
            // means discovery can occur.
            // ----------------------------------------------------------------------------

            DefaultPlexusContainer c = appRuntimeProfile.getApplicationContainer();

            ClassRealm realm = c.getContainerRealm();

            c.discoverComponents( realm );

            servletContainer.startApplication( application.getContext() );
        }
    }
}
