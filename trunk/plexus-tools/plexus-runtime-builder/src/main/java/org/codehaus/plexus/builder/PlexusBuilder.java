package org.codehaus.plexus.builder;

/*
 * LICENSE
 */

import org.apache.maven.project.MavenProject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface PlexusBuilder
{
    void setBaseDirectory( String outputDirectory );
    void setProject( MavenProject project );
    void setMavenRepoLocal( String mavenRepoLocal );
    void setPlexusConfiguration( String plexusConfiguration );
    void setConfigurationsDirectory( String configurationsDirectory );
    void setConfigurationPropertiesFile( String configurationPropertiesFile );

    void build()
        throws PlexusRuntimeBuilderException;
}
