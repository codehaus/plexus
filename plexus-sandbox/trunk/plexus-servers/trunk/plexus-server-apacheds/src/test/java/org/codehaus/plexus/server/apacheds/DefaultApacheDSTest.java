package org.codehaus.plexus.server.apacheds;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultApacheDSTest
    extends PlexusTestCase
{
    private String baseDN = "dc=test,dc=org";

    private Hashtable enviroment;

    private String bindDn = null;

    private String password = null;

    private static PersonObjectFactory object = new PersonObjectFactory();

    private static PersonStateFactory state = new PersonStateFactory();

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void setUp()
        throws Exception
    {
        super.setUp();

        // ----------------------------------------------------------------------
        // Create a enviroment
        // ----------------------------------------------------------------------

        enviroment = new Hashtable();

        enviroment.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );

        enviroment.put( Context.PROVIDER_URL, "ldap://localhost:10389/" );

        enviroment.put( Context.SECURITY_AUTHENTICATION, "simple" );

        if ( bindDn != null )
        {
            enviroment.put( Context.SECURITY_PRINCIPAL, bindDn );
        }

        if ( password != null )
        {
            enviroment.put( Context.SECURITY_CREDENTIALS, password );
        }

        enviroment.put( Context.OBJECT_FACTORIES, new PersonObjectFactory().getClass().getName() );

        enviroment.put( Context.STATE_FACTORIES, new PersonStateFactory().getClass().getName() );

        // ----------------------------------------------------------------------
        // Clean out the store for ApacheDS
        // ----------------------------------------------------------------------

        File store = new File( getContainer().getContext().get( "plexus.home" ).toString(), "apacheds" );

        if ( store.isDirectory() )
        {
            FileUtils.deleteDirectory( store );
        }

        assertTrue( store.mkdirs() );
    }

    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void testFoo()
        throws Exception
    {
        DefaultApacheDS server;

        // this will start the server
        server = (DefaultApacheDS) lookup( DefaultApacheDS.ROLE );

        // ----------------------------------------------------------------------
        // Adding a Person
        // ----------------------------------------------------------------------

        String uid = "rodriguez";

        Person expectedPerson = new Person( "Rodriguez", "Mr. Kerberos", "noices", "555-1212", "erodriguez", "committer" );

        System.err.println( "Adding" );
        Context context = new InitialDirContext( enviroment );

        context.bind( "uid=" + uid + "," + baseDN, expectedPerson );

        context.close();
        System.err.println( "added" );

        // ----------------------------------------------------------------------
        // Querying for the Person
        // ----------------------------------------------------------------------

        System.err.println( "quering" );
        context = new InitialDirContext( enviroment );

        Object object = context.lookup( "uid=" + uid + "," + baseDN );

        context.close();
        System.err.println( "queried" );

        assertNotNull( "Lookup on uid='" + uid + "', returned null." );

        assertEquals( "The returned object was of the wrong type", Person.class, object.getClass() );

        Person actualPerson = (Person) object;

        // ----------------------------------------------------------------------
        // Assert the Person
        // ----------------------------------------------------------------------

        assertEquals( expectedPerson.getLastname(), actualPerson.getLastname() );

        assertEquals( expectedPerson.getCn(), actualPerson.getCn() );

        assertEquals( expectedPerson.getPassword(), actualPerson.getPassword() );

        assertEquals( expectedPerson.getTelephoneNumber(), actualPerson.getTelephoneNumber() );

        assertEquals( expectedPerson.getSeealso(), actualPerson.getSeealso() );

        assertEquals( expectedPerson.getDescription(), actualPerson.getDescription() );

        // this will stop the server
        release( server );
    }
}
