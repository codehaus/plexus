package org.codehaus.plexus.smtp.mailbox;

import org.codehaus.plexus.smtp.SmtpMessage;

import java.io.IOException;
import java.io.Reader;

public interface Mailbox
{
    public static String ROLE = Mailbox.class.getName();

    /**
     * Returns the total number of messages currently stored in this mailbox.
     *
     * @return the number of messages stored.
     */
    long getMessageCount();

    /**
     * Returns the total size of the mailbox in bytes.
     *
     * @return the size (in bytes) of the entire maibox.
     */
    long getMailboxSize();

    /**
     * Returns the message ID for each message.
     *
     * @return an array of the message IDs of the messages in this mailbox.
     */
    String[] getMessageIds();

    /**
     * Returns the size (in bytes) of the specified message.
     *
     * @param messageId the unique ID of the message.
     * @return the size (in bytes) of the specified message.
     */
    long getMessageSize( String messageId );

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
    Reader getMessage( String messageId ) throws IOException;

    /**
     * Marks a message for deletion.  The message will actually be
     * deleted once the commitUpdates() method is called.
     *
     * @param messageId
     * @throws java.io.IOException thrown if there is an error accessing the mailbox
     */
    void deleteMessage( String messageId ) throws IOException;

    /**
     * Indicates whether the specified message is already marked
     * for deletion.
     *
     * @param messageId the unique ID of the message.
     * @return true if the messages is marked for deletion.
     * @throws java.io.IOException thrown if there is an error accessing the mailbox
     */
    boolean isMessageDeleted( String messageId ) throws IOException;

    /**
     * Clears the deleted flag on all messages.
     *
     * @throws java.io.IOException thrown if there is an error accessing the mailbox
     */
    void resetDeleteFlags() throws IOException;

    /**
     * Stores the specified message for retrieval.
     *
     * @param message the message to deliver.
     * @throws java.io.IOException thrown if there is an error accessing the mailbox
     */
    void deliverMessage( SmtpMessage message ) throws IOException;

    /**
     * Commits any changes and release the mailbox lock.
     *
     * @throws java.io.IOException thrown if there is an error accessing the mailbox
     */
    void commitUpdates() throws IOException;

    /**
     * Clear all changes made to the mailbox and release the mailbox lock.
     */
    void rollbackUpdates();

}
