package org.codehaus.plexus.spe;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ProcessException
    extends Exception
{
    public ProcessException( String message )
    {
        super( message );
    }

    public ProcessException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
