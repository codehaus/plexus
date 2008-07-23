package org.codehaus.plexus.maven.plugin.application;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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
import org.codehaus.plexus.maven.plugin.AbstractAppServerMojo;

import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 * @goal add-app
 * @requiresDependencyResolution
 * @phase package
 * @description Adds the Plexus appliction created from this build to a Plexus runtime. This is used
 * when your Plexus runtime is housing the Plexus application created from this build. You
 * typically create the Plexus runtime and the Plexus application in this build. If you are
 * creating a Plexus runtime that houses many applications then you will want to use the add-apps
 * goal.
 */
public class ApplicationRuntimePopulatorMojo
    extends AbstractAppServerMojo
{
    public void execute()
        throws MojoExecutionException
    {
        File applicationJarFile = new File( target, finalName + ".jar" );

        if ( !applicationJarFile.canRead() )
        {
            throw new MojoExecutionException(
                "Can't read Plexus application artifact '" + applicationJarFile.getAbsolutePath() + "'." );
        }

        try
        {
            runtimeBuilder.addPlexusApplication( applicationJarFile, runtimePath );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error while building test runtimePath.", e );
        }
    }
}
