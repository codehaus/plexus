package org.codehaus.plexus.examples.simple;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.codehaus.plexus.PlexusTestCase;

public class DefaultHelloWorldTest
    extends PlexusTestCase
{
    public DefaultHelloWorldTest( String name )
    {
        super( name );
    }

    public static Test suite()
    {
        return new TestSuite( DefaultHelloWorldTest.class );
    }

    public void testComponent()
        throws Exception
    {
        HelloWorld component = (HelloWorld) lookup( HelloWorld.ROLE );

        assertEquals( "Hello World!", component.sayHello() );
    }
}
