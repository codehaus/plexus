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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.codehaus.plexus.builder.application.ApplicationBuilder;
import org.codehaus.plexus.builder.application.ApplicationBuilderException;

/**
 * @goal app
 *
 * @requiresDependencyResolution
 *
 * @description Assembles the Plexus application.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusApplicationGenerator
    extends AbstractMojo
{
    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    /**
     * @parameter expression="${applicationConfiguration}"
     * @required
     */
    private File applicationConfiguration;

    /**
     * @parameter expression="${configurationsDirectory}"
     */
    private File configurationsDirectory;

    /**
     * @parameter expression="${configurationProperties}"
     */
    private File configurationProperties;

    /**
     * @parameter expression="${applicationName}"
     * @required
     */
    private String applicationName;

    // ----------------------------------------------------------------------
    // Read Only Configuration
    // ----------------------------------------------------------------------

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File target;

    /**
     * @parameter expression="${project.artifacts}"
     * @required
     */
    private Set applicationArtifacts;

    /**
     * @parameter expression="${project.build.directory}/plexus-application"
     * @required
     */
    private File applicationAssemblyDirectory;

    // ----------------------------------------------------------------------
    // Components
    // ----------------------------------------------------------------------

    /**
     * @parameter expression="${component.org.codehaus.plexus.builder.application.ApplicationBuilder}"
     * @required
     */
    private ApplicationBuilder builder;

    /**
     * @parameter expression="${localRepository}"
     * @required
     */
    private ArtifactRepository localRepository;

    /**
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     */
    private List remoteRepositories;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void execute()
        throws MojoExecutionException
    {
        // ----------------------------------------------------------------------
        // Find all services
        // ----------------------------------------------------------------------

        Set services = new HashSet();

        for ( Iterator it = applicationArtifacts.iterator(); it.hasNext(); )
        {
            Artifact artifact = (Artifact) it.next();

            if ( artifact.getType().equals( "plexus-service" ) )
            {
                services.add( artifact );
            }
        }

        // ----------------------------------------------------------------------
        // Build the application
        // ----------------------------------------------------------------------

        getLog().debug( "Building the application '" + applicationName + "' into '" + applicationAssemblyDirectory.getAbsolutePath() + "'." );

        try
        {
            builder.assemble( applicationName,
                              applicationAssemblyDirectory,
                              remoteRepositories,
                              localRepository,
                              applicationArtifacts,
                              services,
                              applicationConfiguration,
                              configurationsDirectory,
                              configurationProperties );
        }
        catch ( ApplicationBuilderException e )
        {
            throw new MojoExecutionException( "Error while assembling the application.", e );
        }
    }
}
