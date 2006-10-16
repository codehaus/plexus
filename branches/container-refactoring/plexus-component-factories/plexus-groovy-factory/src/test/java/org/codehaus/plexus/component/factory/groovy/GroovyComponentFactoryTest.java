package org.codehaus.plexus.component.factory.groovy;

import org.codehaus.plexus.PlexusTestCase;

public class GroovyComponentFactoryTest
    extends PlexusTestCase
{
    public GroovyComponentFactoryTest( String name )
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
