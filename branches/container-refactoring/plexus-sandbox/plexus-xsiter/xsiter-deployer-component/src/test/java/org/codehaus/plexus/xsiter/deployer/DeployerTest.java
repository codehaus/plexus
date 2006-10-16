package org.codehaus.plexus.xsiter.deployer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.xsiter.deployer.model.DeployableProject;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;

/**
 * Tests for the {@link Deployer}.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class DeployerTest
    extends AbstractDeployerTest
{

    /**
     * Workspace/Deployable Project label.
     */
    private static final String PROJECT_LABEL_SAMPLE_WEB = "sample-web";

    /**
     * SCM repository URL.
     */
    private static final String SCM_SAMPLE_WEB = "scm:svn:file:///" + getBasedir().replace( '\\', '/' )
        + "/target/scm-test/repository/trunk";

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
        component.removeProject( project );
        project.setLabel( "non-existent-component" );
        project.setScmURL( SCM_SAMPLE_WEB );
        component.removeProject( project );
    }

    public void testCheckoutProjectWithoutTag()
        throws Exception
    {
        DeployableProject project = new DeployableProject();
        project.setLabel( PROJECT_LABEL_SAMPLE_WEB );
        project.setScmURL( SCM_SAMPLE_WEB );
        project.setScmUsername( "" );
        project.setScmPassword( "" );
        project.setScmTag( null );

        checkoutProject( project );
    }

    public void testCheckoutProjectWithTag()
        throws Exception
    {
        DeployableProject project = new DeployableProject();
        project.setLabel( PROJECT_LABEL_SAMPLE_WEB );
        project.setScmURL( SCM_SAMPLE_WEB );
        project.setScmUsername( "" );
        project.setScmPassword( "" );
        project.setScmTag( TAG_VERSION_1_0_0 );

        checkoutProject( project );
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
        project.setScmUsername( "" );
        project.setScmPassword( "" );
        project.setScmTag( TAG_VERSION_1_0_0 );

        MavenProject mavenProject = mgr.getMavenProjectForCheckedoutProject( project );
        assertNotNull( mavenProject );
        Properties props = mavenProject.getProperties();
        assertEquals( 2, props.size() );
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
        project.setScmUsername( "" );
        project.setScmPassword( "" );
        project.setScmTag( TAG_VERSION_1_0_0 );

        component.addVirtualHost( project );
    }

    /**
     * Helper for checking out a {@link DeployableProject}.
     * 
     * @param project
     * @throws Exception
     * @throws IOException
     */
    private void checkoutProject( DeployableProject project )
        throws Exception, IOException
    {
        Deployer component = (Deployer) lookup( Deployer.ROLE );
        DefaultDeployer mgr = (DefaultDeployer) component;
        String deployerWorkingDir = mgr.getWorkingDirectory();
        //start clean 
        FileUtils.forceDelete( deployerWorkingDir );

        component.checkoutProject( project );
        assertTrue( FileUtils.fileExists( deployerWorkingDir ) );
        DeploymentWorkspace workspace = mgr.getDeploymentWorkspace( project );
        assertNotNull( workspace );

        // verify the workspace structure was created as we expected
        File projectDir = new File( workspace.getRootDirectory() );
        assertTrue( projectDir.exists() );

        File workingDir = new File( projectDir, workspace.getWorkingDirectory() );
        assertTrue( workingDir.exists() );

        File checkoutDir = new File( workingDir, ( null == project.getScmTag() ? "HEAD" : project.getScmTag() ) );
        assertTrue( checkoutDir.exists() );
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
        project.setScmUsername( "" );
        project.setScmPassword( "" );
        project.setScmTag( null );

        component.buildProject( project, "clean" );
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
        DefaultDeployer mgr = (DefaultDeployer) component;
        DeployableProject project = new DeployableProject();
        project.setLabel( "ghost-vhost-component" );
        project.setScmURL( SCM_SAMPLE_WEB + "/ghost-module" );
        project.setScmUsername( "" );
        project.setScmPassword( "" );

        try
        {
            component.buildProject( project, "clean compile" );
            fail( "Expected Exception" );
        }
        catch ( Exception e )
        {
            // expected!
        }
        finally
        {
            // cleanup 
            FileUtils.deleteDirectory( new File( mgr.getWorkingDirectory(), project.getLabel() ) );
        }
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
        project.setScmUsername( "" );
        project.setScmPassword( "" );
        project.setScmTag( null );

        // should deploy successfully
        component.deployProject( project );
    }

    public void testGetAllDeploymentWorkspaces()
        throws Exception
    {
        Deployer component = (Deployer) lookup( Deployer.ROLE );
        DefaultDeployer mgr = (DefaultDeployer) component;
        List workspaces = mgr.getAllDeploymentWorkspaces();
        assertEquals( 1, workspaces.size() );
    }
}
