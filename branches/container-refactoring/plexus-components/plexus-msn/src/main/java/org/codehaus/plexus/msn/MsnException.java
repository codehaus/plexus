package org.codehaus.plexus.msn;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id: IrcBot.java 1462 2005-02-09 15:47:38Z jvanzyl $
 */
public class MsnException
    extends Exception
{
    public MsnException( String message )
    {
        super( message );
    }

    public MsnException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
}
