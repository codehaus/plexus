package org.codehaus.plexus.maven.plugin;

/*
 * The MIT License
 *
 * Copyright (c) 2004-2006, The Codehaus
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
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.cdc.ComponentDescriptorCreator;
import org.codehaus.plexus.cdc.ComponentDescriptorCreatorException;
import org.codehaus.plexus.component.repository.cdc.ComponentDescriptor;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @since 1.3.4
 */
public abstract class AbstractDescriptorMojo
    extends AbstractMojo
{
    // -----------------------------------------------------------------------
    // Parameters
    // -----------------------------------------------------------------------

    /**
     * @parameter expression="META-INF/plexus/components.xml"
     * @required
     */
    private String fileName;

    /**
     * Whether to generate a Plexus Container descriptor instead of a component descriptor.
     *
     * @parameter default-value="false"
     * @required
     */
    private boolean containerDescriptor;

    /**
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject mavenProject;

    /**
     * @parameter
     */
    private ComponentDescriptor[] roleDefaults;

    /**
     * @component
     */
    private ComponentDescriptorCreator cdc;

    /**
     * @component
     */
    private MavenProjectHelper mavenProjectHelper;

    protected MavenProject getMavenProject()
    {
        return mavenProject;
    }

    protected MavenProjectHelper getMavenProjectHelper()
    {
        return mavenProjectHelper;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /**
     * Create the component set descriptor from the source files.
     *
     * @param sourceDirectories
     * @param outputDirectory
     * @throws MojoExecutionException
     */
    protected void generateDescriptor( List sourceDirectories, File outputDirectory )
        throws MojoExecutionException
    {
        File[] sources = new File[sourceDirectories.size()];

        Iterator it = sourceDirectories.iterator();

        for ( int i = 0; i < sources.length; i++ )
        {
            sources[i] = new File( (String) it.next() );
        }

        try
        {
            cdc.processSources( sources, new File( outputDirectory, fileName ), containerDescriptor, roleDefaults );
        }
        catch ( ComponentDescriptorCreatorException e )
        {
            throw new MojoExecutionException( "Error while executing component descritor creator.", e );
        }
    }
}
