package org.codehaus.plexus.maven.plugin.service;

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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.builder.service.ServiceBuilderException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 * @goal assemble-service
 * @requiresDependencyResolution
 * @requiresProject
 * @description Assembled and bundles a Plexus service.
 * @phase package
 */
public class ServiceAssemblerMojo
    extends AbstractAppServerServiceMojo
{
    /**
     * @parameter expression="${serviceName}"
     * @required
     */
    private String serviceName;

    /**
     * @parameter expression="${serviceConfiguration}"
     * @required
     */
    private File serviceConfiguration;

    /**
     * @parameter expression="${configurationsDirectory}"
     */
    private File configurationsDirectory;

    /**
     * @parameter expression="${configurationProperties}"
     */
    private File configurationProperties;

    public void execute()
        throws MojoExecutionException
    {
        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        File outputFile = new File( target, finalName + ".sar" );

        File serviceJar = new File( target, finalName + ".jar" );

        // ----------------------------------------------------------------------
        // Build the service
        // ----------------------------------------------------------------------

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

        try
        {
            builder.build( serviceName, serviceAssemblyDirectory, serviceJar, remoteRepositories, localRepository,
                           projectArtifacts, serviceConfiguration, configurationsDirectory, interpolationProperties );

            // ----------------------------------------------------------------------
            // Bundle the service
            // ----------------------------------------------------------------------

            builder.bundle( outputFile, serviceAssemblyDirectory );
        }
        catch ( ServiceBuilderException e )
        {
            throw new MojoExecutionException( "Error while making service.", e );
        }

        project.getArtifact().setFile( outputFile );
    }
}
