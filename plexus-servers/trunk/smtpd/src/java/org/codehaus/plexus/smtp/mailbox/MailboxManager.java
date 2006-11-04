package org.codehaus.plexus.smtp.mailbox;

import org.codehaus.plexus.smtp.Address;
import org.codehaus.plexus.smtp.mailbox.Mailbox;

import java.net.InetAddress;

public interface MailboxManager
{
    public static String ROLE = MailboxManager.class.getName();

    /**
     * Verifies whether connections should be accepted from the
     * specified client address.
     *
     * @param address client's Internet address
     * @return true if it is acceptable.
     */
    boolean acceptClient( InetAddress address );

    /**
     * Verifies whether the specified user is a local user.
     * <p>
     * This class is called when the processor accepts a username
     * from the client.  Implementations may choose to always
     * return true and delay the validation until after the
     * password has been specified.  Performing this validation
     * will provide more information to the user, but may cause
     * security concerns (username harvesting).
     *
     * @param userName the username specified by the client.
     * @return true if the username is valid, false otherwise.
     */
    boolean validateUser( String userName );

    /**
     * Verfies that the specified login criteria match an existing
     * mailbox.  If they match, the mailbox id is returned.  This
     * id can be used by the openMailbox method to access the mailbox.
     * If they do not match, -1 is returned.
     *
     * @param userName the username specified by the client.
     * @param password the password specified by the client.
     * @return the mailbox's id, if matched, or null the mailbox does not exist.
     */
    String valiateMailbox( String userName, String password );

    /**
     * Determines which mailbox mail addressed to the the specified address
     * should be delivered.  If the address is invalid, null is returned.
     *
     * @param address email address of mailbox to deliver to.
     * @return the mailboxId the mail should be delivered to, or null if the address is invalid.
     */
    String validateAddress( Address address );

    /**
     * Opens a mailbox for exclusive access.  The mailboxId should be
     * retrieved using the validateMailbox method.
     *
     * @param mailboxId the unique id for the requested mailbox.
     * @return a Mailbox opened for exclusive access.
     */
    Mailbox lockMailbox( String mailboxId );

}
