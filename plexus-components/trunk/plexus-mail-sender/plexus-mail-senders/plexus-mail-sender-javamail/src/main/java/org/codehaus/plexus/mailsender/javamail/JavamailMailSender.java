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

import org.codehaus.plexus.mailsender.MailSenderException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.StringUtils;

import javax.mail.Session;
import java.util.Iterator;
import java.util.Properties;

/**
 * JavamailMailSender
 *
 * @version $Id$
 */
public class JavamailMailSender
    extends AbstractJavamailMailSender
    implements Initializable
{
    private Properties userProperties;

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
        if ( userProperties != null )
        {
            for ( Iterator i = userProperties.keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();

                String value = userProperties.getProperty( key );

                addProperty( key, value );
            }
        }
    }

    public void updateProps( Properties userProperties )
    {
        setUserProperties( userProperties );
        updateProps();
    }

    public Session getSession()
        throws MailSenderException
    {
        if ( "smtps".equals( getProperties().getProperty( AbstractJavamailMailSender.MAIL_TRANSPORT_PROTOCOL ) ) )
        {
            setSslMode( true );
        }
        else
        {
            setSslMode( false );
        }

        if ( StringUtils.isEmpty( getProperties().getProperty( AbstractJavamailMailSender.MAIL_SMTP_TIMEOUT ) ) )
        {
            addProperty( AbstractJavamailMailSender.MAIL_SMTP_TIMEOUT, "30000" );
        }
        Session session = Session.getInstance( getProperties(), null );

        return session;
    }

    public Properties getUserProperties()
    {
        return userProperties;
    }

    public void setUserProperties( Properties userProperties )
    {
        this.userProperties = userProperties;
    }

    public void setSslMode( boolean sslEnabled )
    {
        setSslMode( sslEnabled, false );
    }

    public void setSslMode( boolean sslEnabled, boolean tlsEnabled )
    {
        super.setSslMode( sslEnabled );
        if ( sslEnabled )
        {
            addProperty( "mail.smtps.socketFactory.class", DummySSLSocketFactory.class.getName() );

            if ( tlsEnabled )
            {
                addProperty( AbstractJavamailMailSender.MAIL_SMTP_STARTTLS_ENABLE, "true" );
            }
        }
        else
        {
            removeProperty( "mail.smtps.socketFactory.class" );
        }
    }
}
