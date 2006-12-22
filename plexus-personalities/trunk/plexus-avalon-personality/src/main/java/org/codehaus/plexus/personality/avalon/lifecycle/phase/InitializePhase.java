package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.activity.Initializable;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public class InitializePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( object instanceof Initializable )
        {
            try
            {
                ( (Initializable) object ).initialize();
            }
            catch ( Exception e )
            {
                throw new PhaseExecutionException( "initialize threw Exception", e );                
            }
        }
    }
}
