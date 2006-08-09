package org.codehaus.plexus.xfire.old;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.service.ServiceRegistry;

/**
 * An instance of XFire that is managed by Plexus.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 19, 2004
 */
public class PlexusXFire
        extends DefaultXFire
        implements Serviceable
{
    private ServiceRegistry serviceRegistry;

    private TransportManager transportManager;

    private ServiceLocator locator;

    public PlexusXFire()
    {
        super();
    }

    public ServiceRegistry getServiceRegistry()
    {
        try
        {
            return (ServiceRegistry) locator.lookup(ServiceRegistry.ROLE);
        }
        catch (ComponentLookupException e)
        {
            throw new XFireRuntimeException("Couldn't find component.", e);
        }
    }

    public TransportManager getTransportManager()
    {
        try
        {
            return (TransportManager) locator.lookup(TransportManager.ROLE);
        }
        catch (ComponentLookupException e)
        {
            throw new XFireRuntimeException("Couldn't find component.", e);
        }
    }

    /**
     * @see org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable#service(org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator)
     */
    public void service(ServiceLocator locator)
    {
        this.locator = locator;
    }
}
