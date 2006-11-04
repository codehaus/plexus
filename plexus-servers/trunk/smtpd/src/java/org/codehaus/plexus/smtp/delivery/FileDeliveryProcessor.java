package org.codehaus.plexus.smtp.delivery;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.smtp.Address;
import org.codehaus.plexus.smtp.SmtpMessage;
import org.codehaus.plexus.smtp.mailbox.MailboxManager;
import org.codehaus.plexus.smtp.mailbox.Mailbox;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;

import java.util.Iterator;
import java.util.List;

public class FileDeliveryProcessor
    extends AbstractLogEnabled
    implements Serviceable, DeliveryProcessor
{
    private MailboxManager mailboxManager;

    public FileDeliveryProcessor()
    {
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /** @see org.apache.avalon.framework.service.Serviceable#service */
    public void service( ServiceManager serviceManager )
        throws ServiceException
    {
        mailboxManager = (MailboxManager) serviceManager.lookup( MailboxManager.ROLE );
    }

    /**
     * Delivers the specified message to the addresses specified.
     *
     * Once a message has been successfully delivered (or a permanant error
     * occurs) to an address, the address
     * should be removed from the SMTPMessage.  Otherwise, delivery will
     * be reattempted for the addresses.
     *
     * @param message the message to deliver.
     * @param domain the domain to deliver the message to for these addresses.
     * @param addresses List of addresses that all get delivered to this domain.
     */
    public void deliverMessage( SmtpMessage message, String domain, List addresses )
    {
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Attempting to deliver message: " + message.getId() + " to local file mailboxes." );
        }

        // Attempt to deliver the message to each address.
        Iterator addressIterator = addresses.iterator();
        while ( addressIterator.hasNext() )
        {
            Address address = (Address) addressIterator.next();

            try
            {
                String mailboxId = mailboxManager.validateAddress( address );

                // If the address is a valid local address, deliver it.
                if ( mailboxId != null )
                {

                    // Lock the mailbox.
                    Mailbox mailbox = mailboxManager.lockMailbox( mailboxId );

                    // If we locked the mailbox, deliver the message.
                    if ( mailbox != null )
                    {
                        mailbox.deliverMessage( message );
                        mailbox.commitUpdates();
                        message.removeAddress( address );
                        if ( getLogger().isDebugEnabled() )
                        {
                            getLogger().debug( "Delivery successful for address: " + address.toString() +
                                               " for message: " + message.getId() );
                        }
                    }
                    // If we did not lock the mailbox, fail the delivery for this address.
                    else
                    {
                        if ( getLogger().isInfoEnabled() )
                        {
                            getLogger().info( "Delivery failed for address: " + address.toString() +
                                              " because the mailbox could not be locked." );
                        }
                    }
                }
                // The address is invalid, so bounce the email.
                else
                {
                    if ( getLogger().isInfoEnabled() )
                    {
                        getLogger().info( "Delivery failed for address: " + address.toString() +
                                          " because it does not map to a known mailbox." );
                    }
                    //TODO: Build bounce logic.
                }
            }
            catch ( Throwable throwable )
            {
                getLogger().warn( "Delivery failed for address: " + address.toString() +
                                  " for message: " + message.getId() + " due to: " + throwable, throwable );
            }
        }
    }
}
