package org.codehaus.plexus.jabber;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id: IrcBot.java 1462 2005-02-09 15:47:38Z jvanzyl $
 */
public class JabberClientException
    extends Exception
{
    public JabberClientException( String message )
    {
        super( message );
    }

    public JabberClientException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
}
