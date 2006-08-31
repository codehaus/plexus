package org.codehaus.plexus.rmi;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class RmiServiceException
    extends Exception
{
    public RmiServiceException( String message )
    {
        super( message );
    }

    public RmiServiceException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
