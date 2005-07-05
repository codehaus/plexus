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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.codehaus.plexus.application.deploy.ApplicationDeployer;
import org.codehaus.plexus.application.profile.ApplicationRuntimeProfile;
import org.codehaus.plexus.application.service.PlexusService;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.service.jetty.configuration.ServiceConfiguration;
import org.codehaus.plexus.service.jetty.configuration.builder.ServiceConfigurationBuilder;
import org.codehaus.plexus.service.jetty.configuration.WebApplication;
import org.codehaus.plexus.service.jetty.configuration.HttpListener;
import org.codehaus.plexus.service.jetty.configuration.ProxyHttpListener;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class JettyPlexusService
    extends AbstractLogEnabled
    implements PlexusService, Initializable, Startable
{
    /** @plexus.requirement */
    private ServiceConfigurationBuilder configurationBuilder;

    /** @plexus.requirement */
    private ApplicationDeployer deployer;

    /** @plexus.requirement */
    private ServletContainer servletContainer;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
    {
        getLogger().info( "Initializing servlet container service." );
    }

    public void start()
    {
        getLogger().info( "Starting servlet container service." );
    }

    public void stop()
    {
        getLogger().info( "Stopping servlet container service." );
    }

    // ----------------------------------------------------------------------
    // PlexusService Implementation
    // ----------------------------------------------------------------------

    public void beforeApplicationStart( ApplicationRuntimeProfile applicationRuntimeProfile,
                                        PlexusConfiguration serviceConfiguration )
        throws Exception
    {
        ServiceConfiguration configuration = configurationBuilder.buildConfiguration( serviceConfiguration );

        for ( Iterator it = configuration.getWebapps().iterator(); it.hasNext(); )
        {
            WebApplication application = (WebApplication) it.next();

            File webAppDir;

            if ( application.getPath() == null)
            {
                // ----------------------------------------------------------------------
                // Extract the jar
                // ----------------------------------------------------------------------

                extractJarFile( new File( application.getFile() ), application.getExtractionPath() );

                webAppDir = new File( application.getExtractionPath() );
            }
            else
            {
                webAppDir = new File( application.getPath() );
            }

            deployDirectory( webAppDir,
                             application.getContext(),
                             application.getVirtualHost(),
                             applicationRuntimeProfile );
        }
    }

    public void afterApplicationStart( ApplicationRuntimeProfile applicationRuntimeProfile,
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
                getLogger().info( "Deploying application '" + applicationRuntimeProfile.getName() + "'." );
            }
            else
            {
                getLogger().info( "Deploying application '" + applicationRuntimeProfile.getName() + "' " +
                                  "on virtual host '" + application.getVirtualHost() + "'." );
            }

            System.err.println( application.getListeners() + " listeners" );

            for ( Iterator it2 = application.getListeners().iterator(); it2.hasNext(); )
            {
                HttpListener httpListener = (HttpListener) it2.next();

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

                    servletContainer.addProxyListener( proxyHttpListener.getHost(),
                                                       proxyHttpListener.getPort(),
                                                       proxyHttpListener.getProxyHost(),
                                                       proxyHttpListener.getProxyPort() );
                }
                else
                {
                    getLogger().info( "Adding HTTP listener on " + listener );

                    servletContainer.addListener( httpListener.getHost(),
                                                  httpListener.getPort() );
                }
            }

            servletContainer.startApplication( application.getContext() );
        }
    }

    // ----------------------------------------------------------------------
    // Deployment
    // ----------------------------------------------------------------------

    private void deployDirectory( File directory,
                                  String context,
                                  String virtualHost,
                                  ApplicationRuntimeProfile runtimeProfile )
        throws Exception
    {
        if ( !directory.isDirectory() )
        {
            throw new Exception( "The webapp isn't a directory: '" + directory.getAbsolutePath() + "'." );
        }

        try
        {
            servletContainer.deployWarDirectory( directory,
                                                 runtimeProfile.getContainer(), context,
                                                 virtualHost );
        }
        catch ( ServletContainerException e )
        {
            getLogger().error( "Error while deploying WAR '" + directory.getAbsolutePath() + "'.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void extractJarFile( File file, String extractionPath )
        throws Exception
    {
        if ( !file.exists() )
        {
            throw new FileNotFoundException( file.getAbsolutePath() );
        }

        JarFile jarFile = new JarFile( file );

        try
        {
            Enumeration e = jarFile.entries();

            while ( e.hasMoreElements() )
            {
                JarEntry entry = (JarEntry) e.nextElement();

                InputStream is = jarFile.getInputStream( entry );

                File outputFile = new File( extractionPath, entry.getName() );

                if ( entry.getName().endsWith( "/" ) )
                {
                    if ( !outputFile.exists() && !outputFile.mkdirs() )
                    {
                        throw new Exception( "Error while deploying web application: " +
                                             "Could not make directory: '" + outputFile.getAbsolutePath() + "'." );
                    }
                }
                else
                {
                    File parent = outputFile.getParentFile();

                    if ( !parent.exists() && !parent.mkdirs() )
                    {
                        throw new Exception( "Error while deploying web application: " +
                                             "Could not make directory: '" + parent.getAbsolutePath() + "'." );
                    }

                    OutputStream os = new FileOutputStream( outputFile );

                    IOUtil.copy( is, os );
                }
            }
        }
        finally
        {
            jarFile.close();
        }
    }
}
