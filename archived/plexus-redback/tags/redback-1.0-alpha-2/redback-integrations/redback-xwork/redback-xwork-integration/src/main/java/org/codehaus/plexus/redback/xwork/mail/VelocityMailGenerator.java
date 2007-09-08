package org.codehaus.plexus.redback.xwork.mail;

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
import org.codehaus.plexus.redback.configuration.UserConfiguration;
import org.codehaus.plexus.redback.keys.AuthenticationKey;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Mail generator component implementation using velocity.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id: Mailer.java 5591 2007-02-06 09:09:50Z evenisse $
 * @plexus.component role="org.codehaus.plexus.redback.xwork.mail.MailGenerator" role-hint="velocity"
 */
public class VelocityMailGenerator
    extends AbstractLogEnabled
    implements MailGenerator
{
    /**
     * @plexus.requirement
     */
    private UserConfiguration config;

    /**
     * @plexus.requirement
     */
    private VelocityComponent velocity;

    public String generateMail( String templateName, AuthenticationKey authkey, String baseUrl )
    {
        VelocityContext context = createVelocityContext( authkey, baseUrl );

        String packageName = getClass().getPackage().getName().replace( '.', '/' );
        String templateFile = packageName + "/template/" + templateName + ".vm";

        StringWriter writer = new StringWriter();

        try
        {
            velocity.getEngine().mergeTemplate( templateFile, context, writer );
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

        return writer.getBuffer().toString();
    }

    private VelocityContext createVelocityContext( AuthenticationKey authkey, String appUrl )
    {
        VelocityContext context = new VelocityContext();

        context.put( "applicationUrl", config.getString( "application.url", appUrl ) );

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
}
