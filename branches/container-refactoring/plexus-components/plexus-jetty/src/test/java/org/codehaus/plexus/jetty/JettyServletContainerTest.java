package org.codehaus.plexus.jetty;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class JettyServletContainerTest
    extends PlexusTestCase
{
    public void testJettyServletContainerLookup()
        throws Exception
    {
        lookup( ServletContainer.ROLE );
    }
}