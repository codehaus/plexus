package org.apache.maven.plugin.plexus;

import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.builder.PlexusBuilder;

/**
 * @goal runtime
 *
 * @requiresDependencyResolution
 *
 * @description Builds plexus containers.
 *
 * @parameter
 *  name="project"
 *  type="String"
 *  required="true"
 *  validator=""
 *  expression="#project"
 *  description=""
 * @parameter
 *  name="runtimeBuilder"
 *  type="org.codehaus.plexus.builder.PlexusBuilder"
 *  required="true"
 *  validator=""
 *  expression="#component.org.codehaus.plexus.builder.PlexusBuilder"
 *  description=""
 * 
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
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

        String outputDirectory = (String) request.getParameter( "outputDirectory" );

        MavenProject project = (MavenProject) request.getParameter( "project" );

//        String mavenRepoLocal = (String) request.getParameter( "mavenRepoLocal" );

//        String plexusVersion = (String) request.getParameter( "plexusVersion" );

//        String plexusApplication = (String) request.getParameter( "plexusApplication" );

        String plexusConfiguration = (String) request.getParameter( "plexusConfiguration" );

//        String componentManifest = (String) request.getParameter( "plexusComponentManifest" );

        String configurationsDirectory = (String) request.getParameter( "plexusConfigurationsDirectory" );

        String configurationPropertiesFile = (String) request.getParameter( "plexusConfigurationPropertiesFile" );

        PlexusBuilder builder = (PlexusBuilder) request.getParameter( "runtimeBuilder" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        builder.setBaseDirectory( outputDirectory );

        builder.setProject( project );

        builder.setMavenRepoLocal( project.getLocalRepository() );

//        builder.setApplication( plexusApplication );

//        builder.setPlexusVersion( plexusVersion );

        builder.setPlexusConfiguration( plexusConfiguration );

//        builder.setComponentManifest( componentManifest );

        builder.setConfigurationsDirectory( configurationsDirectory );

        builder.setConfigurationPropertiesFile( configurationPropertiesFile );

        builder.build();
    }
}
