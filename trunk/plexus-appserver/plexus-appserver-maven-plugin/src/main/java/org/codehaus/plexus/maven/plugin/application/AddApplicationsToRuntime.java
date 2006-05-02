package org.codehaus.plexus.maven.plugin.application;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder;

import java.io.File;
import java.util.Set;
import java.util.Iterator;

/**
 * @goal add-apps
 *
 * @requiresDependencyResolution
 *
 * @phase package
 *
 * @description Adds all Plexus applications in the dependencies to the Plexus runtime. This is used when
 *              you are generating a Plexus runtime which houses several Plexus applications.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 */
public class AddApplicationsToRuntime
    extends AbstractMojo
{
    // ----------------------------------------------------------------------
    // Configurable properties
    // ----------------------------------------------------------------------

    /**
     * @parameter expression="${project.build.directory}/plexus-test-runtime"
     * @required
     */
    private File runtimePath;

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File target;

    // ----------------------------------------------------------------------
    // Read-only
    // ----------------------------------------------------------------------

    /**
     * @parameter expression="${project.artifacts}"
     * @required
     * @readonly
     */
    private Set projectArtifacts;

    /**
     * @parameter expression="${component.org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder}"
     * @required
     * @readonly
     */
    private PlexusRuntimeBuilder runtimeBuilder;

    public void execute()
        throws MojoExecutionException
    {
        try
        {
            for ( Iterator it = projectArtifacts.iterator(); it.hasNext(); )
            {
                Artifact artifact = (Artifact) it.next();

                if ( artifact.getType().equals( "plexus-application" ) )
                {
                    runtimeBuilder.addPlexusApplication( artifact.getFile(), runtimePath );
                }
            }
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error while building test runtimePath.", e );
        }
    }
}
