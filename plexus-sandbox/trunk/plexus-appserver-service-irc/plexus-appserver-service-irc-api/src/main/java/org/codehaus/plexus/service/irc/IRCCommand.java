package org.codehaus.plexus.service.irc;

/**
 * Basic listeners for an IRC command
 * User: aje
 * Date: 22-Jul-2006
 * Time: 19:13:25
 * To change this template use File | Settings | File Templates.
 */
public interface IRCCommand {

  public void onCommand(String channel, IRCUser user, String message);

  public void onPrivateCommand(IRCUser user, String message);

  public String getHelp(String parameters);
}
