package $package;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author
 * @version $$Id$$
 */
public class HelloWorldTest
    extends PlexusTestCase
{
    public void testMessage()
        throws Exception
    {
        HelloWorld component = (HelloWorld) lookup( HelloWorld.ROLE );

        assertEquals( "The hello message wasn't as expected.",
                      "Hello World!", component.sayHello() );
    }
}
