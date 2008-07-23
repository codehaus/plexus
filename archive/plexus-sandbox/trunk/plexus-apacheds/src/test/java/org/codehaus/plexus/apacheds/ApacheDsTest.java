package org.codehaus.plexus.apacheds;

/*
 * Copyright 2001-2007 The Codehaus.
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

import org.apache.directory.shared.ldap.util.AttributeUtils;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

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
        createUser( context, cn, createDn( cn ) );
        assertExist( context, createDn( cn ), "cn", cn );

        cn = "bolla";
        createUser( context, cn, createDn( cn ) );
        assertExist( context, createDn( cn ), "cn", cn );

        SearchControls ctls = new SearchControls();

        ctls.setDerefLinkFlag( true );
        ctls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        ctls.setReturningAttributes( new String[] { "*" } );

        String filter = "(&(objectClass=inetOrgPerson)(cn=trygvis))";        
        
        NamingEnumeration<SearchResult> results = context.search( suffix, filter, ctls );
       
        assertTrue( "a result should have been returned", results.hasMoreElements() );
        
        SearchResult result = results.nextElement();
        
        Attributes attrs = result.getAttributes();
        
        System.out.println( AttributeUtils.toString( attrs ) );
        
        assertFalse( "should only have one result returned", results.hasMoreElements() );
        
        apacheDs.stopServer();

        release( apacheDs );

        // ----------------------------------------------------------------------
        // Start it again
        // ----------------------------------------------------------------------

        apacheDs = (ApacheDs) lookup( ApacheDs.ROLE );
        apacheDs.startServer();

        context = apacheDs.getAdminContext();
        
        assertExist( context, createDn( "trygvis" ), "cn", "trygvis" );
        context.unbind( createDn( "trygvis" ) );
        assertExist( context, createDn( "bolla" ), "cn", "bolla" );
        context.unbind( createDn( "bolla" ) );

        apacheDs.stopServer();
    }

    private void createUser( DirContext context, String cn, String dn )
        throws NamingException
    {
        Attributes attributes = new BasicAttributes();
        BasicAttribute objectClass = new BasicAttribute( "objectClass" );
        objectClass.add( "top" );
        objectClass.add( "inetOrgPerson" );
        attributes.put( objectClass );
        attributes.put( "cn", cn );
        attributes.put( "sn", cn );
        context.createSubcontext( dn, attributes );
    }

    private String createDn( String cn )
    {
        return "cn=" + cn + "," + suffix;
    }

    private void assertExist( DirContext context, String dn, String attribute, String value )
        throws NamingException
    {
    	SearchControls ctls = new SearchControls();

        ctls.setDerefLinkFlag( true );
        ctls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        ctls.setReturningAttributes( new String[] { "*" } );
    	   	
    	BasicAttributes matchingAttributes = new BasicAttributes();
    	matchingAttributes.put( attribute, value );
    	
    	NamingEnumeration<SearchResult> results = context.search( suffix, matchingAttributes );
    	//NamingEnumeration<SearchResult> results = context.search( suffix, "(" + attribute + "=" + value + ")", ctls  );

        assertTrue( results.hasMoreElements() );    	
    	SearchResult result = results.nextElement();    	
    	Attributes attrs = result.getAttributes();    	
    	Attribute testAttr = attrs.get( attribute );    	
    	assertEquals( value, testAttr.get() );
      
    }
}
