package org.codehaus.plexus.service.irc;

/**
 * Created by IntelliJ IDEA.
 * User: aje
 * Date: 22-Jul-2006
 * Time: 19:13:59
 * To change this template use File | Settings | File Templates.
 */
public class IRCUser {

  private String nick, login, host;

  public IRCUser(String nick, String login, String host) {
    this.nick = nick;
    this.login = login;
    this.host = host;
  }

  public IRCUser(org.schwering.irc.lib.IRCUser user) {
    if (user == null)
      return;
    this.nick = user.getNick();
    this.login = user.getUsername();
    this.host = user.getHost();
  }

  public String getNick() {
    return nick;
  }

  public String getLogin() {
    return login;
  }

  public String getHost() {
    return host;
  }
}
