package org.codehaus.plexus.appserver.lifecycle;

/**
 * @author Jason van Zyl
 */
public class AppServerLifecycleException
    extends Exception
{
    public AppServerLifecycleException( String string )
    {
        super( string );
    }

    public AppServerLifecycleException( String string, Throwable throwable )
    {
        super( string, throwable );
    }

    public AppServerLifecycleException( Throwable throwable )
    {
        super( throwable );
    }
}
