package org.codehaus.plexus.xfire;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.ServiceFactory;

// use continuum/MRM as a use cases
// creating services
// using qdox/annotations to drive
//   - methods to expose in the service
//   - generation and use of any DTOs used to proxy objects in the datamodel
// most convenient way to expose a component as a set of web services

/**
 * @author Jason van Zyl
 */
public class DefaultServiceCreator
    implements ServiceCreator, Initializable
{
    private XFire xfire;

    private ServiceFactory serviceFactory;

    private PlexusServiceConfiguration serviceConfiguration;

    public void initialize()
        throws InitializationException
    {
        ((PlexusServiceFactory)serviceFactory).getServiceConfigurations().add( serviceConfiguration );
    }
}
