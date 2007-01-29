package org.codehaus.plexus.naming;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.sql.DataSource;

import org.codehaus.plexus.PlexusTestCase;

public class NamingTest
    extends PlexusTestCase
{
    public void testDataSource()
        throws Exception
    {
        Naming naming = (Naming) lookup( Naming.ROLE, "test" );

        assertNotNull( naming );

        Context ctx = naming.createInitialContext();
        ctx = (Context) ctx.lookup( "java:comp/env" );

        DataSource dataSource = (DataSource) ctx.lookup( "jdbc/testDB" );
        Connection conn = dataSource.getConnection();
        conn.close();
    }

    public void testEnvironment()
        throws Exception
    {
        Naming naming = (Naming) lookup( Naming.ROLE, "environments" );

        assertNotNull( naming );

        Context ctx = naming.createInitialContext();
        ctx = (Context) ctx.lookup( "java:comp/env" );

        Integer ten = (Integer) ctx.lookup( "ten" );
        assertNotNull( "ten is null", ten );
        assertEquals( "ten not 10", 10, ten.intValue() );

        String wine = (String) ctx.lookup( "wine" );
        assertNotNull( wine );
        assertEquals( "wine not bordeaux", "bordeaux", wine );

        NamingEnumeration namingEnumeration = ctx.list( "UserPasswords" );
        int userPasswordsNumber = 0;
        Map userPasswords = new HashMap();
        while ( namingEnumeration.hasMoreElements() )
        {
            Object object = namingEnumeration.nextElement();
            NameClassPair nameClassPair = (NameClassPair) object;
            String password = (String) ctx.lookup( "UserPasswords/" + nameClassPair.getName() );
            userPasswords.put( nameClassPair.getName(), password );
            userPasswordsNumber++;
            /*
             Binding binding = (Binding) object;
             String key = binding.getName();
             Object value = binding.getObject();
             */
            //log.info( "key " + key + ", value " + key );
        }
        assertEquals( 2, userPasswordsNumber );
        assertEquals( 2, userPasswords.size() );
        assertEquals( "pwdUser1", userPasswords.get( "User1" ) );
        assertEquals( "pwdUser2", userPasswords.get( "User2" ) );
    }
}
