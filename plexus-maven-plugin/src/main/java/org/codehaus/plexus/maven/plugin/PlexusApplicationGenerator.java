package org.apache.maven.plugin.plexus;

/*
 * Copyright (c) 2004, Codehaus.org
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;

import org.codehaus.plexus.builder.application.ApplicationBuilder;

/**
 * @goal app
 *
 * @requiresDependencyResolution
 *
 * @description Assembles the Plexus application.
 *
 * @parameter name="basedir"
 * type="String"
 * required="true"
 * validator=""
 * expression="#basedir"
 * description=""
 *
 * @parameter name="projectArtifacts"
 * type="java.util.Set"
 * required="true"
 * validator=""
 * expression="#project.artifacts"
 * description=""
 *
 * @parameter name="applicationBuilder"
 * type="org.codehaus.plexus.builder.runtime.DefaultApplicationBuilder"
 * required="true"
 * validator=""
 * expression="#component.org.codehaus.plexus.builder.application.ApplicationBuilder"
 * description=""
 *
 * @parameter name="applicationConfiguration"
 * type="java.lang.String"
 * required="true"
 * validator=""
 * expression="#plexus.runtime.configuration"
 * description=""
 *
 * @parameter name="plexusConfigurationPropertiesFile"
 * type="java.lang.String"
 * required="false"
 * validator=""
 * expression="#plexus.runtime.configuration.propertiesfile"
 * description=""
 *
 * @parameter name="applicationName"
 * type="java.lang.String"
 * required="true"
 * validator=""
 * expression="#applicationName"
 * description=""
 *
 * @parameter name="localRepository"
 * type="org.apache.maven.artifact.ArtifactRepository"
 * required="true"
 * validator=""
 * expression="#localRepository"
 * description=""
 *
 */
public class PlexusApplicationGenerator
    extends AbstractPlugin
{
    public void execute( PluginExecutionRequest request, PluginExecutionResponse response )
        throws Exception
    {
        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String basedir = (String) request.getParameter( "basedir" );

        Set projectArtifacts = (Set) request.getParameter( "projectArtifacts" );

        String applicationConfiguration = (String) request.getParameter( "applicationConfiguration" );

        String configurationProperties = (String) request.getParameter( "plexusConfigurationPropertiesFile" );

        ApplicationBuilder builder = (ApplicationBuilder) request.getParameter( "applicationBuilder" );

        String applicationName = (String) request.getParameter( "applicationName" );

        ArtifactRepository localRepository = (ArtifactRepository) request.getParameter( "localRepository" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        File workingDirectory = new File( basedir, "/plexus-application" );

        File configurationsDir = null;

        File configurationPropertiesFile = null;

        if ( configurationProperties != null )
        {
            configurationPropertiesFile = new File( configurationProperties );
        }

        // ----------------------------------------------------------------------
        // Find all services
        // ----------------------------------------------------------------------

        Set services = new HashSet();

        for ( Iterator it = projectArtifacts.iterator(); it.hasNext(); )
        {
            Artifact artifact = (Artifact) it.next();

            if ( artifact.getType().equals( "plexus-service" ) )
            {
                services.add( artifact );
            }
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        List remoteRepositories = new ArrayList();

        // ----------------------------------------------------------------------
        // Build the application
        // ----------------------------------------------------------------------

        builder.assemble( applicationName,
                          workingDirectory,
                          remoteRepositories,
                          localRepository,
                          projectArtifacts,
                          services,
                          new File( applicationConfiguration ),
                          configurationsDir,
                          configurationPropertiesFile );
    }
}
