package org.codehaus.plexus.ldap.helper.factory;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.apacheds.ApacheDs;
import org.codehaus.plexus.apacheds.Partition;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.BasicAttribute;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ObjectStateHelperTest
    extends PlexusTestCase
{
    private ApacheDs apacheDs;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        apacheDs = (ApacheDs) lookup( ApacheDs.ROLE );

        Partition partition = new Partition();
        partition.setName( "test" );
        partition.setSuffix( "dc=test" );
        partition.getContextAttributes().put( new BasicAttribute( "objectClass", "top" ) );
        apacheDs.addPartition( partition );

        apacheDs.startServer();
    }

    protected void tearDown()
        throws Exception
    {
        apacheDs.stopServer();
    }

    public void testBasic()
        throws Exception
    {
        LdapFactoryHelper helper = (LdapFactoryHelper) lookup( LdapFactoryHelper.ROLE );

        PersonLdapFactory.setHelper( helper );

        InitialDirContext context = apacheDs.getAdminContext();

        context.addToEnvironment( Context.OBJECT_FACTORIES, PersonLdapFactory.class.getName() );
        context.addToEnvironment( Context.STATE_FACTORIES, PersonLdapFactory.class.getName() );

        LdapName name = new LdapName( "uid=trygvis,dc=test" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        try
        {
            context.unbind( name );
        }
        catch ( NamingException e )
        {
            // ignore
        }

        Person person = new Person();
        person.setName( "Trygve Laugstol" );
        person.setLastName( "Laugstol" );

        context.bind( name, person );

        Object o = context.lookup( name );

        assertEquals( Person.class, o.getClass() );
        person = (Person) o;
        assertEquals( "Trygve Laugstol", person.getName() );
        assertEquals( "Laugstol", person.getLastName() );
        assertEquals( null, person.getDescription() );

        context.close();
    }
}
