package org.codehaus.plexus.mailsender.simple;

/*
 * LICENSE
 */

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.mailsender.MailSender;
import org.codehaus.plexus.mailsender.MailSenderException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SimpleMailSender
	extends AbstractLogEnabled
	implements MailSender, Initializable
{
    /** @configuration */
    private String smtpHost;

    /** @configuration */
    private Integer smtpPort;

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    private final static Integer DEFAULT_SMTP_PORT = new Integer( 25 );

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
    	throws Exception
    {
        if ( smtpHost == null || smtpHost.length() == 0 )
        {
            throw new MailSenderException( "Error in configuration: Missing smtp-host." );
        }

        if ( smtpPort == null )
        {
            smtpPort = DEFAULT_SMTP_PORT;
        }
    }

    // ----------------------------------------------------------------------
    // MailSender Implementation
    // ----------------------------------------------------------------------

    public void sendMail( String subject, String content, String toAddress, String toName, String fromAddress, String fromName )
    	throws MailSenderException
	{
        sendMail( subject, content, toAddress, toName, fromAddress, fromName );
	}

    public void sendMail( String subject, String content, String toAddress, String toName, String fromAddress, String fromName, Map extraHeaders )
    	throws MailSenderException
    {
        try
        {
            MailMessage message = new MailMessage( smtpHost, smtpPort.intValue() );

            message.from( makeEmailAddress( fromAddress, fromName ) );

            message.to( makeEmailAddress( toAddress, toName ) );

            message.setSubject( subject );

            for ( Iterator it = extraHeaders.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();

                message.setHeader( entry.getKey().toString(), entry.getValue().toString() );
            }

            message.setHeader( "Date", DateFormatUtils.getDateHeader( new Date() ) );

            message.getPrintStream().print( message );

            message.sendAndClose();
        }
        catch( IOException ex )
        {
            throw new MailSenderException( "Error while sending mail.", ex );
        }
    }


    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    private String makeEmailAddress( String address, String name )
    {
        if ( name == null || name.length() == 0 )
        {
            return "<" + address + ">";
        }

        return name + "<" + address + ">";
    }
}
