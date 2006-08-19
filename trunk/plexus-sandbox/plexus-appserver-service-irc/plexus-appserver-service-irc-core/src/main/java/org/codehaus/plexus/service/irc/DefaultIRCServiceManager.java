package org.codehaus.plexus.service.irc;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.*;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCUtil;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * An IRCServiceManager
 * User: aje
 * Date: 21-Jul-2006
 * Time: 22:10:27
 *
 * @plexus.component
 *   role="org.codehaus.plexus.service.irc.IRCServiceManager"
 */
public class DefaultIRCServiceManager extends AbstractLogEnabled
    implements IRCServiceManager, Startable, Serviceable {

  private IRCConnection conn = null;

  private ServiceLocator locator;

  public void start() throws StartingException {
    getLogger().info("IRCServiceManager starting");
  }

  public void stop() throws StoppingException {
    getLogger().info("IRCServiceManager stopping");
    try {
      disconnect();
    } catch (IOException e) {
      throw new StoppingException("Error disconnecting from server", e);
    }
  }

  public boolean connect(String host, String nick, String username,
                         String realname) throws IOException {
    return connect(host, nick, null, username, realname);
  }

  public boolean connect(String host, String nick, String pass,
                         String username, String realname) throws IOException {
    if (conn != null && conn.isConnected())
      return false;
    int portMin = 6667;
    int portMax = 6669;

    getLogger().debug("connecting");
    conn = new IRCConnection(host, portMin, portMax, pass, nick, username,
        realname);
    conn.setPong(true);

    conn.addIRCEventListener(new DefaultIRCServiceListener(this));

    conn.connect();
    return true;
  }

  public void disconnect() throws IOException {
    if (conn != null)
      conn.doQuit("Application shutting down");
  }

  public void disconnect(String reason) throws IOException {
    if (conn != null)
      conn.doQuit(reason);
  }

  public void sendMessage(String to, String message) {
    if (to == null || message == null)
      return;
    if (conn != null) {
      String[] lines = message.split("\n");
      for (int i = 0; i < lines.length; i++) {
        conn.doPrivmsg(to, lines[i]);
      }
    }
  }

  public void sendAction(String to, String action) {
    if (conn != null)
      conn.doPrivmsg(to, IRCUtil.actionIndicator + "ACTION " + action);
  }

  public void sendNotice(String to, String notice) {
    if (conn != null)
      conn.doNotice(to, notice);
  }

  public void join(String channel) {
    if (conn != null)
      conn.doJoin(channel);
  }

  public void part(String channel) {
    if (conn != null)
      conn.doPart(channel);
  }

  public void part(String channel, String reason) {
    if (conn != null)
      conn.doPart(channel, reason);
  }

  public Map getCommands() {
    try {
      return locator.lookupMap("org.codehaus.plexus.service.irc.IRCCommand");
    } catch (ComponentLookupException e) {
      getLogger().info("No IRCCommands found", e);
      return new HashMap();
    }
  }

  public List getListeners() {
    try {
      return locator.lookupList("org.codehaus.plexus.service.irc.IRCListener");
    } catch (ComponentLookupException e) {
      getLogger().info("No IRCListeners found", e);
      return new LinkedList();
    }
  }

  public void service(ServiceLocator serviceLocator) {
    this.locator = serviceLocator;
  }

  public String getHost() {
    if (conn != null)
      return conn.getHost();
    return null;
  }

  public String getNick() {
    if (conn != null)
      return conn.getNick();
    return null;
  }

  public void setNick(String nick) {
    if (conn != null)
      conn.doNick(nick);
  }

  public String getUsername() {
    if (conn != null)
      return conn.getUsername();
    return null;
  }

  public String getRealname() {
    if (conn != null)
      return conn.getRealname();
    return null;
  }

  public String getTopic(String channel) {
    return (new ReplyMonitor(conn, IRCUtil.RPL_TOPIC, channel)).getReply();
  }

  public void setTopic(String channel, String topic) {
    if (conn != null)
      conn.doTopic(channel, topic);
  }
}

