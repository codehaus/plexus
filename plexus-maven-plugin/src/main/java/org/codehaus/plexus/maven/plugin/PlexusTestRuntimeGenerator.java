package org.codehaus.plexus.maven.plugin;

/*
 * Copyright (c) 2004, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder;

/**
 * @goal test-runtime
 *
 * @requiresDependencyResolution
 *
 * @description Generates a complete runtime with the application and it's requires services.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusTestRuntimeGenerator
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
    private File basedir;

    /**
     * @parameter name="basedir"
     * type="String"
     * required="true"
     * validator=""
     * expression="#project.build.directory"
     * description=""
     */
    private File target;

    /**
     * @parameter name="finalName"
     * type="java.lang.String"
     * required="true"
     * validator=""
     * expression="#maven.final.name"
     * description=""
     */
    private String finalName;

    /**
     * @parameter name="artifacts"
     * type="java.util.Set"
     * required="true"
     * validator=""
     * expression="#project.artifacts"
     * description=""
     */
    private Set projectArtifacts;

    /**
     * @parameter required="true"
     * validator=""
     * expression="#plexus.runtime.configuration"
     * description=""
     */
    private String testRuntimeConfiguration;

    /**
     * @parameter name="runtimeBuilder"
     * type="org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder"
     * required="true"
     * validator=""
     * expression="#component.org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder"
     * description=""
     */
    private PlexusRuntimeBuilder runtimeBuilder;

    /**
     * @parameter name="localRepository"
     * type="org.apache.maven.artifact.ArtifactRepository"
     * required="true"
     * validator=""
     * expression="#localRepository"
     * description=""
     */
    private ArtifactRepository localRepository;

    /**
     * @parameter name="remoteRepositories"
     * type="java.util.List"
     * required="true"
     * validator=""
     * expression="#project.remoteArtifactRepositories"
     * description=""
     */
    private List remoteRepositories;

    public void execute()
        throws MojoExecutionException
    {
        File applicationJarFile = PlexusBundleApplicationMojo.getApplicationJarFile( target,
                                                                                     finalName );

        if ( !applicationJarFile.canRead() )
        {
            throw new MojoExecutionException( "Can't read Plexus application artifact '" + applicationJarFile.getAbsolutePath() + "'." );
        }

        try
        {
            File runtimeRoot = new File( target, "plexus-test-runtime" );

            runtimeBuilder.build( runtimeRoot,
                                  remoteRepositories,
                                  localRepository,
                                  projectArtifacts,
                                  new File( basedir, testRuntimeConfiguration ),
                                  null );

            // ----------------------------------------------------------------------
            // Copy the application
            // ----------------------------------------------------------------------

            runtimeBuilder.addPlexusApplication( applicationJarFile, runtimeRoot );

            // ----------------------------------------------------------------------
            // Copy any services
            // ----------------------------------------------------------------------

            for ( Iterator it = projectArtifacts.iterator(); it.hasNext(); )
            {
                Artifact artifact = (Artifact) it.next();

                if ( artifact.getType().equals( "plexus-service" ) )
                {
                    runtimeBuilder.addPlexusService( artifact.getFile(), runtimeRoot );
                }
            }
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error while building test runtime.", e );
        }
    }
}
