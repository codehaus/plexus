package org.codehaus.plexus.jabber;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
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
