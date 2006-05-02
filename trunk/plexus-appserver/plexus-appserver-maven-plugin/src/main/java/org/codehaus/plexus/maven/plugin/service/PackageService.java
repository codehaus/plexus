package org.codehaus.plexus.maven.plugin.service;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.builder.service.ServiceBuilder;
import org.codehaus.plexus.builder.service.ServiceBuilderException;

import java.io.File;

/**
 * @goal package-service
 *
 * @requiresDependencyResolution
 * @requiresProject
 *
 * @description Assembled and bundles a Plexus service.
 *
 * @phase package
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PackageService
    extends AbstractMojo
{
    /**
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String finalName;

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File target;

    /**
     * @parameter expression="${project.build.directory}/plexus-service"
     * @required
     */
    private File serviceAssemblyDirectory;

    /**
     * @parameter expression="${component.org.codehaus.plexus.builder.service.ServiceBuilder}"
     * @required
     */
    private ServiceBuilder builder;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

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
