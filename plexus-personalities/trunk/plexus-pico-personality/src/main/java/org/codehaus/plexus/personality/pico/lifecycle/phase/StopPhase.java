package org.codehaus.plexus.personality.pico.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.picocontainer.Startable;

/**
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 */
public class StopPhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        //this is not misatke :)
        // Startable interface has stop method
        if ( object instanceof Startable )
        {
            Startable startable = ( Startable ) object;

            startable.stop();
        }
    }
}
