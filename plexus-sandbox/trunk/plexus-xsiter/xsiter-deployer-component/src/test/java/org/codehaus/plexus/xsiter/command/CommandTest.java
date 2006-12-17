package org.codehaus.plexus.xsiter.command;

import java.io.File;
import java.util.List;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;

/**
 * Tests for the basic Command framework.
 *  
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 */
public class CommandTest
    extends PlexusTestCase
{
    private File testDeploymentDir = getTestFile( getBasedir(), "target/deployments" );

    protected void setUp()
        throws Exception
    {
        super.setUp();
        if ( testDeploymentDir.exists() )
            FileUtils.forceDelete( testDeploymentDir );
    }

    public void testCreateDeploymentWorkspace()
        throws CommandException
    {
        CreateDeploymentWorkspaceCommand command = new CreateDeploymentWorkspaceCommand( "add-project" );
        CommandContext context = new CommandContext();

        context.setDeployerWorkingDirectory( testDeploymentDir );
        // set up a deployment workspace for test 
        DeploymentWorkspace workspace = new DeploymentWorkspace();
        context.setWorkspace( workspace );
        assertNotNull( context.getWorkspace() );
        workspace.setid( "xsiter-web" );

        // verify result
        CommandResult result = command.execute( context );
        assertNotNull( result );
        assertNotNull( result.getMessages() );
        assertEquals( 1, result.getMessages().size() );
        assertSame( ResultState.SUCCESS, result.getState() );

        // verify deployment workspace create
        assertTrue( testDeploymentDir.exists() );
        assertTrue( new File( testDeploymentDir, "xsiter-web/workspace.xml" ).exists() );
    }

    public void testListWorkspaces()
        throws CommandException
    {
        CreateDeploymentWorkspaceCommand command = new CreateDeploymentWorkspaceCommand( "add-project" );
        CommandContext context = new CommandContext();
        File testDeploymentDir = getTestFile( getBasedir(), "target/deployments" );
        context.setDeployerWorkingDirectory( testDeploymentDir );

        // set up a deployment workspace for test 
        DeploymentWorkspace workspace = new DeploymentWorkspace();
        context.setWorkspace( workspace );
        workspace.setid( "xsiter-web" );

        // verify result
        CommandResult result = command.execute( context );
        assertNotNull( result );
        assertNotNull( result.getMessages() );
        assertEquals( 1, result.getMessages().size() );
        assertSame( ResultState.SUCCESS, result.getState() );

        // verify deployment workspace create
        assertTrue( testDeploymentDir.exists() );
        assertTrue( new File( testDeploymentDir, "xsiter-web/workspace.xml" ).exists() );

        // Verify that list command works
        ListDeploymentWorkspacesCommand command2 = new ListDeploymentWorkspacesCommand( "list" );
        context = new CommandContext();
        context.setDeployerWorkingDirectory( testDeploymentDir );
        result = command2.execute( context );
        assertNotNull( result );
        assertNotNull( result.getData() );
        assertEquals( 1, ( (List) result.getData() ).size() );
    }
}
