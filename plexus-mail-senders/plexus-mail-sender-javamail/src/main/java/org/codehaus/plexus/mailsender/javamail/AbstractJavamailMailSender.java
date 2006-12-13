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

import org.codehaus.plexus.mailsender.AbstractMailSender;
import org.codehaus.plexus.mailsender.MailMessage;
import org.codehaus.plexus.mailsender.MailSenderException;
import org.codehaus.plexus.mailsender.util.DateFormatUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Iterator;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public abstract class AbstractJavamailMailSender
    extends AbstractMailSender
{
    public abstract Session getSession()
        throws MailSenderException;

    // ----------------------------------------------------------------------
    // MailSender Implementation
    // ----------------------------------------------------------------------

    public void send( MailMessage mail )
        throws MailSenderException
    {
        verify( mail );

        try
        {
            Session session = getSession();

            Message msg = new MimeMessage( session );
            InternetAddress addressFrom = new InternetAddress( mail.getFrom().getRfc2822Address() );
            msg.setFrom( addressFrom );

            if ( mail.getToAddresses().size() > 0 )
            {
                InternetAddress[] addressTo = new InternetAddress[mail.getToAddresses().size()];
                int count = 0;
                for ( Iterator i = mail.getToAddresses().iterator(); i.hasNext(); )
                {
                    String address = ( (MailMessage.Address) i.next() ).getRfc2822Address();
                    addressTo[count++] = new InternetAddress( address );
                }
                msg.setRecipients( Message.RecipientType.TO, addressTo );
            }

            if ( mail.getCcAddresses().size() > 0 )
            {
                InternetAddress[] addressCc = new InternetAddress[mail.getCcAddresses().size()];
                int count = 0;
                for ( Iterator i = mail.getCcAddresses().iterator(); i.hasNext(); )
                {
                    String address = ( (MailMessage.Address) i.next() ).getRfc2822Address();
                    addressCc[count++] = new InternetAddress( address );
                }
                msg.setRecipients( Message.RecipientType.CC, addressCc );
            }

            if ( mail.getBccAddresses().size() > 0 )
            {
                InternetAddress[] addressBcc = new InternetAddress[mail.getBccAddresses().size()];
                int count = 0;
                for ( Iterator i = mail.getBccAddresses().iterator(); i.hasNext(); )
                {
                    String address = ( (MailMessage.Address) i.next() ).getRfc2822Address();
                    addressBcc[count++] = new InternetAddress( address );
                }
                msg.setRecipients( Message.RecipientType.BCC, addressBcc );
            }

            // Setting the Subject and Content Type
            msg.setSubject( mail.getSubject() );
            if ( MailMessage.TYPE_HTML.equals( mail.getContentType() ) )
            {
                msg.setContent( mail.getContent(), "text/html" );
            }
            else
            {
                msg.setContent( mail.getContent(), "text/plain" );
            }

            if ( mail.getSendDate() != null )
            {
                msg.setHeader( "Date", DateFormatUtils.getDateHeader( mail.getSendDate() ) );
            }
            else
            {
                msg.setHeader( "Date", DateFormatUtils.getDateHeader( new Date() ) );
            }

            // Send the message
            Transport.send( msg );
        }
        catch ( MessagingException e )
        {
            throw new MailSenderException( "Error while sending mail.", e );
        }
    }
}
