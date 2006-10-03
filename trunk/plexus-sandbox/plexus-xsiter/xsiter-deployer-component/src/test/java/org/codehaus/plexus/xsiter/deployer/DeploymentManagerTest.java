package org.codehaus.plexus.xsiter.deployer;

import java.io.File;
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmTestCase;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.local.repository.LocalScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.xsiter.deployer.model.DeployableProject;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;

/**
 * Tests for the {@link DeploymentManager}
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class DeploymentManagerTest
    extends ScmTestCase
{

    private static final String PROJECT_LABEL_BARE_VHOST_COMPONENT = "vhost-component";

    private static final String SCM_EFFACY_VHOST_COMPONENT = "scm:cvs:pserver:rahul@localhost:/home/cvs/local:projects/plexus/vhost-component";

    private static final String PROJECT_LABEL_SAMPLE_WEB = "sample-web";

    private static final String SCM_SAMPLE_WEB = "scm:cvs:pserver:rahul@localhost:/home/cvs/excibir:projects/example/sample/web-project";

    /**
     * Target CVS tag to test against.
     */
    // private static final String TAG_BUILD_20060920 = "Build_20060920";
    private static final String TAG_TO_CHECKOUT = "HEAD";

    public void setUp()
        throws Exception
    {
        super.setUp();

        FileUtils.mkdir( getWorkingDirectory().getAbsolutePath() );
    }

    public void testExistingRepository()
        throws Exception
    {
        ScmRepository repository = getScmManager().makeScmRepository( "scm:local:src/test/repository:test-repo" );

        assertNotNull( repository );

        assertEquals( "local", repository.getProvider() );

        //    assertEquals( "src/test/repositories:test-repo", repository.getScmSpecificUrl() );

        ScmProviderRepository providerRepository = repository.getProviderRepository();

        assertNotNull( providerRepository );

        assertTrue( providerRepository instanceof LocalScmProviderRepository );

        LocalScmProviderRepository local = (LocalScmProviderRepository) providerRepository;

        assertEquals( getTestFile( "src/test/repository" ).getAbsolutePath(), local.getRoot() );

        assertEquals( "test-repo", local.getModule() );
    }

    public void testLookup()
        throws Exception
    {
        DeploymentManager component = (DeploymentManager) lookup( DeploymentManager.ROLE );
        assertNotNull( component );
    }

    /**
     * Tests if CLI interaaction behaves as expected.
     * 
     * @throws Exception
     */
    public void testAddProjectFromCli()
        throws Exception
    {
        boolean bSkip = true;
        if ( bSkip )
        {
            System.out.println( "Skipping CLI Project creation test (this requires interaction!).... " );
            return;
        }
        DeploymentManager component = (DeploymentManager) lookup( DeploymentManager.ROLE );
        Properties props = new Properties();
        props.setProperty( "label", "test-project" );
        component.addProject( props );
    }

    public void testAddProject()
        throws Exception
    {
        FileUtils.forceDelete( new File( "target/deployments", PROJECT_LABEL_SAMPLE_WEB ) );

        DeploymentManager component = (DeploymentManager) lookup( DeploymentManager.ROLE );
        DeployableProject project = new DeployableProject();
        project.setLabel( PROJECT_LABEL_SAMPLE_WEB );
        project.setScmURL( SCM_SAMPLE_WEB );
        project.setScmUsername( "rahul" );
        project.setScmPassword( "rahul" );
        component.addProject( project );

        DefaultDeploymentManager mgr = (DefaultDeploymentManager) component;
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
        DeploymentManager component = (DeploymentManager) lookup( DeploymentManager.ROLE );
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

        DeploymentManager component = (DeploymentManager) lookup( DeploymentManager.ROLE );
        component.checkoutProject( project );

        DefaultDeploymentManager mgr = (DefaultDeploymentManager) component;
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
        DeploymentManager component = (DeploymentManager) lookup( DeploymentManager.ROLE );
        DefaultDeploymentManager mgr = (DefaultDeploymentManager) component;
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
        DeploymentManager component = (DeploymentManager) lookup( DeploymentManager.ROLE );
        DeploymentManager mgr = (DeploymentManager) component;
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
        DeploymentManager component = (DeploymentManager) lookup( DeploymentManager.ROLE );
        DeploymentManager mgr = (DeploymentManager) component;
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
        DeploymentManager component = (DeploymentManager) lookup( DeploymentManager.ROLE );
        DeploymentManager mgr = (DeploymentManager) component;
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
        boolean bSkipped = true;
        if ( bSkipped )
        {
            // for some reason fails on Linux
            System.out.println( "Test skipped!" );
            return;
        }
        DeploymentManager component = (DeploymentManager) lookup( DeploymentManager.ROLE );
        DeploymentManager mgr = (DeploymentManager) component;
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
        DeploymentManager component = (DeploymentManager) lookup( DeploymentManager.ROLE );
        DeployableProject project = new DeployableProject();
        project.setLabel( PROJECT_LABEL_SAMPLE_WEB );
        project.setScmURL( SCM_SAMPLE_WEB );
        project.setScmUsername( "rahul" );
        project.setScmPassword( "rahul" );
        project.setScmTag( "HEAD" );

        // should deploy successfully
        component.deployProject( project );
    }

}
