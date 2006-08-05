package org.codehaus.plexus.service.irc;

/**
 * A Command Stub
 * User: aje
 * Date: 22-Jul-2006
 * Time: 19:47:51
 */
public class CommandStub implements IRCCommand {

  private IRCServiceManager manager;

  public void onCommand(String channel, IRCUser user, String message) {
    manager.sendMessage(channel, "Got your command with params \"" + message + "\", " + user.getNick());
  }

  public void onPrivateCommand(IRCUser user, String message) {
    
  }
}
