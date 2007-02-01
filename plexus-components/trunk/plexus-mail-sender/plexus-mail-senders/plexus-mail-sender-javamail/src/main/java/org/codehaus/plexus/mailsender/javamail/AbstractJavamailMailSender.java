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
import org.codehaus.plexus.util.StringUtils;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public abstract class AbstractJavamailMailSender
    extends AbstractMailSender
{
    public static final String MAIL_SMTP_HOST = "mail.smtp.host";

    public static final String MAIL_SMTP_PORT = "mail.smtp.port";

    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

    public static final String MAIL_SMTP_USER = "mail.smtp.user";

    public static final String MAIL_SMTP_PASSWORD = "password";

    public static final String MAIL_SMTP_SOCKETFACTORY_PORT = "mail.smtp.socketFactory.port";

    public static final String MAIL_SMTP_SOCKETFACTORY_CLASS = "mail.smtp.socketFactory.class";

    public static final String MAIL_SMTP_SOCKETFACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";

    public static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";

    public static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";

    public static final String MAIL_SMTP_DEBUG = "mail.smtp.debug";

    /**
     * @deprecated
     */
    public static final String MAIL_SMTP_DEBUG2 = "mail.debug";

    private Properties props = new Properties();

    public abstract Session getSession()
        throws MailSenderException;

    // ----------------------------------------------------------------------
    // MailSender Implementation
    // ----------------------------------------------------------------------

    public void send( MailMessage mail )
        throws MailSenderException
    {
        verify( mail );

        Session session = getSession();

        // To see the mail commands exchanged with the mail server, set the debug flag:
        if ( StringUtils.isNotEmpty( session.getProperty( MAIL_SMTP_DEBUG ) ) )
        {
            session.setDebug( ( new Boolean( session.getProperty( MAIL_SMTP_DEBUG ) ) ).booleanValue() );
        }
        else if ( StringUtils.isNotEmpty( session.getProperty( MAIL_SMTP_DEBUG2 ) ) )
        {
            session.setDebug( ( new Boolean( session.getProperty( MAIL_SMTP_DEBUG2 ) ) ).booleanValue() );
        }
        else
        {
            session.setDebug( getLogger().isDebugEnabled() );
        }

        try
        {
            // create a message that understands MIME types and headers
            MimeMessage message = new MimeMessage( session );

            // To have a name appear next to the email address, pass it to the constructor
            Address fromAddress = new InternetAddress( mail.getFrom().getRfc2822Address() );

            message.setFrom( fromAddress );

            // Make all the reply-to fields set to the given address.
            Address[] replyToAddresses = new Address[]{new InternetAddress( mail.getReplyTo().getRfc2822Address() )};
            message.setReplyTo( replyToAddresses );

            if ( mail.getToAddresses().size() > 0 )
            {
                InternetAddress[] addressTo = new InternetAddress[mail.getToAddresses().size()];
                int count = 0;
                for ( Iterator i = mail.getToAddresses().iterator(); i.hasNext(); )
                {
                    String address = ( (MailMessage.Address) i.next() ).getRfc2822Address();
                    addressTo[count++] = new InternetAddress( address );
                }
                message.setRecipients( Message.RecipientType.TO, addressTo );
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
                message.setRecipients( Message.RecipientType.CC, addressCc );
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
                message.setRecipients( Message.RecipientType.BCC, addressBcc );
            }

            // Set the subject of the message
            message.setSubject( mail.getSubject() );

            // To set the message content, specify the content and the mime type
            if ( MailMessage.TYPE_HTML.equals( mail.getContentType() ) )
            {
                message.setContent( mail.getContent(), "text/html" );
            }
            else
            {
                message.setContent( mail.getContent(), "text/plain; format=flowed" );
            }

            if ( mail.getSendDate() != null )
            {
                message.setHeader( "Date", DateFormatUtils.getDateHeader( mail.getSendDate() ) );
            }
            else
            {
                message.setHeader( "Date", DateFormatUtils.getDateHeader( new Date() ) );
            }

            message.saveChanges();

            String protocol = "smtp";
            if ( StringUtils.isNotEmpty( session.getProperty( MAIL_TRANSPORT_PROTOCOL ) ) )
            {
                protocol = session.getProperty( MAIL_TRANSPORT_PROTOCOL );
            }

            // Can't use the static Transport.send() method because it doesn't support smtps/ssl.
            Transport transport = session.getTransport( protocol );
            transport.connect( session.getProperty( MAIL_SMTP_HOST ),
                               new Integer( session.getProperty( MAIL_SMTP_PORT ) ).intValue(),
                               session.getProperty( MAIL_SMTP_USER ), session.getProperty( MAIL_SMTP_PASSWORD ) );
            transport.sendMessage( message, message.getAllRecipients() );
            transport.close();


        }
        catch ( SendFailedException e )
        {
            throw new MailSenderException( "Error while sending the message.", e );

        }
        catch ( MessagingException e )
        {
            throw new MailSenderException( "Error while sending the message.", e );

        }
        catch ( Exception e )
        {
            throw new MailSenderException( "Error while sending the message.", e );
        }
    }

    protected Properties getProperties()
    {
        return props;
    }

    protected void addProperty( String key, String value )
    {
        props.put( key, value );
    }

    protected void removeProperty( String key )
    {
        props.remove( key );
    }

    public void setSmtpHost( String host )
    {
        super.setSmtpHost( host );

        if ( StringUtils.isNotEmpty( host ) )
        {
            addProperty( MAIL_SMTP_HOST, host );
        }
        else
        {
            removeProperty( MAIL_SMTP_HOST );
        }
    }

    public void setSmtpPort( int port )
    {
        super.setSmtpPort( port );
        addProperty( MAIL_SMTP_PORT, String.valueOf( port ) );
    }

    public void setUsername( String username )
    {
        super.setUsername( username );

        if ( StringUtils.isNotEmpty( username ) )
        {
            addProperty( MAIL_SMTP_USER, username );
            addProperty( MAIL_SMTP_AUTH, "true" );
        }
        else
        {
            removeProperty( MAIL_SMTP_AUTH );
        }
    }

    public void setPassword( String password )
    {
        super.setPassword( password );
        if ( StringUtils.isNotEmpty( password ) )
        {
            addProperty( MAIL_SMTP_PASSWORD, password );
        }
    }
}
