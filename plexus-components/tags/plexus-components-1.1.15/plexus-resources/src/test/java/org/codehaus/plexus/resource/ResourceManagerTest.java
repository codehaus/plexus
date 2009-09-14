package org.codehaus.plexus.resource;

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
import java.io.InputStream;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ResourceManagerTest
    extends PlexusTestCase
{

    public void testResourceManagerRetrievingInputStreams()
        throws Exception
    {
        ResourceManager resourceManager = (ResourceManager) lookup( ResourceManager.ROLE );

        InputStream in;

        File absoluteFile = new File( getBasedir(), "src/test/file-resources/dir/file.txt" ).getAbsoluteFile();
        assertTrue( absoluteFile.isFile() );
        assertTrue( absoluteFile.isAbsolute() );
        in = resourceManager.getResourceAsInputStream( absoluteFile.getAbsolutePath() );
        assertEquals( "file.txt", IOUtil.toString( in, "UTF-8" ) );

        in = resourceManager.getResourceAsInputStream( "/dir/file.txt" );
        assertEquals( "file.txt", IOUtil.toString( in, "UTF-8" ) );

        in = resourceManager.getResourceAsInputStream( "dir/file.txt" );
        assertEquals( "file.txt", IOUtil.toString( in, "UTF-8" ) );

        in = resourceManager.getResourceAsInputStream( "/dir/classpath.txt" );
        assertEquals( "classpath.txt", IOUtil.toString( in, "UTF-8" ) );

        in = resourceManager.getResourceAsInputStream( "dir/classpath.txt" );
        assertEquals( "classpath.txt", IOUtil.toString( in, "UTF-8" ) );
    }

    public void testResourceManagerRetrievingFiles()
        throws Exception
    {
        ResourceManager resourceManager = (ResourceManager) lookup( ResourceManager.ROLE );

        File f;

        File absoluteFile = new File( getBasedir(), "src/test/file-resources/dir/file.txt" ).getAbsoluteFile();
        assertTrue( absoluteFile.isFile() );
        assertTrue( absoluteFile.isAbsolute() );
        f = resourceManager.getResourceAsFile( absoluteFile.getAbsolutePath() );
        assertEquals( "file.txt", FileUtils.fileRead( f, "UTF-8" ) );

        f = resourceManager.getResourceAsFile( "/dir/file.txt" );
        assertEquals( "file.txt", FileUtils.fileRead( f, "UTF-8" ) );

        f = resourceManager.getResourceAsFile( "dir/file.txt" );
        assertEquals( "file.txt", FileUtils.fileRead( f, "UTF-8" ) );

        f = resourceManager.getResourceAsFile( "/dir/classpath.txt" );
        assertEquals( "classpath.txt", FileUtils.fileRead( f, "UTF-8" ) );

        f = resourceManager.getResourceAsFile( "dir/classpath.txt" );
        assertEquals( "classpath.txt", FileUtils.fileRead( f, "UTF-8" ) );
    }

    public void testResourceManagerRetrievingFilesToSpecificLocation()
        throws Exception
    {
        File outDir = new File( getBasedir(), "target/test/unit/output-directory" );

        ResourceManager resourceManager = (ResourceManager) lookup( ResourceManager.ROLE );

        resourceManager.setOutputDirectory( outDir );

        File ef = new File( outDir, "test/f.txt" );
        FileUtils.forceDelete( ef );
        assertFalse( ef.exists() );
        File f = resourceManager.getResourceAsFile( "dir/file.txt", "test/f.txt" );
        assertEquals( "file.txt", FileUtils.fileRead( f, "UTF-8" ) );
        assertEquals( ef, f );

        File ec = new File( outDir, "test/c.txt" );
        FileUtils.forceDelete( ec );
        assertFalse( ec.exists() );
        File c = resourceManager.getResourceAsFile( "dir/classpath.txt", "test/c.txt" );
        assertEquals( "classpath.txt", FileUtils.fileRead( c, "UTF-8" ) );
        assertEquals( ec, c );
    }

}
