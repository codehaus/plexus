package org.codehaus.plexus.webdav.test;

/*
 * Copyright 2001-2007 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.context.DefaultContext;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.webdav.BasicWebDavServlet;
import org.codehaus.plexus.webdav.DavServerManager;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * AbstractWebdavServer - Baseline server for starting up a BasicWebDavServlet to allow experimentation with.  
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractWebdavServer
{
    public static final int PORT = 14541;

    protected PlexusContainer container;

    protected String basedir;

    protected Map context;

    /** the jetty server */
    protected Server server;

    private DavServerManager manager;

    public void init()
    {
        context = new HashMap();
    }

    public String getBasedir()
    {
        if ( basedir != null )
        {
            return basedir;
        }

        basedir = System.getProperty( "basedir" );
        if ( basedir == null )
        {
            basedir = new File( "" ).getAbsolutePath();
        }

        return basedir;
    }

    public File getTestFile( String path )
    {
        return new File( getBasedir(), path );
    }

    protected abstract String getProviderHint();

    public void startServer()
        throws Exception
    {
        basedir = getBasedir();

        // ----------------------------------------------------------------------------
        // Context Setup
        // ----------------------------------------------------------------------------

        context = new HashMap();

        context.put( "basedir", getBasedir() );

        customizeContext( new DefaultContext( context ) );

        boolean hasPlexusHome = context.containsKey( "plexus.home" );

        if ( !hasPlexusHome )
        {
            File f = getTestFile( "target/plexus-home" );

            if ( !f.isDirectory() )
            {
                f.mkdir();
            }

            context.put( "plexus.home", f.getAbsolutePath() );
        }

        // ----------------------------------------------------------------------------
        // Configuration
        // ----------------------------------------------------------------------------

        String config = getCustomConfigurationName();
        InputStream is;

        if ( config != null )
        {
            is = getClass().getClassLoader().getResourceAsStream( config );

            if ( is == null )
            {
                try
                {
                    File configFile = new File( config );

                    if ( configFile.exists() )
                    {
                        is = new FileInputStream( configFile );
                    }
                }
                catch ( IOException e )
                {
                    throw new Exception( "The custom configuration specified is null: " + config );
                }
            }

        }
        else
        {
            config = getConfigurationName( null );

            is = getClass().getClassLoader().getResourceAsStream( config );
        }

        // Look for a configuration associated with this test but return null if we
        // can't find one so the container doesn't look for a configuration that we
        // know doesn't exist. Not all tests have an associated Foo.xml for testing.

        if ( is == null )
        {
            config = null;
        }
        else
        {
            is.close();
        }

        // ----------------------------------------------------------------------------
        // Create the container
        // ----------------------------------------------------------------------------

        container = createContainerInstance( context, config );
        
        // ----------------------------------------------------------------------------
        // Create the DavServerManager
        // ----------------------------------------------------------------------------

        manager = (DavServerManager) container.lookup( DavServerManager.ROLE, getProviderHint() );

        // ----------------------------------------------------------------------------
        // Create the jetty server
        // ----------------------------------------------------------------------------

        System.setProperty( "DEBUG", "" );
        System.setProperty( "org.mortbay.log.class", "org.slf4j.impl.SimpleLogger" );

        server = new Server( PORT );
        Context root = new Context( server, "/", Context.SESSIONS );
        ServletHandler servletHandler = root.getServletHandler();
        root.setContextPath( "/" );
        root.setAttribute( PlexusConstants.PLEXUS_KEY, container );
        
        // ----------------------------------------------------------------------------
        // Configure the webdav servlet
        // ----------------------------------------------------------------------------

        ServletHolder holder = servletHandler.addServletWithMapping( BasicWebDavServlet.class, "/projects/*" );

        // Initialize server contents directory.
        File serverContentsDir = new File( "target/test-server/" );

        FileUtils.deleteDirectory( serverContentsDir );
        if ( serverContentsDir.exists() )
        {
            throw new IllegalStateException( "Unable to execute test, server contents test directory ["
                + serverContentsDir.getAbsolutePath() + "] exists, and cannot be deleted by the test case." );
        }

        if ( !serverContentsDir.mkdirs() )
        {
            throw new IllegalStateException( "Unable to execute test, server contents test directory ["
                + serverContentsDir.getAbsolutePath() + "] cannot be created." );
        }

        holder.setInitParameter( "dav.root", serverContentsDir.getAbsolutePath() );

        // ----------------------------------------------------------------------------
        // Start the jetty server
        // ----------------------------------------------------------------------------

        server.start();
    }

    protected PlexusContainer createContainerInstance( Map context, String configuration )
        throws PlexusContainerException
    {
        return new DefaultPlexusContainer( "test", context, configuration );
    }

    protected void customizeContext( DefaultContext ctx )
    {
        /* override to specify more */
    }

    protected String getCustomConfigurationName()
    {
        /* override to specify */
        return null;
    }

    protected String getConfigurationName( String subname )
        throws Exception
    {
        return getClass().getName().replace( '.', '/' ) + ".xml";
    }

    public void stopServer()
    {
        if ( server != null )
        {
            try
            {
                server.stop();
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }

        if ( container != null )
        {
            container.dispose();
        }
    }
}
