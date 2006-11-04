package org.codehaus.plexus.personality.avalon;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
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

public class AvalonServiceManager
    implements ServiceManager, Contextualizable
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
    // Avalon ServiceManager API
    // ----------------------------------------------------------------------

    /**
     * @see org.codehaus.plexus.component.repository.ComponentRepository#lookup(java.lang.String)
     */
    public Object lookup( String role )
        throws ServiceException
    {
        try
        {
            return container.lookup( role );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServiceException( role, "Cannot find component: " + role, e);
        }
    }

    /**
     * @see org.codehaus.plexus.component.repository.ComponentRepository#hasService(java.lang.String)
     */
    public boolean hasService( String role )
    {
        return container.hasComponent( role );
    }

    /**
     * @see org.codehaus.plexus.component.repository.ComponentRepository#release(java.lang.Object)
     */
    public void release( Object service )
    {
        try
        {
            container.release( service );
        }
        catch(Exception ex)
        {
            // ignore
        }
    }
}
