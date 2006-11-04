package org.codehaus.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;

/**
 * @author Jason van Zyl
 */
public class ExecutePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
    {
        if ( object instanceof Executable )
        {
            ( (Executable) object ).execute();
        }
    }
}
