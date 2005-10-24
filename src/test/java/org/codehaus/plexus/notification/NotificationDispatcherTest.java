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

import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.notification.notifier.EenyNotifier;
import org.codehaus.plexus.notification.notifier.MeenyNotifier;
import org.codehaus.plexus.notification.notifier.Message;
import org.codehaus.plexus.notification.notifier.MinyNotifier;
import org.codehaus.plexus.notification.notifier.MoNotifier;
import org.codehaus.plexus.notification.notifier.manager.NotifierManager;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class NotificationDispatcherTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        NotificationDispatcher notificationDispatcher = (NotificationDispatcher) lookup( NotificationDispatcher.ROLE );

        NotifierManager notifierManager = (NotifierManager) lookup( NotifierManager.ROLE );

        EenyNotifier eenyNotifier = (EenyNotifier) notifierManager.getNotifier( "eeny" );

        MeenyNotifier meenyNotifier = (MeenyNotifier) notifierManager.getNotifier( "meeny" );

        MinyNotifier minyNotifier = (MinyNotifier) notifierManager.getNotifier( "miny" );

        MoNotifier moNotifier = (MoNotifier) notifierManager.getNotifier( "mo" );

        // ----------------------------------------------------------------------
        // Send message
        // ----------------------------------------------------------------------

        String messageId = "buildComplete";

        Map context = new HashMap();

        notificationDispatcher.sendNotification( messageId, context );

        // ----------------------------------------------------------------------
        // Assert
        // ----------------------------------------------------------------------

        assertEquals( 1, eenyNotifier.messages.size() );

        assertEquals( 3, ( (Message) eenyNotifier.messages.get( 0 ) ).getRecipients().size() );

        assertEquals( new String[]{ "brett", "jason", "trygve" },
                      (String[]) ( (Message) eenyNotifier.messages.get( 0 ) ).getRecipients().toArray( new String[ 3 ] ) );

        assertEquals( 1, meenyNotifier.messages.size() );

        assertEquals( 3, ( (Message) meenyNotifier.messages.get( 0 ) ).getRecipients().size() );

        assertEquals( new String[]{ "bob", "brett", "jt" },
                      (String[]) ( (Message) meenyNotifier.messages.get( 0 ) ).getRecipients().toArray( new String[ 3 ] ) );

        assertEquals( 1, minyNotifier.messages.size() );

        assertEquals( 3, ( (Message) minyNotifier.messages.get( 0 ) ).getRecipients().size() );

        assertEquals( new String[]{ "brett", "dan", "topping" },
                      (String[]) ( (Message) minyNotifier.messages.get( 0 ) ).getRecipients().toArray( new String[ 3 ] ) );

        assertEquals( 1, moNotifier.messages.size() );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void assertEquals( String[] expected, String[] actual )
    {
        assertEquals( "Error in array length.", expected.length, actual.length );

        for ( int i = 0; i < expected.length; i++ )
        {
            assertEquals( "Error in element #" + i, expected[ i ], actual[ i ] );
        }
    }
}
