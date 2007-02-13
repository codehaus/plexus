/**
 * 
 */
package org.codehaus.plexus.xsiter.commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;
import org.codehaus.plexus.xsiter.command.AbstractCommand;
import org.codehaus.plexus.xsiter.command.Command;
import org.codehaus.plexus.xsiter.command.CommandContext;
import org.codehaus.plexus.xsiter.command.CommandException;
import org.codehaus.plexus.xsiter.command.CommandResult;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;
import org.codehaus.plexus.xsiter.utils.DeployerUtils;

/**
 * Invokes Maven goals on the workspace project.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class InvokeMavenBuildCommand
    extends AbstractCommand
    implements Command
{

    /**
     * Maven 2.x executable name that we expect to find to be able to build and
     * deploy projects.
     */
    public static final String MAVEN_EXECUTABLE = "mvn";

    /**
     * Date Formatter.
     */
    private static final SimpleDateFormat sdf = new SimpleDateFormat( "dd-MM-yyyy, HH:mm:ss" );

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.xsiter.command.Command#execute(org.codehaus.plexus.xsiter.command.CommandContext)
     */
    public CommandResult execute( CommandContext context )
        throws CommandException
    {
        DeploymentWorkspace workspace = context.getWorkspace();

        if ( null == workspace.getScmURL() || workspace.getScmURL().trim().equals( "" ) )
            throw new CommandException( "Unable to perform checkout. No SCM URL found for workspace '"
                + workspace.getId() + "'" );

        CommandResult cmdResult = new CommandResult();

        File workspaceWorkingDir = new File( context.getDeployerWorkingDirectory(), workspace.getWorkingDirectory() );
        String scmTag = null != workspace.getScmTag() ? workspace.getScmTag() : "HEAD";

        File checkoutDir = new File( workspaceWorkingDir, scmTag );

        if ( !checkoutDir.exists() )
            throw new CommandException( "No check out directory exists. No project to invoke build on." );

        // XXX: It would be nice to have an attempt to check that 'mvn' command
        // was on $PATH

        Commandline cmd = new Commandline();
        cmd.setExecutable( MAVEN_EXECUTABLE );

        String goals = ""; // TODO: add hook to context.
        List argsList = DeployerUtils.string2List( goals, ' ' );
        for ( Iterator it = argsList.iterator(); it.hasNext(); )
        {
            String arg = (String) it.next();
            cmd.createArgument().setValue( arg );
        }
        cmd.setWorkingDirectory( checkoutDir.getAbsolutePath() );
        System.out.println( "Command to run is: " + cmd.toString() );
        int exitValue = -1;
        try
        {
            exitValue = CommandLineUtils.executeCommandLine( cmd, new DefaultConsumer(), new DefaultConsumer() );
            System.out.println( "Command returned exit value: " + exitValue );

            if ( exitValue != 0 )
                throw new CommandException( "Unable to execute command: '" + goals + "'" );

            System.out.println( "Process ID : " + cmd.getPid() );
            FileWriter fw = new FileWriter( new File( workspaceWorkingDir, "pid.txt" ), true );
            fw.append( "# PID for goal(s): '" + goals + "' run on " + sdf.format( new Date() ) );
            fw.write( "\n" + cmd.getPid() );
            fw.close();

        }
        catch ( CommandLineException e )
        {
            throw new CommandException( "Unable to execute command: '" + cmd.toString() + "'", e );
        }
        catch ( IOException e )
        {
            throw new CommandException( "Unable to execute command: '" + cmd.toString() + "'", e );
        }
        return null;
    }

}
