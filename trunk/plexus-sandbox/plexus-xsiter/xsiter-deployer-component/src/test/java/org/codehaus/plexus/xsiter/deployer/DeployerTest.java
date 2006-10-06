package org.codehaus.plexus.xsiter.deployer;

import java.io.File;

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

    private static final String PROJECT_LABEL_SAMPLE_WEB = "sample-web";

    private static final String SCM_SAMPLE_WEB = "scm:svn:file:///" + getBasedir() + "/target/scm-test/repository/";

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

    public void testCheckoutProject()
        throws Exception
    {
        DeployableProject project = new DeployableProject();
        project.setLabel( PROJECT_LABEL_SAMPLE_WEB );
        project.setScmURL( SCM_SAMPLE_WEB );
        project.setScmUsername( "" );
        project.setScmPassword( "" );
        project.setScmTag( null );

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
        File checkoutDir = new File( workingDir, "Head" );
        assertTrue( checkoutDir.exists() );
        //assertTrue( checkoutDir.list().length > 3 );
    }

}
