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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.mailsender.AbstractMailSender;
import org.codehaus.plexus.mailsender.MailMessage;
import org.codehaus.plexus.mailsender.MailSenderException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class MockMailSender
	extends AbstractMailSender
{
    private List messages = new ArrayList();

    /**
    * Get email received by this instance since start up.
    * 
    * @return Returns a <code>Iterator</code> of <code>MailMessage</code>s.
    */
    public Iterator getReceivedEmail()
    {
        return messages.iterator();
    }

    /**
     * Returns the last received email or <code>null</code> if no emails has been sent.
     * 
     * @return Returns the last received email or <code>null</code> if no emails has been sent.
     */
    public MailMessage getLastReceivedEmail()
    {
        if ( messages.size() == 0 )
        {
            return null;
        }

        return (MailMessage) messages.get( messages.size() - 1 );
    }

    /**
     * Returns the email with the specified index in the list of sent emails.
     * 
     * @param index The index.
     * @return Returns the email with the specified index in the list of sent emails.
     */
    public MailMessage getReceivedEmail( int index )
    {
        return (MailMessage) messages.get( index );
    }

    /**
    * Get the number of messages received.
    * 
    * @return Returns the number of received mails.
    */
    public int getReceivedEmailSize()
    {
        return messages.size();
    }

    // ----------------------------------------------------------------------
    // MailSender Implementation
    // ----------------------------------------------------------------------

    public void send( MailMessage message ) throws MailSenderException
	{
	    verify( message );

	    messages.add( message );
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
    	throws Exception
    {
        if ( getSmtpPort() == 0 )
        {
            setSmtpPort( AbstractMailSender.DEFAULT_SMTP_PORT );
        }
    }
}
