package org.codehaus.plexus.personality.avalon;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * Extended <code>ServiceManager</code> implementation.
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 *
 * @version $Id$
 */

public class AvalonComponentManager
    implements ComponentManager, Contextualizable
{
    /** Plexus component repository. */
    private PlexusContainer container;

    // ----------------------------------------------------------------------
    // Contextualizable
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    // ----------------------------------------------------------------------
    // Avalon ComponentManager API
    // ----------------------------------------------------------------------

    public Component lookup( String role )
        throws ComponentException
    {
        try
        {
            return (Component) container.lookup( role );
        }
        catch ( ComponentLookupException e )
        {
            throw new ComponentException( role, "Cannot find component: " + role, e);
        }
    }

    public boolean hasComponent( String role )
    {
        return container.hasComponent( role );
    }

    public void release( Component component )
    {
        try
        {
            container.release( component );
        }
        catch(Exception ex)
        {
            // ignore
        }
    }
}
