package org.codehaus.plexus.service.irc;

/**
 * Created by IntelliJ IDEA.
 * User: aje
 * Date: 29-Jul-2006
 * Time: 20:28:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractIRCListener implements IRCListener {

  public void onRegistered() { }

  public void onDisconnected() { }

  public void onError(String message) { }

  public void onError(int num, String message) { }

  public void onInvite(String channel, IRCUser user, String invitee) { }

  public void onJoin(String channel, IRCUser ircUser) { }

  public void onKick(String channel, IRCUser user, String kickee, String message) { }

  public void onMode(IRCUser user, String string, String mode) { }

  public void onNick(IRCUser user, String newNick) { }

  public void onNotice(String target, IRCUser user, String message) { }

  public void onPart(String channel, IRCUser user, String message) { }

  public void onPing(String ping) {
    /* The correct response is handled internally by IRCLib */
  }

  public void onMessage(String channel, IRCUser user, String message) { }

  public void onPrivateMessage(IRCUser user, String message) {
    onMessage(user.getNick(), user, message);
  }

  public void onAction(String target, IRCUser user, String action) { }

  public void onQuit(IRCUser user, String message) { }

  public void onReply(int num, String value, String message) { }

  public void onTopic(String channel, IRCUser user, String topic) { }

  public void onVersion(String target, IRCUser user) {
    /* The correct response is now handled internally */
  }

  public void onTime(String target, IRCUser user) {
    /* The correct response is now handled internally */
  }
}
