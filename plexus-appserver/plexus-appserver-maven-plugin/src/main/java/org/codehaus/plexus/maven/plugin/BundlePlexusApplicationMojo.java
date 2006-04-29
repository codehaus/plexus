package org.codehaus.plexus.maven.plugin;

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

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.builder.application.ApplicationBuilder;
import org.codehaus.plexus.builder.application.ApplicationBuilderException;

/**
 * @goal bundle-application
 *
 * @requiresDependencyResolution
 *
 * @description Packages the Plexus appserver into a redistributable jar file.
 *
 * @phase package
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class BundlePlexusApplicationMojo
    extends AbstractMojo
{
    /**
     * @parameter expression="${basedir}"
     *
     * @required
     */
    private File basedir;

    /**
     * @parameter expression="${project}"
     *
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${project.build.directory}"
     *
     * @required
     */
    private File target;

    /**
     * @parameter expression="${project.build.finalName}"
     *
     * @required
     */
    private String finalName;

    /**
     * @parameter expression="${component.org.codehaus.plexus.builder.application.ApplicationBuilder}"
     *
     * @required
     */
    private ApplicationBuilder builder;

    /**
     * @parameter expression="${project.build.directory}/plexus-appserver"
     *
     * @required
     */
    private File applicationAssemblyDirectory;

    public void execute()
        throws MojoExecutionException
    {
        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        try
        {
            File outputFile = new File( target, finalName + ".jar" );
            builder.bundle( outputFile, applicationAssemblyDirectory );

            // TODO: m2 needs a better way to deal with this
            project.getArtifact().setFile( outputFile );
        }
        catch ( ApplicationBuilderException e )
        {
            throw new MojoExecutionException( "Error while bundling appserver.", e );
        }
    }
}
