package org.codehaus.plexus.maven.plugin.application;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.maven.plugin.AbstractAppServerMojo;

import java.util.Iterator;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 * @goal add-apps
 * @requiresDependencyResolution
 * @phase package
 * @description Adds all Plexus applications in the dependencies to the Plexus runtime. This is used when
 * you are generating a Plexus runtime which houses several Plexus applications.
 */
public class ApplicationsRuntimePopulatorMojo
    extends AbstractAppServerMojo
{
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
