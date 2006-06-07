package org.codehaus.xfire.plexus.transport.xmpp;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.xmpp.XMPPTransport;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DefaultXMPPTransportService
    implements Initializable, Serviceable, Disposable
{
    private String username;
    private String password;
    private String server;
    private ServiceLocator locator;
    private XMPPTransport transport;
    
    /**
     * @throws Exception
     */
    public void initialize()
    {
        transport = new XMPPTransport(getXFire(), server, username, password);
        getTransportManager().register(transport);
    }

    public XFire getXFire()
    {
        try
        {
            return (XFire) locator.lookup(XFire.ROLE);
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

    public void dispose()
    {
        transport.dispose();
    }
}
