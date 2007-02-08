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

import java.util.Collection;

/**
 * Defines the plugin interface for a DeliveryQueue.  The queue's job is
 * to persist the messages when they are received and make them
 * available provide access to them for delivery.
 */
public interface Queue
{
    /** Role of this component. */
    public static String ROLE = Queue.class.getName();

    /**
     * Adds a message to the queue for delivery.
     *
     * @param message the new message to process.
     * @exception org.codehaus.plexus.smtp.queue.DeliveryQueueException thrown when there is an error adding the message to the queue.
     */
    void addMessage( SmtpMessage message )
        throws DeliveryQueueException;

    /**
     * Returns an Collection containing all the messages
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
    Collection getMessagesForDelivery();

    /**
     * Updates a message in the queue.  This should be called when
     * a message has been partially delivered.  The delivery attempts
     * and delivery time should have been updated, and any addresses
     * that were successful delievery should be removed from the list.
     *
     * @param message the updated message.
     */
    void updateMessage( SmtpMessage message );

    /**
     * Removes the message from the queue, indicating that it has been
     * successfully delivered, or delivery has permamnantly failed.
     *
     * @param message the message to remove.
     */
    void removeMessage( SmtpMessage message );

}
