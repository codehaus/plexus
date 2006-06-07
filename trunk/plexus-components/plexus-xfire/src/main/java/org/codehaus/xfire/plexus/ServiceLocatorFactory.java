package org.codehaus.xfire.plexus;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.util.factory.Factory;


/**
 * This is a simple adapter from plexus service locator to {@link Factory}
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:ajoo.email@gmail.com">Ben Yu</a>
 * @since Nov 18, 2004
 */
public class ServiceLocatorFactory implements Factory
{
    //private static Log logger = LogFactory.getLog(ServiceInvoker.class.getName());
    
    private ServiceLocator locator;
    private String role;
    
    public ServiceLocatorFactory(String role, ServiceLocator locator){
        this.role = role;
        this.locator = locator;
    }

    public Object create()
        throws XFireFault
    {
        try
        {
            return locator.lookup(role);
        }
        catch (ComponentLookupException e)
        {
            throw new XFireFault("Couldn't find service object.", e, XFireFault.RECEIVER);
        }
    }

}
