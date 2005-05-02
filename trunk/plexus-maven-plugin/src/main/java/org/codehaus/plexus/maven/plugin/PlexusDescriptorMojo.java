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
     * @parameter expression="${basedir}"
     *
     * @required
     */
    private String basedir;

    /**
     * @parameter expression="${project}"
     *
     * @required
     */
    private MavenProject project;

    /**
     * @parameter expression="${project.build.outputDirectory}"
     *
     * @required
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
