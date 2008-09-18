package org.codehaus.plexus.component.factory.bsh;

import org.codehaus.plexus.PlexusTestCase;

public class BshComponentFactoryTest
    extends PlexusTestCase
{
    public BshComponentFactoryTest()
    {
    }

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
