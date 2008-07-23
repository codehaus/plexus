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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilderException;
import org.codehaus.plexus.maven.plugin.AbstractAppServerMojo;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 * @goal assemble-runtime
 * @requiresDependencyResolution
 * @phase package
 * @description Builds plexus containers.
 */
public class RuntimeAssemblerMojo
    extends AbstractAppServerMojo
{
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

    /**
     * This is a property file that's placed in conf/ and is used at runtime to interpolate the conf/plexus.xml.
     *
     * @parameter expression="${runtimeContextProperties}"
     * @required
     */
    private File runtimeContextProperties;

    /**
     * @parameter expression="${additionalCoreArtifacts}"
     */
    private HashSet additionalCoreArtifacts;

    /**
     * @parameter expression="${addManagementAgent}"
     */
    private boolean addManagementAgent;

    /**
     * @parameter expression="${managementArtifacts}"
     */
    private HashSet managementArtifacts;

    public void execute()
        throws MojoExecutionException
    {
        Properties interpolationProperties = new Properties();

        if ( runtimeConfigurationProperties != null )
        {
            try
            {
                interpolationProperties.load( new FileInputStream( runtimeConfigurationProperties ) );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Cannot load configuration properties file.", e );
            }
        }

        interpolationProperties.putAll( project.getProperties() );

        try
        {
            if ( addManagementAgent && managementArtifacts != null && managementArtifacts.size() > 0 )
            {
                runtimeBuilder.build(
                    runtimePath,
                    remoteRepositories,
                    localRepository,
                    projectArtifacts,
                    additionalCoreArtifacts,
                    runtimeConfiguration,
                    runtimeContextProperties,
                    interpolationProperties,
                    addManagementAgent,
                    managementArtifacts );
            }
            else
            {
                runtimeBuilder.build(
                    runtimePath,
                    remoteRepositories,
                    localRepository,
                    projectArtifacts,
                    additionalCoreArtifacts,
                    runtimeConfiguration,
                    runtimeContextProperties,
                    interpolationProperties,
                    addManagementAgent );
            }
        }
        catch ( PlexusRuntimeBuilderException e )
        {
            throw new MojoExecutionException( "Error while building runtime.", e );
        }
    }
}
