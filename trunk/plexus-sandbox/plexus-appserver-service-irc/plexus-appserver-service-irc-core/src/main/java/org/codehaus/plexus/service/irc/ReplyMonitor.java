package org.codehaus.plexus.service.irc;

import org.schwering.irc.lib.*;
import org.schwering.irc.lib.IRCUser;

/**
 * Register an interest in a property and wait for the reply to come in.
 * Currently it only supports "getTopic".
 */
class ReplyMonitor extends IRCEventAdapter {

  private IRCConnection conn;
  private int type;
  private String param, reply;

  /**
   * Make an asynchronous request in a synchronous manner.
   *
   * @param conn  The connection to request on
   * @param type  The type of request to make
   */
  public ReplyMonitor(IRCConnection conn, int type) {
    this(conn, type, null);
  }

  /**
   * Make an asynchronous request in a synchronous manner.
   *
   * @param conn  The connection to request on
   * @param type  The type of request to make
   * @param param An optional parameter
   */
  public ReplyMonitor(IRCConnection conn, int type, String param) {
    this.conn = conn;
    this.type = type;
    this.param = param;
  }

  /* This compensates for a big in IRCLib - hopefully soon to be fixed */
  public void onNotice(String target, IRCUser ircUser, String message) {
    if (type == IRCUtil.RPL_TIME && message.startsWith("TIME ")) {
      reply = message.substring(5);

      synchronized(this) {
        notify();
      }
    }
  }

  /**
   * The server informs us of any replies.
   * If they match the one we are watching for then we need to let folk know
   * it has arrived.
   *
   * @param num     The reply type
   * @param value   The username and other info related to the request
   * @param message The reply
   */
  public void onReply(int num, String value, String message) {
    if (num == type ||
        (type == IRCUtil.RPL_TOPIC && num == IRCUtil.RPL_NOTOPIC)) {
      reply = message;

      synchronized(this) {
        notify();
      }
    }
  }

  /**
   * get the reply to our request. This blocks and waits for the reply to
   * arrive.
   * @return The reply sent back from the server
   */
  public String getReply() {
    /* we only handle the topic and time replies at the moment */
    if (type != IRCUtil.RPL_TOPIC && type != IRCUtil.RPL_TIME)
      return null;

    conn.addIRCEventListener(this);
    if (type == IRCUtil.RPL_TOPIC)
      conn.doTopic(param);
    else if (type == IRCUtil.RPL_TIME)
      conn.doPrivmsg(param, IRCUtil.actionIndicator + "TIME");
    try {
      synchronized(this) {
        wait();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    conn.removeIRCEventListener(this);
    return reply;
  }
}
