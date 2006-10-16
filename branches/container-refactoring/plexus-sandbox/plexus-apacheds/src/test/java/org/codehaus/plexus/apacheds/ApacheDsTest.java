package org.codehaus.plexus.apacheds;

import org.codehaus.plexus.PlexusTestCase;

import javax.naming.directory.InitialDirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.BasicAttribute;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ApacheDsTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        ApacheDs apacheDs = (ApacheDs) lookup( ApacheDs.ROLE );

        Partition partition = apacheDs.addSimplePartition( "plexus", "codehaus", "org" );

        apacheDs.startServer();

        InitialDirContext context = apacheDs.getAdminContext();

        String dn = "uid=trygvis," + partition.getSuffix();

        Attributes attributes = new BasicAttributes();
        attributes.put( "cn", "Trygve Laugstol" );
        BasicAttribute objectClass = new BasicAttribute( "objectClass" );
        objectClass.add( "top" );
        objectClass.add( "inetOrgPerson" );
        objectClass.add( "uidObject" );
        attributes.put( objectClass );
        attributes.put( "uid", "trygvis" );
        context.bind( dn, attributes );

        Object o = context.lookup( dn );
        assertTrue( o instanceof Attributes );
        apacheDs.stopServer();
    }

    public void testResourceManagement()
        throws Exception
    {
        // ----------------------------------------------------------------------
        // A naively simple test case to ensure that the DS properly clean up
        // after itself
        // ----------------------------------------------------------------------

        for ( int i = 0; i < 100; i++ )
        {
            ApacheDs apacheDs = (ApacheDs) lookup( ApacheDs.ROLE );

            apacheDs.addSimplePartition( "plexus", "codehaus", "org" );
            apacheDs.setEnableNetworking( true );

            release( apacheDs );
        }
    }
}
