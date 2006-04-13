package org.codehaus.plexus.apacheds;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ApacheDsTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        for ( int i = 0; i < 100; i++ )
        {
            ApacheDs apacheDs = (ApacheDs) lookup( ApacheDs.ROLE );

            release( apacheDs );
        }
    }
}
