package org.apache.maven.plugin.plexus;

import java.net.URL;

import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.builder.PlexusBuilder;
import org.codehaus.plexus.embed.Embedder;

/**
 * @goal runtime
 *
 * @requiresDependencyResolution
 *
 * @description Builds plexus containers.
 *
 * @parameter
 *  name="basedir"
 *  type="String"
 *  required="true"
 *  validator=""
 *  expression="#basedir"
 *  description=""
 * @parameter
 *  name="project"
 *  type="org.apache.maven.project.MavenProject"
 *  required="true"
 *  validator=""
 *  expression="#project"
 *  description=""
 * @parameterXXX
 *  name="runtimeBuilder"
 *  type="org.codehaus.plexus.builder.PlexusBuilder"
 *  required="true"
 *  validator=""
 *  expression="#component.org.codehaus.plexus.builder.PlexusBuilder"
 *  description=""
 * @parameter
 *  name="plexusApplicationName"
 *  type="java.lang.String"
 *  required="true"
 *  validator=""
 *  expression="#plexus.application.name"
 *  description=""
 * @parameter
 *  name="plexusConfiguration"
 *  type="java.lang.String"
 *  required="true"
 *  validator=""
 *  expression="#plexus.runtime.configuration"
 *  description=""
 * @parameter
 *  name="plexusConfigurationsDirectory"
 *  type="java.lang.String"
 *  required="true"
 *  validator=""
 *  expression="#plexus.runtime.configurations.directory"
 *  description=""
 * @parameter
 *  name="plexusConfigurationPropertiesFile"
 *  type="java.lang.String"
 *  required="true"
 *  validator=""
 *  expression="#plexus.runtime.configuration.propertiesfile"
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

        String basedir = (String) request.getParameter( "basedir" );

        MavenProject project = (MavenProject) request.getParameter( "project" );

//        String mavenRepoLocal = (String) request.getParameter( "mavenRepoLocal" );

//        String plexusVersion = (String) request.getParameter( "plexusVersion" );

        String plexusApplicationName = (String) request.getParameter( "plexusApplicationName" );

        String plexusConfiguration = (String) request.getParameter( "plexusConfiguration" );

//        String componentManifest = (String) request.getParameter( "plexusComponentManifest" );

        String configurationsDirectory = (String) request.getParameter( "plexusConfigurationsDirectory" );

        String configurationPropertiesFile = (String) request.getParameter( "plexusConfigurationPropertiesFile" );

        PlexusContainer plexus = getPlexus( basedir );

//        PlexusBuilder builder = (PlexusBuilder) request.getParameter( "runtimeBuilder" );
        PlexusBuilder builder = (PlexusBuilder) plexus.lookup( PlexusBuilder.ROLE );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        builder.setBaseDirectory( basedir + "/generated-runtime" );

        builder.setProject( project );

        builder.setMavenRepoLocal( project.getLocalRepository() );

        builder.setApplicationName( plexusApplicationName );

        builder.setPlexusConfiguration( plexusConfiguration );

        builder.setConfigurationsDirectory( configurationsDirectory );

        builder.setConfigurationPropertiesFile( configurationPropertiesFile );

        builder.build();
    }

    public static PlexusContainer getPlexus( String outputDirectory )
        throws Exception
    {
        Embedder plexus = new Embedder();

        URL url = PlexusContainerGenerator.class.getResource( "/plexus.xml" );

        if ( url == null )
        {
            throw new Exception( "Could not find /plexus.xml" );
        }

        System.err.println( "url: " + url );

        plexus.setConfiguration( url );

        plexus.start();

        plexus.getContainer().getContext().put( "plexus.home", outputDirectory + "/plexus-home" );

        return plexus.getContainer();
    }
}
