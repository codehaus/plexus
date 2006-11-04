package org.codehaus.plexus.appserver.application.deploy.lifecycle;

/**
 * @author Jason van Zyl
 */
public class AppDeploymentException
    extends Exception
{
    public AppDeploymentException( String string )
    {
        super( string );
    }

    public AppDeploymentException( String string, Throwable throwable )
    {
        super( string, throwable );
    }

    public AppDeploymentException( Throwable throwable )
    {
        super( throwable );
    }
}
