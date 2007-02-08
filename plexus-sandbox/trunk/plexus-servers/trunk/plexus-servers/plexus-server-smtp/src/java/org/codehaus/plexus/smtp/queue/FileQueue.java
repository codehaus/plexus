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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.activity.Initializable;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.smtp.Address;
import org.codehaus.plexus.smtp.SmtpMessage;
import org.codehaus.plexus.smtp.InvalidAddressException;
import org.codehaus.plexus.smtp.queue.Queue;
import org.codehaus.plexus.smtp.queue.DeliveryQueueException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Provides an implementation of the DeliveryQueue interface that
 * uses simple flat files to persist queued messages.
 *
 * @author Eric Daugherty
 */
public class FileQueue
    extends AbstractLogEnabled
    implements Configurable, Initializable, Queue
{
    private static final String DELIMITER = "\r\n";

    private List messages = new ArrayList();

    private File queueDirectory;

    public FileQueue()
    {
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /** @see org.apache.avalon.framework.configuration.Configurable#configure */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        queueDirectory = new File( configuration.getChild( "directory" ).getValue() );
    }

    public void initialize()
        throws Exception
    {
        if ( !queueDirectory.exists() )
        {
            if ( !queueDirectory.mkdirs() )
            {
                getLogger().error( "Unable to initialize FileDeliveryQueue.  Directory: " + queueDirectory.getAbsolutePath() + " does not exist and could not be created." );
                throw new RuntimeException( "Unable to initialize FileDeliveryQueue.  Directory: " + queueDirectory.getAbsolutePath() + " does not exist and could not be created." );
            }
        }
        else if ( !queueDirectory.isDirectory() )
        {
            getLogger().error( "Unable to initialize FileDeliveryQueue.  The specified path: " + queueDirectory.getAbsolutePath() + " is not a directory." );
            throw new RuntimeException( "Unable to initialize FileDeliveryQueue.  Directory: " + queueDirectory.getAbsolutePath() + " is not a directory." );
        }

        loadMessages();
    }

    //***************************************************************
    // DeliveryQueue Interface Methods
    //***************************************************************

    /**
     * Adds a message to the queue for delivery.
     *
     * @param message the new message to process.
     */
    public void addMessage( SmtpMessage message )
        throws DeliveryQueueException
    {
        try
        {
            saveMessage( message );
            messages.add( message );
        }
        catch ( IOException ioException )
        {
            getLogger().error( "Unable to save SMTPMessage to a file.  Message ID: " + message.getId(), ioException );
            throw new DeliveryQueueException();
        }

    }

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
        try
        {
            saveMessage( message );
        }
        catch ( IOException ioException )
        {
            getLogger().error( "Unable to save updated SMTPMessage to a file.  Message ID: " + message.getId() + " Message may be delivered multiple times!", ioException );
            // No point in throwing an exception here since the message has already been delivered.
        }
    }

    /**
     * Removes the message from the queue, indicating that it has been
     * successfully delivered, or delivery has permamnantly failed.
     *
     * @param message the message to remove.
     */
    public void removeMessage( SmtpMessage message )
    {
        try
        {
            deleteMessage( message );
        }
        catch ( IOException ioException )
        {
            getLogger().error( "Unable to delete SMTPMessage after completed delivery.  Message ID: " + message.getId() + " Message removed from memory queue but must be deleted manually.", ioException );
        }
        finally
        {
            // Remove the message from the memory queue, so we don't redeliver it right away.
            // Hopefully, the administrator will monitor the log and delete it manually.
            messages.remove( message );
        }
    }


    /**
     * Write the message to disk so it will survive a process restart.
     *
     * @param message the message to persist.
     * @throws java.io.IOException thrown if there is an error writing the file.
     */
    private void saveMessage( SmtpMessage message ) throws IOException
    {

        File messageFile = new File( queueDirectory, message.getId() );
        FileWriter writer = new FileWriter( messageFile );
        try
        {
            writer.write( message.getId() );
            writer.write( DELIMITER );
            writer.write( message.getFromAddress().toString() );
            writer.write( DELIMITER );
            writer.write( flattenAddresses( message.getToAddresses() ) );
            writer.write( DELIMITER );
            writer.write( String.valueOf( message.getTimeReceived().getTime() ) );
            writer.write( DELIMITER );
            writer.write( String.valueOf( message.getScheduledDelivery().getTime() ) );
            writer.write( DELIMITER );
            writer.write( String.valueOf( message.getDeliveryAttempts() ) );
            writer.write( DELIMITER );
            writer.write( message.getData() );
        }
        finally
        {
            try
            {
                if ( writer != null )
                {
                    writer.close();
                }
            }
            catch ( IOException e )
            {
                getLogger().warn( "Unable to close spool file for SMTPMessage " + message.getId() );
            }
        }
    }

    /**
     * Deletes the queue file for the specified message.
     *
     * @param message the message to delete.
     * @throws java.io.IOException thrown if the file is not deleted successfully.
     */
    private void deleteMessage( SmtpMessage message ) throws IOException
    {
        File messageFile = new File( queueDirectory, message.getId() );
        if ( !messageFile.delete() )
        {
            throw new IOException( "Unable to delete file: " + messageFile.getAbsolutePath() );
        }
    }

    /**
     * Loads all the messages stored in the spool directory.  Any errors are logged
     * but not thrown.  If a message failes, processing continues to the next message.
     */
    private void loadMessages()
    {
        File[] messageFiles = queueDirectory.listFiles();

        File messageFile;
        int numFiles = messageFiles.length;
        for ( int index = 0; index < numFiles; index++ )
        {
            messageFile = messageFiles[index];
            if ( messageFile.isFile() )
            {
                try
                {
                    loadMessage( messageFile );
                }
                catch ( IOException ioException )
                {
                    getLogger().warn( "Unable to load SMTPMessage from file: " + messageFile.getAbsolutePath() + "  This message will not be delivered.", ioException );
                }
            }
        }
    }

    /**
     * Loads an individual message from disk.
     *
     * @param messageFile the File that contains the message.
     * @throws java.io.IOException thrown if there is any IO error while reading the message.
     */
    private void loadMessage( File messageFile ) throws IOException
    {

        FileReader fileReader = new FileReader( messageFile );
        BufferedReader reader = new BufferedReader( fileReader );


        try
        {
            SmtpMessage message = new SmtpMessage( reader.readLine() );
            message.setFromAddress( new Address( reader.readLine() ) );
            message.setToAddresses( inflateAddresses( reader.readLine() ) );
            message.setTimeReceived( new Date( Long.parseLong( reader.readLine() ) ) );
            message.setScheduledDelivery( new Date( Long.parseLong( reader.readLine() ) ) );
            message.setDeliveryAttempts( Integer.parseInt( reader.readLine() ) );

            String inputLine = reader.readLine();
            StringBuffer data = new StringBuffer();
            while ( inputLine != null )
            {
                data.append( inputLine );
                data.append( "\r\n" );
                inputLine = reader.readLine();
            }
            message.setData( data.toString() );

            messages.add( message );
        }
        catch ( InvalidAddressException invalidAddressException )
        {
            throw new IOException( "Unable to parse the address from the stored file." );
        }
        catch ( NumberFormatException numberFormatException )
        {
            throw new IOException( "Unable to parse the data from the stored file into a number.  " + numberFormatException.toString() );
        }
    }

    /**
     * Converts a <code>Collection</code> of <code>Address</code>
     * instances into a comma delimited string.
     *
     * @param addresses Collection of Address instances.
     * @return Comma delimited String of the addresses.
     */
    private String flattenAddresses( Collection addresses )
    {
        StringBuffer toAddresses = new StringBuffer();
        Address address;
        Iterator addressIterator = addresses.iterator();
        while ( addressIterator.hasNext() )
        {
            address = (Address) addressIterator.next();
            toAddresses.append( address.toString() );
            toAddresses.append( "," );
        }

        // Remove the last comma.
        toAddresses.deleteCharAt( toAddresses.length() - 1 );

        return toAddresses.toString();
    }

    /**
     * Converts a comma delimited string of addresses into a
     * <code>Collection</code> of <code>Address</code> instances.
     *
     * @param addresses Comma delimited String of addresses.
     * @return Collection of Address instances.
     */
    private Collection inflateAddresses( String addresses )
    {
        StringTokenizer addressTokenizer = new StringTokenizer( addresses, "," );
        List addressList = new ArrayList();
        Address address;

        try
        {
            while ( addressTokenizer.hasMoreTokens() )
            {
                address = new Address( addressTokenizer.nextToken() );
                addressList.add( address );
            }

            return addressList;
        }
        catch ( InvalidAddressException invalidAddressException )
        {
            getLogger().error( "Unable to parse to address read from database.  Full String is: " + addresses, invalidAddressException );
            throw new RuntimeException( "Error parsing address.  Message Delivery Failed." );
        }
    }
}
