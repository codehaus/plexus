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

package org.codehaus.plexus.smtp;

import org.codehaus.plexus.smtp.Address;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Represents a SMTP Email message.  This message is used by the SMTPServerProcessor and
 * the SMTPDelivery systems to represent the current state of an incoming/outgoing
 * SMTP message.
 *
 * @author Eric Daugherty
 */
public class SmtpMessage
{
    private String id;
    private Address fromAddress;
    private Collection toAddresses = new ArrayList();
    private String data;
    private Date timeReceived;
    private Date scheduledDelivery;
    private int deliveryAttempts;

    public SmtpMessage( String messageId )
    {
        id = messageId;
        Date now = new Date();
        timeReceived = now;
        scheduledDelivery = now;
        deliveryAttempts = 0;
    }

    /**
     * The ID of the SMTP Message.
     *
     * @return The message's Id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * The ID of the SMTP Message.
     *
     * @param id The message's Id.
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * Address the email is from.
     *
     * @return sender's address.
     */
    public Address getFromAddress()
    {
        return fromAddress;
    }

    /**
     * Address the email is from.
     *
     * @param fromAddress sender's address.
     */
    public void setFromAddress( Address fromAddress )
    {
        this.fromAddress = fromAddress;
    }

    /**
     * A collection of addresses this email message needs to be delivered to.
     *
     * @return delivery addresses.
     */
    public Collection getToAddresses()
    {
        return toAddresses;

    }

    /**
     *
     * @param toAddresses
     */
    public void setToAddresses( Collection toAddresses )
    {
        this.toAddresses = toAddresses;
    }

    /**
     * Adds a new address to deliver this message to.
     *
     * @param address delivery address.
     */
    public void addToAddress( Address address )
    {
        toAddresses.add( address );
    }

    /**
     * Removes an address from the list, after successful delivery.
     *
     * @param address delivery Address.
     */
    public void removeAddress( Address address )
    {
        toAddresses.remove( address );
    }

    /**
     * Removes all the addresses in the collection, after successful delivery (or permanent error).
     *
     * @param addresses collection of addresses to remove.
     */
    public void removeAddresses( Collection addresses )
    {
        toAddresses.removeAll( addresses );
    }

    /**
     * The actual message.
     *
     * @return the message.
     */
    public String getData()
    {
        return data;
    }

    /**
     * The actual message.
     *
     * @param data the message.
     */
    public void setData( String data )
    {
        this.data = data;
    }

    /**
     * The time the message was first received by the system.
     *
     * @return time the message was received.
     */
    public Date getTimeReceived()
    {
        return timeReceived;
    }

    /**
     * The time the message was first received by the system.
     *
     * @param timeReceived time the message was received.
     */
    public void setTimeReceived( Date timeReceived )
    {
        this.timeReceived = timeReceived;
    }

    /**
     * The next time delivery of this message should be attempted.
     *
     * @return the next schedule delivery time.
     */
    public Date getScheduledDelivery()
    {
        return scheduledDelivery;
    }

    /**
     * The next time delivery of this message should be attempted.
     *
     * @param scheduledDelivery the next schedule delivery time.
     */
    public void setScheduledDelivery( Date scheduledDelivery )
    {
        this.scheduledDelivery = scheduledDelivery;
    }

    /**
     * The number of times delivery of this message has been attempted.
     *
     * @return number of delivery attempts.
     */
    public int getDeliveryAttempts()
    {
        return deliveryAttempts;
    }

    /**
     * The number of times delivery of this message has been attempted.
     *
     * @param deliveryAttempts number of delivery attempts.
     */
    public void setDeliveryAttempts( int deliveryAttempts )
    {
        this.deliveryAttempts = deliveryAttempts;
    }
}
