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
import javax.naming.directory.SearchResult;


import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.apacheds.ApacheDs;
import org.codehaus.plexus.apacheds.Partition;
import org.codehaus.plexus.ldap.helper.LdapConnection;
import org.codehaus.plexus.ldap.helper.LdapConnectionFactory;
import org.codehaus.plexus.redback.users.UserManager;

import sun.security.action.GetLongAction;

import com.sun.jndi.ldap.LdapCtx;

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
	
	private String suffix = "dc=redback,dc=plexus,dc=codehaus,dc=org";
	
	/**
	 * @plexus.requirement role-hint="configurable"
	 */
	private LdapConnectionFactory connectionFactory;
	
	
    protected void setUp()
        throws Exception
    {
    	super.setUp();
    		
    	apacheDs = (ApacheDs)lookup(ApacheDs.ROLE, "test" );    	
         
    	suffix = apacheDs.addSimplePartition( "test", new String[]{"redback", "plexus", "codehaus", "org"} ).getSuffix();
    	
     	apacheDs.startServer();
    	
    	
    	makeUsers();
    	
    	
    	userManager = (UserManager)lookup( UserManager.ROLE, "ldap" );
    	
    	connectionFactory = (LdapConnectionFactory) lookup( LdapConnectionFactory.ROLE, "configurable" );
    }
    
    

	protected void tearDown() throws Exception {
		
		InitialDirContext context = apacheDs.getAdminContext();
		
		context.unbind( createDn( "cn=jesse" ) );
        
        context.unbind( createDn( "cn=joakim" ) );
		
		apacheDs.stopServer();
		
		// TODO Auto-generated method stub
		super.tearDown();
	}



	private void makeUsers() throws Exception
    {
        InitialDirContext context = apacheDs.getAdminContext();        

        String cn = "cn=jesse";
        bindUserObject( context, cn, createDn( cn ) );
        assertExist( context, createDn( cn ), "cn", cn );

        cn = "cn=joakim";
        bindUserObject( context, cn, createDn( cn ) );
        assertExist( context, createDn( cn ), "cn", cn );

    }
    
    public void testInitialization( ) throws Exception
    {
    	assertNotNull( connectionFactory );
    	
    	LdapConnection connection = connectionFactory.getConnection();
    	
    	assertNotNull( connection );
    	
    	DirContext context = connection.getDirContext();
    	
    	assertNotNull( context );
    	
    	assertNotNull( userManager );
    	
    	assertTrue( userManager.userExists( "jesse" ) );
    	
    	//List users = userManager.getUsers();
    	
    	//assertNotNull( users );
    	
    	//System.out.println(users.size());
    }
    
    private void bindUserObject(DirContext context, String cn, String dn)
			throws NamingException 
	{
		Attributes attributes = new BasicAttributes();
		BasicAttribute objectClass = new BasicAttribute("objectClass");
		objectClass.add("top");
		objectClass.add("inetOrgPerson");
		// objectClass.add( "uidObject" );
		attributes.put(objectClass);
		attributes.put("cn", cn);
		attributes.put("email", "foo");
		attributes.put("userPassword", "foo");
		attributes.put("givenName", "foo");
		context.bind(dn, attributes);
	}

private String createDn( String cn )
{
    return cn + "," + suffix;
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
