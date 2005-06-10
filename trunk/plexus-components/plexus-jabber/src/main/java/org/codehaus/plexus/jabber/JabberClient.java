package org.codehaus.plexus.jabber;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id: IrcBot.java 1462 2005-02-09 15:47:38Z jvanzyl $
 */
public interface JabberClient
{
    static final String ROLE = JabberClient.class.getName();

    void connect()
        throws JabberClientException;

    void disconnect()
        throws JabberClientException;

    void logon()
        throws JabberClientException;

    void logoff()
        throws JabberClientException;

    void sendMessageToUser( String recipient, String message )
        throws JabberClientException;

    void sendMessageToGroup( String recipient, String message )
        throws JabberClientException;
}
