package org.codehaus.plexus.service.irc;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.appserver.service.PlexusService;

/**
 * PlexusIRCService Tester.
 *
 * @author <Authors name>
 * @since <pre>07/24/2006</pre>
 * @version 1.0
 */
public class PlexusIRCServiceTest extends PlexusTestCase {

  IRCServiceManager ircManager;

  public void testLookup() throws Exception {
    PlexusService irc = (PlexusService)
        lookup("org.codehaus.plexus.appserver.service.PlexusService", "irc");
    assertNotNull(irc);
  }

  public static Test suite() {
    return new TestSuite(PlexusIRCServiceTest.class);
  }
}
