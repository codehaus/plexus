package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.apache.avalon.framework.activity.Suspendable;

public class SuspendPhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( object instanceof Suspendable )
        {
            ( (Suspendable) object ).suspend();
        }
    }
}
