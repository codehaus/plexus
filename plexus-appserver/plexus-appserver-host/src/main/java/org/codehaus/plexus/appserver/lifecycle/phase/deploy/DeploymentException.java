package org.codehaus.plexus.appserver.lifecycle.phase.deploy;

/**
 * @author Jason van Zyl
 */
public class DeploymentException
    extends Exception
{
    public DeploymentException( String string )
    {
        super( string );
    }

    public DeploymentException( String string,
                                Throwable throwable )
    {
        super( string, throwable );
    }

    public DeploymentException( Throwable throwable )
    {
        super( throwable );
    }
}
