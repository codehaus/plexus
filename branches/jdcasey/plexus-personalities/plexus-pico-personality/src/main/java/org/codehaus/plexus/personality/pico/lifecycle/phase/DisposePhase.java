package org.codehaus.plexus.personality.pico.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.picocontainer.Startable;
import org.picocontainer.Disposable;

/**
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 */
public class DisposePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {        
        if ( object instanceof Disposable )
        {
            Disposable disposable = ( Disposable ) object;

            disposable.dispose();
        }
    }
}
