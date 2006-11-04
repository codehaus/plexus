package org.codehaus.plexus.component.factory.jython;

import org.codehaus.plexus.PlexusTestCase;

public class JythonComponentFactoryTest
    extends PlexusTestCase
{
    public JythonComponentFactoryTest( String name )
    {
        super( name );
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
