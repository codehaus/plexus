package org.codehaus.plexus.maven.plugin;

/*
 * Copyright (c) 2004-2005, Codehaus.org
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.cdc.ComponentDescriptorCreator;
import org.codehaus.plexus.cdc.ComponentDescriptorCreatorException;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

/**
 * @goal descriptor
 *
 * @phase process-sources
 *
 * @description Builds a Plexus descriptor.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusDescriptorMojo
    extends AbstractMojo
{
    // ----------------------------------------------------------------------
    // Parameters
    // ----------------------------------------------------------------------

    /**
     * @parameter expression="${project.compileSourceRoots}"
     * @required
     */
    private List sourceDirectories;

    /**
     * @parameter expression="${project.build.directory}/generated-resources/plexus/"
     * @required
     */
    private File outputDirectory;

    /**
     * @parameter expression="META-INF/plexus/components.xml"
     * @required
     */
    private String fileName;

    /**
     * Whether to generate a Plexus Container descriptor instead of a component descriptor.
     * @parameter default-value="false"
     * @required
     */
    private boolean containerDescriptor;

    /**
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject mavenProject;

    // ----------------------------------------------------------------------
    // Components
    // ----------------------------------------------------------------------

    /**
     * @component
     */
    private ComponentDescriptorCreator cdc;

    /**
     * @component
     */
    private MavenProjectHelper mavenProjectHelper;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void execute()
        throws MojoExecutionException
    {
        // ----------------------------------------------------------------------
        // Create the component set descriptor from the source files
        // ----------------------------------------------------------------------

        File[] sources = new File[ sourceDirectories.size() ];

        Iterator it = sourceDirectories.iterator();

        for ( int i = 0; i < sources.length; i++ )
        {
            sources[ i ] = new File( (String) it.next() );
        }

        try
        {
            cdc.processSources( sources, new File( outputDirectory, fileName ), containerDescriptor );
        }
        catch ( ComponentDescriptorCreatorException e )
        {
            throw new MojoExecutionException( "Error while executing component descritor creator.", e );
        }

        mavenProjectHelper.addResource( mavenProject, outputDirectory.getAbsolutePath(), Collections.EMPTY_LIST, Collections.EMPTY_LIST );
    }
}
