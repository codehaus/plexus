package org.codehaus.plexus.maven.plugin.runtime;

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
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilderException;
import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder;

/**
 * @goal assemble-runtime
 *
 * @requiresDependencyResolution
 *
 * @phase package
 *
 * @description Builds plexus containers.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AssembleRuntime
    extends AbstractMojo
{
    // ----------------------------------------------------------------------
    // Configurable properties
    // ----------------------------------------------------------------------

    /**
     * @parameter expression="${project.build.directory}/plexus-runtime"
     * @required
     */
    private File runtimePath;

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File target;

    /**
     * @parameter expression="${runtimeConfiguration}"
     * @required
     */
    private File runtimeConfiguration;

    /**
     * @parameter expression="${runtimeConfigurationProperties}"
     * @required
     */
    private File runtimeConfigurationProperties;

    // ----------------------------------------------------------------------
    // Read-only
    // ----------------------------------------------------------------------

    /**
     * @parameter expression="${component.org.codehaus.plexus.builder.runtime.AssembleRuntime}"
     * @required
     * @readonly
     */
    private PlexusRuntimeBuilder builder;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * @parameter expression="${project.artifacts}"
     * @required
     * @readonly
     */
    private Set projectArtifacts;

    /**
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     * @readonly
     */
    private List remoteRepositories;

    public void execute()
        throws MojoExecutionException
    {
        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        try
        {
            builder.build( runtimePath,
                           remoteRepositories,
                           localRepository,
                           projectArtifacts,
                           runtimeConfiguration,
                           runtimeConfigurationProperties );
        }
        catch ( PlexusRuntimeBuilderException e )
        {
            throw new MojoExecutionException( "Error while building runtime.", e );
        }
    }
}
