package org.codehaus.plexus.jetty;

/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
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

import org.codehaus.plexus.PlexusTestCase;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Test the JettyPlusServletContainer class.
 */
public class JettyPlusServletContainerTest
    extends PlexusTestCase
{
    public void testJndi()
        throws Exception
    {
        JettyPlusServletContainer container = (JettyPlusServletContainer) lookup( ServletContainer.ROLE, "jetty-plus" );
        container.setJettyXmlFile( getTestFile( "target/test-classes/jetty.xml" ) );

        try
        {
            container.start();

            Context ctx = new InitialContext();
            ctx = (Context) ctx.lookup( "java:comp/env" );

            DataSource dataSource = (DataSource) ctx.lookup( "jdbc/testDS" );
            assertNotNull( dataSource );
            Connection conn = dataSource.getConnection();
            assertNotNull( conn );
            conn.close();
        }
        finally
        {
            container.stop();
        }
    }
}
