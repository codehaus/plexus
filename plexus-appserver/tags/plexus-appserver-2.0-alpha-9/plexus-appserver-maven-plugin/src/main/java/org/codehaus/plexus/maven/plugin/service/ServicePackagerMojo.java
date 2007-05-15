package org.codehaus.plexus.maven.plugin.service;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.builder.service.ServiceBuilderException;

import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 * @goal package-service
 * @requiresDependencyResolution
 * @requiresProject
 * @description Assembled and bundles a Plexus service.
 * @phase package
 */
public class ServicePackagerMojo
    extends AbstractAppServerServiceMojo
{
    public void execute()
        throws MojoExecutionException
    {
        File outputFile = new File( target, finalName + ".sar" );

        try
        {
            builder.bundle( outputFile, serviceAssemblyDirectory );
        }
        catch ( ServiceBuilderException e )
        {
            throw new MojoExecutionException( "Error while making service.", e );
        }

        project.getArtifact().setFile( outputFile );
    }
}
