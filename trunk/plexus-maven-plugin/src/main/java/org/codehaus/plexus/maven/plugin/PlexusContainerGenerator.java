package org.apache.maven.plugin.plexus;

import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder;

import java.lang.reflect.Method;

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
 * @parameter name="plexusConfigurationPropertiesFile"
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

        String configurationPropertiesFile = (String) request.getParameter( "plexusConfigurationPropertiesFile" );

        displayClassLoader( request.getParameter( "runtimeBuilder" ) );

        PlexusRuntimeBuilder builder = (PlexusRuntimeBuilder) request.getParameter( "runtimeBuilder" );

        ArtifactRepository localRepository = (ArtifactRepository) request.getParameter( "localRepository" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        builder.setBaseDirectory( basedir + "/generated-runtime" );

        builder.setProject( project );

        builder.setLocalRepository( localRepository );

        builder.setPlexusConfiguration( plexusConfiguration );

        builder.setConfigurationPropertiesFile( configurationPropertiesFile );

        builder.build();
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
