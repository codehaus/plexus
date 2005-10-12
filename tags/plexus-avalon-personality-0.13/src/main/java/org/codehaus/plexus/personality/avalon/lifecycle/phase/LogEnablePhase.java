package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.personality.avalon.AvalonLogger;

/**
 * @version $Id$
 */
public class LogEnablePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        if ( object instanceof LogEnabled )
        {
            PlexusContainer container = manager.getContainer();

            LoggerManager lm = (LoggerManager) container.lookup( LoggerManager.ROLE );

            Logger logger = null;
            
            String role = manager.getComponentDescriptor().getRole();
            String roleHint = manager.getComponentDescriptor().getRoleHint();
            
            if ( roleHint != null )
            {
                logger = new AvalonLogger( lm.getLoggerForComponent( role, roleHint ) );
            }
            else
            {
                logger = new AvalonLogger( lm.getLoggerForComponent( role ) );
            }

            if ( null == logger )
            {
                final String message = "logger is null";

                throw new IllegalArgumentException( message );
            }

            ( (LogEnabled) object ).enableLogging( logger );
        }
    }
}
