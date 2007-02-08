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

package org.codehaus.plexus.smtp.mailbox;

import org.codehaus.plexus.smtp.SmtpMessage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implemenets the Mailbox interface to provide a File (directory) based
 * mailbox implementation.
 *
 * @author Eric Daugherty
 */
public class FileMailbox implements Mailbox
{
    //***************************************************************
    // Variables
    //***************************************************************

    /**
     * The MailboxManager that manages this mailbox.
     */
    private FileMailboxManager fileMailboxManager;

    /**
     * The mailboxId of this mailbox instance.
     */
    private String mailboxId;

    /**
     * The directory that represents the current mailbox.
     */
    private File mailboxDirectory;

    /**
     * A list of the messages marked for deletion
     */
    private List deletedIds;

    //***************************************************************
    // Constructor
    //***************************************************************

    public FileMailbox( FileMailboxManager fileMailboxManager, String mailboxId, File mailboxDirectory )
    {
        this.fileMailboxManager = fileMailboxManager;
        this.mailboxId = mailboxId;
        this.mailboxDirectory = mailboxDirectory;
        deletedIds = new ArrayList();
    }

    //***************************************************************
    // Mailbox Methods
    //***************************************************************

    /**
     * Returns the total number of messages currently stored in this mailbox.
     *
     * @return the number of messages stored.
     */
    public long getMessageCount()
    {
        return mailboxDirectory.listFiles().length;
    }

    /**
     * Returns the total size of the mailbox in bytes.
     *
     * @return the size (in bytes) of the entire maibox.
     */
    public long getMailboxSize()
    {
        long size = 0;
        File[] files = mailboxDirectory.listFiles();
        for ( int index = 0; index < files.length; index++ )
        {
            size = files[index].length();
        }

        return size;
    }

    /**
     * Returns the message ID for each message.
     *
     * @return an array of the message IDs of the messages in this mailbox.
     */
    public String[] getMessageIds()
    {
        File[] files = mailboxDirectory.listFiles();
        String[] messageIds = new String[files.length];
        for ( int index = 0; index < files.length; index++ )
        {
            messageIds[index] = files[index].getName();
        }

        return messageIds;
    }

    /**
     * Returns the size (in bytes) of the specified message.
     *
     * @param messageId the unique ID of the message.
     * @return the size (in bytes) of the specified message.
     */
    public long getMessageSize( String messageId )
    {
        File message = new File( mailboxDirectory, messageId );
        return message.length();
    }

    /**
     * Returns a java.io.Reader which is positioned to read
     * the message from the storage system.
     * <p>
     * The client should close the Reader once the message has
     * been retrieved.
     *
     * @param messageId the unique ID of the message
     * @return a Reader that can access the message data.
     * @throws java.io.IOException thrown if there is an error accessing the message.
     */
    public Reader getMessage( String messageId ) throws IOException
    {
        File message = new File( mailboxDirectory, messageId );
        return new FileReader( message );
    }

    /**
     * Marks a message for deletion.  The message will actually be
     * deleted once the commitUpdates() method is called.
     *
     * @param messageId
     * @throws java.io.IOException thrown if there is an error accessing the mailbox
     */
    public void deleteMessage( String messageId ) throws IOException
    {
        deletedIds.add( messageId );
    }

    /**
     * Indicates whether the specified message is already marked
     * for deletion.
     *
     * @param messageId the unique ID of the message.
     * @return true if the messages is marked for deletion.
     * @throws java.io.IOException thrown if there is an error accessing the mailbox
     */
    public boolean isMessageDeleted( String messageId ) throws IOException
    {
        return deletedIds.contains( messageId );
    }

    /**
     * Clears the deleted flag on all messages.
     *
     * @throws java.io.IOException thrown if there is an error accessing the mailbox
     */
    public void resetDeleteFlags() throws IOException
    {
        deletedIds = new ArrayList();
    }

    /**
     * Stores the specified message for retrieval.
     *
     * @param message the message to deliver.
     * @throws java.io.IOException thrown if there is an error accessing the mailbox
     */
    public void deliverMessage( SmtpMessage message ) throws IOException
    {
        File messageFile = new File( mailboxDirectory, message.getId() );

        if ( messageFile.createNewFile() )
        {
            String messageData = message.getData();
            FileWriter fileWriter = new FileWriter( messageFile );
            try
            {
                fileWriter.write( messageData );
            }
            finally
            {
                fileWriter.close();
            }
        }
        else
        {
            throw new IOException( "Error creating message file: " + messageFile.getAbsolutePath() );
        }
    }

    /**
     * Commits any changes and release the mailbox lock.
     *
     * @throws java.io.IOException thrown if there is an error accessing the mailbox
     */
    public void commitUpdates() throws IOException
    {
        deleteMessages();
        fileMailboxManager.unlockMailbox( mailboxId );
    }

    /**
     * Clear all changes made to the mailbox and release the mailbox lock.
     */
    public void rollbackUpdates()
    {
        fileMailboxManager.unlockMailbox( mailboxId );
    }

    //***************************************************************
    // Private Methods
    //***************************************************************

    /**
     * Removes all messages marked for deletion from disk.
     *
     * @throws java.io.IOException thrown if any error occurs while removing a message.
     */
    private void deleteMessages() throws IOException
    {
        Iterator messages = deletedIds.iterator();
        File messageFile;

        while ( messages.hasNext() )
        {
            messageFile = new File( mailboxDirectory, (String) messages.next() );
            if ( !messageFile.delete() )
            {
                throw new IOException( "Unable to delete message: " + messageFile.getAbsolutePath() );
            }
        }
    }
}
