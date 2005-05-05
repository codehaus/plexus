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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder;
import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilderException;

/**
 * @goal bundle-runtime
 *
 * @requiresDependencyResolution
 *
 * @description Packages the runtime into a redistributable jar file.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusBundleRuntimeMojo
    extends AbstractMojo
{
    /**
     * @parameter expression="${basedir}"
     *
     * @required
     */
    private String basedir;

    /**
     * @parameter expression="${project.build.finalName}"
     *
     * @required
     */
    private String finalName;

    /**
     * @parameter expression="${component.org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilder}"
     *
     * @required
     */
    private PlexusRuntimeBuilder builder;

    /**
     * @parameter expression="${project}"
     *
     * @required
     */
    private MavenProject project;

    public void execute()
        throws MojoExecutionException
    {
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

        File runtimeDirectory = new File( workingBasedir, "plexus-runtime" );

        File outputFile = new File( workingBasedir, finalName + "-runtime.jar" );

        try
        {
            builder.bundle( outputFile, runtimeDirectory );
        }
        catch ( PlexusRuntimeBuilderException e )
        {
            throw new MojoExecutionException( "Error while bundling runtime.", e );
        }
    }
}
