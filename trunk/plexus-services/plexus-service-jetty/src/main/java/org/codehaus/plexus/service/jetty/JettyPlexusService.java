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

import org.codehaus.plexus.application.deploy.ApplicationDeployer;
import org.codehaus.plexus.application.profile.ApplicationRuntimeProfile;
import org.codehaus.plexus.application.service.PlexusService;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class JettyPlexusService
    extends AbstractLogEnabled
    implements PlexusService, Initializable, Startable
{
    /** @requirement */
    private ApplicationDeployer deployer;

    /** @requirement */
    private ServletContainer servletContainer;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        getLogger().info( "Initializing servlet container service." );
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
    // PlexusService Implementation
    // ----------------------------------------------------------------------

    public void onApplicationStart( ApplicationRuntimeProfile applicationRuntimeProfile,
                                    PlexusConfiguration serviceConfiguration )
        throws Exception
    {
        PlexusConfiguration[] webapps = serviceConfiguration.getChild( "webapps" ).getChildren( "webapp" );

        for ( int i = 0; i < webapps.length; i++ )
        {
            String path = webapps[ i ].getChild( "path" ).getValue( null );

            String file = webapps[ i ].getChild( "file" ).getValue( null );

            String context = webapps[ i ].getChild( "context" ).getValue( null );

            if ( StringUtils.isEmpty( context ) )
            {
                getLogger().warn( "Error while deploying web application: 'context' is missing or empty." );

                continue;
            }

            // TODO: Read virtual host definitions

            if ( StringUtils.isEmpty( path ) )
            {
                if ( StringUtils.isEmpty( file ) )
                {
                    getLogger().warn( "Error while deploying web application: " +
                                      "For each 'webapp' a 'path' or 'file' element has to be specified." );

                    continue;
                }

                boolean extractWar = Boolean.valueOf( webapps[ i ].getChild( "extractWar" ).getValue( "true" ) ).booleanValue();

                String extractionPath = webapps[ i ].getChild( "extractionPath" ).getValue( null );

                File extractionFile = null;

                if ( !StringUtils.isEmpty( extractionPath ) )
                {
                    extractionFile = new File( extractionPath );
                }

                deployFile( new File( file ), extractWar, extractionFile, context, applicationRuntimeProfile );
            }
            else
            {
                deployDirectory( new File( path ), context, applicationRuntimeProfile );
            }
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void deployFile( File file, boolean extractWar, File extractionPath, String context,
                             ApplicationRuntimeProfile applicationRuntimeProfile )
        throws Exception
    {
        if ( !file.isFile() )
        {
            throw new Exception( "The webapp isn't a file: '" + file.getAbsolutePath() + "'." );
        }

        try
        {
            String virtualHost = null;

            servletContainer.deployWarFile( file, extractWar, extractionPath, context,
                                            applicationRuntimeProfile.getContainer(), virtualHost );
        }
        catch ( ServletContainerException e )
        {
            getLogger().error( "Error while deploying WAR '" + file.getAbsolutePath() + "'.", e );
        }
    }

    private void deployDirectory( File directory, String context, ApplicationRuntimeProfile runtimeProfile )
        throws Exception
    {
        if ( !directory.isDirectory() )
        {
            throw new Exception( "The webapp isn't a directory: '" + directory.getAbsolutePath() + "'." );
        }

        try
        {
            String virtualHost = null;

            servletContainer.deployWarDirectory( directory, context, runtimeProfile.getContainer(), virtualHost );
        }
        catch ( ServletContainerException e )
        {
            getLogger().error( "Error while deploying WAR '" + directory.getAbsolutePath() + "'.", e );
        }
    }
}
