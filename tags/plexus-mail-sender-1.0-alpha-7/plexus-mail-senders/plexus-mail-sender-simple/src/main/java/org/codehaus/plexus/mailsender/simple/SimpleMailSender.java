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
import java.util.List;

import org.codehaus.plexus.mailsender.AbstractMailSender;
import org.codehaus.plexus.mailsender.MailMessage;
import org.codehaus.plexus.mailsender.MailSenderException;
import org.codehaus.plexus.mailsender.util.DateFormatUtils;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class SimpleMailSender
	extends AbstractMailSender
    implements Initializable
{
    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        if ( StringUtils.isEmpty( getSmtpHost() ) )
        {
            throw new InitializationException( "Error in configuration: Missing smtpHost." );
        }

        if ( getSmtpPort() == 0 )
        {
            setSmtpPort( DEFAULT_SMTP_PORT );
        }
    }

    // ----------------------------------------------------------------------
    // MailSender Implementation
    // ----------------------------------------------------------------------

    public void send( MailMessage mail )
        throws MailSenderException
	{
	    verify( mail );

        try
        {
            SimpleMailMessage message = new SimpleMailMessage( getSmtpHost(), getSmtpPort() );

            message.from( mail.getFrom().getRfc2822Address() );

            for( Iterator iter = mail.getToAddresses().iterator(); iter.hasNext(); )
            {
                message.to( ((MailMessage.Address) iter.next()).getRfc2822Address() );
            }

            for( Iterator iter = mail.getCcAddresses().iterator(); iter.hasNext(); )
            {
                message.cc( ((MailMessage.Address) iter.next()).getRfc2822Address() );
            }

            for( Iterator iter = mail.getBccAddresses().iterator(); iter.hasNext(); )
            {
                message.bcc( ((MailMessage.Address) iter.next()).getRfc2822Address() );
            }

            message.setSubject( mail.getSubject() );

            for ( Iterator it = mail.getHeaders().entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();

                Object value = entry.getValue();
                if (value instanceof List)
                {
                    for (Iterator values = ((List) value).iterator(); values.hasNext();)
                    {
                        String content = (String) values.next();
                        message.setHeader( entry.getKey().toString(), StringUtils.clean( content ) );
                    }
                }
                else
                {
                    String content = (String) value;
                    message.setHeader( entry.getKey().toString(), StringUtils.clean( content ) );
                }
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
