package org.codehaus.plexus.naming;

import org.codehaus.plexus.PlexusTestCase;

import javax.naming.Context;
import javax.sql.DataSource;
import java.sql.Connection;

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
    }
}
