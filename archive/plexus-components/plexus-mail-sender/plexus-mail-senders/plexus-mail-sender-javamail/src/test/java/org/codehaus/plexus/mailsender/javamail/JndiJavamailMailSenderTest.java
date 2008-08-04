package org.codehaus.plexus.mailsender.javamail;

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

import com.dumbster.smtp.SmtpMessage;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.mailsender.MailSender;
import org.codehaus.plexus.mailsender.test.SmtpServer;
import org.codehaus.plexus.naming.Naming;

import javax.mail.Session;
import javax.naming.Context;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class JndiJavamailMailSenderTest
    extends PlexusTestCase
{
    private JndiJavamailMailSender sender;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        // ----------------------------------------------------------------------
        // Get the sender and make sure it's the correct one
        // ----------------------------------------------------------------------

        MailSender mailSender = (MailSender) lookup( MailSender.ROLE );

        assertNotNull( mailSender );

        assertTrue( mailSender instanceof JndiJavamailMailSender );

        sender = (JndiJavamailMailSender) mailSender;

        assertEquals( "java:comp/env/mail/Session", sender.getJndiSessionName() );

        Naming naming = (Naming) lookup( Naming.ROLE, "test" );

        assertNotNull( naming );

        Context ctx = naming.createInitialContext();

        ctx = (Context) ctx.lookup( "java:comp/env" );

        Session session = (Session) ctx.lookup( "mail/Session" );

        assertEquals( "4000", session.getProperty( "mail.smtp.port" ) );
    }

    public void testSender()
        throws Exception
    {
        // ----------------------------------------------------------------------
        // Start the SMTP sever
        // ----------------------------------------------------------------------

        SmtpServer smtpServer = (SmtpServer) lookup( SmtpServer.ROLE );

        sender.send( "my subject", "my content", "to@server.com", "to.name", "from@server.com", "from.name" );

        Thread.sleep( 1000 );

        assertEquals( 1, smtpServer.getReceivedEmailSize() );

        SmtpMessage email = (SmtpMessage) smtpServer.getReceivedEmail().next();

        assertTrue( email.getHeaderValue( "Subject" ).equals( "my subject" ) );

        assertTrue( email.getBody().equals( "my content" ) );
    }
}
