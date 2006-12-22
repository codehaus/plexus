package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Recontextualizable;
import org.apache.avalon.framework.context.ContextException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.avalon.AvalonContext;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public class RecontextualizePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( object instanceof Recontextualizable )
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
                ( (Recontextualizable) object ).recontextualize( context );
            }
            catch ( ContextException e )
            {
                throw new PhaseExecutionException( "recontextualize threw ContextException", e );                
            }
        }
    }
}
