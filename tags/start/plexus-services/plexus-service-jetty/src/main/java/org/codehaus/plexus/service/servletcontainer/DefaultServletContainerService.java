package org.codehaus.plexus.service.servletcontainer;

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
import java.io.FileFilter;

import org.codehaus.plexus.application.deploy.ApplicationDeployer;
import org.codehaus.plexus.application.event.ApplicationListener;
import org.codehaus.plexus.application.event.DeployEvent;
import org.codehaus.plexus.application.profile.ApplicationRuntimeProfile;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.servletcontainer.ServletContainer;
import org.codehaus.plexus.servletcontainer.ServletContainerException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultServletContainerService
    extends AbstractLogEnabled
    implements Initializable, Startable
{
    /**
     * @requirement
     */
    private ApplicationDeployer deployer;

    /**
     * @requirement
     */
    private ServletContainer servletContainer;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private static class WarFileFilter
        implements FileFilter
    {
        public boolean accept( File file )
        {
            return file.isFile() && file.getName().endsWith( ".war" );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private class ServletContainerServiceApplicationListener
        implements ApplicationListener
    {
        public void deployedApplication( DeployEvent event )
        {
            applicationDeployed( event.getRuntimeProfile() );
        }

        public void redeployedApplication( DeployEvent event )
        {
        }

        public void undeployedApplication( DeployEvent event )
        {
        }
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        getLogger().info( "Initializing servlet container service." );

        deployer.addApplicationListener( new ServletContainerServiceApplicationListener() );
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Starting servlet container service." );

        servletContainer.addListener( null, 8080 );
    }

    public void stop()
        throws Exception
    {
        getLogger().info( "Stopping servlet container service." );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void deployWebApp( File war, ApplicationRuntimeProfile profile )
    {
        String context = "/foo";

        ClassLoader classLoader = profile.getContainer().getCoreRealm().getClassLoader();

        String virtualHost = null;

        try
        {
            servletContainer.deployWAR( war, context, classLoader, virtualHost );
        }
        catch ( ServletContainerException e )
        {
            getLogger().error( "Error while deploying WAR '" + war.getAbsolutePath() + "'.", e );
        }
    }

    // ----------------------------------------------------------------------
    // Event handlers
    // ----------------------------------------------------------------------

    private void applicationDeployed( ApplicationRuntimeProfile runtimeProfile )
    {
        getLogger().info( "Looking for WARs to deploy in application " + runtimeProfile.getName() + "." );

        getLogger().info( runtimeProfile.getLib().getAbsolutePath() );

        File lib = runtimeProfile.getLib();

        File[] wars = lib.listFiles( new WarFileFilter() );

        for ( int i = 0; i < wars.length; i++ )
        {
            File war = wars[ i ];

            getLogger().info( "Found WAR " + war.getAbsolutePath() + ", deploying." );

            deployWebApp( war, runtimeProfile );
        }
    }
}
