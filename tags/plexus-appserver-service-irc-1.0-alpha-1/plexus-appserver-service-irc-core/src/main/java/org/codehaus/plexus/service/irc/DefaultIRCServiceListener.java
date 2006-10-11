package org.codehaus.plexus.service.irc;

import org.schwering.irc.lib.*;
import org.schwering.irc.lib.IRCUser;
import org.codehaus.plexus.logging.Logger;

import java.util.Iterator;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: aje
 * Date: 22-Jul-2006
 * Time: 19:25:52
 * To change this template use File | Settings | File Templates.
 */
public class DefaultIRCServiceListener implements IRCEventListener {

  private static String ADDRESS_CHARS = ":,";
  private static String INTRO_CHARS = "!~'";

  private IRCServiceManager manager;

  public DefaultIRCServiceListener(IRCServiceManager manager) {
    this.manager = manager;
  }

  private void handleException(IRCListener listener, Exception e) {
    writeException("IRCListener", listener.getClass().getName(), e);
  }

  private void handleException(IRCCommand command, Exception e) {
    writeException("IRCCommand", command.getClass().getName(), e);
  }

  private void writeException(String type, String source, Exception e) {
    Logger log = manager.getLogger();
    log.error("-------------------------------------------------");
    log.error(" Plexus IRC Framework detected an exception in   ");
    log.error(" " + type + ": " + source, e);
    log.error("-------------------------------------------------");
  }

  public void onRegistered() {
    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onRegistered();
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onDisconnected() {
    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onDisconnected();
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onError(String message) {
    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onError(message);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onError(int num, String message) {
    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onError(num, message);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onInvite(String channel, IRCUser user, String invitee) {
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new DefaultIRCUser(user);

    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onInvite(channel, ircUser, invitee);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onJoin(String channel, IRCUser user) {
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new DefaultIRCUser(user);

    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onJoin(channel, ircUser);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onKick(String channel, IRCUser user, String kickee, String message) {
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new DefaultIRCUser(user);

    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onKick(channel, ircUser, kickee, message);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onMode(String channel, IRCUser user, IRCModeParser modeParser) {
/* FIXME - figure out the modeparser stuff - do we need it?
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new org.codehaus.plexus.service.irc.IRCUser(user);

    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onMode(channel, ircUser, modeParser);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
*/
  }

  public void onMode(IRCUser user, String string, String mode) {
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new DefaultIRCUser(user);

    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onMode(ircUser, string, mode);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onNick(IRCUser user, String newNick) {
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new DefaultIRCUser(user);

    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onNick(ircUser, newNick);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onNotice(String target, IRCUser user, String message) {
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new DefaultIRCUser(user);

    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onNotice(target, ircUser, message);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onPart(String channel, IRCUser user, String message) {
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new DefaultIRCUser(user);

    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onPart(channel, ircUser, message);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onPing(String ping) {
    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onPing(ping);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  private void doCommand(String target, org.codehaus.plexus.service.irc.IRCUser user,
                         String message) {
    String command = message.split(" ")[0];
    IRCCommand match = (IRCCommand) manager.getCommands().get(command);
    if (match == null)
      return;
    String rest = message.substring(command.length()).trim();

    try {
      if (target.equals(manager.getNick())) {
        match.onPrivateCommand(user, rest);
      } else {
        match.onCommand(target, user, rest);
      }
    } catch (Exception e) {
      handleException(match, e);
    }
  }

  public void onPrivmsg(String target, IRCUser user, String message) {
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new DefaultIRCUser(user);

    if (message.startsWith("ACTION ")) {
      String theMessage = message.substring(7).trim();
      onAction(target, user, theMessage);
      return;
    } else if (message.equals("VERSION")) {
      onVersion(target, user);
      return;
    } else if (message.equals("TIME")) {
      onTime(target, user);
      return;
    }


    /* addressed through a shortcut */
    if (message.length() > 0 && isIntroChar(message.charAt(0))) {
      doCommand(target, ircUser, message.substring(1).trim());
      return;
    } else {
      /* addressed using our nick */
      String arg1 = message.split(" ")[0];
      if (arg1 != null && arg1.length() != 0 &&
          isAddressChar(arg1.charAt(arg1.length() - 1))) {
        arg1 = arg1.substring(0, arg1.length() - 1);

        if (arg1.equals(manager.getNick())) {
          doCommand(target, ircUser, message.substring(arg1.length() + 1).trim());
          return;
        }
      }
    }

    if (target.equals(manager.getNick())) {
      /* private commands */
      doCommand(target, ircUser, message);

      Iterator listenerIter = manager.getListeners().iterator();
      while (listenerIter.hasNext()) {
        IRCListener next = (IRCListener) listenerIter.next();
        try {
          next.onPrivateMessage(ircUser, message);
        } catch (Exception e) {
          handleException(next, e);
        }
      }
    } else {
      Iterator listenerIter = manager.getListeners().iterator();
      while (listenerIter.hasNext()) {
        IRCListener next = (IRCListener) listenerIter.next();
        try {
          next.onMessage(target, ircUser, message);
        } catch (Exception e) {
          handleException(next, e);
        }
      }
    }
  }

  public void onQuit(IRCUser user, String message) {
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new DefaultIRCUser(user);

    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onQuit(ircUser, message);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onReply(int num, String value, String message) {
    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onReply(num, value, message);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onTopic(String channel, IRCUser user, String topic) {
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new DefaultIRCUser(user);

    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onTopic(channel, ircUser, topic);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onAction(String target, IRCUser user, String action) {
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new DefaultIRCUser(user);

    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onAction(target, ircUser, action);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onVersion(String target, IRCUser user) {
    manager.sendNotice(user.getNick(), IRCUtil.actionIndicator +
        "VERSION Plexus IRC Service 1.0-SNAPSHOT - plexus.codehaus.org");
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new DefaultIRCUser(user);

    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onVersion(target, ircUser);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void onTime(String target, IRCUser user) {
    manager.sendNotice(user.getNick(), IRCUtil.actionIndicator +
        "Time " + new Date());
    org.codehaus.plexus.service.irc.IRCUser ircUser =
        new DefaultIRCUser(user);

    Iterator listenerIter = manager.getListeners().iterator();
    while (listenerIter.hasNext()) {
      IRCListener next = (IRCListener) listenerIter.next();
      try {
        next.onTime(target, ircUser);
      } catch (Exception e) {
        handleException(next, e);
      }
    }
  }

  public void unknown(String prefix, String command, String middle, String trailing) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isAddressChar(char in) {
    for (int i = 0; i < ADDRESS_CHARS.length(); i++)
      if (in == ADDRESS_CHARS.charAt(i))
        return true;

    return false;
  }

  public boolean isIntroChar(char in) {
    for (int i = 0; i < INTRO_CHARS.length(); i++)
      if (in == INTRO_CHARS.charAt(i))
        return true;

    return false;
  }
}
