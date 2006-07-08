/**
 * 
 */
package org.codehaus.plexus;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class MonitorMojoTest
    extends AbstractMojoTestCase
{

    public void testMojoLookup()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/unit/plugin-config.xml" );
        MonitorMojo mojo = (MonitorMojo) lookupMojo( "monitor", pluginXml );
        assertNotNull( mojo );
    }

    public void testMojoExecution()
        throws Exception
    {
        File pluginXml = new File( getBasedir(), "src/test/resources/unit/plugin-config.xml" );
        MonitorMojo mojo = (MonitorMojo) lookupMojo( "monitor", pluginXml );
        assertNotNull( mojo.getWebsites() );
        assertEquals( 1, mojo.getWebsites().size() );
        assertNotNull( mojo.getMonitor() );
        try
        {
            mojo.execute();
        }
        catch ( Exception e )
        {
            fail( "Unexpected exception with test data." );
        }
    }
}
