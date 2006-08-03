package org.codehaus.plexus.service.irc;

/**
 * Created by IntelliJ IDEA.
 * User: aje
 * Date: 29-Jul-2006
 * Time: 21:27:51
 */
public abstract class AbstractIRCCommand implements IRCCommand {
  public void onCommand(String channel, IRCUser user, String message) { }

  public void onPrivateCommand(IRCUser user, String message) {
    onCommand(user.getNick(), user, message);
  }
}
