package org.codehaus.plexus.apacheds;

import org.apache.directory.shared.ldap.util.AttributeUtils;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

import sun.util.logging.resources.logging;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ApacheDsTest
    extends PlexusTestCase
{
    private String suffix = "dc=plexus,dc=codehaus,dc=org";

    protected void setUp()
        throws Exception
    {
        super.setUp();

        FileUtils.deleteDirectory( getTestFile( "target/plexus-home" ) );
                
    }

    public void testBasic()
        throws Exception
    {
        ApacheDs apacheDs = (ApacheDs) lookup( ApacheDs.ROLE );
        
        apacheDs.setBasedir( getTestFile( "target/plexus-home" ) );
        
        apacheDs.addSimplePartition( "test", new String[]{"plexus", "codehaus", "org"} ).getSuffix();
        apacheDs.startServer();

        InitialDirContext context = apacheDs.getAdminContext();

        String cn = "trygvis";
        bindObject( context, cn, createDn( cn ) );
        assertExist( context, createDn( cn ), "cn", cn );
        assertExist( context, createDn( "trygvis" ), "testAttr", "test" );

        cn = "bolla";
        bindObject( context, cn, createDn( cn ) );
        assertExist( context, createDn( cn ), "cn", cn );
        assertExist( context, createDn( "trygvis" ), "testAttr", "test" );

        SearchControls ctls = new SearchControls();

        ctls.setDerefLinkFlag( true );
        ctls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        ctls.setReturningAttributes( new String[] { "*" } );

        //String filter = "(&(objectClass=inetOrgPerson)(cn=trygvis))";        
        String filter =  "(cn=trygvis)";      
        
        NamingEnumeration<SearchResult> results = context.search( suffix, filter, ctls );
       
        assertTrue( "a result should have been returned", results.hasMoreElements() );
        
        SearchResult result = results.nextElement();
        
        Attributes attrs = result.getAttributes();
        
        System.out.println( AttributeUtils.toString( attrs ) );;
        
        assertEquals( "test", attrs.get( "testAttr" ) );
        
        assertFalse( "should only have one result returned", results.hasMoreElements() );
        
        apacheDs.stopServer();

        release( apacheDs );

        // ----------------------------------------------------------------------
        // Start it again
        // ----------------------------------------------------------------------

        apacheDs = (ApacheDs) lookup( ApacheDs.ROLE );
        //apacheDs.addSimplePartition( "test", new String[]{"plexus", "codehaus", "org"} ).getSuffix();
        apacheDs.startServer();

        assertExist( context, createDn( "trygvis" ), "cn", "trygvis" );
        assertExist( context, createDn( "trygvis" ), "testAttr", "test" );
        context.unbind( createDn( "trygvis" ) );
        assertExist( context, createDn( "bolla" ), "cn", "bolla" );
        assertExist( context, createDn( "bolla" ), "testAttr", "test" );
        context.unbind( createDn( "bolla" ) );

        apacheDs.stopServer();
    }
/*
    public void testResourceManagement()
        throws Exception
    {
        // ----------------------------------------------------------------------
        // A naively simple test case to ensure that the DS properly clean up
        // after itself
        // ----------------------------------------------------------------------

        // Create the context
        FileUtils.forceMkdir( getTestFile( "target/plexus-home/system" ) );
        FileUtils.forceMkdir( getTestFile( "target/plexus-home/test" ) );
        ApacheDs apacheDs = (ApacheDs) lookup( ApacheDs.ROLE );
//        apacheDs.addSimplePartition( "test", new String[]{"plexus", "codehaus", "org"} ).getSuffix();
        System.out.println( "suffix = " + suffix );
        apacheDs.startServer();
        release( apacheDs );

        for ( int i = 0; i < 1; i++ )
        {
            apacheDs = (ApacheDs) lookup( ApacheDs.ROLE );
//            apacheDs.addSimplePartition( "test", new String[]{"plexus", "codehaus", "org"} );

            apacheDs.setEnableNetworking( true );
            apacheDs.startServer();

//            InitialDirContext context = apacheDs.getAdminContext();
            DirContext context = getNetContext();

            String cn = "cn=user" + i;
            bindObject( context, cn, createDn( cn ) );
            Object o = context.lookup( createDn( cn ) );
            assertTrue( o instanceof Attributes );
            System.out.println( "o = " + o );
            context.unbind( createDn( cn ) );

            Thread.sleep( 100000 );

            release( apacheDs );
        }

        // ----------------------------------------------------------------------
        // Check that the entires get created
        // ----------------------------------------------------------------------

        apacheDs = (ApacheDs) lookup( ApacheDs.ROLE );
//        apacheDs.addSimplePartition( "test", new String[]{"plexus", "codehaus", "org"} ).getSuffix();
        apacheDs.setEnableNetworking( true );
        apacheDs.startServer();

        DirContext netContext = new InitialDirContext( getEnvironment() );

        List<Attributes> results = new ArrayList<Attributes>();
        SearchControls searchControls = new SearchControls( SearchControls.SUBTREE_SCOPE, 0, 0, null, false, true );
        NamingEnumeration<SearchResult> result = netContext.search( suffix, "(objectClass=*)", null, searchControls );

        while( result.hasMore() )
        {
            SearchResult searchResult = result.next();

            Attributes a = searchResult.getAttributes();

            System.err.println( "a = " + a );
            results.add( a );
        }

        assertEquals( 100, results.size() );

        release( apacheDs );
    }
*/
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

//    private DirContext getNetContext()
//        throws Exception
//    {
//        return new InitialDirContext( getEnvironment() );
//    }

//    private Hashtable<Object, Object> getEnvironment()
//        throws Exception
//    {
//        Properties env = new Properties();
//
//        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
//
//        env.put( Context.PROVIDER_URL, "ldap://localhost:10389/dc=plexus,dc=codehaus,dc=org" );
//
//        return env;
//    }

    private void bindObject( DirContext context, String cn, String dn )
        throws NamingException
    {
        Attributes attributes = new BasicAttributes();
        BasicAttribute objectClass = new BasicAttribute( "objectClass" );
        objectClass.add( "top" );
        objectClass.add( "inetOrgPerson" );
//        objectClass.add( "uidObject" );
        attributes.put( objectClass );
        attributes.put( "cn", cn );
        attributes.put( "testAttr", "test" );        
        context.bind( dn, attributes );
    }

    private String createDn( String cn )
    {
        return "cn=" + cn + "," + suffix;
    }

    private void assertExist( DirContext context, String dn, String attribute, String value )
        throws NamingException
    {
        Object o = context.lookup( dn );
        assertTrue( o instanceof Attributes );
        Attributes attributes = (Attributes) o;
        assertEquals( value, attributes.get( attribute ).get() );
    }
}
