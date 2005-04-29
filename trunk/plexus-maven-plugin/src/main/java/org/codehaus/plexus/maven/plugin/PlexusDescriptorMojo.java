package org.codehaus.plexus.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.cdc.ComponentDescriptorCreator;

/**
 * @goal descriptor
 *
 * @phase process-sources
 *
 * @description Builds a plexus descriptor.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusDescriptorMojo
    extends AbstractMojo
{
    /**
     * @parameter name="basedir"
     * type="String"
     * required="true"
     * validator=""
     * expression="#basedir"
     * description=""
     */
    private String basedir;

    /**
     * @parameter name="project"
     * type="org.apache.maven.project.MavenProject"
     * required="true"
     * validator=""
     * expression="#project"
     * description=""
     */
    private MavenProject project;

    /**
     * @parameter name="output"
     * type="String"
     * required="true"
     * validator=""
     * expression="#project.build.outputDirectory"
     * description=""
     */
    private String output;

    public void execute()
        throws MojoExecutionException
    {
        ComponentDescriptorCreator cdc = new ComponentDescriptorCreator();

        cdc.setBasedir( basedir );

        cdc.setProject( project );

        cdc.setDestDir( output + "/META-INF/plexus" );

        try
        {
            cdc.execute();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error while executing component descritor creator.", e );
        }
    }
}
