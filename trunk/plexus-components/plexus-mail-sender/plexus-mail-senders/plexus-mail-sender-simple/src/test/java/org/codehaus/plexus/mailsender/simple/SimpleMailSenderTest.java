package org.codehaus.plexus.mailsender.simple;

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

import java.util.Iterator;

import org.codehaus.plexus.mailsender.MailSender;
import org.codehaus.plexus.mailsender.test.MailSenderTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class SimpleMailSenderTest
	extends MailSenderTestCase
{
    public void testDefaultComponentConfiguration()
    	throws Exception
    {
        startSmtpServer( 4000 );

        MailSender mailSender = (MailSender) lookup( MailSender.ROLE );

        assertNotNull( mailSender );

        assertTrue( mailSender instanceof SimpleMailSender );

        assertEquals( "localhost", mailSender.getSmtpHost() );

        assertEquals( 4000, mailSender.getSmtpPort() );

        mailSender.send( "mySubject", "myContent", "to@server.com", "to.name", "from@server.com", "from.name" );

        assertEquals( 1, getReceievedEmailSize() );

        SmtpMessage email = (SmtpMessage) getReceivedEmail().next();
        
        assertTrue( email.getHeaderValue( "Subject" ).equals( "mySubject" ) );

        assertTrue( email.getBody().equals( "myContent" ) );
    }

    public void testSend() throws Exception
    {
        startSmtpServer( 4000 );

        sendMessage( 4000, "sender@here.com", "Test", "Test Body", "receiver@there.com" );

        assertEquals( 1, getReceievedEmailSize() );

        Iterator emailIter = getReceivedEmail();

        SmtpMessage email = (SmtpMessage) emailIter.next();

        assertTrue( email.getHeaderValue( "Subject" ).equals( "Test" ) );

        assertTrue( email.getBody().equals( "Test Body" ) );

        stopSmtpServer();
    }

    private void sendMessage(int port, String from, String subject, String body, String to) throws Exception
    {
        org.codehaus.plexus.mailsender.simple.MailMessage message = new org.codehaus.plexus.mailsender.simple.MailMessage( "localhost", port );

        message.from( from );

        message.to( to );

        message.setSubject( subject );

        message.getPrintStream().print( body );

        message.sendAndClose();
    }
}
