package org.codehaus.plexus.spe.action;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.codehaus.plexus.action.AbstractAction;

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AntRunAction
    extends AbstractAction
{
    private Target target;

    public void execute( Map context )
        throws Exception
    {
        Project antProject = target.getProject();

        DefaultLogger antLogger = new DefaultLogger();
        antLogger.setOutputPrintStream( System.out );
        antLogger.setErrorPrintStream( System.err );
        antLogger.setMessageOutputLevel( getLogger().isDebugEnabled() ? Project.MSG_DEBUG : Project.MSG_INFO );
        antLogger.setMessageOutputLevel( Project.MSG_DEBUG);

        antProject.addBuildListener( antLogger );
        //TODO: antProject.setBaseDir(  );

        if ( getLogger().isInfoEnabled() )
        {
            getLogger().info( "Executing tasks" );
        }

        target.execute();

        if ( getLogger().isInfoEnabled() )
        {
            getLogger().info( "Executed tasks" );
        }
    }
}
