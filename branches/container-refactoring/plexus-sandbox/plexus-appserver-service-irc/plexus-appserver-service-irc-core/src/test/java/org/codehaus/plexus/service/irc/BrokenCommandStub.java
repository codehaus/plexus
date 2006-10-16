package org.codehaus.plexus.service.irc;

/**
 * A Broken Command Stub
 * User: aje
 * Date: 25-Aug-2006
 * Time: 08:02:32
 */
public class BrokenCommandStub implements IRCCommand {

  public void onCommand(String channel, IRCUser user, String message) {
    System.out.println("throwing exception");
    throw new RuntimeException("We broke on purpose");
  }

  public void onPrivateCommand(IRCUser user, String message) {
  }

  public String getHelp(String channel) {
    return "This is a broken stub command";
  }
}
