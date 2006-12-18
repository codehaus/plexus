package org.codehaus.plexus.xsiter.command;

import java.io.File;
import java.util.List;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.xsiter.commands.CheckoutProjectCommand;
import org.codehaus.plexus.xsiter.commands.CreateDeploymentWorkspaceCommand;
import org.codehaus.plexus.xsiter.commands.ListDeploymentWorkspacesCommand;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;

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
        CreateDeploymentWorkspaceCommand command = null;
        try
        {
            command = (CreateDeploymentWorkspaceCommand) lookup( Command.ROLE, "create" );
        }
        catch ( Exception e )
        {
            fail( "Unexpected Exception while looking up " + CreateDeploymentWorkspaceCommand.class.getName() );
        }
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
        CreateDeploymentWorkspaceCommand command = null;
        try
        {
            command = (CreateDeploymentWorkspaceCommand) lookup( Command.ROLE, "create" );
        }
        catch ( Exception e )
        {
            fail( "Unexpected Exception while looking up " + CreateDeploymentWorkspaceCommand.class.getName() );
        }
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

    public void testCheckoutProjectWithoutSCMUrl()
    {
        CreateDeploymentWorkspaceCommand command = null;
        try
        {
            command = (CreateDeploymentWorkspaceCommand) lookup( Command.ROLE, "create" );
        }
        catch ( Exception e )
        {
            fail( "Unexpected Exception while looking up " + CreateDeploymentWorkspaceCommand.class.getName() );
        }
        CommandContext context = new CommandContext();

        context.setDeployerWorkingDirectory( testDeploymentDir );
        // set up a deployment workspace for test 
        DeploymentWorkspace workspace = new DeploymentWorkspace();
        context.setWorkspace( workspace );
        assertNotNull( context.getWorkspace() );
        workspace.setid( "xsiter-web" );

        // verify result
        CommandResult result = null;
        try
        {
            result = command.execute( context );
        }
        catch ( CommandException e )
        {
            fail( "Unexpected Exception while creating project" );
        }
        assertNotNull( result );
        assertNotNull( result.getMessages() );
        assertEquals( 1, result.getMessages().size() );
        assertSame( ResultState.SUCCESS, result.getState() );

        // verify deployment workspace create
        assertTrue( testDeploymentDir.exists() );
        assertTrue( new File( testDeploymentDir, "xsiter-web/workspace.xml" ).exists() );

        // Attempt to check out project
        CheckoutProjectCommand checkoutCommand = new CheckoutProjectCommand( "checkout" );
        try
        {
            checkoutCommand.execute( context );
            fail( "Expected CommandException while checking out project with no SCM URL." );
        }
        catch ( CommandException e )
        {
            // do nothing.
        }

    }

    public void testCheckoutProjectWithSCMUrl()
        throws Exception
    {
        CreateDeploymentWorkspaceCommand command = null;
        try
        {
            command = (CreateDeploymentWorkspaceCommand) lookup( Command.ROLE, "create" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            fail( "Unexpected Exception looking up component: "
                + CreateDeploymentWorkspaceCommand.class.getSimpleName() );
        }
        CommandContext context = new CommandContext();

        context.setDeployerWorkingDirectory( testDeploymentDir );
        // set up a deployment workspace for test 
        DeploymentWorkspace workspace = new DeploymentWorkspace();
        workspace.setScmURL( "scm:svn:http://svn.codehaus.org/plexus/plexus-sandbox/trunk/plexus-xsiter/xsiter-web" );
        workspace.setScmUsername( "" );
        workspace.setScmPassword( "" );
        workspace.setScmTag( "HEAD" );
        context.setWorkspace( workspace );
        assertNotNull( context.getWorkspace() );
        workspace.setid( "xsiter-web" );

        // verify result
        CommandResult result = null;
        try
        {
            result = command.execute( context );
        }
        catch ( CommandException e )
        {
            fail( "Unexpected Exception while creating project" );
        }
        assertNotNull( result );
        assertNotNull( result.getMessages() );
        assertEquals( 1, result.getMessages().size() );
        assertSame( ResultState.SUCCESS, result.getState() );

        // verify deployment workspace create
        assertTrue( testDeploymentDir.exists() );
        assertTrue( new File( testDeploymentDir, "xsiter-web/workspace.xml" ).exists() );

        // Attempt to check out project
        CheckoutProjectCommand checkoutCommand = new CheckoutProjectCommand( "checkout" );
        try
        {
            checkoutCommand.execute( context );
        }
        catch ( CommandException e )
        {
            e.printStackTrace();
            fail( "UnExpected CommandException while checking out project with no SCM URL." );
        }

    }
}
