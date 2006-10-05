package org.codehaus.plexus.xsiter.deployer;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.maven.scm.ScmTestCase;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.provider.svn.SvnScmTestUtils;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.util.FileUtils;

/**
 * Tests for the {@link Deployer}
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class DeployerTest
    extends ScmTestCase
{

    public void setUp()
        throws Exception
    {
        super.setUp();

        FileUtils.forceDelete( getRepositoryRoot().getAbsolutePath() );
        FileUtils.forceDelete( getWorkingCopy() );

        SvnScmTestUtils.initializeRepository( getRepositoryRoot() );
        ScmProvider scmRepo = getScmManager().getProviderByRepository( getScmRepository() );

        // create a working directory for checkouts
        getWorkingCopy().mkdir();
        scmRepo.checkOut( getScmRepository(), getScmFileSet(), "HEAD" );

        //      Make sure that the correct files was checked out        
        File fooJava = new File( getWorkingCopy(), "Foo.java" );
        File barJava = new File( getWorkingCopy(), "Bar.java" );
        File readmeTxt = new File( getWorkingCopy(), "readme.txt" );

        assertFalse( "check Foo.java doesn't yet exist", fooJava.canRead() );
        assertFalse( "check Bar.java doesn't yet exist", barJava.canRead() );
        assertFalse( "check readme.txt doesn't yet exist", readmeTxt.canRead() );

        // Change the files
        createFooJava( fooJava );
        createBarJava( barJava );
        createReadmeText( readmeTxt );
    }

    public void testLookup()
        throws Exception
    {
        Deployer component = (Deployer) lookup( Deployer.ROLE );
        assertNotNull( component );
    }

    /**
     * Creates a Foo.java resource in working copy location.
     */
    private void createFooJava( File fooJava )
        throws Exception
    {
        FileWriter output = new FileWriter( fooJava );
        PrintWriter printer = new PrintWriter( output );
        printer.println( "public class Foo" );
        printer.println( "{" );
        printer.println( "    public void foo()" );
        printer.println( "    {" );
        printer.println( "        int i = 10;" );
        printer.println( "    }" );
        printer.println( "}" );
        printer.close();

        output.close();
    }

    /**
     * Creates a Bar.java resource in working copy location.
     */
    private void createBarJava( File barJava )
        throws Exception
    {
        FileWriter output = new FileWriter( barJava );
        PrintWriter printer = new PrintWriter( output );
        printer.println( "public class Bar" );
        printer.println( "{" );
        printer.println( "    public int bar()" );
        printer.println( "    {" );
        printer.println( "        return 20;" );
        printer.println( "    }" );
        printer.println( "}" );
        printer.close();

        output.close();
    }

    /**
     * Creates a readme.txt resource in working copy location.
     */
    private void createReadmeText( File readmeTxt )
        throws Exception
    {
        FileWriter output = new FileWriter( readmeTxt );
        PrintWriter printer = new PrintWriter( output );
        printer.println( " Test Readme text." );
        printer.close();

        output.close();
    }

    private String getScmUrl()
        throws Exception
    {
        return SvnScmTestUtils.getScmUrl( new File( getRepositoryRoot(), "trunk" ) );
    }

    private ScmRepository getScmRepository()
        throws Exception
    {
        return getScmManager().makeScmRepository( getScmUrl() );
    }
}
