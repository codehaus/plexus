package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.activity.Disposable;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public class DisposePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( object instanceof Disposable )
        {
            ( (Disposable) object ).dispose();
        }
    }
}
