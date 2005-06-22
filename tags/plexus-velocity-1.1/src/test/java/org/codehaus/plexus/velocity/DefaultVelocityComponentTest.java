package org.codehaus.plexus.velocity;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.codehaus.plexus.PlexusTestCase;

public class DefaultVelocityComponentTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        DefaultVelocityComponent velocity;

        VelocityContext context;

        String value;

        velocity = (DefaultVelocityComponent) lookup( VelocityComponent.ROLE );

        // test the properties
        value = (String) velocity.getEngine().getProperty( "hello" );

        assertNotNull( value );

        assertEquals( "world", value );

        // test the rendering
        context = new VelocityContext();

        context.put( "variable", "Value from context" );

        Template template = velocity.getEngine().getTemplate("org/codehaus/plexus/velocity/DefaultVelocityComponentTest.vm" );

        StringWriter writer = new StringWriter();

        template.merge( context, writer );

        assertEquals( "Static text -- Value from context -- More static text", writer.toString() );
    }
}
