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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public abstract class AbstractMailSender extends AbstractLogEnabled
    implements MailSender, Initializable
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

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    public String getSmtpHost()
    {
        return smtpHost;
    }

    public void setSmtpHost( String smtpHost )
    {
        this.smtpHost = smtpHost;
    }

    public int getSmtpPort()
    {
        return smtpPort;
    }

    public void setSmtpPort( int smtpPort )
    {
        this.smtpPort = smtpPort;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public void send( String subject, String content, String toAddress, String toName, String fromAddress, String fromName )
         throws MailSenderException
    {
        send( subject, content, toAddress, toName, fromAddress, fromName, new HashMap() );
    }

    public void send( String subject, String content, String toAddress, String toName, String fromAddress, String fromName, Map headers )
         throws MailSenderException
    {
        MailMessage message = new MailMessage();

        message.setSubject( subject );

        message.setContent( content );

        message.setFromAddress( fromAddress );

        message.setFromName( fromName );

        message.addTo( toName, toAddress );

        for( Iterator iter = headers.keySet().iterator(); iter.hasNext(); )
        {
            String key = (String) iter.next();

            message.addHeader( key, (String) headers.get( key ) );
        }

        send( message );
    }

    public void verify( MailMessage message ) throws MailSenderException
    {
        if ( message.getFromAddress() == null )
        {
            throw new MailSenderException( "From address isn't set." );
        }
        
        if ( message.getToAddresses().size() == 0 )
        {
            throw new MailSenderException( "You must add at least one to address.");
        }
    }

    protected String makeEmailAddress( String address, String name )
    {
        if ( name == null || name.length() == 0 )
        {
            return "<" + address + ">";
        }

        return name + "<" + address + ">";
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
    	throws Exception
    {
        if ( smtpHost == null || smtpHost.length() == 0 )
        {
            throw new MailSenderException( "Error in configuration: Missing smtpHost." );
        }

        if ( smtpPort == 0 )
        {
            smtpPort = DEFAULT_SMTP_PORT;
        }
    }
}
