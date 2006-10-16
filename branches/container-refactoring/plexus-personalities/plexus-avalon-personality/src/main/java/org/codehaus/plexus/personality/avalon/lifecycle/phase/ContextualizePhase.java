package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.avalon.AvalonContext;

public class ContextualizePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        if ( object instanceof Contextualizable )
        {
            PlexusContainer container = manager.getContainer();

            Context context = new AvalonContext( container.getContext() );

            if ( null == context )
            {
                final String message = "context is null";

                throw new IllegalArgumentException( message );
            }

            ( (Contextualizable) object ).contextualize( context );
        }
    }
}
