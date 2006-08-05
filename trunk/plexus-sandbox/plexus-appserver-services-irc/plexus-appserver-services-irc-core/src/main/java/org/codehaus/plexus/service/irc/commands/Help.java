package org.codehaus.plexus.service.irc.commands;

import org.codehaus.plexus.service.irc.AbstractIRCCommand;
import org.codehaus.plexus.service.irc.IRCUser;
import org.codehaus.plexus.service.irc.IRCServiceManager;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: aje
 * Date: 29-Jul-2006
 * Time: 21:26:37
 * @plexus.component
 *   role="org.codehaus.plexus.service.irc.IRCCommand"
 *   role-hint="help"
 */
public class Help extends AbstractIRCCommand {

  /**
   * @plexus.requirement
   */
  private IRCServiceManager manager;

  public void onCommand(String channel, IRCUser user, String message) {
    manager.sendMessage(channel, "Plexus IRC Framework - the following commands are registered");
    Iterator commands = manager.getCommands().keySet().iterator();
    StringBuffer list = new StringBuffer("  ");
    while (commands.hasNext()) {
      String command = (String) commands.next();
      list.append(command);
      list.append(" ");
    }

    manager.sendMessage(channel, list.toString());
  }
}
