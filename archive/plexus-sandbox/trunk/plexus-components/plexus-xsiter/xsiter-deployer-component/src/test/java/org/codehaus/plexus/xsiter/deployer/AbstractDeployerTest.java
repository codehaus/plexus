package org.codehaus.plexus.xsiter.deployer;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmTestCase;
import org.apache.maven.scm.command.add.AddScmResult;
import org.apache.maven.scm.command.checkin.CheckInScmResult;
import org.apache.maven.scm.command.checkout.CheckOutScmResult;
import org.apache.maven.scm.command.tag.TagScmResult;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.provider.svn.SvnScmTestUtils;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.util.FileUtils;

/**
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public abstract class AbstractDeployerTest
    extends ScmTestCase
{

    /**
     * SCM tag to test against.
     */
    protected static final String TAG_VERSION_1_0_0 = "Version-1_0_0";

    public void setUp()
        throws Exception
    {
        super.setUp();

        FileUtils.forceDelete( getRepositoryRoot().getAbsolutePath() );
        FileUtils.forceDelete( getWorkingCopy() );

        SvnScmTestUtils.initializeRepository( getRepositoryRoot() );
        ScmProvider scmProvider = getScmManager().getProviderByRepository( getScmRepository() );

        // create a working directory for checkouts
        getWorkingCopy().mkdir();
        CheckOutScmResult result = scmProvider.checkOut( getScmRepository(), new ScmFileSet( getWorkingDirectory() ),
                                                         null );
        assertTrue( "Check result was successful, output: " + result.getCommandOutput(), result.isSuccess() );

        createDeployableWebApp();

        addWebAppToSVNRepo();

        tagWebAppInSVNRepo();
    }

    /**
     * Returns an SCM Url for the local repository for the unit test.
     * @return
     * @throws Exception
     */
    protected String getScmUrl()
        throws Exception
    {
        return SvnScmTestUtils.getScmUrl( getRepositoryRoot() );
    }

    /**
     * Returns an {@link ScmRepository} from the Scm Url for the local repository.
     * @return
     * @throws Exception
     */
    protected ScmRepository getScmRepository()
        throws Exception
    {
        System.err.println( getScmUrl() );
        return getScmManager().makeScmRepository( getScmUrl() );
    }

    /**
     * Sets up minimal set of resources that constitute a web app.
     */
    private void createDeployableWebApp()
        throws Exception
    {
        String webAppDir = getWorkingCopy().getAbsolutePath() + "/trunk/src/webapp/";
        FileUtils.mkdir( webAppDir );
        // create conf dir
        String confDir = getWorkingDirectory().getAbsolutePath() + "/trunk/src/conf";
        FileUtils.mkdir( confDir );
        // create WEB-INF dir
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

        createPomXml( new File( getWorkingDirectory().getAbsolutePath() + "/trunk/", "pom.xml" ) );

        createVirtualHostTemplate( new File( confDir, "vhosts.vm" ) );

    }

    private void createVirtualHostTemplate( File vhostTemplate )
        throws Exception
    {

        FileWriter vhostVm = new FileWriter( vhostTemplate );
        PrintWriter printer = new PrintWriter( vhostVm );

        printer.println( "<VirtualHost $vhostIP>" );
        printer.println( "  ServerName $vhostName" );
        printer.println( "  ErrorLog $vhostLogDirectory/apache_error.log" );
        printer.println( "  CustomLog $vhostLogDirectory/apache_access.log combined" );
        printer.println( "  ProxyPass         /  $vhostConnectorProtocol://localhost:$vhostConnectorPort/" );
        printer.println( "  ProxyPassReverse  /  $vhostConnectorProtocol://localhost:$vhostConnectorPort/" );
        printer.println( "</VirtualHost>" );

        printer.close();
        vhostVm.close();
    }

    /**
     * Creates <code>pom.xml</code>.
     * @param pom
     * @throws Exception
     */
    private void createPomXml( File pom )
        throws Exception
    {
        FileWriter pomXml = new FileWriter( pom );
        PrintWriter printer = new PrintWriter( pomXml );

        printer.println( "<project>" );
        printer.println( "  <modelVersion>4.0.0</modelVersion>" );
        printer.println( "  <groupId>sample</groupId>" );
        printer.println( "  <artifactId>sample-webapp</artifactId>" );
        printer.println( "  <version>1.0-SNAPSHOT</version>" );
        printer.println( "  <packaging>war</packaging>" );
        printer.println( "  <scm>" );
        printer.println( "    <connection>" );
        printer.println( "      scm:svn:file:///" + getBasedir().replace( '\\', '/' )
            + "/target/scm-test/repository/trunk" );
        printer.println( "    </connection>" );
        printer.println( "    <developerConnection>" );
        printer.println( "      scm:svn:file:///" + getBasedir().replace( '\\', '/' )
            + "/target/scm-test/repository/trunk" );
        printer.println( "    </developerConnection>" );
        printer.println( "  </scm>" );
        printer.println( "  <build>" );
        printer.println( "    <plugins>" );
        printer.println( "      <plugin>" );
        printer.println( "        <groupId>org.codehaus.cargo</groupId>" );
        printer.println( "        <artifactId>cargo-maven2-plugin</artifactId>" );
        printer.println( "        <configuration>" );
        printer.println( "          <!-- wait>false</wait -->" );
        printer.println( "          <container>" );
        printer.println( "            <containerId>tomcat5x</containerId>" );
        printer.println( "            <zipUrlInstaller>" );
        printer.println( "              <url>" );
        printer
            .println( "                http://mirrors.ccs.neu.edu/Apache/dist/tomcat/tomcat-5/v5.5.16/bin/apache-tomcat-5.5.16.zip" );
        printer.println( "              </url>" );
        printer.println( "              <installDir>${installDir}</installDir>" );
        printer.println( "            </zipUrlInstaller>" );
        printer.println( "            <output>${basedir}/tomcat5x.log</output>" );
        printer.println( "            <log>${basedir}/cargo.log</log>" );
        printer.println( "          </container>" );
        printer.println( "          <configuration>" );
        printer.println( "            <home>" );
        printer.println( "              ${basedir}/../../deploy/webapp/${artifactId}/tomcat5x/" );
        printer.println( "            </home>" );
        printer.println( "            <properties>" );
        printer.println( "              <!-- This is the SHUTDOWN port for Tomcat -->" );
        printer.println( "              <!-- Tomcat admin and servlet ports -->" );
        printer.println( "              <cargo.rmi.port>9191</cargo.rmi.port>" );
        printer.println( "              <cargo.servlet.port>9090</cargo.servlet.port>" );
        printer.println( "              <cargo.jvmargs>-Xmx256m -Xms256m</cargo.jvmargs>" );
        printer.println( "            </properties>" );
        printer.println( "            <deployables>" );
        printer.println( "              <deployable>" );
        printer.println( "                <properties>" );
        printer.println( "                  <context>ROOT</context>" );
        printer.println( "                </properties>" );
        printer.println( "              </deployable>" );
        printer.println( "            </deployables>" );
        printer.println( "          </configuration>" );
        printer.println( "        </configuration>" );
        printer.println( "      </plugin>   " );
        printer.println( "    </plugins> " );
        printer.println( "  </build> " );
        printer.println( "  <properties>" );
        printer.println( "    <installDir>/opt/tools/cargo</installDir>" );
        printer.println( "    <deployer.default.goals>clean compile war:war -P integration</deployer.default.goals>" );
        printer.println( "    <vhosts.configuration>" );
        printer.println( "      <![CDATA[<vhosts>" );
        printer.println( "        <vhost>" );
        printer.println( "          <id>integration</id>" );
        printer.println( "          <vhostTemplate>src/conf/vhosts.vm</vhostTemplate>" );
        printer.println( "          <vhostDirectory>deploy/apache</vhostDirectory>" );
        printer.println( "          <vhostIP>127.0.0.1</vhostIP>" );
        printer.println( "          <vhostName>staging.localhost.com</vhostName>" );
        printer.println( "          <vhostLogDirectory>deploy/apache/logs</vhostLogDirectory>" );
        printer.println( "          <vhostConnectorProtocol>http</vhostConnectorProtocol>" );
        printer.println( "          <vhostConnectorPort>9090</vhostConnectorPort>          " );
        printer.println( "        </vhost>" );
        printer.println( "      </vhosts>]]>" );
        printer.println( "    </vhosts.configuration> " );
        printer.println( "  </properties>" );
        printer.println( "</project>" );

        printer.close();
        pomXml.close();
    }

    /**
     * Adds and commits the created webapp resources to local SVN repo.
     * @throws ScmException
     * @throws Exception
     */
    private void addWebAppToSVNRepo()
        throws ScmException, Exception
    {
        ScmProvider scmProvider = getScmManager().getProviderByRepository( getScmRepository() );
        File webappDir = new File( getWorkingDirectory().getAbsolutePath() + "/trunk/src", "webapp" );
        // add webapp dir
        AddScmResult addScmResult = scmProvider.add( getScmRepository(), new ScmFileSet( getWorkingDirectory(),
                                                                                         webappDir ) );
        assertTrue( "Check result was successful, output: " + addScmResult.getCommandOutput(), addScmResult.isSuccess() );

        // add files under webapp dir
        addScmResult = scmProvider.add( getScmRepository(), new ScmFileSet( getWorkingDirectory(), webappDir
            .listFiles() ) );
        assertTrue( "Check result was successful, output: " + addScmResult.getCommandOutput(), addScmResult.isSuccess() );

        // add files under WEB-INF dir
        File webInfDir = new File( webappDir, "WEB-INF" );
        addScmResult = scmProvider.add( getScmRepository(), new ScmFileSet( getWorkingDirectory(), webInfDir
            .listFiles() ) );
        assertTrue( "Check result was successful, output: " + addScmResult.getCommandOutput(), addScmResult.isSuccess() );

        // add src/conf dir
        File confDir = new File( getWorkingDirectory().getAbsolutePath() + "/trunk/src", "conf" );
        addScmResult = scmProvider.add( getScmRepository(), new ScmFileSet( getWorkingDirectory(), confDir ) );
        assertTrue( "Check result was successful, output: " + addScmResult.getCommandOutput(), addScmResult.isSuccess() );

        // add files under src/conf dir
        addScmResult = scmProvider
            .add( getScmRepository(), new ScmFileSet( getWorkingDirectory(), confDir.listFiles() ) );
        assertTrue( "Check result was successful, output: " + addScmResult.getCommandOutput(), addScmResult.isSuccess() );

        // Commit all added files for the webapp. 
        CheckInScmResult checkInScmResult = scmProvider.checkIn( getScmRepository(),
                                                                 new ScmFileSet( getWorkingDirectory() ), null,
                                                                 "Added webapp" );
        assertTrue( "Check if result was successful, output: " + checkInScmResult.getCommandOutput(), checkInScmResult
            .isSuccess() );
    }

    /**
     * Create a Tag to test against.
     *  
     * @throws ScmException
     * @throws Exception
     */
    private void tagWebAppInSVNRepo()
        throws ScmException, Exception
    {
        ScmProvider scmProvider = getScmManager().getProviderByRepository( getScmRepository() );
        File sourcesToTag = new File( getWorkingDirectory(), "trunk" );
        TagScmResult tagScmResult = scmProvider.tag( getScmRepository(), new ScmFileSet( sourcesToTag ),
                                                     TAG_VERSION_1_0_0 );

        assertTrue( tagScmResult.isSuccess() );
    }

}