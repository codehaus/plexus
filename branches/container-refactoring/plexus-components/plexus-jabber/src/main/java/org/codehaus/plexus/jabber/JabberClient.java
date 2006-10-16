package org.codehaus.plexus.jabber;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
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

    String getHost();

    void setHost( String host );

    int getPort();

    void setPort( int port );

    boolean isSslConnection();

    void setSslConnection( boolean isSslConnection );

    String getUser();

    void setUser( String user );

    void setPassword( String password );

    String getImDomainName();

    void setImDomainName( String imDomainName );
}
