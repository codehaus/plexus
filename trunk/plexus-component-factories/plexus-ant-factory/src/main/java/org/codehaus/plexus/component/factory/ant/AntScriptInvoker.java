package org.codehaus.plexus.component.factory.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.DemuxInputStream;
import org.apache.tools.ant.DemuxOutputStream;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.codehaus.plexus.component.MapOrientedComponent;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AntScriptInvoker
    extends AbstractLogEnabled
    implements MapOrientedComponent
{

    public static final String BASEDIR_PARAMETER = "basedir";

    public static final String MESSAGE_LEVEL_PARAMETER = "ant.messageLevel";

    private final ComponentDescriptor descriptor;

    private final URL script;

    private String target;

    private Map references = new HashMap();

    private Project project;

    private File basedir;

    private String messageLevel;

    public AntScriptInvoker( ComponentDescriptor descriptor, ClassLoader loader )
    {
        this.descriptor = descriptor;

        String impl = descriptor.getImplementation();

        int colon = impl.indexOf( ':' );

        String resourceName;
        if ( colon > -1 )
        {
            resourceName = impl.substring( 0, colon );
            target = impl.substring( colon + 1 );
        }
        else
        {
            resourceName = impl;
        }

        script = loader.getResource( resourceName );
    }

    public static String[] getImplicitRequiredParameters()
    {
        return new String[] { BASEDIR_PARAMETER };
    }

    public static String[] getImplicitOptionalParameters()
    {
        return new String[] { MESSAGE_LEVEL_PARAMETER };
    }

    public void addComponentRequirement( ComponentRequirement rd, Object rv )
        throws ComponentConfigurationException
    {
        if ( !descriptor.getRequirements().contains( rd ) )
        {
            throw new ComponentConfigurationException( "Requirement: " + rd.getHumanReadableKey()
                + " is not listed in this component's descriptor." );
        }

        references.put( rd.getRole() + "_" + rd.getRoleHint(), rv );
    }

    public void setComponentConfiguration( Map componentConfiguration )
        throws ComponentConfigurationException
    {
        references.putAll( componentConfiguration );

        this.basedir = (File) componentConfiguration.get( BASEDIR_PARAMETER );

        this.messageLevel = (String) componentConfiguration.get( MESSAGE_LEVEL_PARAMETER );
    }

    public void execute()
        throws AntComponentExecutionException
    {
        InputStream oldSysIn = System.in;
        PrintStream oldSysOut = System.out;
        PrintStream oldSysErr = System.err;

        try
        {
            initializeProject();

            project.setDefaultInputStream( System.in );

            System.setIn( new DemuxInputStream( project ) );
            System.setOut( new PrintStream( new DemuxOutputStream( project, false ) ) );
            System.setErr( new PrintStream( new DemuxOutputStream( project, true ) ) );

            project.fireBuildStarted();

            Throwable error = null;

            try
            {
                try
                {
                    ProjectHelper helper = new ProjectHelper2();

                    project.addReference( "ant.projectHelper", helper );

                    helper.parse( project, script );
                }
                catch ( BuildException ex )
                {
                    error = ex;
                    throw new AntComponentExecutionException( script, target, "Failed to parse.", ex );
                }

                for ( Iterator it = references.entrySet().iterator(); it.hasNext(); )
                {
                    Map.Entry entry = (Map.Entry) it.next();

                    String key = (String) entry.getKey();

                    project.addReference( key, entry.getValue() );
                }

                try
                {
                    project.executeTarget( target );
                }
                catch ( BuildException e )
                {
                    error = e;
                    throw new AntComponentExecutionException( script, target, "Failed to execute.", e );
                }
            }
            finally
            {
                project.fireSubBuildFinished( error );
            }

        }
        finally
        {
            // help the gc
            project = null;

            System.setIn( oldSysIn );
            System.setOut( oldSysOut );
            System.setErr( oldSysErr );
        }
    }

    private void initializeProject()
    {
        this.project = new Project();

        project.init();
        project.setUserProperty( "ant.version", Main.getAntVersion() );

        DefaultLogger antLogger = new DefaultLogger();
        antLogger.setOutputPrintStream( System.out );
        antLogger.setErrorPrintStream( System.err );

        int level = convertMsgLevel( messageLevel );

        getLogger().debug( "Ant message level is set to: " + messageLevel + "(" + level + ")" );

        antLogger.setMessageOutputLevel( level );

        project.addBuildListener( antLogger );
        
        project.setBaseDir( basedir );
    }

    protected int convertMsgLevel( String msgLevel )
    {
        int level = Project.MSG_ERR;
        
        msgLevel = msgLevel.toLowerCase();
        
        if ( msgLevel.equals( "error" ) )
        {
            level = Project.MSG_ERR;
        }
        else if ( msgLevel.equals( "warning" ) || msgLevel.equals( "warn" ) )
        {
            level = Project.MSG_WARN;
        }
        else if ( msgLevel.equals( "information" ) || msgLevel.equals( "info" ) )
        {
            level = Project.MSG_INFO;
        }
        else if ( msgLevel.equals( "debug" ) )
        {
            level = Project.MSG_DEBUG;
        }
        else if ( msgLevel.equals( "verbose" ) )
        {
            level = Project.MSG_VERBOSE;
        }
        else
        {
            getLogger().info( "Unknown Ant Message Level (" + msgLevel + ") -- using \"error\" instead" );
            level = Project.MSG_ERR;
        }
        
        return level;
    }

}
