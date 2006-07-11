package org.codehaus.plexus.jetty;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class HttpdTest
    extends PlexusTestCase
{
    public void testServer()
        throws Exception
    {
        Httpd h = (Httpd) lookup( Httpd.ROLE );

        assertNotNull( h ); 
    }
}
