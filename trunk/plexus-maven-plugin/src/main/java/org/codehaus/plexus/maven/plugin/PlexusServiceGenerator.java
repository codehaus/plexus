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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.builder.service.ServiceBuilder;

/**
 * @goal service
 *
 * @requiresDependencyResolution
 *
 * @description Assembled and bundles a Plexus service.
 *
 * @parameter name="basedir"
 * type="String"
 * required="true"
 * validator=""
 * expression="#basedir"
 * description=""
 *
 * @parameter name="project"
 * type="org.apache.maven.project.MavenProject"
 * required="true"
 * validator=""
 * expression="#project"
 * description=""
 *
 * @parameter name="finalName"
 * type="java.lang.String"
 * required="true"
 * validator=""
 * expression="#maven.final.name"
 * description=""
 *
 * @parameter name="plexusConfiguration"
 * type="java.lang.String"
 * required="true"
 * validator=""
 * expression="#plexus.runtime.configuration"
 * description=""
 *
 * @parameter name="plexusConfigurationProperties"
 * type="java.lang.String"
 * required="true"
 * validator=""
 * expression="#plexus.runtime.configuration.properties"
 * description=""
 *
 * @parameter name="serviceName"
 * type="java.lang.String"
 * required="true"
 * validator=""
 * expression="#serviceName"
 * description=""
 *
 * @parameter name="localRepository"
 * type="org.apache.maven.artifact.ArtifactRepository"
 * required="true"
 * validator=""
 * expression="#localRepository"
 * description=""
 *
 * @parameter name="serviceBuilder"
 * type="org.codehaus.plexus.builder.runtime.DefaultApplicationBuilder"
 * required="true"
 * validator=""
 * expression="#component.org.codehaus.plexus.builder.service.ServiceBuilder"
 * description=""
 *
 */
public class PlexusServiceGenerator
    extends AbstractPlugin
{
    public void execute( PluginExecutionRequest request, PluginExecutionResponse response )
        throws Exception
    {
        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String basedir = (String) request.getParameter( "basedir" );

        MavenProject project = (MavenProject) request.getParameter( "project" );

        String finalName = (String) request.getParameter( "finalName" );

        String plexusConfiguration = (String) request.getParameter( "plexusConfiguration" );

        String configurationPropertiesFile = (String) request.getParameter( "plexusConfigurationProperties" );

        String serviceName = (String) request.getParameter( "serviceName" );

        ArtifactRepository localRepository = (ArtifactRepository) request.getParameter( "localRepository" );

        ServiceBuilder builder = (ServiceBuilder) request.getParameter( "serviceBuilder" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        File workingDirectory = new File( basedir, "/plexus-service" );

        File outputFile = new File( basedir, finalName + "-service.jar" );

        File configurationsDir = null;

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        List remoteRepositories = new ArrayList();

        Set projectArtifacts = project.getArtifacts();

        Artifact projectArtifact = new DefaultArtifact( project.getGroupId(), project.getArtifactId(),
                                                        project.getVersion(), project.getPackaging() );

        projectArtifacts.add( projectArtifact );

        // ----------------------------------------------------------------------
        // Build the service
        // ----------------------------------------------------------------------

        builder.build( serviceName, workingDirectory,
                       remoteRepositories, localRepository, projectArtifacts,
                       new File( plexusConfiguration ), configurationsDir, new File( configurationPropertiesFile ) );

        // ----------------------------------------------------------------------
        // Bundle the service
        // ----------------------------------------------------------------------

        builder.bundle( outputFile, workingDirectory );
    }
}
