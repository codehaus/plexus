package org.codehaus.plexus.xfire;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;

/**
 * Basic support methods for xfire XFire components.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PlexusXFireComponent
	extends AbstractLogEnabled
	implements Serviceable
{
    private ServiceLocator manager;
    
    public void service( ServiceLocator manager )
    {
        this.manager = manager;
    }
    
    /**
     * @return Returns the service manager.
     */
    protected ServiceLocator getServiceLocator()
    {
        return manager;
    }
}
