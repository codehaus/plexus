package org.codehaus.plexus.xsiter.deployer;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmTestCase;
import org.apache.maven.scm.command.add.AddScmResult;
import org.apache.maven.scm.provider.svn.SvnScmTestUtils;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.xsiter.deployer.model.DeployableProject;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;

/**
 * Tests for the {@link Deployer}
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class DeploymentManagerTest
    extends ScmTestCase
{

    private static final String PROJECT_LABEL_BARE_VHOST_COMPONENT = "vhost-component";

    private static final String SCM_VHOST_COMPONENT = "scm:cvs:pserver:rahul@localhost:/home/cvs/local:projects/plexus/vhost-component";

    private static final String PROJECT_LABEL_SAMPLE_WEB = "sample-web";

    private static final String SCM_SAMPLE_WEB = "scm:cvs:pserver:rahul@localhost:/home/cvs/local:projects/example/sample/web-project";

    /**
     * Target CVS tag to test against.
     */
    // private static final String TAG_BUILD_20060920 = "Build_20060920";
    private static final String TAG_TO_CHECKOUT = "HEAD";

    public void setUp()
        throws Exception
    {
        super.setUp();

        FileUtils.forceDelete( getRepositoryRoot().getAbsolutePath() );
        SvnScmTestUtils.initializeRepository( getRepositoryRoot() );
        FileUtils.mkdir( getWorkingDirectory().getAbsolutePath() );

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

        AddScmResult addResult = getScmManager().getProviderByUrl( getScmUrl() )
            .add( getScmRepository(), new ScmFileSet( getWorkingCopy(), "Foo.java", null ) );

        if ( !addResult.isSuccess() )
        {
            System.out.println( "SCM Add result: " + addResult.getProviderMessage() );
        }

        assertResultIsSuccess( addResult );
    }

    public void testLookup()
        throws Exception
    {
        Deployer component = (Deployer) lookup( Deployer.ROLE );
        assertNotNull( component );
    }

    public void testAddProject()
        throws Exception
    {
        FileUtils.forceDelete( new File( "target/deployments", PROJECT_LABEL_SAMPLE_WEB ) );

        Deployer component = (Deployer) lookup( Deployer.ROLE );
        DeployableProject project = new DeployableProject();
        project.setLabel( PROJECT_LABEL_SAMPLE_WEB );
        project.setScmURL( SCM_SAMPLE_WEB );
        project.setScmUsername( "rahul" );
        project.setScmPassword( "rahul" );
        component.addProject( project );

        DefaultDeployer mgr = (DefaultDeployer) component;
        DeploymentWorkspace workspace = mgr.getDeploymentWorkspace( project );
        assertNotNull( workspace );

        String deployerWorkingDir = mgr.getWorkingDirectory();
        assertTrue( FileUtils.fileExists( deployerWorkingDir ) );

        // verify the workspace structure was created as we expected
        File projectDir = new File( workspace.getRootDirectory() );
        assertTrue( projectDir.exists() );
        assertTrue( new File( projectDir, workspace.getWebappDirectory() ).exists() );
        assertTrue( new File( projectDir, workspace.getTempDirectory() ).exists() );
        assertTrue( new File( projectDir, workspace.getWebserverDirectory() ).exists() );
        assertTrue( new File( projectDir, workspace.getWorkingDirectory() ).exists() );
        assertTrue( new File( projectDir, workspace.getWebappDirectory() ).exists() );
    }

    public void testRemoveProject()
        throws Exception
    {
        DeployableProject project = new DeployableProject();
        project.setLabel( PROJECT_LABEL_SAMPLE_WEB );
        project.setScmURL( SCM_SAMPLE_WEB );
        Deployer component = (Deployer) lookup( Deployer.ROLE );
        // XXX: Just removed for testing, Re-enable it when done!
        // component.removeProject (project);
        project.setLabel( "non-existent-component" );
        project.setScmURL( SCM_SAMPLE_WEB );
        component.removeProject( project );
    }

    public void testCheckoutProject()
        throws Exception
    {
        DeployableProject project = new DeployableProject();
        project.setLabel( PROJECT_LABEL_SAMPLE_WEB );
        project.setScmURL( SCM_SAMPLE_WEB );
        project.setScmUsername( "rahul" );
        project.setScmPassword( "rahul" );
        project.setScmTag( TAG_TO_CHECKOUT );

        Deployer component = (Deployer) lookup( Deployer.ROLE );
        component.checkoutProject( project );

        DefaultDeployer mgr = (DefaultDeployer) component;
        String deployerWorkingDir = mgr.getWorkingDirectory();
        assertTrue( FileUtils.fileExists( deployerWorkingDir ) );
        DeploymentWorkspace workspace = mgr.getDeploymentWorkspace( project );
        assertNotNull( workspace );
        // verify the workspace structure was created as we expected
        File projectDir = new File( workspace.getRootDirectory() );
        assertTrue( projectDir.exists() );
        File workingDir = new File( projectDir, workspace.getWorkingDirectory() );
        assertTrue( workingDir.exists() );
        File checkoutDir = new File( workingDir, TAG_TO_CHECKOUT );
        assertTrue( checkoutDir.exists() );
        assertTrue( checkoutDir.list().length > 3 );
    }

    /**
     * Tests if a {@link MavenProject} instance can be obtained for a checked
     * out project.
     */
    public void testGetMavenProjectForCheckedoutProject()
        throws Exception
    {
        Deployer component = (Deployer) lookup( Deployer.ROLE );
        DefaultDeployer mgr = (DefaultDeployer) component;
        DeployableProject project = new DeployableProject();
        project.setLabel( PROJECT_LABEL_SAMPLE_WEB );
        project.setScmURL( SCM_SAMPLE_WEB );
        project.setScmUsername( "rahul" );
        project.setScmPassword( "rahul" );
        project.setScmTag( TAG_TO_CHECKOUT );

        MavenProject mavenProject = mgr.getMavenProjectForCheckedoutProject( project );
        assertNotNull( mavenProject );
        // Properties props = mavenProject.getProperties ();
        // assertEquals (0, props.size ());
        // String scmVersion = (String) props.get ("maven-scm.version");
        // assertEquals ("1.0-beta-3", scmVersion);
    }

    /**
     * Tests the Virtual Host set up for a checked out project constructing and
     * then using the Virtual Host config from the Project's pom.xml
     * 
     * @throws Exception
     */
    public void testAddVirtualHostForCheckedoutproject()
        throws Exception
    {
        Deployer component = (Deployer) lookup( Deployer.ROLE );
        DeployableProject project = new DeployableProject();
        project.setLabel( PROJECT_LABEL_SAMPLE_WEB );
        project.setScmURL( SCM_SAMPLE_WEB );
        project.setScmUsername( "rahul" );
        project.setScmPassword( "rahul" );
        project.setScmTag( TAG_TO_CHECKOUT );

        component.addVirtualHost( project );
    }

    /**
     * Tests if the Deployer Component can delegate build goals to the checked
     * out project.
     * 
     * @throws Exception
     */
    public void testBuildProject()
        throws Exception
    {
        Deployer component = (Deployer) lookup( Deployer.ROLE );
        DeployableProject project = new DeployableProject();
        project.setLabel( PROJECT_LABEL_SAMPLE_WEB );
        project.setScmURL( SCM_SAMPLE_WEB );
        project.setScmUsername( "rahul" );
        project.setScmPassword( "rahul" );
        project.setScmTag( TAG_TO_CHECKOUT );

        component.buildProject( project, "clean compile" );
    }

    /**
     * Tests the case of a non-existent project to build
     * 
     * @throws Exception
     */
    public void testBuildNonExistentProject()
        throws Exception
    {
        Deployer component = (Deployer) lookup( Deployer.ROLE );
        DeployableProject project = new DeployableProject();
        project.setLabel( "ghost-vhost-component" );
        project.setScmURL( "scm:cvs:pserver:rahul@localhost:/home/cvs/local:projects/plexus/vhost-component" );
        project.setScmUsername( "rahul" );
        project.setScmPassword( "rahul" );
        project.setScmTag( TAG_TO_CHECKOUT );

        try
        {
            component.buildProject( project, "clean compile" );
            fail( "Expected Exception" );
        }
        catch ( Exception e )
        {
            // expected!
        }
    }

    /**
     * @throws Exception
     */
    public void testBuildProjectWithIncorrectGoal()
        throws Exception
    {
        Deployer component = (Deployer) lookup( Deployer.ROLE );
        DeployableProject project = new DeployableProject();
        project.setLabel( PROJECT_LABEL_SAMPLE_WEB );
        project.setScmURL( SCM_SAMPLE_WEB );
        project.setScmUsername( "rahul" );
        project.setScmPassword( "rahul" );
        project.setScmTag( TAG_TO_CHECKOUT );

        component.buildProject( project, "cleaner goal -e" );
    }

    /**
     * Tests a deployment to an application server.
     * 
     * @throws Exception
     */
    public void testDeployProject()
        throws Exception
    {
        boolean bSkipped = true;
        if ( bSkipped )
        {
            System.out.println( "Deployment test skipped! This will else result lot of Tomcat instances to start up" );
            return;
        }
        Deployer component = (Deployer) lookup( Deployer.ROLE );
        DeployableProject project = new DeployableProject();
        project.setLabel( PROJECT_LABEL_SAMPLE_WEB );
        project.setScmURL( SCM_SAMPLE_WEB );
        project.setScmUsername( "rahul" );
        project.setScmPassword( "rahul" );
        project.setScmTag( "HEAD" );

        // should deploy successfully
        component.deployProject( project );
    }

    // Service methods 
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
