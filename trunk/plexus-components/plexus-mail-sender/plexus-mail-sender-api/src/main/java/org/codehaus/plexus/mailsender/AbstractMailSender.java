package org.codehaus.plexus.mailsender;

/*
 * LICENSE
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractMailSender extends AbstractLogEnabled
    implements MailSender, Initializable
{
    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    private final static int DEFAULT_SMTP_PORT = 25;

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
