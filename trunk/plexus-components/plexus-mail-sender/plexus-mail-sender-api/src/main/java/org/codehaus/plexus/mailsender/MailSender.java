package org.codehaus.plexus.mailsender;

/*
 * LICENSE
 */

import java.util.Date;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface MailSender
{
    String ROLE = MailSender.class.getName();

    String getSmtpHost();

    void setSmtpHost( String smtpHost );

    int getSmtpPort();

    void setSmtpPort( int smtpPort );

    String getUsername();

    void setUsername( String username );

    void send( MailMessage message ) throws MailSenderException;

    void send( String subject, String content, String toAddress, String toName, String fromAddress, String fromName )
         throws MailSenderException;

    void send( String subject, String content, String toAddress, String toName, String fromAddress, String fromName, Map extraHeaders )
         throws MailSenderException;
}
