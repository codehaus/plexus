/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.maven.plugin.rar;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;
import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.builder.DefaultPlexusBuilder;

/**
 * A mojo that build a J2EE RAR archive.
 *
 * @version $Revision$ $Date$
 * @maven.plugin.id plexus
 * @maven.plugin.description Maven pojo to build plexus containers
 *
 * @parameter mavenRepoLocal String true validator description
 * @parameter outputDirectory String true validator description
 * @parameter pom String true validator description
 * @parameter plexusVersion String true validator description
 * @parameter plexusApplication String true validator description
 * @parameter plexusConfiguration String true validator description
 * @parameter plexusConfigurationsDirectory String true validator description
 * @parameter plexusConfigurationPropertiesFile String true validator description
 *
 * @goal.name plexus:container
 * @goal.plexus:container.description assemble a container
 * @goal.plexus:container.parameter pom #basedir/project.xml
 * @goal.plexus:container.parameter outputDirectory #maven.build.dir/plexus
 * @goal.plexus:container.parameter mavenRepoLocal #maven.repo.local
 * @goal.plexus:container.parameter plexusVersion #plexus.version
 * @goal.plexus:container.parameter plexusApplication #plexus.application
 * @goal.plexus:container.parameter plexusConfiguration #plexus.configuration
 * @goal.plexus:container.parameter plexusComponentManifest #plexus.componentManifest
 * @goal.plexus:container.parameter plexusConfigurationsDirectory #plexus.configurationsDirectory
 * @goal.plexus:container.parameter plexusConfigurationPropertiesFile #plexus.configurationPropertiesFile
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

        String pom = (String) request.getParameter( "pom" );

        String mavenRepoLocal = (String) request.getParameter( "mavenRepoLocal" );

        String plexusVersion = (String) request.getParameter( "plexusVersion" );

        String plexusApplication = (String) request.getParameter( "plexusApplication" );

        String plexusConfiguration = (String) request.getParameter( "plexusConfiguration" );

        String componentManifest = (String) request.getParameter( "plexusComponentManifest" );

        String configurationsDirectory = (String) request.getParameter( "plexusConfigurationsDirectory" );

        String configurationPropertiesFile = (String) request.getParameter( "plexusConfigurationPropertiesFile" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        DefaultPlexusBuilder builder = new DefaultPlexusBuilder();

        builder.setBaseDirectory( outputDirectory );

        builder.setProjectPom( pom );

        builder.setMavenRepoLocal( mavenRepoLocal );

        builder.setApplication( plexusApplication );

        builder.setPlexusVersion( plexusVersion );

        builder.setPlexusConfiguration( plexusConfiguration );

        builder.setComponentManifest( componentManifest );

        builder.setConfigurationsDirectory( configurationsDirectory );

        builder.setConfigurationPropertiesFile( configurationPropertiesFile );

        builder.build();
    }
}
