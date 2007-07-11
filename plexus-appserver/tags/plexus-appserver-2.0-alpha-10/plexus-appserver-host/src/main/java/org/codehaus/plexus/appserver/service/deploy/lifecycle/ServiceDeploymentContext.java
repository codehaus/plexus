package org.codehaus.plexus.appserver.service.deploy.lifecycle;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class ServiceDeploymentContext
{
    private String serviceId;

    private File sar;

    private File servicesDirectory;

    private DefaultPlexusContainer container;

    // ----------------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------------

    private File serviceDirectory;

    private ClassRealm realm;

    public ServiceDeploymentContext( String serviceId, File sar, File servicesDirectory,
        DefaultPlexusContainer container )
        throws ServiceDeploymentException
    {
        this.serviceId = serviceId;
        this.sar = sar;
        this.servicesDirectory = servicesDirectory;
        this.container = container;
        this.realm = container.getContainerRealm();

//
//        try
//        {
//            this.realm = container.getClassWorld().newRealm( "service." + serviceId, container.getContainerRealm() );
//        }
//        catch ( DuplicateRealmException e )
//        {
//            try
//            {
//                container.getClassWorld().disposeRealm( "service." + serviceId );
//                this.realm = container.getClassWorld().newRealm( "service." + serviceId, container.getContainerRealm() );
//            }
//            catch ( NoSuchRealmException e1 )
//            {
//                throw new ServiceDeploymentException( "Cannot create service realm", e1 );
//            }
//            catch ( DuplicateRealmException e1 )
//            {
//                throw new ServiceDeploymentException( "Cannot create service realm", e1 );
//            }
//        }
    }

    public String getServiceId()
    {
        return serviceId;
    }

    public File getSar()
    {
        return sar;
    }

    public File getServicesDirectory()
    {
        return servicesDirectory;
    }

    public DefaultPlexusContainer getContainer()
    {
        return container;
    }

    // ----------------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------------

    public void setServiceDirectory( File serviceDirectory )
    {
        this.serviceDirectory = serviceDirectory;
    }

    public File getServiceDirectory()
    {
        return serviceDirectory;
    }

    public ClassRealm getRealm()
    {
        return realm;
    }
}
