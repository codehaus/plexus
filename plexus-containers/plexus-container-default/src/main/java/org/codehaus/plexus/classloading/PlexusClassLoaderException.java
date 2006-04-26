package org.codehaus.plexus.classloading;

/**
 * Created by IntelliJ IDEA.
 * User: jdcasey
 * Date: Apr 13, 2006
 * Time: 9:57:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlexusClassLoaderException extends RuntimeException
{
    public PlexusClassLoaderException( String message )
    {
        super( message );
    }

    public PlexusClassLoaderException( String message, Throwable cause )
    {
        super( message, cause );
    }
    
}
