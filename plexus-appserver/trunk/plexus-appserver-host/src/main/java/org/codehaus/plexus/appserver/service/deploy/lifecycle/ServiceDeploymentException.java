package org.codehaus.plexus.appserver.service.deploy.lifecycle;

import org.codehaus.plexus.appserver.ApplicationServerException;

/**
 * @author Jason van Zyl
 */
public class ServiceDeploymentException
    extends ApplicationServerException
{
    public ServiceDeploymentException( String string )
    {
        super( string );
    }

    public ServiceDeploymentException( String string, Throwable throwable )
    {
        super( string, throwable );
    }

    public ServiceDeploymentException( Throwable throwable )
    {
        super( null, throwable );
    }
}
