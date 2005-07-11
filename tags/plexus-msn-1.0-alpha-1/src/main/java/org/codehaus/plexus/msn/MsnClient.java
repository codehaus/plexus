package org.codehaus.plexus.msn;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id: IrcBot.java 1462 2005-02-09 15:47:38Z jvanzyl $
 */
public interface MsnClient
{
    String ROLE = MsnClient.class.getName();

    void login()
        throws MsnException;

    void logout()
        throws MsnException;

    void sendMessage( String receiver, String message )
        throws MsnException;

    void setLogin( String login );

    String getLogin();

    void setPassword( String password );
}
