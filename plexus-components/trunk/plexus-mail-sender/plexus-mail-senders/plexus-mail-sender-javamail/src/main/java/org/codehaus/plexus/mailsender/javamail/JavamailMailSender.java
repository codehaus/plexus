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
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.StringUtils;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Provider;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

/**
 * JavamailMailSender
 *
 * @version $Id$
 */
public class JavamailMailSender
    extends AbstractMailSender
    implements Initializable
{
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    public static final String MAIL_SMTP_HOST = "mail.smtp.host";

    public static final String MAIL_SMTP_PORT = "mail.smtp.port";

    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

    public static final String MAIL_SMTP_SOCKETFACTORY_PORT = "mail.smtp.socketFactory.port";

    public static final String MAIL_SMTP_SOCKETFACTORY_CLASS = "mail.smtp.socketFactory.class";

    public static final String MAIL_SMTP_SOCKETFACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String sslProvider;

    private Properties userProperties;

    private Properties props = new Properties();

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

        setUsername( super.getUsername() );
        setSslMode( super.isSslMode() );

        updateProps();
    }

    public void updateProps()
    {
        props.put( "mail.debug", String.valueOf( getLogger().isDebugEnabled() ) );

        if ( userProperties != null )
        {
            for ( Iterator i = userProperties.keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();

                String value = userProperties.getProperty( key );

                props.put( key, value );
            }
        }
    }

    public void updateProps( Properties userProperties )
    {
        setUserProperties( userProperties );
        updateProps();
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
            Authenticator auth = null;

            if ( StringUtils.isNotEmpty( getUsername() ) )
            {
                auth = new Authenticator()
                {
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication( getUsername(), getPassword() );
                    }
                };
            }

            Session session = Session.getDefaultInstance( props, auth );

            session.setDebug( getLogger().isDebugEnabled() );

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
            msg.setContent( mail.getContent(), "text/plain" );

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

    public String getSslProvider()
    {
        return sslProvider;
    }

    public void setSslProvider( String sslProvider )
    {
        this.sslProvider = sslProvider;
    }

    public Properties getUserProperties()
    {
        return userProperties;
    }

    public void setUserProperties( Properties userProperties )
    {
        this.userProperties = userProperties;
    }

    public void setSmtpHost( String host )
    {
        super.setSmtpHost( host );

        if ( StringUtils.isNotEmpty( host ) )
        {
            props.put( MAIL_SMTP_HOST, host );
        }
        else
        {
            props.remove( MAIL_SMTP_HOST );
        }
    }

    public void setSmtpPort( int port )
    {
        super.setSmtpPort( port );
        props.put( MAIL_SMTP_PORT, String.valueOf( port ) );
    }

    public void setUsername( String name )
    {
        super.setUsername( name );

        if ( StringUtils.isNotEmpty( name ) )
        {
            props.put( MAIL_SMTP_AUTH, "true" );
        }
        else
        {
            props.remove( MAIL_SMTP_AUTH );
        }
    }

    public void setSslMode( boolean sslEnabled )
    {
        super.setSslMode( sslEnabled );

        if ( sslEnabled )
        {
            try
            {
                Provider p = (Provider) Class.forName( sslProvider ).newInstance();

                Security.addProvider( p );
            }
            catch ( Exception e )
            {
                throw new IllegalStateException(
                    "could not instantiate ssl security provider, check that " + "you have JSSE in your classpath" );
            }

            props.put( MAIL_SMTP_SOCKETFACTORY_PORT, String.valueOf( getSmtpPort() ) );

            props.put( MAIL_SMTP_SOCKETFACTORY_CLASS, SSL_FACTORY );

            props.put( MAIL_SMTP_SOCKETFACTORY_FALLBACK, "false" );
        }
        else
        {
            // TODO: Security.removeProvider( p.getName() );

            props.remove( MAIL_SMTP_SOCKETFACTORY_PORT );

            props.remove( MAIL_SMTP_SOCKETFACTORY_CLASS );

            props.remove( MAIL_SMTP_SOCKETFACTORY_FALLBACK );
        }
    }
}
