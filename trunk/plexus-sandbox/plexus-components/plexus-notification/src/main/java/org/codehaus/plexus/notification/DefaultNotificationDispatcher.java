package org.codehaus.plexus.notification;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.notification.notifier.Notifier;
import org.codehaus.plexus.notification.notifier.manager.NotifierManager;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultNotificationDispatcher
    extends AbstractLogEnabled
    implements NotificationDispatcher
{
    /** @requirement */
    private NotifierManager notifierManager;

    /** @requirement */
    private RecipientSource recipientSource;

    // ----------------------------------------------------------------------
    // NotificationDispatcher Implementation
    // ----------------------------------------------------------------------

    public void sendNotification( String messageId, Map context )
        throws NotificationException
    {
        Map notifiers = notifierManager.getNotifiers();

        for ( Iterator it = notifiers.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();

            String notifierType = (String) entry.getKey();

            Notifier notifier = (Notifier) entry.getValue();

            Set recipients = recipientSource.getRecipients( messageId, notifierType );

            if ( recipients == null )
            {
                getLogger().error( "RecipientSource.getRecipients() returned null. " +
                                   "Message id: '" + messageId + "', notifier type: '" + notifierType + "'." );

                continue;
            }

            if ( recipients.size() == 0 )
            {
                continue;
            }

            try
            {
                notifier.sendNotification( messageId, recipients, context );
            }
            catch ( NotificationException e )
            {
                getLogger().warn( "Error while sending notification.", e );
            }
        }
    }
}
