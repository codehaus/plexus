package org.codehaus.plexus.mailsender.test;

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

import com.dumbster.smtp.SimpleSmtpServer;

import java.util.Iterator;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class MailSenderTestCase
	extends PlexusTestCase
{
    private SimpleSmtpServer smtpServer;
    
    public void tearDown() throws Exception
    {
        stopSmtpServer();

        super.tearDown();
    }

    public void startSmtpServer( int port )
    {
        smtpServer = SimpleSmtpServer.start( port );
    }

    public void stopSmtpServer()
    {
        if (smtpServer != null )
        {
            smtpServer.stop();
            smtpServer = null;
        }
    }

    public SimpleSmtpServer getSmtpServer()
    {
        return smtpServer;
    }

    /**
    * Get email received by this instance since start up.
    * @return List of String
    */
    public Iterator getReceivedEmail()
    {
        return smtpServer.getReceivedEmail();
    }

    /**
    * Get the number of messages received.
    * @return size of received email list
    */
    public int getReceievedEmailSize()
    {
        return smtpServer.getReceievedEmailSize();
    }
}
