package org.codehaus.plexus.personality.avalon;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
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

public class AvalonComponentManager
    extends AbstractLogEnabled
    implements ComponentManager
{
    private PlexusContainer container;

    public AvalonComponentManager( PlexusContainer container )
    {
        if ( container == null )
        {
            throw new IllegalStateException( "PlexusContainer is null." );
        }

        this.container = container;
    }

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
        container.release( component );
    }
}
