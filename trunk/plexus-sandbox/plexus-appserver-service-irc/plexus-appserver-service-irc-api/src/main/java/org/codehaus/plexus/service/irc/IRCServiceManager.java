package org.codehaus.plexus.service.irc;

import java.io.IOException;
import java.util.Map;

/**
 * An IRCServiceManager definition
 * User: aje
 * Date: 21-Jul-2006
 * Time: 22:10:27
 */
public interface IRCServiceManager {

  public boolean connect(String host, String nick, String username,
                         String realname) throws IOException;

  public boolean connect(String host, String nick, String pass,
                         String username, String realname)
      throws IOException;

  public void disconnect() throws IOException;

  public void disconnect(String reason) throws IOException;

  public void sendMessage(String to, String message);

  public void sendAction(String to, String action);

  public void join(String channel);

  public void part(String channel);

  public void part(String channel, String reason);

  public Map getCommands();

  public String getHost();

  public String getNick();

  public void setNick(String nick);

  public String getUsername();

  public String getRealname();
}
