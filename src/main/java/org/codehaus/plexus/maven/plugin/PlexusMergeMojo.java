/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
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

package org.codehaus.plexus.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.model.Resource;
import org.codehaus.plexus.cdc.ComponentDescriptorCreator;
import org.codehaus.plexus.cdc.ComponentDescriptorCreatorException;
import org.codehaus.plexus.util.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.IOException;

/**
 * @goal merge-descriptors
 *
 * @phase process-resources
 *
 * @description Merges all Plexus descriptors.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: PlexusDescriptorMojo.java 2977 2006-01-05 21:05:18Z trygvis $
 */
public class PlexusMergeMojo
    extends AbstractMojo
{
    // ----------------------------------------------------------------------
    // Parameters
    // ----------------------------------------------------------------------

    /**
     * @parameter expression="${project.resources}"
     * @required
     */
    private List resources;

    /**
     * @parameter
     */
    private File[] descriptors;

    /**
     * @parameter expression="${project.build.outputDirectory}/META-INF/plexus/components.xml"
     * @required
     */
    private File output;

    // ----------------------------------------------------------------------
    // Components
    // ----------------------------------------------------------------------

    /**
     * @component
     */
    private ComponentDescriptorCreator cdc;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void execute()
        throws MojoExecutionException
    {
        // ----------------------------------------------------------------------
        // Locate files
        // ----------------------------------------------------------------------

        List files = new ArrayList();

        for ( Iterator it = resources.iterator(); it.hasNext(); )
        {
            Resource resource = (Resource) it.next();

            String includes = "META-INF/plexus/components.xml";

            String excludes = "";

            for ( Iterator j = resource.getExcludes().iterator(); j.hasNext(); )
            {
                String exclude = (String) j.next();
                excludes += exclude + ",";
            }

            try
            {
                File basedir = new File( resource.getDirectory() );

                getLog().debug( "Searching for component.xml files. Basedir: " + basedir.getAbsolutePath() + ", includes: " + includes + ", excludes=" + excludes );

                if ( !basedir.isDirectory() )
                {
                    getLog().debug( "Skipping, not a directory." );

                    continue;
                }

                List list = FileUtils.getFiles( basedir, includes, excludes );

                files.addAll( list );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error while scanning for component.xml files.", e );
            }
        }

        if ( descriptors != null )
        {
            files.addAll( Arrays.asList( descriptors ) );
        }

        // ----------------------------------------------------------------------
        // Merge the component set descriptors
        // ----------------------------------------------------------------------

        if ( files.isEmpty() )
        {
            getLog().debug( "Didn't find any files to merge." );

            return;
        }

        getLog().debug( "Found " + files.size() + " files to merge:" );

        for ( Iterator it = files.iterator(); it.hasNext(); )
        {
            File file = (File) it.next();

            getLog().debug( file.getAbsolutePath() );
        }

        try
        {
            cdc.mergeDescriptors( output, files );
        }
        catch ( ComponentDescriptorCreatorException e )
        {
            throw new MojoExecutionException( "Error while executing component descritor creator.", e );
        }
    }
}
