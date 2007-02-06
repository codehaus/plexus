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

import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
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
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

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
     * @plexus.requirement
     */
    private UserConfiguration config;

    /**
     * @plexus.requirement
     */
    private VelocityComponent velocity;

    /**
     * @plexus.requirement
     */
    private MailSender mailSender;

    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;

    public void sendAccountValidationEmail( Collection recipients, AuthenticationKey authkey )
    {
        UserSecurityPolicy policy = securitySystem.getPolicy();
        UserValidationSettings validation = policy.getUserValidationSettings();
        VelocityContext context = createVelocityContext( authkey );

        sendVelocityEmail( "newAccountValidationEmail", context, recipients, validation.getEmailSubject() );
    }

    private VelocityContext createVelocityContext( AuthenticationKey authkey )
    {
        VelocityContext context = new VelocityContext();

        context.put( "applicationName", config.getString( "application.name" ) );

        String appUrl = config.getString( "application.url" );

        context.put( "applicationUrl", appUrl );

        String feedback = config.getString( "email.feedback.path" );

        if ( feedback != null )
        {
            if ( feedback.startsWith( "/" ) )
            {
                feedback = appUrl + feedback;
            }

            context.put( "feedback", feedback );
        }

        context.put( "authkey", authkey.getKey() );

        context.put( "accountId", authkey.getForPrincipal() );

        SimpleDateFormat dateformatter = new SimpleDateFormat( config.getString( "application.timestamp" ), Locale.US );

        context.put( "requestedOn", dateformatter.format( authkey.getDateCreated() ) );

        if ( authkey.getDateExpires() != null )
        {
            context.put( "expiresOn", dateformatter.format( authkey.getDateExpires() ) );
        }
        else
        {
            context.put( "expiresOn", "(does not expire)" );
        }
        return context;
    }

    public void sendPasswordResetEmail( Collection recipients, AuthenticationKey authkey )
    {
        UserSecurityPolicy policy = securitySystem.getPolicy();
        UserValidationSettings validation = policy.getUserValidationSettings();
        VelocityContext context = createVelocityContext( authkey );

        sendVelocityEmail( "passwordResetEmail", context, recipients, validation.getEmailSubject() );
    }

    public void sendVelocityEmail( String templateName, VelocityContext context, Collection recipients, String subject )
    {
        String packageName = getClass().getPackage().getName().replace( '.', '/' );
        String templateFile = packageName + "/template/" + templateName + ".vm";

        StringWriter writer = new StringWriter();

        try
        {
            velocity.getEngine().mergeTemplate( templateFile, context, writer );

            String content = writer.getBuffer().toString();

            sendMessage( recipients, subject, content );
        }
        catch ( ResourceNotFoundException e )
        {
            getLogger().error( "No such template: '" + templateFile + "'." );
        }
        catch ( ParseErrorException e )
        {
            getLogger().error( "Unable to generate email for template '" + templateFile + "': " + e.getMessage(), e );
        }
        catch ( MethodInvocationException e )
        {
            getLogger().error( "Unable to generate email for template '" + templateFile + "': " + e.getMessage(), e );
        }
        catch ( Exception e )
        {
            getLogger().error( "Unable to generate email for template '" + templateFile + "': " + e.getMessage(), e );
        }
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
                mailSender.setSslMode( config.getBoolean( "email.smtp.ssl.enabled" ) );

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
