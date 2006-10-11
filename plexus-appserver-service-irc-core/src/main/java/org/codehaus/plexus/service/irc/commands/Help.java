package org.codehaus.plexus.service.irc.commands;

import org.codehaus.plexus.service.irc.AbstractIRCCommand;
import org.codehaus.plexus.service.irc.IRCUser;
import org.codehaus.plexus.service.irc.IRCServiceManager;
import org.codehaus.plexus.service.irc.IRCCommand;

import java.util.Iterator;
import java.util.Map;

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
    Map commands = manager.getCommands();

    if (message == null || message.equals("")) {
      manager.sendMessage(channel, "Plexus IRC Framework - the following commands are registered");
      Iterator commandIter = commands.keySet().iterator();
      StringBuffer list = new StringBuffer("  ");
      while (commandIter.hasNext()) {
        String command = (String) commandIter.next();
        list.append(command);
        list.append(" ");
      }

      manager.sendMessage(channel, list.toString());
    } else {
      String command, parameters;
      int space;
      if ((space = message.indexOf(' ')) == -1) {
        command = message;
        parameters = "";
      } else {
        command = message.substring(0, space);
        parameters = message.substring(space + 1).trim();
      }

      IRCCommand ircCommand = (IRCCommand) commands.get(command);
      if (ircCommand == null) {
        manager.sendMessage(channel, "Command \"" + command + "\" not found");
      } else {
        manager.sendMessage(channel, ircCommand.getHelp(parameters));
      }
    }

  }
}
