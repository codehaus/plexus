package org.codehaus.plexus.mailsender;

/*
 * LICENSE
 */

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface MailSender
{
    String ROLE = MailSender.class.getName();

    void sendMail( String subject, String content, String toAddress, String toName, String fromAddress, String fromName )
    	throws MailSenderException;

    void sendMail( String subject, String content, String toAddress, String toName, String fromAddress, String fromName, Map extraHeaders )
    	throws MailSenderException;
}
