package org.codehaus.plexus.service.irc;

import org.codehaus.plexus.PlexusTestCase;

/**
 * A simple test of the IRCServiceManager framework
 * User: aje
 * Date: 11-Jul-2006
 * Time: 23:40:30
 */
public class IRCServiceManagerTest extends PlexusTestCase {

  public void testStartup() throws Exception {
    IRCServiceManager ircManager = (IRCServiceManager)
        lookup(IRCServiceManager.class.getName());
    assertNotNull(ircManager);
    ircManager.connect("irc.codehaus.org", "nick", "user", "real");

    ircManager.join("#test");
    Thread.sleep(2000); // this seems to help establish the connection
    assertNotNull(ircManager.getTopic("#test"));

    ircManager.disconnect("test ending");
  }
}
