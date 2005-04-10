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
import java.util.Iterator;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder;

/**
 * @goal runtime
 *
 * @requiresDependencyResolution
 *
 * @description Builds plexus containers.
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
 * expression="#plexus.runtime.configuration.propertiesfile"
 * description=""
 *
 * @parameter name="runtimeBuilder"
 * type="org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder"
 * required="true"
 * validator=""
 * expression="#component.org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder"
 * description=""
 *
 * @parameter name="localRepository"
 * type="org.apache.maven.artifact.ArtifactRepository"
 * required="true"
 * validator=""
 * expression="#localRepository"
 * description=""
 *
 * @parameter name="project"
 * type="org.apache.maven.project.MavenProject"
 * required="true"
 * validator=""
 * expression="#project"
 * description="current MavenProject instance"
 */
public class PlexusContainerGenerator
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

        String plexusConfiguration = (String) request.getParameter( "plexusConfiguration" );

        String configurationProperties = (String) request.getParameter( "plexusConfigurationProperties" );

        PlexusRuntimeBuilder builder = (PlexusRuntimeBuilder) request.getParameter( "runtimeBuilder" );

        ArtifactRepository localRepository = (ArtifactRepository) request.getParameter( "localRepository" );

        List remoteRepositories = (List) request.getParameter( "remoteRepositories" );

        MavenProject project = (MavenProject) request.getParameter( "project" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String projectBasedir = project.getFile().getParentFile().getAbsolutePath();

        File workingBasedir = null;
        
        if ( new File( basedir ).isAbsolute() )
        {
            workingBasedir = new File( basedir );
        }
        else
        {
            workingBasedir = new File( projectBasedir, basedir );
        }

        File outputDirectory = new File( workingBasedir, "plexus-runtime" );

        File plexusConfigurationFile = new File( projectBasedir, plexusConfiguration );

        File configurationPropertiesFile = null;

        if ( configurationProperties != null )
        {
            configurationPropertiesFile = new File( projectBasedir, configurationProperties );
        }

        builder.build( outputDirectory,
                       remoteRepositories, localRepository, projectArtifacts,
                       plexusConfigurationFile, configurationPropertiesFile );
    }
}
