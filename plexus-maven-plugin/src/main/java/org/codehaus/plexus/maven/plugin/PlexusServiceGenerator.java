package org.codehaus.plexus.maven.plugin;

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

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;

import org.codehaus.plexus.builder.service.ServiceBuilder;

/**
 * @goal service
 *
 * @requiresDependencyResolution
 *
 * @description Assembled and bundles a Plexus service.
 *
 * @phase package
 *
 * @parameter name="basedir"
 * type="String"
 * required="true"
 * validator=""
 * expression="#basedir"
 * description=""
 *
 * @parameter name="serviceArtifacts"
 * type="java.util.Set"
 * required="true"
 * validator=""
 * expression="#project.artifacts"
 * description=""
 *
 * @parameter name="finalName"
 * type="java.lang.String"
 * required="true"
 * validator=""
 * expression="#maven.final.name"
 * description=""
 *
 * @parameter name="classes"
 * type="java.lang.String"
 * required="true"
 * validator=""
 * expression="#project.build.outputDirectory"
 * description=""
 *
 * @parameter name="plexusConfiguration"
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
 * expression="#plexus.runtime.configuration.propertiesFile"
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

        Set serviceArtifacts = (Set) request.getParameter( "serviceArtifacts" );

        String finalName = (String) request.getParameter( "finalName" );

        File classes = new File( (String) request.getParameter( "classes" ) );

        String plexusConfiguration = (String) request.getParameter( "plexusConfiguration" );

        String configurationProperties = (String) request.getParameter( "plexusConfigurationPropertiesFile" );

        String serviceName = (String) request.getParameter( "serviceName" );

        ArtifactRepository localRepository = (ArtifactRepository) request.getParameter( "localRepository" );

        ServiceBuilder builder = (ServiceBuilder) request.getParameter( "serviceBuilder" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        File workingDirectory = new File( basedir, "/plexus-service" );

        File outputFile = new File( basedir, finalName + ".jar" );

        File configurationsDir = null;

        File configurationPropertiesFile = null;

        if ( configurationProperties != null )
        {
            configurationPropertiesFile = new File( configurationProperties );
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        List remoteRepositories = new ArrayList();

        // ----------------------------------------------------------------------
        // Build the service
        // ----------------------------------------------------------------------

        builder.build( serviceName,
                       workingDirectory,
                       classes,
                       remoteRepositories,
                       localRepository,
                       serviceArtifacts,
                       new File( basedir, plexusConfiguration ),
                       configurationsDir,
                       configurationPropertiesFile );

        // ----------------------------------------------------------------------
        // Bundle the service
        // ----------------------------------------------------------------------

        builder.bundle( outputFile, workingDirectory );
    }
}
