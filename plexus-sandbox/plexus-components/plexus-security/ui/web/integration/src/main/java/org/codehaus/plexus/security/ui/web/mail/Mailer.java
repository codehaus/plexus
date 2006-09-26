package org.codehaus.plexus.security.ui.web.mail;

/*
 * Copyright 2001-2006 The Codehaus.
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
import org.apache.velocity.exception.ResourceNotFoundException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.mailsender.MailMessage;
import org.codehaus.plexus.mailsender.MailSender;
import org.codehaus.plexus.mailsender.MailSenderException;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.policy.UserValidationSettings;
import org.codehaus.plexus.security.system.ApplicationDetails;
import org.codehaus.plexus.security.system.EmailSettings;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
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
        VelocityContext context = new VelocityContext();
        ApplicationDetails appdetails = securitySystem.getApplicationDetails();
        UserSecurityPolicy policy = securitySystem.getPolicy();
        UserValidationSettings validation = policy.getUserValidationSettings();
        EmailSettings email = securitySystem.getEmailSettings();

        context.put( "applicationName", appdetails.getApplicationName() );

        String loginUrl = appdetails.getApplicationUrl() + validation.getEmailLoginPath();
        
        context.put( "loginUrl", loginUrl );

        String feedback = email.getFeedback();
        
        if(feedback.startsWith( "/" ))
        {
            feedback = appdetails.getApplicationUrl() + feedback;
        }
        
        context.put( "feedback", feedback );

        context.put( "authkey", authkey.getKey() );
        
        context.put( "accountId", authkey.getForPrincipal() );

        SimpleDateFormat dateformatter = new SimpleDateFormat( appdetails.getTimestampFormat() );

        context.put( "requestedOn", dateformatter.format( authkey.getDateCreated() ) );

        if ( authkey.getDateExpires() != null )
        {
            context.put( "expiresOn", dateformatter.format( authkey.getDateExpires() ) );
        }
        else
        {
            context.put( "expiresOn", "(does not expire)" );
        }

        sendVelocityEmail( "newAccountValidationEmail", context, recipients, validation.getEmailSubject() );
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
        catch ( Exception e )
        {
            getLogger().error( "Unable to generate email for template '" + templateFile + "': " + e.getMessage(), e );
        }
    }

    public void sendMessage( Collection recipients, String subject, String content )
    {
        EmailSettings email = securitySystem.getEmailSettings();
        
        if ( recipients.isEmpty() )
        {
            getLogger().warn( "Mail Not Sent - No mail recipients for email. subject [" + subject + "]" );
            return;
        }

        if ( StringUtils.isEmpty( email.getFromAddress() ) )
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

            MailMessage.Address from = new MailMessage.Address( email.getFromAddress(), email.getFromUsername() );

            message.setFrom( from );

            Iterator it = recipients.iterator();
            while ( it.hasNext() )
            {
                String mailbox = (String) it.next();

                MailMessage.Address to = new MailMessage.Address( mailbox );
                message.addTo( to );
            }

            mailSender.send( message );
        }
        catch ( MailSenderException e )
        {
            getLogger().error( "Unable to send message, subject [" + subject + "]", e );
        }
    }
}
