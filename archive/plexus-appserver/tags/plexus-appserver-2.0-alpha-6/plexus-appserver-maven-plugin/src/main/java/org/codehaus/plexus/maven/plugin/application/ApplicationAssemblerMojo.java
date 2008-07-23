package org.codehaus.plexus.maven.plugin.application;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.builder.application.ApplicationBuilderException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 * @goal assemble-app
 * @requiresDependencyResolution
 * @description Assemble a Plexus application.
 * @phase package
 */
public class ApplicationAssemblerMojo
    extends AbstractAppServerApplicationMojo
{
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

    /**
     * @parameter expression="${additionalCoreArtifacts}"
     */
    private HashSet additionalCoreArtifacts;

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

        for ( Iterator it = projectArtifacts.iterator(); it.hasNext(); )
        {
            Artifact artifact = (Artifact) it.next();

            if ( artifact.getType().equals( "plexus-service" ) )
            {
                services.add( artifact );
            }
        }

        // ----------------------------------------------------------------------
        // Build the appserver
        // ----------------------------------------------------------------------

        getLog().debug( "Building the appserver '" + applicationName + "' into '" +
            applicationAssemblyDirectory.getAbsolutePath() + "'." );

        Properties interpolationProperties = new Properties();

        if ( configurationProperties != null )
        {
            try
            {
                interpolationProperties.load( new FileInputStream( configurationProperties ) );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Cannot load configuration properties file.", e );
            }
        }

        interpolationProperties.putAll( project.getProperties() );

        try
        {
            applicationBuilder.assemble( applicationName, applicationAssemblyDirectory, remoteRepositories,
                                         localRepository, projectArtifacts, additionalCoreArtifacts, services,
                                         applicationConfiguration, configurationsDirectory, interpolationProperties );
        }
        catch ( ApplicationBuilderException e )
        {
            throw new MojoExecutionException( "Error while assembling the appserver.", e );
        }
    }
}
