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

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.plexus.mailsender.AbstractMailSender;
import org.codehaus.plexus.mailsender.MailMessage;
import org.codehaus.plexus.mailsender.MailSenderException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class SimpleMailSender
	extends AbstractMailSender
{
    // ----------------------------------------------------------------------
    // MailSender Implementation
    // ----------------------------------------------------------------------

    public void send( MailMessage mail ) throws MailSenderException
	{
	    verify( mail );

        try
        {
            org.codehaus.plexus.mailsender.simple.MailMessage message = new org.codehaus.plexus.mailsender.simple.MailMessage( getSmtpHost(), getSmtpPort() );

            message.from( makeEmailAddress( mail.getFromAddress(), mail.getFromName() ) );

            for( Iterator iter = mail.getToAddresses().keySet().iterator(); iter.hasNext(); )
            {
                String toAddress = (String) iter.next();
                String toName = (String) mail.getToAddresses().get( toAddress );
                message.to( makeEmailAddress( toAddress, toName ) );
            }

            for( Iterator iter = mail.getCcAddresses().keySet().iterator(); iter.hasNext(); )
            {
                String ccAddress = (String) iter.next();
                String ccName = (String) mail.getCcAddresses().get( ccAddress );
                message.cc( makeEmailAddress( ccAddress, ccName ) );
            }

            for( Iterator iter = mail.getBccAddresses().keySet().iterator(); iter.hasNext(); )
            {
                String bccAddress = (String) iter.next();
                String bccName = (String) mail.getBccAddresses().get( bccAddress );
                message.bcc( makeEmailAddress( bccAddress, bccName ) );
            }

            message.setSubject( mail.getSubject() );

            for ( Iterator it = mail.getHeaders().entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();

                message.setHeader( entry.getKey().toString(), entry.getValue().toString() );
            }

            if ( mail.getSendDate() != null )
            {
                message.setHeader( "Date", DateFormatUtils.getDateHeader( mail.getSendDate() ) );
            }
            else
            {
                message.setHeader( "Date", DateFormatUtils.getDateHeader( new Date() ) );
            }

            message.getPrintStream().print( mail.getContent() );

            message.sendAndClose();
        }
        catch( IOException ex )
        {
            throw new MailSenderException( "Error while sending mail.", ex );
        }
    }
}
