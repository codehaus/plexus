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

    String TYPE_HTML = "html";

    String TYPE_TEST = "test";

    String getFromAddress();

    void setFromAddress( String fromAddress);

    String getFromName();

    void setFromName( String fromName );

    Map getToAddresses();

    void addTo( String toName, String toAddress );

    Map getCcAddresses();

    void addCc( String ccName, String ccAddress );

    Map getBccAddresses();

    void addBcc( String bccName, String bccAddress );

    String getSubject();

    void setSubject( String subject );

    String getContent();

    void setContent( String content );

    String getContentType();

    void setContentType( String contentType );

    Date getSendDate();

    void setSendDate( Date sendDate );

    Map getHeaders();

    void addHeader( String headerName, String headerValue );

    String getSmtpHost();

    void setSmtpHost( String smtpHost );

    int getSmtpPort();

    void setSmtpPort( int smtpPort );

    String getUsername();

    void setUsername( String username );

    void send() throws MailSenderException;

    void send( String subject, String content, String toAddress, String toName, String fromAddress, String fromName )
         throws MailSenderException;

    void send( String subject, String content, String toAddress, String toName, String fromAddress, String fromName, Map extraHeaders )
         throws MailSenderException;
}
