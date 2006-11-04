/******************************************************************************
 * $Workfile:  $
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************
 * Copyright (c) 2003, Eric Daugherty (http://www.ericdaugherty.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Eric Daugherty nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 * *****************************************************************************
 * For current versions and more information, please visit:
 * http://www.ericdaugherty.com/java/mailserver
 *
 * or contact the author at:
 * java@ericdaugherty.com
 *****************************************************************************/

package org.codehaus.plexus.smtp.queue;

import org.codehaus.plexus.smtp.SmtpMessage;
import org.codehaus.plexus.smtp.queue.Queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Simple Memory based delivery queue.  This delivery queue should only be
 * used in cases where the loss of queued messages across process restarts
 * is acceptable.
 * <p>
 * Because this version is memory only, it will be faster than the
 * other alternatives.
 * <p>
 * Normal installs should use the FileDeliveryQueue or other persistance
 * based DeliveryQueues.
 *
 * @author Eric Daugherty
 */
public class MemoryQueue
    implements Queue
{
    private List messages = new ArrayList();

    /**
     * Adds a message to the queue for delivery.
     *
     * @param message the new message to process.
     */
    public void addMessage( SmtpMessage message )
    {
        messages.add( message );
    }

    /**
     * Returns a Collection containing all the messages
     * in the queue for delivery.
     * <p>
     * The queue should only return messages that are ready for delivery.  If a message
     * has a delivery date in the future, it should not be included in this iterator.
     * <p>
     * This method should continue to return all the messages with current delivery dates
     * for each call.  The caller must insure that message delivery is not attempted more
     * than once.
     *
     * @return SMTPMessages to deliver.
     */
    public Collection getMessagesForDelivery()
    {
        List deliveryMessages = new ArrayList();
        Iterator allMessages = messages.iterator();
        SmtpMessage message;
        long currentTime = System.currentTimeMillis();
        while ( allMessages.hasNext() )
        {
            message = (SmtpMessage) allMessages.next();
            if ( message.getScheduledDelivery().getTime() <= currentTime )
            {
                deliveryMessages.add( message );
            }
        }

        return deliveryMessages;
    }

    /**
     * Updates a message in the queue.  This should be called when
     * a message has been partially delivered.  The delivery attempts
     * and delivery time should have been updated, and any addresses
     * that were successful delievery should be removed from the list.
     *
     * @param message the updated message.
     */
    public void updateMessage( SmtpMessage message )
    {
        // Do nothing, since the message is just in memory, it is already updated.
    }

    /**
     * Removes the message from the queue, indicating that it has been
     * successfully delivered, or delivery has permamnantly failed.
     *
     * @param message the message to remove.
     */
    public void removeMessage( SmtpMessage message )
    {
        messages.remove( message );
    }

}
