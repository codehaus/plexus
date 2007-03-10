package org.codehaus.plexus.component.factory.groovy;

import org.codehaus.plexus.PlexusTestCase;

/**
 * Basic tests to see if things are working or not.
 *
 * @version $Id$
 */
public class GroovyComponentFactoryTest
    extends PlexusTestCase
{
    public void testComponent()
        throws Exception
    {
        Hello hello = (Hello) lookup( Hello.ROLE );

        assertNotNull( hello );

        hello.initialize();

        hello.start();

        hello.hello();

        hello.dispose();
    }
}
