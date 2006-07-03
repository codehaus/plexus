package org.codehaus.plexus.cdc;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.discovery.DefaultComponentDiscoverer;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileReader;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ComponentDescriptorCreatorTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        ComponentDescriptorCreator cdc = (ComponentDescriptorCreator) lookup( ComponentDescriptorCreator.ROLE );

        // ----------------------------------------------------------------------
        // Generate
        // ----------------------------------------------------------------------

        File[] sourceDirectories = new File[]{getTestFile( "src/test-project" ),};

        File outputDirectory = getTestFile( "target/cdc-output/META-INF/plexus" );

        if ( outputDirectory.exists() )
        {
            FileUtils.deleteDirectory( outputDirectory );
        }

        assertTrue( outputDirectory.mkdirs() );

        File outputFile = new File( outputDirectory, "components.xml" );

        cdc.processSources( sourceDirectories, outputFile );

        // ----------------------------------------------------------------------
        // Check the generated components.xml
        // ----------------------------------------------------------------------

        assertTrue( "Output file is missing: " + outputFile.getAbsolutePath(), outputFile.exists() );

//        System.err.println( FileUtils.fileRead( outputFile ) );

        DefaultComponentDiscoverer discoverer = new DefaultComponentDiscoverer();

        FileReader reader = new FileReader( outputFile );

        ComponentSetDescriptor csd = discoverer.createComponentDescriptors( reader, outputFile.getAbsolutePath() );

        assertEquals( 1, csd.getComponents().size() );

        ComponentDescriptor cd = (ComponentDescriptor) csd.getComponents().get( 0 );

        assertEquals( "My super component.", cd.getDescription() );

        assertEquals( "foo", cd.getAlias() );

        assertEquals( "bar", cd.getRoleHint() );

        assertEquals( "1.2", cd.getVersion() );
    }
}
