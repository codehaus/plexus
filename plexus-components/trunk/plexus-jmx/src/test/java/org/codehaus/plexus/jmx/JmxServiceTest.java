package org.codehaus.plexus.jmx;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.jmx.mgmt.PartyMgmt;
import org.codehaus.plexus.jmx.mgmt.PartyMgmtMBean;

import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class JmxServiceTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        JmxService jmxService = (JmxService) lookup( JmxService.ROLE );

        PartyMgmtMBean partyMgmtMBean = new PartyMgmt();

        ObjectName objectName = new ObjectName( "org.codehaus.plexus:Type=Party" );

        jmxService.getMBeanServer().registerMBean( partyMgmtMBean, objectName );

        // -----------------------------------------------------------------------
        // Find the bean again
        // -----------------------------------------------------------------------

        MBeanServer mBeanServer = (MBeanServer) MBeanServerFactory.findMBeanServer( null ).get( 0 );

        Object attribute = mBeanServer.getAttribute( objectName, "Party" );

        assertEquals( Boolean.class, attribute.getClass() );

        assertEquals( false, ( (Boolean) attribute ).booleanValue() );

        // -----------------------------------------------------------------------
        // Set a property
        // -----------------------------------------------------------------------

        mBeanServer.setAttribute( objectName, new Attribute( "Party", Boolean.TRUE ) );

        attribute = mBeanServer.getAttribute( objectName, "Party" );

        assertEquals( true, ( (Boolean) attribute ).booleanValue() );
    }

    public void testInternalMBeanList()
        throws Exception
    {
        JmxService jmxService = (JmxService) lookup( JmxService.ROLE );

        // -----------------------------------------------------------------------
        // Register an mbean and remove it again.
        // -----------------------------------------------------------------------

        ObjectName objectName = jmxService.registerMBean( new PartyMgmt(), "org.codehaus.plexus:Type=Party" );

        jmxService.releaseAllMBeansQuiet();

        // -----------------------------------------------------------------------
        // Find the bean again
        // -----------------------------------------------------------------------

        MBeanServer mBeanServer = jmxService.getMBeanServer();

        try
        {
            mBeanServer.getObjectInstance( objectName );

            fail( "Expected InstanceNotFoundException" );
        }
        catch ( InstanceNotFoundException e )
        {
            // expected
        }
        catch ( NullPointerException e )
        {
            fail( "Expected InstanceNotFoundException" );
        }
    }
}
