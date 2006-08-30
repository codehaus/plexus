package org.codehaus.plexus.appserver.service.deploy.lifecycle;

import org.codehaus.plexus.DefaultPlexusContainer;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class ServiceDeploymentContext
{
    String serviceId;

    File sar;

    File servicesDirectory;

    DefaultPlexusContainer container;

    // ----------------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------------

    File serviceDirectory;

    public ServiceDeploymentContext( String serviceId, File sar, File servicesDirectory,
                                     DefaultPlexusContainer container )
    {
        this.serviceId = serviceId;
        this.sar = sar;
        this.servicesDirectory = servicesDirectory;
        this.container = container;
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
}
