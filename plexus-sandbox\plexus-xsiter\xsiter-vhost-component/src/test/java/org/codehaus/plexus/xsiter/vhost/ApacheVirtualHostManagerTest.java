/**
 * 
 */
package org.codehaus.plexus.xsiter.vhost;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class ApacheVirtualHostManagerTest
    extends PlexusTestCase
{

    /**
     * Tests of the component can be looked up properly
     * 
     * @throws Exception
     */
    public void testLookup()
        throws Exception
    {
        VirtualHostManager component = (VirtualHostManager) lookup( VirtualHostManager.ROLE );
        assertNotNull( component );
    }

    /**
     * Tests if the vhost.vm template is being interpolated properly.
     */
    public void testRawVelocityInterpolation()
    {
        try
        {
            Velocity.setProperty( Velocity.RUNTIME_LOG_LOGSYSTEM, this );
            Velocity.init();

            VelocityContext context = new VelocityContext();
            // setup the Properties in Velocity Context.
            context.put( "vhostIP", "127.0.0.1" );
            context.put( "vhostName", "xsiter.localhost.com" );
            context.put( "vhostLogDirectory", "test" );
            context.put( "vhostConnectorProtocol", "ajp" );
            context.put( "vhostConnectorPort", "9190" );

            // Temlate location
            String tpl = "src/test/resources/vhosts.vm";

            Template t = Velocity.getTemplate( tpl );
            StringWriter sw = new StringWriter();
            t.merge( context, sw );
            StringBuffer sb = sw.getBuffer();
            // System.out.println (sb.toString ());
            sw.close();
            String lineSeparator = System.getProperty( "line.separator" );
            String expected = "<VirtualHost 127.0.0.1>" + lineSeparator + "  ServerName xsiter.localhost.com"
                + lineSeparator + "  ErrorLog test/apache_error.log" + lineSeparator
                + "  CustomLog test/apache_access.log combined" + lineSeparator
                + "  ProxyPass         /  ajp://localhost:9190/" + lineSeparator
                + "  ProxyPassReverse  /  ajp://localhost:9190/" + lineSeparator + "</VirtualHost>" + lineSeparator;
            System.out.println( sb.toString() );
            assertEquals( expected.length(), sb.toString().length() );
            assertEquals( expected, sb.toString() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    /**
     * Tests if a Virtual host could be added as expected.
     */
    public void testAddVritualHost()
        throws Exception
    {
        VirtualHostConfiguration config = new VirtualHostConfiguration();
        config.setVhostIP( "127.0.0.1" );
        config.setVhostName( "xsiter.localhost.com" );
        config.setVhostLogDirectory( "apache/logs" );
        config.setVhostConnectorProtocol( "ajp" );
        config.setVhostConnectorPort( "9190" );
        config.setVhostTemplate( "src/test/resources/vhosts.vm" );
        config.setVhostDirectory( "target" );

        VirtualHostManager mgr = (VirtualHostManager) lookup( VirtualHostManager.ROLE );
        assertNotNull( mgr );

        mgr.addVirtualHost( config, false );
        File f = new File( "target", "vhosts.conf" );
        assertTrue( f.exists() );
        assertTrue( f.length() > 0 );
    }

    /**
     * Tests if a Virtual host could be removed as expected.
     */
    public void testRemoveVirtualHost()
    {
        // TODO: Not implemented yet
    }

    /**
     * @throws Exception
     */
    public void testLoadVirtualHostConfigurations()
        throws Exception
    {
        StringReader sr = new StringReader( "<vhosts>\r\n" + "        <vhost>\r\n"
            + "          <id>integration</id>\r\n" + "          <vhostTemplate>vhosts.vm</vhostTemplate>\r\n"
            + "          <vhostDirectory>deploy/apache</vhostDirectory>\r\n"
            + "          <vhostIP>127.0.0.1</vhostIP>\r\n"
            + "          <vhostName>mock.vhost.localhost</vhostName>\r\n"
            + "          <vhostLogDirectory>deploy/apache/logs</vhostLogDirectory>\r\n"
            + "          <vhostConnectorProtocol>ajp</vhostConnectorProtocol>\r\n"
            + "          <vhostConnectorPort>9090</vhostConnectorPort>        \r\n" + "        </vhost>\r\n"
            + "        <vhost>\r\n" + "          <id>development</id>\r\n"
            + "          <vhostTemplate>vhosts.vm</vhostTemplate>\r\n"
            + "          <vhostDirectory>deploy/apache</vhostDirectory>\r\n"
            + "          <vhostIP>127.0.0.1</vhostIP>\r\n"
            + "          <vhostName>mock.vhost.localhost</vhostName>\r\n"
            + "          <vhostLogDirectory>dev.vhost.localhost</vhostLogDirectory>\r\n"
            + "          <vhostConnectorProtocol>ajp</vhostConnectorProtocol>\r\n"
            + "          <vhostConnectorPort>9080</vhostConnectorPort>        \r\n" + "        </vhost>\r\n"
            + "      </vhosts>" );
        VirtualHostManager component = (VirtualHostManager) lookup( VirtualHostManager.ROLE );
        assertNotNull( component );

        List list = component.loadVirtualHostConfigurations( sr );
        assertNotNull( list );
        assertEquals( 2, list.size() );
        Iterator it = list.iterator();
        VirtualHostConfiguration cfg = (VirtualHostConfiguration) it.next();
        assertEquals( "9090", cfg.getVhostConnectorPort() );
        assertEquals( "ajp", cfg.getVhostConnectorProtocol() );
        assertEquals( "mock.vhost.localhost", cfg.getVhostName() );
        assertEquals( "deploy/apache/logs", cfg.getVhostLogDirectory() );
        assertEquals( "deploy/apache", cfg.getVhostDirectory() );
        assertEquals( "127.0.0.1", cfg.getVhostIP() );
        assertEquals( "vhosts.vm", cfg.getVhostTemplate() );

        // test with invalid configuration
        sr = new StringReader( "<vhosts>\r\n" + "        <vhost>\r\n" + "          <id>integration</id>\r\n"
            + "          <vhostTemplate>vhosts.vm</vhostTemplate>\r\n"
            + "          <vhostDirectory>deploy/apache</vhostDirectory>\r\n"
            + "          <vhostIP>127.0.0.1</vhostIP>\r\n"
            + "          <vhostName>mock.vhost.localhost</vhostName>\r\n"
            + "          <vhostLogDirectory>deploy/apache/logs</vhostLogDirectory>\r\n"
            + "          <vhostConnectorProtocol>ajp</vhostConnectorProtocol>\r\n"
            + "          <vhostConnectorPort>9090</vhostConnectorPort>        \r\n"
            + "        </vhost>\r\n"
            + "        <vhost>\r\n"
            + "          <id>development</id>\r\n"
            + "          <vhostTemplate>vhosts.vm</vhostTemplate>\r\n"
            + "          <vhostDirectory>deploy/apache</vhostDirectory>\r\n"
            + "          <vhostIP>127.0.0.1</vhostIP>\r\n"
            // missing vhost name
            // + "
            // <vhostName>mock.vhost.localhost</vhostName>\r\n"
            + "          <vhostLogDirectory>dev.vhost.localhost</vhostLogDirectory>\r\n"
            + "          <vhostConnectorProtocol>ajp</vhostConnectorProtocol>\r\n"
            // missing port
            // + "
            // <vhostConnectorPort>9080</vhostConnectorPort>
            // \r\n"
            + "        </vhost>\r\n" + "      </vhosts>" );

        try
        {
            component.loadVirtualHostConfigurations( sr );
            fail( "Expected Exception (for Missing Configuration elements)" );
        }
        catch ( Exception e )
        {
            // System.err.println (e.getMessage ());
            // do nothing expected
        }

    }
}
