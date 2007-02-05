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

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.smtp.Address;
import org.codehaus.plexus.smtp.mailbox.FileMailbox;

import java.io.File;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides an implementation of the MailboxManager interface that uses the
 * local file system to store messages.
 *
 * @author Eric Daugherty
 */
public class FileMailboxManager
    extends AbstractLogEnabled
    implements Configurable, Initializable, MailboxManager
{
    public static final String CONFIGURATION_PREFIX = "filemailbox.";

    public static final String MAILBOX_PATH_PARAMETER = CONFIGURATION_PREFIX + "root.dir";

    private File rootDirectory;

    private Map mailboxLocks;

    private Configuration configuration;

    //***************************************************************
    // Constructor
    //***************************************************************

    /**
     * Initializes the using the file path from the ConfigurationManager.
     */
    public FileMailboxManager()
    {
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /** @see org.apache.avalon.framework.configuration.Configurable#configure */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        this.configuration = configuration;

        rootDirectory = new File( configuration.getChild( "root-directory" ).getValue() );
    }

    public void initialize()
        throws Exception
    {
        mailboxLocks = Collections.synchronizedMap( new HashMap() );

        // Verify that it exists, and create it if it does not.
        if ( !rootDirectory.exists() )
        {
            if ( getLogger().isDebugEnabled() ) getLogger().debug( "Mailbox Root Directory does not exist ( " + rootDirectory.getAbsolutePath() + " ).  Creating..." );

            if ( !rootDirectory.mkdirs() )

            {
                getLogger().error( "Unable to create Maibox Root Directory ( " + rootDirectory.getAbsolutePath() + " ). Unable to initialize FileMaiboxValidator Plugin." );
                throw new RuntimeException( "Unable to create Maibox Root Directory ( " + rootDirectory.getAbsolutePath() + " ). Unable to initialize FileMaiboxValidator Plugin." );
            }
        }

        // If the rootDirectory refers to a file or other non-directory, throw an error.
        if ( !rootDirectory.isDirectory() )
        {
            getLogger().error( "Mailbox Root Directory ( " + rootDirectory.getAbsolutePath() + " ) is not a directory!.  Unable to initialize FileMaiboxValidator Plugin." );
            throw new RuntimeException( "Mailbox Root Directory ( " + rootDirectory.getAbsolutePath() + " ) is not a directory!.  Unable to initialize FileMaiboxValidator Plugin." );
        }
    }

    //***************************************************************
    // MailboxManager Methods
    //***************************************************************

    /**
     * Verifies whether connections should be accepted from the
     * specified client address.
     *
     * @param address client's Internet address
     * @return true if it is acceptable.
     */
    public boolean acceptClient( InetAddress address )
    {
        // Do not filter clients
        return true;
    }

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
    public boolean validateUser( String userName )
    {
        // Only verify users on validateMailbox
        return true;
    }

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
    public String valiateMailbox( String userName, String password )
    {
        String mailboxId = null;

        userName = userName.toLowerCase();
        String realPassword = configuration.getChild( CONFIGURATION_PREFIX + "user." + userName + ".password" ).getValue( null );

        // If the user specified password is valid, encrypt it.
        // the realPassword is already encrypted.
        if ( password != null )
        {
            password = encryptPassword( password );
        }

        // Verify the password.
        if ( password == null || realPassword == null || !password.equals( realPassword ) )
        {
            getLogger().info( "Authentication failed for user: " + userName );
        }

        // Load the mailbox ID
        else
        {
            mailboxId = configuration.getChild( CONFIGURATION_PREFIX + "user." + userName + ".mailbox" ).getValue( null );
            if ( mailboxId == null || mailboxId.length() == 0 )
            {
                mailboxId = null;
                getLogger().error( "User " + userName + " has a valid password, but is not assigned a mailbox." );
            }
            else
            {
                if ( getLogger().isInfoEnabled() ) getLogger().info( "User: " + userName + " successfully authenticated." );
            }
        }

        return mailboxId;
    }

    /**
     * Determines which mailbox mail addressed to the the specified address
     * should be delivered.  If the address is invalid, null is returned.
     *
     * @param address email address of mailbox to deliver to.
     * @return the mailboxId the mail should be delivered to, or null if the address is invalid.
     */
    public String validateAddress( Address address )
    {
        String userName = address.getUsername().toLowerCase();
        String realPassword = configuration.getChild( CONFIGURATION_PREFIX + "user." + userName + ".password" ).getValue( null );

        // If the user is defined then open the mailbox.
        if ( realPassword != null )
        {
            return userName;
        }

        return null;
    }

    /**
     * Opens a mailbox for exclusive access.  The mailboxId should be
     * retrieved using the validateMailbox method.
     *
     * @param mailboxId the unique id for the requested mailbox.
     * @return a Mailbox opened for exclusive access.
     */
    public synchronized Mailbox lockMailbox( String mailboxId )
    {

        // Get the mailbox's directory and validate it.
        File mailboxDirectory = getMailboxDirectory( mailboxId );
        if ( mailboxDirectory == null )
        {
            return null;
        }

        // If there is a lock, verify it is still valid.
        if ( mailboxLocks.containsKey( mailboxId ) )
        {
            // Get the time the file was last touched.
            Long lockTime = (Long) mailboxLocks.get( mailboxId );
            GregorianCalendar lastModified = new GregorianCalendar();
            lastModified.setTimeInMillis( lockTime.longValue() );

            // Get the oldest valid lock file.
            GregorianCalendar unlockTime = new GregorianCalendar();
            unlockTime.setTimeInMillis( System.currentTimeMillis() );
            unlockTime.roll( Calendar.MINUTE, -5 );

            // If the lock file is too old, remove it.
            if ( lastModified.before( unlockTime ) )
            {
                if ( getLogger().isInfoEnabled() ) getLogger().info( "Lock timed out for mailbox: " + mailboxId );
                mailboxLocks.remove( mailboxId );
            }
        }

        // Lock mailbox if unlocked.
        if ( !mailboxLocks.containsKey( mailboxId ) )
        {
            mailboxLocks.put( mailboxId, new Long( System.currentTimeMillis() ) );
            if ( getLogger().isInfoEnabled() ) getLogger().info( "Mailbox: " + mailboxId + " successfully locked." );
            if ( getLogger().isDebugEnabled() ) getLogger().debug( "Mailbox: " + mailboxId + " using directory: " + mailboxDirectory.getAbsolutePath() );
            return new FileMailbox( this, mailboxId, mailboxDirectory );
        }
        else
        {
            if ( getLogger().isInfoEnabled() ) getLogger().info( "Unable to aquire lock for mailbox: " + mailboxId + " because it is already locked" );
            return null;
        }
    }

    //***************************************************************
    // Public Utility Methods
    //***************************************************************

    /**
     * Unlocks a mailbox.
     *
     * @param mailboxId the unique ID of the mailbox.
     */
    synchronized void unlockMailbox( String mailboxId )
    {
        mailboxLocks.remove( mailboxId );
        if ( getLogger().isInfoEnabled() ) getLogger().info( "Mailbox: " + mailboxId + " successfully unlocked." );
    }

    /**
     * Creates a one-way has of the specified password.  This allows passwords to be
     * safely stored without exposing the original plain text password.
     *
     * @param password the password to encrypt.
     * @return the encrypted password, or null if encryption failed.
     */
    public String encryptPassword( String password )
    {

        try
        {
            MessageDigest md = MessageDigest.getInstance( "SHA" );

            // Conver the password to bytes and hash it.
            md.update( password.getBytes() );
            byte[] hash = md.digest();

            // Convert the hashed password back to a string.
            int hashLength = hash.length;
            StringBuffer hashStringBuf = new StringBuffer();
            String byteString;
            int byteLength;

            // Convert each byte back to char in a string.
            for ( int index = 0; index < hashLength; index++ )
            {

                byteString = String.valueOf( hash[index] + 128 );

                //Pad string to 3.  Otherwise hash may not be unique.
                byteLength = byteString.length();
                switch ( byteLength )
                {
                    case 1:
                        byteString = "00" + byteString;
                        break;
                    case 2:
                        byteString = "0" + byteString;
                        break;
                }
                hashStringBuf.append( byteString );
            }

            return hashStringBuf.toString();
        }
        catch ( NoSuchAlgorithmException nsae )
        {
            getLogger().error( "Error getting password hash: " + nsae.getMessage() );
            return null;
        }
    }

    //***************************************************************
    // Private Utility Methods
    //***************************************************************

    /**
     * Returns a file that references a specific mailbox's directory.
     *
     * @param mailboxId the unique id of the mailbox.
     * @return the mailbox directory File, or null if it could not be found or created.
     */
    private File getMailboxDirectory( String mailboxId )
    {
        File mailboxDirectory = new File( rootDirectory, mailboxId );

        // Verify the directory exists.  If not, create it.
        if ( !mailboxDirectory.exists() )
        {
            if ( !mailboxDirectory.mkdirs() )
            {
                getLogger().error( "Unable to create mailbox: " + mailboxId + " directory: " + mailboxDirectory.getAbsolutePath() );
                mailboxDirectory = null;
            }
        }

        return mailboxDirectory;
    }

}
