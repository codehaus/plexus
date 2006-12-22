package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.ContextException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.avalon.AvalonContext;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public class ContextualizePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
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

            try
            {
                ( (Contextualizable) object ).contextualize( context );
            }
            catch ( ContextException e )
            {
                throw new PhaseExecutionException( "contextualize threw ContextException", e );                
            }
        }
    }
}
