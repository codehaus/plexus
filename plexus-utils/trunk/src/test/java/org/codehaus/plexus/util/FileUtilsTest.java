package org.codehaus.plexus.util;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

/**
 * This is used to test FileUtils for correctness.
 *
 * @author Peter Donald
 * @author Matthew Hawthorne
 * @version $Id$
 * @see FileUtils
 */
public final class FileUtilsTest
    extends FileBasedTestCase
{
    // Test data

    /**
     * Size of test directory.
     */
    private static final int TEST_DIRECTORY_SIZE = 0;

    private final File testFile1;

    private final File testFile2;

    private static int testFile1Size;

    private static int testFile2Size;

    public FileUtilsTest()
        throws Exception
    {
        testFile1 = new File( getTestDirectory(), "file1-test.txt" );
        testFile2 = new File( getTestDirectory(), "file1a-test.txt" );

        testFile1Size = (int) testFile1.length();
        testFile2Size = (int) testFile2.length();
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        getTestDirectory().mkdirs();
        createFile( testFile1, testFile1Size );
        createFile( testFile2, testFile2Size );
        FileUtils.deleteDirectory( getTestDirectory() );
        getTestDirectory().mkdirs();
        createFile( testFile1, testFile1Size );
        createFile( testFile2, testFile2Size );
    }

    // byteCountToDisplaySize

    public void testByteCountToDisplaySize()
    {
        assertEquals( FileUtils.byteCountToDisplaySize( 0 ), "0 bytes" );
        assertEquals( FileUtils.byteCountToDisplaySize( 1024 ), "1 KB" );
        assertEquals( FileUtils.byteCountToDisplaySize( 1024 * 1024 ), "1 MB" );
        assertEquals( FileUtils.byteCountToDisplaySize( 1024 * 1024 * 1024 ), "1 GB" );
    }

    // waitFor

    public void testWaitFor()
    {
        FileUtils.waitFor( "", -1 );

        FileUtils.waitFor( "", 2 );
    }

    // Hacked to sanity by Trygve
    public void testToURLs()
        throws Exception
    {
        File[] files = new File[]{new File( "file1" ), new File( "file2" ),};

        URL[] urls = FileUtils.toURLs( files );

        assertEquals( "The length of the generated URL's is not equals to the length of files. " + "Was " + files
            .length + ", expected " + urls.length, files.length, urls.length );

        for ( int i = 0; i < urls.length; i++ )
        {
            assertEquals( files[i].toURL(), urls[i] );
        }
    }

    public void testGetFilesFromExtension()
    {
        // TODO I'm not sure what is supposed to happen here
        FileUtils.getFilesFromExtension( "dir", null );

        // Non-existent files
        final String[] emptyFileNames =
            FileUtils.getFilesFromExtension( getTestDirectory().getAbsolutePath(), new String[]{"java"} );
        assertTrue( emptyFileNames.length == 0 );

        // Existing files
        // TODO Figure out how to test this
        /*
        final String[] fileNames =
            FileUtils.getFilesFromExtension(
                getClass().getResource("/java/util/").getFile(),
                new String[] { "class" });
        assertTrue(fileNames.length > 0);
        */
    }

    // mkdir

    public void testMkdir()
    {
        final File dir = new File( getTestDirectory(), "testdir" );
        FileUtils.mkdir( dir.getAbsolutePath() );
        dir.deleteOnExit();
    }

    // contentEquals

    public void testContentEquals()
        throws Exception
    {
        // Non-existent files
        final File file = new File( getTestDirectory(), getName() );
        assertTrue( FileUtils.contentEquals( file, file ) );

        // TODO Should comparing 2 directories throw an Exception instead of returning false?
        // Directories
        assertTrue( !FileUtils.contentEquals( getTestDirectory(), getTestDirectory() ) );

        // Different files
        final File objFile1 = new File( getTestDirectory(), getName() + ".object" );
        objFile1.deleteOnExit();
        FileUtils.copyURLToFile( getClass().getResource( "/java/lang/Object.class" ), objFile1 );

        final File objFile2 = new File( getTestDirectory(), getName() + ".collection" );
        objFile2.deleteOnExit();
        FileUtils.copyURLToFile( getClass().getResource( "/java/util/Collection.class" ), objFile2 );

        assertTrue( "Files should not be equal.", !FileUtils.contentEquals( objFile1, objFile2 ) );

        // Equal files
        file.createNewFile();
        assertTrue( FileUtils.contentEquals( file, file ) );
    }

    // removePath

    public void testRemovePath()
    {
        final String fileName = FileUtils.removePath( new File( getTestDirectory(), getName() ).getAbsolutePath() );
        assertEquals( getName(), fileName );
    }

    // getPath

    public void testGetPath()
    {
        final String fileName = FileUtils.getPath( new File( getTestDirectory(), getName() ).getAbsolutePath() );
        assertEquals( getTestDirectory().getAbsolutePath(), fileName );
    }

    // copyURLToFile

    public void testCopyURLToFile()
        throws Exception
    {
        // Creates file
        final File file = new File( getTestDirectory(), getName() );
        file.deleteOnExit();

        // Loads resource
        final String resourceName = "/java/lang/Object.class";
        FileUtils.copyURLToFile( getClass().getResource( resourceName ), file );

        // Tests that resuorce was copied correctly
        final FileInputStream fis = new FileInputStream( file );
        try
        {
            assertTrue( "Content is not equal.",
                        IOUtil.contentEquals( getClass().getResourceAsStream( resourceName ), fis ) );
        }
        finally
        {
            fis.close();
        }
    }

    // catPath

    public void testCatPath()
    {
        // TODO StringIndexOutOfBoundsException thrown if file doesn't contain slash.
        // Is this acceptable?
        //assertEquals("", FileUtils.catPath("a", "b"));

        assertEquals( "/a/c", FileUtils.catPath( "/a/b", "c" ) );
        assertEquals( "/a/d", FileUtils.catPath( "/a/b/c", "../d" ) );
    }

    // forceMkdir

    public void testForceMkdir()
        throws Exception
    {
        // Tests with existing directory
        FileUtils.forceMkdir( getTestDirectory() );

        // Creates test file
        final File testFile = new File( getTestDirectory(), getName() );
        testFile.deleteOnExit();
        testFile.createNewFile();
        assertTrue( "Test file does not exist.", testFile.exists() );

        // Tests with existing file
        try
        {
            FileUtils.forceMkdir( testFile );
            fail( "Exception expected." );
        }
        catch ( IOException ex )
        {
        }

        testFile.delete();

        // Tests with non-existent directory
        FileUtils.forceMkdir( testFile );
        assertTrue( "Directory was not created.", testFile.exists() );
    }

    // sizeOfDirectory

    public void testSizeOfDirectory()
        throws Exception
    {
        final File file = new File( getTestDirectory(), getName() );

        // Non-existent file
        try
        {
            FileUtils.sizeOfDirectory( file );
            fail( "Exception expected." );
        }
        catch ( IllegalArgumentException ex )
        {
        }

        // Creates file
        file.createNewFile();
        file.deleteOnExit();

        // Existing file
        try
        {
            FileUtils.sizeOfDirectory( file );
            fail( "Exception expected." );
        }
        catch ( IllegalArgumentException ex )
        {
        }

        // Existing directory
        file.delete();
        file.mkdir();

        assertEquals( "Unexpected directory size", TEST_DIRECTORY_SIZE, FileUtils.sizeOfDirectory( file ) );
    }

    // isFileNewer

    // TODO Finish test

    public void XtestIsFileNewer()
    {
    }

    // copyFile
    public void testCopyFile1()
        throws Exception
    {
        final File destination = new File( getTestDirectory(), "copy1.txt" );
        FileUtils.copyFile( testFile1, destination );
        assertTrue( "Check Exist", destination.exists() );
        assertTrue( "Check Full copy", destination.length() == testFile1Size );
    }

    public void testCopyFile2()
        throws Exception
    {
        final File destination = new File( getTestDirectory(), "copy2.txt" );
        FileUtils.copyFile( testFile1, destination );
        assertTrue( "Check Exist", destination.exists() );
        assertTrue( "Check Full copy", destination.length() == testFile2Size );
    }

    // copyFileIfModified

    public void testCopyIfModifiedWhenSourceIsNewer()
        throws Exception
    {
        FileUtils.forceMkdir( new File( getTestDirectory() + "/temp" ) );

        // Place destination
        File destination = new File( getTestDirectory() + "/temp/copy1.txt" );
        FileUtils.copyFile( testFile1, destination );

        // Make sure source is newer
        Thread.sleep( 1000 );

        // Place source
        File source = new File( getTestDirectory(), "copy1.txt" );
        FileUtils.copyFile( testFile1, source );

        // Copy will occur when source is newer
        assertTrue( "Failed copy. Target file should have been updated.",
                    FileUtils.copyFileIfModified( source, destination ) );
    }

    public void testCopyIfModifiedWhenSourceIsOlder()
        throws Exception
    {
        FileUtils.forceMkdir( new File( getTestDirectory() + "/temp" ) );

        // Place source
        File source = new File( getTestDirectory() + "copy1.txt" );
        FileUtils.copyFile( testFile1, source );

        // Make sure desintation is newer
        Thread.sleep( 1000 );

        // Place destination
        File desintation = new File( getTestDirectory(), "/temp/copy1.txt" );
        FileUtils.copyFile( testFile1, desintation );

        // Copy will occur when desintation is newer
        assertFalse( "Source file should not have been copied.", FileUtils.copyFileIfModified( source, desintation ) );
    }

    // forceDelete

    public void testForceDeleteAFile1()
        throws Exception
    {
        final File destination = new File( getTestDirectory(), "copy1.txt" );
        destination.createNewFile();
        assertTrue( "Copy1.txt doesn't exist to delete", destination.exists() );
        FileUtils.forceDelete( destination );
        assertTrue( "Check No Exist", !destination.exists() );
    }

    public void testForceDeleteAFile2()
        throws Exception
    {
        final File destination = new File( getTestDirectory(), "copy2.txt" );
        destination.createNewFile();
        assertTrue( "Copy2.txt doesn't exist to delete", destination.exists() );
        FileUtils.forceDelete( destination );
        assertTrue( "Check No Exist", !destination.exists() );
    }

    // copyFileToDirectory

    public void testCopyFile1ToDir()
        throws Exception
    {
        final File directory = new File( getTestDirectory(), "subdir" );
        if ( !directory.exists() )
        {
            directory.mkdirs();
        }
        final File destination = new File( directory, testFile1.getName() );
        FileUtils.copyFileToDirectory( testFile1, directory );
        assertTrue( "Check Exist", destination.exists() );
        assertTrue( "Check Full copy", destination.length() == testFile1Size );
    }

    public void testCopyFile2ToDir()
        throws Exception
    {
        final File directory = new File( getTestDirectory(), "subdir" );
        if ( !directory.exists() )
        {
            directory.mkdirs();
        }
        final File destination = new File( directory, testFile1.getName() );
        FileUtils.copyFileToDirectory( testFile1, directory );
        assertTrue( "Check Exist", destination.exists() );
        assertTrue( "Check Full copy", destination.length() == testFile2Size );
    }

    // copyFileToDirectoryIfModified

    public void testCopyFile1ToDirIfModified()
        throws Exception
    {
        final File directory = new File( getTestDirectory(), "subdir" );
        if ( directory.exists() )
        {
            FileUtils.forceDelete( directory );
        }
        directory.mkdirs();

        final File destination = new File( directory, testFile1.getName() );

        FileUtils.copyFileToDirectoryIfModified( testFile1, directory );

        final File target = new File( getTestDirectory() + "/subdir", testFile1.getName() );
        long timestamp = target.lastModified();

        assertTrue( "Check Exist", destination.exists() );
        assertTrue( "Check Full copy", destination.length() == testFile1Size );

        FileUtils.copyFileToDirectoryIfModified( testFile1, directory );

        assertTrue( "Timestamp was changed", timestamp == target.lastModified() );
    }

    public void testCopyFile2ToDirIfModified()
        throws Exception
    {
        final File directory = new File( getTestDirectory(), "subdir" );
        if ( directory.exists() )
        {
            FileUtils.forceDelete( directory );
        }
        directory.mkdirs();

        final File destination = new File( directory, testFile2.getName() );

        FileUtils.copyFileToDirectoryIfModified( testFile2, directory );

        final File target = new File( getTestDirectory() + "/subdir", testFile2.getName() );
        long timestamp = target.lastModified();

        assertTrue( "Check Exist", destination.exists() );
        assertTrue( "Check Full copy", destination.length() == testFile2Size );

        FileUtils.copyFileToDirectoryIfModified( testFile2, directory );

        assertTrue( "Timestamp was changed", timestamp == target.lastModified() );
    }

    // forceDelete

    public void testForceDeleteDir()
        throws Exception
    {
        FileUtils.forceDelete( getTestDirectory().getParentFile() );
        assertTrue( "Check No Exist", !getTestDirectory().getParentFile().exists() );
    }

    // resolveFile

    public void testResolveFileDotDot()
        throws Exception
    {
        final File file = FileUtils.resolveFile( getTestDirectory(), ".." );
        assertEquals( "Check .. operator", file, getTestDirectory().getParentFile() );
    }

    public void testResolveFileDot()
        throws Exception
    {
        final File file = FileUtils.resolveFile( getTestDirectory(), "." );
        assertEquals( "Check . operator", file, getTestDirectory() );
    }

    // normalize

    public void testNormalize()
        throws Exception
    {
        final String[] src = {"", "/", "///", "/foo", "/foo//", "/./", "/foo/./", "/foo/./bar", "/foo/../bar",
            "/foo/../bar/../baz", "/foo/bar/../../baz", "/././", "/foo/./../bar", "/foo/.././bar/", "//foo//./bar",
            "/../", "/foo/../../"};

        final String[] dest = {"", "/", "/", "/foo", "/foo/", "/", "/foo/", "/foo/bar", "/bar", "/baz", "/baz", "/",
            "/bar", "/bar/", "/foo/bar", null, null};

        assertEquals( "Oops, test writer goofed", src.length, dest.length );

        for ( int i = 0; i < src.length; i++ )
        {
            assertEquals( "Check if '" + src[i] + "' normalized to '" + dest[i] + "'", dest[i],
                          FileUtils.normalize( src[i] ) );
        }
    }

    private String replaceAll( String text, String lookFor, String replaceWith )
    {
        StringBuffer sb = new StringBuffer( text );
        while ( true )
        {
            int idx = sb.indexOf( lookFor );
            if ( idx < 0 )
            {
                break;
            }
            sb.replace( idx, idx + lookFor.length(), replaceWith );
        }
        return sb.toString();
    }

    /**
     * Test the FileUtils implementation.
     */
    // Used to exist as IOTestCase class
    public void testFileUtils()
        throws Exception
    {
        // Loads file from classpath
        final String path = "/test.txt";
        final URL url = this.getClass().getResource( path );
        assertNotNull( path + " was not found.", url );

        String filename = url.getFile();
        //The following line applies a fix for spaces in a path
        filename = replaceAll( filename, "%20", " " );
        final String filename2 = "test2.txt";

        assertTrue( "test.txt extension == \"txt\"", FileUtils.getExtension( filename ).equals( "txt" ) );

        assertTrue( "Test file does not exist: " + filename, FileUtils.fileExists( filename ) );

        assertTrue( "Second test file does not exist", !FileUtils.fileExists( filename2 ) );

        FileUtils.fileWrite( filename2, filename );
        assertTrue( "Second file was written", FileUtils.fileExists( filename2 ) );

        final String file2contents = FileUtils.fileRead( filename2 );
        assertTrue( "Second file's contents correct", FileUtils.fileRead( filename2 ).equals( file2contents ) );

        FileUtils.fileAppend( filename2, filename );
        assertTrue( "Second file's contents correct",
                    FileUtils.fileRead( filename2 ).equals( file2contents + file2contents ) );

        FileUtils.fileDelete( filename2 );
        assertTrue( "Second test file does not exist", !FileUtils.fileExists( filename2 ) );

        final String contents = FileUtils.fileRead( filename );
        assertTrue( "FileUtils.fileRead()", contents.equals( "This is a test" ) );

    }

    public void testGetExtension()
    {
        final String[][] tests =
            {{"filename.ext", "ext"}, {"README", ""}, {"domain.dot.com", "com"}, {"image.jpeg", "jpeg"}};
        for ( int i = 0; i < tests.length; i++ )
        {
            assertEquals( tests[i][1], FileUtils.getExtension( tests[i][0] ) );
            //assertEquals(tests[i][1], FileUtils.extension(tests[i][0]));
        }
    }

    /* TODO: Reenable this test */
    public void DISABLED__testGetExtensionWithPaths()
    {
        final String[][] testsWithPaths = {{"/tmp/foo/filename.ext", "ext"}, {"C:\\temp\\foo\\filename.ext", "ext"},
            {"/tmp/foo.bar/filename.ext", "ext"}, {"C:\\temp\\foo.bar\\filename.ext", "ext"},
            {"/tmp/foo.bar/README", ""}, {"C:\\temp\\foo.bar\\README", ""}, {"../filename.ext", "ext"}};
        for ( int i = 0; i < testsWithPaths.length; i++ )
        {
            assertEquals( testsWithPaths[i][1], FileUtils.getExtension( testsWithPaths[i][0] ) );
            //assertEquals(testsWithPaths[i][1], FileUtils.extension(testsWithPaths[i][0]));
        }
    }

    public void testRemoveExtension()
    {
        final String[][] tests = {{"filename.ext", "filename"}, {"first.second.third.ext", "first.second.third"},
            {"README", "README"}, {"domain.dot.com", "domain.dot"}, {"image.jpeg", "image"}};

        for ( int i = 0; i < tests.length; i++ )
        {
            assertEquals( tests[i][1], FileUtils.removeExtension( tests[i][0] ) );
            //assertEquals(tests[i][1], FileUtils.basename(tests[i][0]));
        }
    }

    /* TODO: Reenable this test */
    public void DISABLED__testRemoveExtensionWithPaths()
    {
        final String[][] testsWithPaths = {{"/tmp/foo/filename.ext", "filename"},
            {"C:\\temp\\foo\\filename.ext", "filename"}, {"/tmp/foo.bar/filename.ext", "filename"},
            {"C:\\temp\\foo.bar\\filename.ext", "filename"}, {"/tmp/foo.bar/README", "README"},
            {"C:\\temp\\foo.bar\\README", "README"}, {"../filename.ext", "filename"}};

        for ( int i = 0; i < testsWithPaths.length; i++ )
        {
            assertEquals( testsWithPaths[i][1], FileUtils.removeExtension( testsWithPaths[i][0] ) );
            //assertEquals(testsWithPaths[i][1], FileUtils.basename(testsWithPaths[i][0]));
        }
    }

    public void testCopyDirectoryStructureWithAEmptyDirectoryStruture()
        throws Exception
    {
        File from = new File( getTestDirectory(), "from" );

        FileUtils.deleteDirectory( from );

        assertTrue( from.mkdirs() );

        File to = new File( getTestDirectory(), "to" );

        assertTrue( to.mkdirs() );

        FileUtils.copyDirectoryStructure( from, to );
    }

    public void testCopyDirectoryStructureWithAPopulatedStructure()
        throws Exception
    {
        // Make a structure to copy
        File from = new File( getTestDirectory(), "from" );

        FileUtils.deleteDirectory( from );

        File fRoot = new File( from, "root.txt" );

        File d1 = new File( from, "1" );

        File d1_1 = new File( d1, "1_1" );

        File d2 = new File( from, "2" );

        File f2 = new File( d2, "2.txt" );

        File d2_1 = new File( d2, "2_1" );

        File f2_1 = new File( d2_1, "2_1.txt" );

        assertTrue( from.mkdir() );

        assertTrue( d1.mkdir() );

        assertTrue( d1_1.mkdir() );

        assertTrue( d2.mkdir() );

        assertTrue( d2_1.mkdir() );

        createFile( fRoot, 100 );

        createFile( f2, 100 );

        createFile( f2_1, 100 );

        File to = new File( getTestDirectory(), "to" );

        assertTrue( to.mkdirs() );

        FileUtils.copyDirectoryStructure( from, to );

        checkFile( fRoot, new File( to, "root.txt" ) );

        assertIsDirectory( new File( to, "1" ) );

        assertIsDirectory( new File( to, "1/1_1" ) );

        assertIsDirectory( new File( to, "2" ) );

        assertIsDirectory( new File( to, "2/2_1" ) );

        checkFile( f2, new File( to, "2/2.txt" ) );

        checkFile( f2_1, new File( to, "2/2_1/2_1.txt" ) );
    }

    public void testCopyDirectoryStructureIfModified()
        throws Exception
    {
        // Make a structure to copy
        File from = new File( getTestDirectory(), "from" );

        FileUtils.deleteDirectory( from );

        File fRoot = new File( from, "root.txt" );

        File d1 = new File( from, "1" );

        File d1_1 = new File( d1, "1_1" );

        File d2 = new File( from, "2" );

        File f2 = new File( d2, "2.txt" );

        File d2_1 = new File( d2, "2_1" );

        File f2_1 = new File( d2_1, "2_1.txt" );

        assertTrue( from.mkdir() );

        assertTrue( d1.mkdir() );

        assertTrue( d1_1.mkdir() );

        assertTrue( d2.mkdir() );

        assertTrue( d2_1.mkdir() );

        createFile( fRoot, 100 );

        createFile( f2, 100 );

        createFile( f2_1, 100 );

        File to = new File( getTestDirectory(), "to" );

        assertTrue( to.mkdirs() );

        FileUtils.copyDirectoryStructureIfModified( from, to );

        File files[] = {new File( to, "root.txt" ), new File( to, "2/2.txt" ), new File( to, "2/2_1/2_1.txt" )};

        long timestamps[] = {files[0].lastModified(), files[1].lastModified(), files[2].lastModified()};

        checkFile( fRoot, files[0] );

        assertIsDirectory( new File( to, "1" ) );

        assertIsDirectory( new File( to, "1/1_1" ) );

        assertIsDirectory( new File( to, "2" ) );

        assertIsDirectory( new File( to, "2/2_1" ) );

        checkFile( f2, files[1] );

        checkFile( f2_1, files[2] );

        FileUtils.copyDirectoryStructureIfModified( from, to );

        assertTrue( "Unmodified file was overwritten", timestamps[0] == files[0].lastModified() );
        assertTrue( "Unmodified file was overwritten", timestamps[1] == files[1].lastModified() );
        assertTrue( "Unmodified file was overwritten", timestamps[2] == files[2].lastModified() );

        files[1].setLastModified( f2.lastModified() - 5000L );
        timestamps[1] = files[1].lastModified();

        FileUtils.copyDirectoryStructureIfModified( from, to );

        assertTrue( "Unmodified file was overwritten", timestamps[0] == files[0].lastModified() );
        assertTrue( "Outdated file was not overwritten", timestamps[1] < files[1].lastModified() );
        assertTrue( "Unmodified file was overwritten", timestamps[2] == files[2].lastModified() );

    }

    public void testCopyDirectoryStructureToSelf()
        throws Exception
    {
        // Make a structure to copy
        File toFrom = new File( getTestDirectory(), "tofrom" );

        FileUtils.deleteDirectory( toFrom );

        File fRoot = new File( toFrom, "root.txt" );

        File dSub = new File( toFrom, "subdir" );

        File f1 = new File( dSub, "notempty.txt" );

        File dSubSub = new File( dSub, "subsubdir" );

        File f2 = new File( dSubSub, "notemptytoo.txt" );

        assertTrue( toFrom.mkdir() );

        assertTrue( dSub.mkdir() );

        assertTrue( dSubSub.mkdir() );

        createFile( fRoot, 100 );

        createFile( f1, 100 );

        createFile( f2, 100 );

        try
        {
            FileUtils.copyDirectoryStructure( toFrom, toFrom );
            fail( "An exception must be thrown." );
        }
        catch ( IOException e )
        {
        }
    }

    public void testFilteredFileCopy()
        throws Exception
    {
        File compareFile = new File( getTestDirectory(), "compare.txt" );
        OutputStream compareStream = new FileOutputStream( compareFile );
        compareStream.write( "This is a test.  Test sample text\n".getBytes() );
        compareStream.flush();

        File destFile = new File( getTestDirectory(), "target.txt" );

        final Properties filterProperties = new Properties();
        filterProperties.setProperty( "s", "sample text" );

        // test ${token}
        FileUtils.FilterWrapper[] wrappers1 = new FileUtils.FilterWrapper[]{new FileUtils.FilterWrapper()
        {
            public Reader getReader( Reader reader )
            {
                return new InterpolationFilterReader( reader, filterProperties, "${", "}" );
            }
        }};

        File srcFile = new File( getTestDirectory(), "root.txt" );
        OutputStream os = new FileOutputStream( srcFile );
        os.write( ( "This is a test.  Test ${s}\n" ).getBytes() );
        os.flush();

        FileUtils.copyFile( srcFile, destFile, System.getProperty( "file.encoding" ), wrappers1 );
        assertTrue( "Files should be equal.", FileUtils.contentEquals( compareFile, destFile ) );

        srcFile.delete();
        destFile.delete();
        compareFile.delete();
    }

}
