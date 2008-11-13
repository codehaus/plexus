package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributeUtils;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

public class TarFileAttributesTest
    extends PlexusTestCase
{

    private final List toDelete = new ArrayList();
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        System.out.println( "Octal 0660 is decimal " + 0660 );
        System.out.println( "Octal 0644 is decimal " + 0644 );
        System.out.println( "Octal 0440 is decimal " + 0440 );
    }

    public void tearDown()
        throws Exception
    {
        super.tearDown();
        
        if ( !toDelete.isEmpty() )
        {
            for ( Iterator it = toDelete.iterator(); it.hasNext(); )
            {
                File f = (File) it.next();
                System.out.println( "Deleting: " + f );
                if ( f.isDirectory() )
                {
                    try
                    {
                        FileUtils.deleteDirectory( f );
                    }
                    catch ( IOException e )
                    {
                        System.out.println( "Error deleting test directory: " + f );
                    }
                }
                else
                {
                    f.delete();
                    f.deleteOnExit();
                }
            }
        }
    }

    private void printTestHeader()
    {
        StackTraceElement e = new Throwable().getStackTrace()[1];
        System.out.println( "\n\nRunning: " + e.getMethodName() + "\n\n" );
    }

    public void testUseDetectedFileAttributes()
        throws Exception
    {
        printTestHeader();
        
        File tempFile = File.createTempFile( "tar-file-attributes.", ".tmp" );
        toDelete.add( tempFile );

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( tempFile );
            writer.write( "This is a test file." );
        }
        finally
        {
            IOUtil.close( writer );
        }

        int result = Runtime.getRuntime().exec( "chmod 440 " + tempFile.getAbsolutePath() ).waitFor();
        
        assertEquals( 0, result );

        PlexusIoResourceAttributes fileAttributes =
            PlexusIoResourceAttributeUtils.getFileAttributes( tempFile );
        
        assertEquals( 0440, fileAttributes.getOctalMode() );
        
        TarArchiver tarArchiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );

        File tempTarFile = File.createTempFile( "tar-file.", ".tar" );
        toDelete.add( tempTarFile );

        tarArchiver.setDestFile( tempTarFile );
        tarArchiver.addFile( tempFile, tempFile.getName() );

        tarArchiver.createArchive();

        TarUnArchiver tarUnArchiver = (TarUnArchiver) lookup( UnArchiver.ROLE, "tar" );

        File tempTarDir = File.createTempFile( "tar-test.", ".dir" );
        tempTarDir.delete();
        tempTarDir.mkdirs();

        toDelete.add( tempTarDir );

        tarUnArchiver.setDestDirectory( tempTarDir );
        tarUnArchiver.setSourceFile( tempTarFile );

        tarUnArchiver.extract();

        fileAttributes =
            PlexusIoResourceAttributeUtils.getFileAttributes( new File( tempTarDir, tempFile.getName() ) );

        assertEquals( 0440, fileAttributes.getOctalMode() );
    }

    public void testOverrideDetectedFileAttributes()
        throws Exception
    {
        printTestHeader();
        
        File tempFile = File.createTempFile( "tar-file-attributes.", ".tmp" );
        toDelete.add( tempFile );

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( tempFile );
            writer.write( "This is a test file." );
        }
        finally
        {
            IOUtil.close( writer );
        }

        int result = Runtime.getRuntime().exec( "chmod 440 " + tempFile.getAbsolutePath() ).waitFor();
        assertEquals( 0, result );

        TarArchiver tarArchiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );

        File tempTarFile = File.createTempFile( "tar-file.", ".tar" );
        toDelete.add( tempTarFile );

        tarArchiver.setDestFile( tempTarFile );
        tarArchiver.addFile( tempFile, tempFile.getName(), 0660 );

        tarArchiver.createArchive();

        TarUnArchiver tarUnArchiver = (TarUnArchiver) lookup( UnArchiver.ROLE, "tar" );

        File tempTarDir = File.createTempFile( "tar-test.", ".dir" );
        tempTarDir.delete();
        tempTarDir.mkdirs();

        toDelete.add( tempTarDir );

        tarUnArchiver.setDestDirectory( tempTarDir );
        tarUnArchiver.setSourceFile( tempTarFile );

        tarUnArchiver.extract();

        PlexusIoResourceAttributes fileAttributes =
            PlexusIoResourceAttributeUtils.getFileAttributes( new File( tempTarDir, tempFile.getName() ) );

        assertEquals( 0660, fileAttributes.getOctalMode() );
    }

    public void testOverrideDetectedFileAttributesUsingDefaultFileMode()
        throws Exception
    {
        printTestHeader();
        
        File tempFile = File.createTempFile( "tar-file-attributes.", ".tmp" );
        toDelete.add( tempFile );

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( tempFile );
            writer.write( "This is a test file." );
        }
        finally
        {
            IOUtil.close( writer );
        }

        int result = Runtime.getRuntime().exec( "chmod 440 " + tempFile.getAbsolutePath() ).waitFor();
        assertEquals( 0, result );

        TarArchiver tarArchiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );

        File tempTarFile = File.createTempFile( "tar-file.", ".tar" );
        toDelete.add( tempTarFile );

        tarArchiver.setDestFile( tempTarFile );
        tarArchiver.setDefaultFileMode( 0660 );
        tarArchiver.addFile( tempFile, tempFile.getName() );

        tarArchiver.createArchive();

        TarUnArchiver tarUnArchiver = (TarUnArchiver) lookup( UnArchiver.ROLE, "tar" );

        File tempTarDir = File.createTempFile( "tar-test.", ".dir" );
        tempTarDir.delete();
        tempTarDir.mkdirs();

        toDelete.add( tempTarDir );

        tarUnArchiver.setDestDirectory( tempTarDir );
        tarUnArchiver.setSourceFile( tempTarFile );

        tarUnArchiver.extract();

        PlexusIoResourceAttributes fileAttributes =
            PlexusIoResourceAttributeUtils.getFileAttributes( new File( tempTarDir, tempFile.getName() ) );

        assertEquals( 0660, fileAttributes.getOctalMode() );
    }

}
