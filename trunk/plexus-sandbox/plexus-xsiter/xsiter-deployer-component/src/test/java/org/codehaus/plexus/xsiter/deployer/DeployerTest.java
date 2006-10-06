package org.codehaus.plexus.xsiter.deployer;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmTestCase;
import org.apache.maven.scm.command.add.AddScmResult;
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

    /**
     * Sample webapp to test against.
     */
    private static final String SAMPLE_WEBAPP = "sample-webapp";

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

        setupDeployableWebApp();

        File module = new File( getWorkingCopy(), SAMPLE_WEBAPP );
        ScmFileSet fileSet = new ScmFileSet( module, module.listFiles() );
        AddScmResult addScmResult = scmRepo.add( getScmRepository(), fileSet );
        assertTrue( addScmResult.isSuccess() );
    }

    public void testLookup()
        throws Exception
    {
        Deployer component = (Deployer) lookup( Deployer.ROLE );
        assertNotNull( component );
    }

    private String getScmUrl()
        throws Exception
    {
        return SvnScmTestUtils.getScmUrl( getRepositoryRoot() );
    }

    private ScmRepository getScmRepository()
        throws Exception
    {
        return getScmManager().makeScmRepository( getScmUrl() );
    }

    /**
     * Sets up minimal set of resources that constitute a web app.
     */
    private void setupDeployableWebApp()
        throws Exception
    {
        String webAppDir = getWorkingCopy().getAbsolutePath() + "/" + SAMPLE_WEBAPP + "/src/webapp/";
        FileUtils.mkdir( webAppDir );
        String webInfDir = webAppDir + "/WEB-INF";
        FileUtils.mkdir( webInfDir );

        FileWriter webXml = new FileWriter( new File( webInfDir, "web.xml" ) );
        PrintWriter printer = new PrintWriter( webXml );
        printer.println( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        printer
            .println( "<web-app id=\"WebApp_ID\" version=\"2.4\" xmlns=\"http://java.sun.com/xml/ns/j2ee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd\">" );
        printer.println( "    <display-name>Sample Web app </display-name>" );
        printer.println( "    <welcome-file-list>" );
        printer.println( "        <welcome-file>index.html</welcome-file>" );
        printer.println( "        <welcome-file>index.jsp</welcome-file>" );
        printer.println( "    </welcome-file-list>" );
        printer.println( "</web-app>" );

        printer.close();
        webXml.close();

        FileWriter indexJsp = new FileWriter( new File( webAppDir, "index.jsp" ) );
        printer = new PrintWriter( indexJsp );
        printer.println( " Hello World! " );

        printer.close();
        indexJsp.close();
    }
}
