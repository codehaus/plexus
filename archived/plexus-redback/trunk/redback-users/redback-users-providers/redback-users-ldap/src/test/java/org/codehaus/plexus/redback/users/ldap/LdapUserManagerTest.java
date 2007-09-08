package org.codehaus.plexus.redback.users.ldap;

/*
 * Copyright 2001-2006 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.apacheds.ApacheDs;
import org.codehaus.plexus.redback.common.ldap.connection.LdapConnection;
import org.codehaus.plexus.redback.common.ldap.connection.LdapConnectionFactory;
import org.codehaus.plexus.redback.policy.PasswordEncoder;
import org.codehaus.plexus.redback.policy.encoders.SHA1PasswordEncoder;
import org.codehaus.plexus.redback.policy.encoders.SHA256PasswordEncoder;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.UserManager;


/**
 * LdapUserManagerTest 
 *
 * @author <a href="mailto:jesse@codehaus.org">Jesse McConnell</a>
 * @version $Id:$
 */  
public class LdapUserManagerTest extends PlexusTestCase
{
	private UserManager userManager;

	private ApacheDs apacheDs;
	
	private String suffix;
	
	private PasswordEncoder passwordEncoder;
	
	/**
	 * @plexus.requirement role-hint="configurable"
	 */
	private LdapConnectionFactory connectionFactory;
    
    public void testFoo() throws Exception
    {

    }
	
    protected void setUp()
        throws Exception
    {
    	super.setUp();
    		
    	passwordEncoder = new SHA1PasswordEncoder();
    	
    	apacheDs = (ApacheDs)lookup(ApacheDs.ROLE, "test" );    	
         
    	suffix = apacheDs.addSimplePartition( "test", new String[]{"redback", "plexus", "codehaus", "org"} ).getSuffix();
    	
    	System.out.println( "DN Suffix: " + suffix );
    	
     	apacheDs.startServer();
    		
    	makeUsers();    	
    	
    	userManager = (UserManager)lookup( UserManager.ROLE, "ldap" );
    	
    	connectionFactory = (LdapConnectionFactory) lookup( LdapConnectionFactory.ROLE, "configurable" );
    }
    
    

	protected void tearDown() throws Exception {
		
		
		
		InitialDirContext context = apacheDs.getAdminContext();
		
		context.unbind( createDn( "jesse" ) );
        
        context.unbind( createDn( "joakim" ) );
		
		apacheDs.stopServer();
		
		// TODO Auto-generated method stub
		super.tearDown();
	}



	private void makeUsers() throws Exception
    {
        InitialDirContext context = apacheDs.getAdminContext();        

        String cn = "jesse";
        bindUserObject( context, cn, createDn( cn ) );
        assertExist( context, createDn( cn ), "cn", cn );

        cn = "joakim";
        bindUserObject( context, cn, createDn( cn ) );
        assertExist( context, createDn( cn ), "cn", cn );

    }
    
    public void testConnection( ) throws Exception
    {
    	assertNotNull( connectionFactory );
    	
    	LdapConnection connection = connectionFactory.getConnection();
    	
    	assertNotNull( connection );
    	
    	DirContext context = connection.getDirContext();
    
    	assertNotNull( context );	
    	
    }
    	
    public void testDirectUsersExistance() throws Exception
    {
    	LdapConnection connection = connectionFactory.getConnection();
    	
    	DirContext context = connection.getDirContext();
    	
    	assertExist( context, createDn( "jesse" ), "cn", "jesse" );
    	assertExist( context, createDn( "joakim" ), "cn", "joakim" );
    	
    }
    
    public void testUserManager() throws Exception
    { 	
    	assertNotNull( userManager );
    	
    	assertTrue( userManager.userExists( "jesse" ));    
    	
    	List users = userManager.getUsers();
    	
    	assertNotNull( users );  
    	
    	assertEquals( 2, users.size() );
    	
    	User jesse = userManager.findUser("jesse");
    	
    	assertNotNull( jesse );
    	
    	assertEquals("jesse", jesse.getPrincipal().toString() );
    	assertEquals("foo", jesse.getEmail() );
    	assertEquals("foo", jesse.getFullName() );
    	assertTrue( passwordEncoder.isPasswordValid(jesse.getEncodedPassword(), "foo") );
    	
    }
    
    
    private void bindUserObject(DirContext context, String cn, String dn)
			throws Exception 
	{
		Attributes attributes = new BasicAttributes(true);
		BasicAttribute objectClass = new BasicAttribute("objectClass");
		objectClass.add("top");
		objectClass.add("inetOrgPerson");
		objectClass.add("person");
		objectClass.add("organizationalperson");
		attributes.put(objectClass);
		attributes.put("cn", cn);
		attributes.put("sn", "foo");
		attributes.put("mail", "foo");
		attributes.put("userPassword", passwordEncoder.encodePassword( "foo" ) );
		attributes.put("givenName", "foo");
        context.createSubcontext( dn, attributes );
	}

	private String createDn( String cn )
    {
        return "cn=" + cn + "," + suffix;
    }

    private void assertExist( DirContext context, String dn, String attribute, String value ) throws NamingException
    {
        SearchControls ctls = new SearchControls();

        ctls.setDerefLinkFlag( true );
        ctls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        ctls.setReturningAttributes( new String[] { "*" } );

        BasicAttributes matchingAttributes = new BasicAttributes();
        matchingAttributes.put( attribute, value );
        BasicAttribute objectClass = new BasicAttribute("objectClass");
        objectClass.add("inetOrgPerson");
        matchingAttributes.put(objectClass);
        
        NamingEnumeration<SearchResult> results = context.search( suffix, matchingAttributes );
        // NamingEnumeration<SearchResult> results = context.search( suffix, "(" + attribute + "=" + value + ")", ctls
        // );

        assertTrue( results.hasMoreElements() );
        SearchResult result = results.nextElement();
        Attributes attrs = result.getAttributes();
        Attribute testAttr = attrs.get( attribute );
        assertEquals( value, testAttr.get() );

    }

}
