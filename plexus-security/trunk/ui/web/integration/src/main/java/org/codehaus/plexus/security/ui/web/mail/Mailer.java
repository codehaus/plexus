package org.codehaus.plexus.security.ui.web.mail;

/*
 * Copyright 2005-2006 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.mailsender.MailMessage;
import org.codehaus.plexus.mailsender.MailSender;
import org.codehaus.plexus.mailsender.MailSenderException;
import org.codehaus.plexus.mailsender.javamail.JndiJavamailMailSender;
import org.codehaus.plexus.security.configuration.UserConfiguration;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.policy.UserValidationSettings;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.util.StringUtils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Mailer
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.security.ui.web.mail.Mailer"
 */
public class Mailer
    extends AbstractLogEnabled
{
    /**
     * @plexus.requirement role-hint="velocity"
     */
    private MailGenerator generator;

    /**
     * @plexus.requirement
     */
    private MailSender mailSender;

    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;

    /**
     * @plexus.requirement
     */
    private UserConfiguration config;

    public void sendAccountValidationEmail( Collection recipients, AuthenticationKey authkey, String baseUrl )
    {
        String content = generator.generateMail( "newAccountValidationEmail", authkey, baseUrl );

        UserSecurityPolicy policy = securitySystem.getPolicy();
        UserValidationSettings validation = policy.getUserValidationSettings();
        sendMessage( recipients, validation.getEmailSubject(), content );
    }

    public void sendPasswordResetEmail( Collection recipients, AuthenticationKey authkey, String baseUrl )
    {
        String content = generator.generateMail( "passwordResetEmail", authkey, baseUrl );

        UserSecurityPolicy policy = securitySystem.getPolicy();
        UserValidationSettings validation = policy.getUserValidationSettings();
        sendMessage( recipients, validation.getEmailSubject(), content );
    }

    public void sendMessage( Collection recipients, String subject, String content )
    {
        if ( recipients.isEmpty() )
        {
            getLogger().warn( "Mail Not Sent - No mail recipients for email. subject [" + subject + "]" );
            return;
        }

        String fromAddress = config.getString( "email.from.address" );
        String fromName = config.getString( "email.from.name" );

        if ( StringUtils.isEmpty( fromAddress ) )
        {
            getLogger().warn( "Mail Not Sent - No from address for email. subject [" + subject + "]" );
            return;
        }

        MailMessage message = new MailMessage();

        // TODO: Allow for configurable message headers.

        try
        {
            message.setSubject( subject );
            message.setContent( content );

            MailMessage.Address from = new MailMessage.Address( fromAddress, fromName );

            message.setFrom( from );

            Iterator it = recipients.iterator();
            while ( it.hasNext() )
            {
                String mailbox = (String) it.next();

                MailMessage.Address to = new MailMessage.Address( mailbox );
                message.addTo( to );
            }

            if ( mailSender instanceof JndiJavamailMailSender )
            {
                JndiJavamailMailSender jndiSender = (JndiJavamailMailSender) mailSender;
                jndiSender.setJndiSessionName( config.getString( "email.jndiSessionName" ) );
            }
            else
            {
                mailSender.setSmtpHost( config.getString( "email.smtp.host" ) );
                mailSender.setSmtpPort( config.getInt( "email.smtp.port" ) );
                mailSender.setUsername( config.getString( "email.smtp.username" ) );
                mailSender.setPassword( config.getString( "email.smtp.password" ) );
                mailSender.setSslMode( config.getBoolean( "email.smtp.ssl.enabled" ),
                                       config.getBoolean( "email.smtp.tls.enabled" ) );

                /* Not supported for now
                if ( mailSender.isSslMode() && ( mailSender instanceof JavamailMailSender ) )
                {
                    JavamailMailSender jmsender = (JavamailMailSender) mailSender;
                    jmsender.updateProps();
                    jmsender.setSslProvider( config.getString( "email.smtp.ssl.provider" ) );
                }*/
            }

            mailSender.send( message );
        }
        catch ( MailSenderException e )
        {
            getLogger().error( "Unable to send message, subject [" + subject + "]", e );
        }
    }
}
