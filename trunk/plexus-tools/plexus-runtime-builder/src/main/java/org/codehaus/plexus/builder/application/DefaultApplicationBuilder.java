package org.codehaus.plexus.builder.application;

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

import org.codehaus.plexus.builder.AbstractBuilder;
import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilderException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DefaultApplicationBuilder
    extends AbstractBuilder
{
    private String applicationName;

    private File appLibDir;

    public void setApplicationName( String applicationName )
    {
        this.applicationName = applicationName;
    }

    private void checkApplicationConfiguration()
        throws ApplicationBuilderException
    {
        if ( applicationName == null || applicationName.trim().length() == 0 )
        {
            throw new ApplicationBuilderException( "The application name must be set." );
        }
    }

    public void build()
        throws ApplicationBuilderException
    {
        try
        {
            checkApplicationConfiguration();
        }
        catch ( ApplicationBuilderException e )
        {
            throw e;
        }
    }

    protected void createDirectoryStructure()
    {
        appLibDir = new File( baseDirectory, "lib" );

        mkdir( appLibDir );
    }

    // use the project to get the deps
    // put copyArtifact in the abstract class

    /*
    private void copyApplicationDependencies( MavenProject project )
        throws PlexusRuntimeBuilderException, IOException
    {
        Iterator it = artifacts.iterator();

        Artifact artifact = new DefaultArtifact( project.getGroupId(), project.getArtifactId(), project.getVersion(), project.getType() );

        try
        {
            artifact = artifactResolver.resolve( artifact, getRemoteRepositories(), getLocalRepository() );
        }
        catch ( ArtifactResolutionException ex )
        {
            throw new PlexusRuntimeBuilderException( "Error while resolving the project artifact.", ex );
        }

        copyArtifact( artifact, appLibDir );

        while ( it.hasNext() )
        {
            artifact = (Artifact) it.next();

            if ( isBootArtifact( artifact ) || isPlexusArtifact( artifact ) )
            {
                continue;
            }

            copyArtifact( artifact, appLibDir );
        }
    }
    */
}
