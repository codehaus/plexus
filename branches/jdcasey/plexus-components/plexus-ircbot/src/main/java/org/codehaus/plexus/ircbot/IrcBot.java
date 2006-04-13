package org.codehaus.plexus.ircbot;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface IrcBot
{
    String ROLE = IrcBot.class.getName();

    void connect( String serverHostname, int serverPort );
    
    void connect( String serverHostname, int serverPort, String botName );    

    void disconnect();

    boolean ircsend( String message );

    void logoff();

    void logon();

    boolean sendMessageToChannel( String channel, String message );

    boolean sendNotice( String username, String message );

    boolean sendPrivateMessage( String username, String message );

    void service();

    String getLogin();

    void setLogin( String login );

    String getFullName();

    void setFullName( String fullName );

    void setPassword( String password );
}
