package org.codehaus.plexus.service.irc;

import org.codehaus.plexus.logging.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * An IRCServiceManager definition
 * User: aje
 * Date: 21-Jul-2006
 * Time: 22:10:27
 */
public interface IRCServiceManager
{

    public Logger getLogger();

    public boolean connect( String host, String nick, String username, String realname )
        throws IOException;

    public boolean connect( String host, String nick, String pass, String username, String realname )
        throws IOException;

    public void disconnect()
        throws IOException;

    public void disconnect( String reason )
        throws IOException;

    public void sendMessage( String to, String message );

    public void sendAction( String to, String action );

    public void sendNotice( String to, String message );

    public void join( String channel );

    public void part( String channel );

    public void part( String channel, String reason );

    public void op( String channel, String nick );

    public void deOp( String channel, String nick );

    public void voice( String channel, String nick );

    public void deVoice( String channel, String nick );

    public void kick( String channel, String nick );

    public void kick( String channel, String nick, String reason );

    public void ban( String channel, String mask );

    public void unBan( String channel, String mask );

    public Map getCommands();

    public List getListeners();

    public String getHost();

    public String getNick();

    public void setNick( String nick );

    public String getUsername();

    public String getRealname();

    public String getTopic( String channel );

    public void setTopic( String channel, String topic );

    public String getTime( String nick );
}
