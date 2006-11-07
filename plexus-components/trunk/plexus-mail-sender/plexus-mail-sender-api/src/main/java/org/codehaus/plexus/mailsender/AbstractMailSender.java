package org.codehaus.plexus.mailsender;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public abstract class AbstractMailSender
    extends AbstractLogEnabled
    implements MailSender
{
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public final static int DEFAULT_SMTP_PORT = 25;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String smtpHost;

    private int smtpPort;

    private String username;

    private String password;

    private boolean sslMode;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public String getSmtpHost()
    {
        return smtpHost;
    }

    public void setSmtpHost( String host )
    {
        this.smtpHost = host;
    }

    public int getSmtpPort()
    {
        return smtpPort;
    }

    public void setSmtpPort( int port )
    {
        this.smtpPort = port;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String name )
    {
        this.username = name;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String pass )
    {
        this.password = pass;
    }

    public boolean isSslMode()
    {
        return sslMode;
    }

    public void setSslMode( boolean sslEnabled )
    {
        this.sslMode = sslEnabled;
    }

    public void send( String subject, String content, String toMailbox, String toName, String fromMailbox,
                      String fromName )
        throws MailSenderException
    {
        send( subject, content, toMailbox, toName, fromMailbox, fromName, new HashMap() );
    }

    public void send( String subject, String content, String toMailbox, String toName, String fromMailbox,
                      String fromName, Map headers )
        throws MailSenderException
    {
        MailMessage message = new MailMessage();

        message.setSubject( subject );

        message.setContent( content );

        message.setFrom( fromMailbox, fromName );

        message.addTo( toMailbox, toName );

        for ( Iterator iter = headers.keySet().iterator(); iter.hasNext(); )
        {
            String key = (String) iter.next();

            Object header = headers.get( key );

            if (header instanceof List)
            {
                for ( Iterator headerVals = ((List) header).iterator(); iter.hasNext(); )
                {
                    message.addHeader( key, (String) headerVals.next() );
                }

            }
            else
            {
                message.addHeader( key, (String) headers.get( key ) );
            }
        }

        send( message );
    }

    public void verify( MailMessage message )
        throws MailSenderException
    {
        MailMessage.Address from = message.getFrom();

        if ( from.getMailbox() == null )
        {
            throw new MailSenderException( "From mailbox isn't set." );
        }

        if ( message.getToAddresses().size() == 0 &&
             message.getCcAddresses().size() == 0 &&
             message.getBccAddresses().size() == 0)
        {
            throw new MailSenderException( "The mail requires at least one recipient." );
        }
    }
}
