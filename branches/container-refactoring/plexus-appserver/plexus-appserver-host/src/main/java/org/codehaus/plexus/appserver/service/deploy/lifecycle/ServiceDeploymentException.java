package org.codehaus.plexus.appserver.service.deploy.lifecycle;

/**
 * @author Jason van Zyl
 */
public class ServiceDeploymentException
    extends Exception
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
        super( throwable );
    }
}
