package org.codehaus.plexus.personality.avalon;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

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
    extends AbstractLogEnabled
    implements ServiceManager
{
    /** Plexus component repository. */
    private PlexusContainer container;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     *
     * @todo We shouldn't be handing the parent container to the child here.
     */
    public AvalonServiceManager( PlexusContainer container )
    {
        if ( container == null )
        {
            throw new IllegalStateException( "PlexusContainer is null." );
        }

        this.container = container;
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
        container.release( service );
    }
}
