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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.builder.service.ServiceBuilder;
import org.codehaus.plexus.builder.service.ServiceBuilderException;

/**
 * @goal service
 *
 * @requiresDependencyResolution
 *
 * @description Assembled and bundles a Plexus service.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusServiceGenerator
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
     * @parameter name="serviceArtifacts"
     * type="java.util.Set"
     * required="true"
     * validator=""
     * expression="#project.artifacts"
     * description=""
     */
    private Set serviceArtifacts;

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
     * @parameter name="classes"
     * type="java.lang.String"
     * required="true"
     * validator=""
     * expression="#project.build.outputDirectory"
     * description=""
     */
    private File classes;

    /**
     * @parameter name="plexusConfiguration"
     * type="java.lang.String"
     * required="true"
     * validator=""
     * expression="#plexus.runtime.configuration"
     * description=""
     */
    private String plexusConfiguration;

    /**
     * @parameter name="plexusConfigurationPropertiesFile"
     * type="java.lang.String"
     * required="false"
     * validator=""
     * expression="#plexus.runtime.configuration.propertiesFile"
     * description=""
     */
    private String configurationProperties;

    /**
     * @parameter name="serviceName"
     * type="java.lang.String"
     * required="true"
     * validator=""
     * expression="#serviceName"
     * description=""
     */
    private String serviceName;

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
     * @parameter name="serviceBuilder"
     * type="org.codehaus.plexus.builder.runtime.DefaultApplicationBuilder"
     * required="true"
     * validator=""
     * expression="#component.org.codehaus.plexus.builder.service.ServiceBuilder"
     * description=""
     */
    private ServiceBuilder builder;

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

        String projectBasedir = project.getFile().getParentFile().getAbsolutePath();

        File workingBasedir = null;

        if ( new File( basedir ).isAbsolute() )
        {
            workingBasedir = new File( basedir );
        }
        else
        {
            workingBasedir = new File( projectBasedir, basedir );
        }

        File workingDirectory = new File( workingBasedir, "/plexus-service" );

        File outputFile = new File( workingBasedir, finalName + ".jar" );

        File configurationsDir = null;

        File configurationPropertiesFile = null;

        if ( configurationProperties != null )
        {
            configurationPropertiesFile = new File( projectBasedir, configurationProperties );
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        List remoteRepositories = new ArrayList();

        try
        {
            // ----------------------------------------------------------------------
            // Build the service
            // ----------------------------------------------------------------------

            builder.build( serviceName,
                           workingDirectory,
                           classes,
                           remoteRepositories,
                           localRepository,
                           serviceArtifacts,
                           new File( projectBasedir, plexusConfiguration ),
                           configurationsDir,
                           configurationPropertiesFile );

            // ----------------------------------------------------------------------
            // Bundle the service
            // ----------------------------------------------------------------------

            builder.bundle( outputFile, workingDirectory );
        }
        catch ( ServiceBuilderException e )
        {
            throw new MojoExecutionException( "Error while making service.", e );
        }
    }
}
