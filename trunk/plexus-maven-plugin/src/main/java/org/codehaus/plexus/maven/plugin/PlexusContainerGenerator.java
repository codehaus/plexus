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

import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder;

import java.lang.reflect.Method;
import java.io.File;
import java.util.Set;
import java.util.HashSet;

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
 * @parameter name="runtimeBuilder"
 * type="org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder"
 * required="true"
 * validator=""
 * expression="#component.org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder"
 * description=""
 *
 * @parameter name="plexusConfiguration"
 * type="java.lang.String"
 * required="true"
 * validator=""
 * expression="#plexus.runtime.configuration"
 * description=""
 *
 * @parameter name="plexusConfigurationsDirectory"
 * type="java.lang.String"
 * required="true"
 * validator=""
 * expression="#plexus.runtime.configurations.directory"
 * description=""
 *
 * @parameter name="plexusConfigurationProperties"
 * type="java.lang.String"
 * required="true"
 * validator=""
 * expression="#plexus.runtime.configuration.propertiesfile"
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

        MavenProject project = (MavenProject) request.getParameter( "project" );

        String plexusConfiguration = (String) request.getParameter( "plexusConfiguration" );

        String configurationProperties = (String) request.getParameter( "plexusConfigurationProperties" );

        displayClassLoader( request.getParameter( "runtimeBuilder" ) );

        PlexusRuntimeBuilder builder = (PlexusRuntimeBuilder) request.getParameter( "runtimeBuilder" );

        ArtifactRepository localRepository = (ArtifactRepository) request.getParameter( "localRepository" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        File outputDirectory = new File( basedir, "plexus-runtime" );

        Set remoteRepositories = new HashSet();

        Set artifacts = project.getArtifacts();

        File plexusConfigurationFile = new File( plexusConfiguration );

        File configurationPropertiesFile = null;

        if ( configurationProperties != null )
        {
            configurationPropertiesFile = new File( configurationProperties );
        }

        builder.build( outputDirectory,
                       remoteRepositories, localRepository, artifacts,
                       plexusConfigurationFile, configurationPropertiesFile );
    }

    private void displayClassLoader( Object o )
        throws Exception
    {
        ClassLoader cl = o.getClass().getClassLoader();

        Method getRealm = cl.getClass().getDeclaredMethod( "getRealm", null );

        getRealm.setAccessible( true );

        Object realm = getRealm.invoke( cl, null );

        Method display = realm.getClass().getMethod( "display", null );

        display.invoke( realm, null );
    }
}
