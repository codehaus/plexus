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
import org.apache.maven.project.MavenProject;

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
     * @parameter type="String"
     * required="true"
     * validator=""
     * expression="#project.build.directory"
     * description=""
     */
    private File target;

    /**
     * @parameter name="projectArtifacts"
     * type="java.util.Set"
     * required="true"
     * validator=""
     * expression="#project.artifacts"
     * description=""
     */
    private Set projectArtifacts;

    /**
     * @parameter name="applicationConfiguration"
     * type="java.lang.String"
     * required="true"
     * validator=""
     * expression="#applicationConfiguration"
     * description=""
     */
    private String applicationConfiguration;

    /**
     * @parameter name="configurationProperties"
     * type="java.lang.String"
     * required="false"
     * validator=""
     * expression="#configurationProperties"
     * description=""
     */
    private String configurationProperties;

    /**
     * @parameter name="configurationDirectory"
     * type="java.lang.String"
     * required="false"
     * validator=""
     * expression="#configurationDirectory"
     * description=""
     */
    private String configurationDirectory;

    /**
     * @parameter name="applicationBuilder"
     * type="org.codehaus.plexus.builder.runtime.DefaultApplicationBuilder"
     * required="true"
     * validator=""
     * expression="#component.org.codehaus.plexus.builder.application.ApplicationBuilder"
     * description=""
     */
    private ApplicationBuilder builder;

    /**
     * @parameter name="applicationName"
     * type="java.lang.String"
     * required="true"
     * validator=""
     * expression="#applicationName"
     * description=""
     */
    private String applicationName;

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

    /**
     * @parameter name="project"
     * type="org.apache.maven.project.MavenProject"
     * required="true"
     * validator=""
     * expression="#project"
     * description="current MavenProject instance"
     */
    private MavenProject project;

    public void execute()
        throws MojoExecutionException
    {
        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        File workingDirectory = new File( target, "plexus-application" );

        File configurationDirectoryFile = null;

        if ( configurationDirectory != null )
        {
            configurationDirectoryFile = new File( basedir, configurationDirectory );
        }

        File configurationPropertiesFile = null;

        if ( configurationProperties != null )
        {
            configurationPropertiesFile = new File( basedir, configurationProperties );
        }

        // ----------------------------------------------------------------------
        // Find all services
        // ----------------------------------------------------------------------

        Set services = new HashSet();

        for ( Iterator it = projectArtifacts.iterator(); it.hasNext(); )
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

        try
        {
            builder.assemble( applicationName,
                              workingDirectory,
                              remoteRepositories,
                              localRepository,
                              projectArtifacts,
                              services,
                              new File( basedir, applicationConfiguration ),
                              configurationDirectoryFile,
                              configurationPropertiesFile );
        }
        catch ( ApplicationBuilderException e )
        {
            throw new MojoExecutionException( "Error while assembling the application.", e );
        }
    }
}
