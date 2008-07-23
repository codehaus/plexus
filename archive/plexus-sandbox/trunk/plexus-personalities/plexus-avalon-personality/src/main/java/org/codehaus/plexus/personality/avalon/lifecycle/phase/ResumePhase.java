package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.apache.avalon.framework.activity.Suspendable;

public class ResumePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager, ClassRealm realm )
        throws PhaseExecutionException
    {
        if ( object instanceof Suspendable )
        {
            ( (Suspendable) object ).resume();
        }
    }
}
