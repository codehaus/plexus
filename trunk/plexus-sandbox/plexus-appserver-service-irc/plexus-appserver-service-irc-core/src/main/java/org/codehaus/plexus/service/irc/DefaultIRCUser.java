package org.codehaus.plexus.service.irc;

/**
 * Created by IntelliJ IDEA.
 * User: aje
 * Date: 22-Jul-2006
 * Time: 19:13:59
 * To change this template use File | Settings | File Templates.
 */
public class DefaultIRCUser implements IRCUser {

  private String nick, login, host;

  public DefaultIRCUser(String nick, String login, String host) {
    this.nick = nick;
    this.login = login;
    this.host = host;
  }

  public DefaultIRCUser(org.schwering.irc.lib.IRCUser user) {
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
