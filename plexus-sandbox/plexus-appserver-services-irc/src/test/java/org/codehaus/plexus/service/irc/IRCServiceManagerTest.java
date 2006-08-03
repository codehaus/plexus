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

//    ircManager.join("#plexus");
//    ircManager.sendMessage("#plexus", "Hello, I am the new plexus IRC framework");
//    Thread.sleep(2000);
//    ircManager.part("#plexus", "bye bye for now");
  }
}
