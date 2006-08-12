package org.codehaus.plexus.service.irc;

import org.schwering.irc.lib.IRCEventAdapter;
import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCUtil;

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
    if (num == type) {
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
    /* we only handle the topic replies at the moment */
    if (type != IRCUtil.RPL_TOPIC)
      return null;

    conn.addIRCEventListener(this);
    if (type == IRCUtil.RPL_TOPIC)
      conn.doTopic(param);
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
