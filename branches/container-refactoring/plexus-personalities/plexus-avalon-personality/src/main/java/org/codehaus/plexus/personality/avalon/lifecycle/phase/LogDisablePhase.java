package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.logger.LogEnabled;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.logging.LoggerManager;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LogDisablePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        if ( object instanceof LogEnabled )
        {
            PlexusContainer container = manager.getContainer();

            LoggerManager lm = (LoggerManager) container.lookup( LoggerManager.ROLE );

            String role = manager.getComponentDescriptor().getRole();
            String roleHint = manager.getComponentDescriptor().getRoleHint();

            if ( roleHint != null )
            {
                lm.returnComponentLogger( role, roleHint );
            }
            else
            {
                lm.returnComponentLogger( role );
            }
        }
    }
}
