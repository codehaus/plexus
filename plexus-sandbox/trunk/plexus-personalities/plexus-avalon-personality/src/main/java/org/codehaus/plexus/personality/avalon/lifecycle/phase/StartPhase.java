package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.activity.Startable;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public class StartPhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( object instanceof Startable )
        {
            try
            {
                ( (Startable) object ).start();
            }
            catch ( Exception e )
            {
                throw new PhaseExecutionException( "start threw Exception", e );                
            }
        }
    }
}
