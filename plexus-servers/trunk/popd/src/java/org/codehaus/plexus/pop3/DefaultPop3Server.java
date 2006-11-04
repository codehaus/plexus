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

package org.codehaus.plexus.pop3;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.smtp.mailbox.Mailbox;
import org.codehaus.plexus.smtp.mailbox.MailboxManager;
import org.codehaus.plexus.smtp.server.AbstractServer;
import org.codehaus.plexus.smtp.server.connection.ConnectionHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.text.MessageFormat;

/**
 * Manages the interaction between the server plugins and the
 * POP3 client.  This class provides the implementation
 * of rfc1939.
 *
 * @author Eric Daugherty
 */
public class DefaultPop3Server
    extends AbstractServer
    implements Serviceable, Initializable
{
    //***************************************************************
    // Constants
    //***************************************************************

    // Message Names.  These constants are used to load messages from
    // the configuration data.
    public static final String MESSAGE_WELCOME = "welcome";
    public static final String MESSAGE_STAT = "stat";
    public static final String MESSAGE_LIST_ALL = "list.all";
    public static final String MESSAGE_LIST_ONE = "list.one";
    public static final String MESSAGE_UIDL_ALL = "uidl.all";
    public static final String MESSAGE_UIDL_ONE = "uidl.one";

    public static final String MESSAGE_OK = "ok";
    public static final String MESSAGE_CLIENT_INVALID = "client.invalid";
    public static final String MESSAGE_UNKNOWN_USER = "unknown.user";
    public static final String MESSAGE_NEED_USER_COMMAND = "need.user.command";
    public static final String MESSAGE_MAILBOX_LOCK_FAILED = "mailbox.lock.failed";
    public static final String MESSAGE_INVALID_AUTHENTICATION = "invalid.authentication";
    public static final String MESSAGE_INVALID_MESSAGE_NUMBER = "invalid.message.number";
    public static final String MESSAGE_MISSING_ARGUMENT = "argument.missing";
    public static final String MESSAGE_MISSING_SECOND_ARGUMENT = "second.argument.missing";
    public static final String MESSAGE_COMMAND_INVALID = "command.invalid";
    public static final String MESSAGE_COMMAND_NOT_IMPLEMENTED = "command.not.implemented";
    public static final String MESSAGE_COMMAND_ORDER_INVALID = "command.order.invalid";

    // These have been made public due to the use
    // of reflection.  Otherwise, they would be private.
    public static final int COMMAND_USER = 0;
    public static final int COMMAND_PASS = 1;
    public static final int COMMAND_APOP = 2;
    public static final int COMMAND_STAT = 3;
    public static final int COMMAND_LIST = 4;
    public static final int COMMAND_RETR = 5;
    public static final int COMMAND_DELE = 6;
    public static final int COMMAND_NOOP = 7;
    public static final int COMMAND_RSET = 8;
    public static final int COMMAND_TOP = 9;
    public static final int COMMAND_UIDL = 10;
    public static final int COMMAND_QUIT = 11;

    // Valid states.
    private static final int STATE_INITIAL = 0;
    private static final int STATE_AUTHORIZATION = 1;
    private static final int STATE_TRANSACTION = 2;

    /**
     * Assigns a bit (flag) to each possible command.
     */
    private static final int[] COMMAND_FLAGS = {( 1 << COMMAND_USER ),
                                                ( 1 << COMMAND_PASS ),
                                                ( 1 << COMMAND_APOP ),
                                                ( 1 << COMMAND_STAT ),
                                                ( 1 << COMMAND_LIST ),
                                                ( 1 << COMMAND_RETR ),
                                                ( 1 << COMMAND_DELE ),
                                                ( 1 << COMMAND_NOOP ),
                                                ( 1 << COMMAND_RSET ),
                                                ( 1 << COMMAND_TOP ),
                                                ( 1 << COMMAND_UIDL ),
                                                ( 1 << COMMAND_QUIT )
    };

    /**
     * These commands are defined in the spec (as optional), but are not implemented.
     */
    private static final int COMMAND_UNIMPLEMENTED_FLAG = ( COMMAND_FLAGS[COMMAND_APOP] );

    /**
     * These commands are always allowed, according to the spec.  The unimplemented
     * commands are included here since they will result in a message indicating
     * they are invalid, so we may as well allow them anywhere.
     */
    private static final int COMMAND_ALWAYS_ALLOWED_FLAG = ( COMMAND_UNIMPLEMENTED_FLAG |
        COMMAND_FLAGS[COMMAND_QUIT]
        );

    /**
     * Defines the commands that are allowed in the AUTHORIZATION state.
     */
    private static final int STATE_AUTHORIZATION_COMMANDS = COMMAND_ALWAYS_ALLOWED_FLAG |
        COMMAND_FLAGS[COMMAND_USER] |
        COMMAND_FLAGS[COMMAND_PASS];

    private static final int STATE_TRANSACTION_COMMANDS = COMMAND_ALWAYS_ALLOWED_FLAG |
        COMMAND_FLAGS[COMMAND_STAT] |
        COMMAND_FLAGS[COMMAND_LIST] |
        COMMAND_FLAGS[COMMAND_RETR] |
        COMMAND_FLAGS[COMMAND_DELE] |
        COMMAND_FLAGS[COMMAND_NOOP] |
        COMMAND_FLAGS[COMMAND_RSET] |
        COMMAND_FLAGS[COMMAND_TOP] |
        COMMAND_FLAGS[COMMAND_UIDL];

    /**
     * Defines the commands that are allowed for each state.
     */
    private static final int[] STATE_ALLOWED_COMMANDS = {( 0 ),
                                                         ( STATE_AUTHORIZATION_COMMANDS ),
                                                         ( STATE_TRANSACTION_COMMANDS )
    };

    //***************************************************************
    // Variables
    //***************************************************************

    /**
     * The current state of the client's 'transaction'.  Used to determine which
     * commands are acceptable.
     */
    private int state;

    /**
     * Provides validation of user authentication and access to individual mailboxes.
     */
    private MailboxManager mailboxManager;

    /**
     * Provides access to a specific user's Mailbox.
     */
    private Mailbox mailbox;

    /**
     * The current line of input.
     */
    private String inputLine;

    /**
     * The name specified to the USER command.  Only valid
     * during the AUTHORIZATION state.
     */
    private String user;

    //***************************************************************
    // Constructor
    //***************************************************************

    public DefaultPop3Server()
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

    /** @see org.apache.avalon.framework.activity.Initializable#initialize */
    public void initialize()
        throws Exception
    {
        state = STATE_INITIAL;
    }

    //***************************************************************
    // LineServerProcessor Method Implementations
    //***************************************************************

    protected boolean setLine( String line )
    {
        if ( line != null && line.trim().length() > 0 )
        {
            inputLine = line.trim();
            return true;
        }
        else
        {
            return false;
        }
    }

    //***************************************************************
    // ServerProcessor Method Implementations
    //***************************************************************

    /**
     * Called when the client input needs to be processed.
     */
    public void process( ConnectionHandler connectionHandler )
    {
        try
        {
            // If the connection was just established, write a welcome message.
            if ( state == STATE_INITIAL )
            {
                InetAddress clientAddress = connectionHandler.getClientAddress();

                // Log the connection.
                if ( getLogger().isInfoEnabled() ) getLogger().info( "POP3 Connection made from client: " + clientAddress.getHostName() + " (" + clientAddress.getHostAddress() + ")." );

                // Verify that this is a valid client.
                if ( mailboxManager.acceptClient( clientAddress ) )
                {
                    state = STATE_AUTHORIZATION;
                    writeLine( connectionHandler, getMessage( MESSAGE_WELCOME ) );
                }
                else
                {
                    // If the client is not valid, display an error message and close the connection.
                    writeLine( connectionHandler, getMessage( MESSAGE_CLIENT_INVALID ) );
                    connectionHandler.close();
                }
            }
            else
            {
                processCommand( connectionHandler );
            }
        }
        catch ( IOException ioException )
        {
            getLogger().error( "IOException while processing POP3 Connection: " + ioException.toString(), ioException );
            // Unlock and roll back the mailbox.
            if ( mailbox != null )
            {
                mailbox.rollbackUpdates();
            }
            try
            {
                connectionHandler.close();
            }
            catch ( IOException e )
            {
                getLogger().warn( "Error closing channel after IOException occured." );
            }
        }
        catch ( Throwable throwable )
        {
            getLogger().error( "Unknown exception occured while processing POP3 Connection: " + throwable.toString(), throwable );
            // Unlock and roll back the mailbox.
            if ( mailbox != null )
            {
                mailbox.rollbackUpdates();
            }
            try
            {
                connectionHandler.close();
            }
            catch ( IOException e )
            {
                getLogger().warn( "Error closing channel after IOException occured." );
            }
        }
    }

    /**
     * This method allows the Service to indicate that the
     * client has closed the connection.  This allows the implementation
     * to free any resources it may have allocated.
     */
    public void cleanup()
    {
        // Nothing to clean up.
    }

    //***************************************************************
    // Private Processing Methods
    //***************************************************************

    /**
     * Process a POP3 Command.
     *
     * @throws java.io.IOException thrown if an error occurs writing a client response.
     */
    private void processCommand( ConnectionHandler connectionHandler ) throws IOException
    {
        String command = inputLine;

        // Get the requested command.
        int commandId = getCommandId( command );
        if ( commandId == -1 )
        {
            String[] invalidCommandArguments = {command};
            writeLine( connectionHandler,  getMessage( MESSAGE_COMMAND_INVALID, invalidCommandArguments ) );
            return;
        }

        // Verify that the command is valid in the current state.
        if ( !isCommandValid( commandId ) )
        {
            String[] invalidOrderArguments = {command};
            writeLine( connectionHandler,  getMessage( MESSAGE_COMMAND_ORDER_INVALID, invalidOrderArguments ) );
            return;
        }

        // Handle the specific command.
        switch ( commandId )
        {
            case COMMAND_USER:
                processUserCommand( connectionHandler);
                break;
            case COMMAND_PASS:
                processPassCommand(connectionHandler);
                break;
            case COMMAND_APOP:
                writeLine( connectionHandler,  getMessage( MESSAGE_COMMAND_NOT_IMPLEMENTED ) );
                break;
            case COMMAND_STAT:
                processStatCommand(connectionHandler);
                break;
            case COMMAND_LIST:
                processListCommand(connectionHandler);
                break;
            case COMMAND_RETR:
                processRetrCommand(connectionHandler);
                break;
            case COMMAND_DELE:
                processDeleCommand(connectionHandler);
                break;
            case COMMAND_NOOP:
                writeLine( connectionHandler,  getMessage( MESSAGE_OK ) );
                break;
            case COMMAND_RSET:
                processRsetCommand(connectionHandler);
                break;
            case COMMAND_TOP:
                processTopCommand(connectionHandler);
                break;
            case COMMAND_UIDL:
                processUidlCommand(connectionHandler);
                break;
            case COMMAND_QUIT:
                if ( mailbox != null )
                {
                    mailbox.commitUpdates();
                }
                writeLine( connectionHandler,  getMessage( MESSAGE_OK ) );
                connectionHandler.close();
                break;
            default:
                String[] defaultArguments = {command};
                writeLine( connectionHandler,  getMessage( MESSAGE_COMMAND_INVALID, defaultArguments ) );
                break;
        }
    }

    /**
     * Handles the verfication and parsing of a USER command during authentication.
     *
     * @throws java.io.IOException
     */
    private void processUserCommand( ConnectionHandler connectionHandler ) throws IOException
    {
        String userName = getCommandArgument();

        if ( userName == null || userName.length() < 1 )
        {
            user = null;
            String[] arguments = {"USER"};
            writeLine( connectionHandler,  getMessage( MESSAGE_MISSING_ARGUMENT, arguments ) );
        }
        else
        {
            if ( mailboxManager.validateUser( userName ) )
            {
                user = userName;
                writeLine( connectionHandler,  getMessage( MESSAGE_OK ) );
            }
            else
            {
                user = null;
                writeLine( connectionHandler,  getMessage( MESSAGE_UNKNOWN_USER ) );
            }
        }
    }

    /**
     * Handles the verification and parsing of a PASS command during authentication.
     *
     * @throws java.io.IOException
     */
    private void processPassCommand( ConnectionHandler connectionHandler ) throws IOException
    {
        String password = getCommandArgument();

        // Must specify the user before the password.
        if ( user == null )
        {
            writeLine( connectionHandler,  getMessage( MESSAGE_NEED_USER_COMMAND ) );
        }
        // Make sure they actually gave a password.
        else if ( password == null || password.length() < 1 )
        {
            String[] arguments = {"PASS"};
            writeLine( connectionHandler,  getMessage( MESSAGE_MISSING_ARGUMENT, arguments ) );
        }
        // Validate the username and password.
        else
        {
            String mailboxId = mailboxManager.valiateMailbox( user, password );
            if ( mailboxId != null )
            {
                mailbox = mailboxManager.lockMailbox( mailboxId );
                if ( mailbox != null )
                {
                    state = STATE_TRANSACTION;
                    if ( getLogger().isInfoEnabled() ) getLogger().info( "User: " + user + " logged in to POP3 Server." );
                    writeLine( connectionHandler,  getMessage( MESSAGE_OK ) );
                }
                else
                {
                    writeLine( connectionHandler,  getMessage( MESSAGE_MAILBOX_LOCK_FAILED ) );
                }
            }
            else
            {
                writeLine( connectionHandler,  getMessage( MESSAGE_INVALID_AUTHENTICATION ) );
            }
        }
    }

    /**
     * Handles the processing of a STAT command during TRANSACTION.
     * <p>
     * The STAT command returns the total number of messages in the mailbox,
     * and the total size of all the messages.
     *
     * @throws java.io.IOException
     */
    private void processStatCommand( ConnectionHandler connectionHandler ) throws IOException
    {
        String messageCount = String.valueOf( mailbox.getMessageCount() );
        String mailboxSize = String.valueOf( mailbox.getMailboxSize() );
        String[] arguments = {messageCount, mailboxSize};
        writeLine( connectionHandler,  getMessage( MESSAGE_STAT, arguments ) );
    }

    /**
     * Handles the processing of a LIST command during TRANSACTION.
     * <p>
     * The LIST command returns the total number of messages in the mailbox,
     * and the total size of all messages.  It also lists each of the individual
     * messages and the size of each.
     * <p>
     * If a specific message number is specified, the LIST command only returns
     * information about that message.
     *
     * @throws java.io.IOException
     */
    private void processListCommand( ConnectionHandler connectionHandler ) throws IOException
    {
        String argument = getCommandArgument();

        // List all messages
        if ( argument.equals( "" ) )
        {
            String messageCount = String.valueOf( mailbox.getMessageCount() );
            String mailboxSize = String.valueOf( mailbox.getMailboxSize() );
            String[] arguments = {messageCount, mailboxSize};
            writeLine( connectionHandler,  getMessage( MESSAGE_LIST_ALL, arguments ) );

            String[] messageIds = mailbox.getMessageIds();
            String messageSize;
            for ( int index = 0; index < messageIds.length; index++ )
            {
                messageSize = String.valueOf( mailbox.getMessageSize( messageIds[index] ) );
                writeLine( connectionHandler,  String.valueOf( index + 1 ) + " " + messageSize );
            }
            writeLine( connectionHandler,  "." );
        }
        // List a specific message
        else
        {
            int messageNumber = getMessageNumber();

            // If the number is invalid, display an error message.
            if ( messageNumber < 0 )
            {
                String[] arguments = {argument};
                writeLine( connectionHandler,  getMessage( MESSAGE_INVALID_MESSAGE_NUMBER, arguments ) );
                return;
            }

            // Calcluate the index into the messageIds array.
            int index = messageNumber - 1;

            // Verify the message exists and is valid.
            String[] messageIds = mailbox.getMessageIds();
            if ( messageIds.length > messageNumber || mailbox.isMessageDeleted( messageIds[index] ) )
            {
                String[] arguments = {argument};
                writeLine( connectionHandler,  getMessage( MESSAGE_INVALID_MESSAGE_NUMBER, arguments ) );
            }
            else
            {
                String messageNumberString = String.valueOf( messageNumber );
                String messageSize = String.valueOf( mailbox.getMessageSize( messageIds[index] ) );
                String[] arguments = {messageNumberString, messageSize};
                writeLine( connectionHandler,  getMessage( MESSAGE_LIST_ONE, arguments ) );
            }
        }
    }

    /**
     * Handles the processing of a RETR command during TRANSACTION.
     * <p>
     * Retrieves the message from the Mailbox and sends it to the user.
     *
     * @throws java.io.IOException
     */
    private void processRetrCommand( ConnectionHandler connectionHandler ) throws IOException
    {
        int messageNumber = getMessageNumber();

        // If the number is invalid, display an error message.
        if ( messageNumber < 0 )
        {
            String[] arguments = {getCommandArgument()};
            writeLine( connectionHandler,  getMessage( MESSAGE_INVALID_MESSAGE_NUMBER, arguments ) );
            return;
        }

        // Calcluate the index into the messageIds array.
        int index = messageNumber - 1;

        // Verify the message exists and is valid.
        String[] messageIds = mailbox.getMessageIds();
        if ( messageIds.length > messageNumber || mailbox.isMessageDeleted( messageIds[index] ) )
        {
            String[] arguments = {getCommandArgument()};
            writeLine( connectionHandler,  getMessage( MESSAGE_INVALID_MESSAGE_NUMBER, arguments ) );
        }
        else
        {
            writeLine( connectionHandler,  getMessage( MESSAGE_OK ) );

            Reader reader = mailbox.getMessage( messageIds[index] );
            BufferedReader messageReader = null;
            try
            {
                messageReader = new BufferedReader( reader );

                String currentLine = messageReader.readLine();
                while ( currentLine != null )
                {
                    writeLine( connectionHandler,  currentLine );
                    currentLine = messageReader.readLine();
                }

                writeLine( connectionHandler,  "." );
            }
            finally
            {
                if ( messageReader != null )
                {
                    messageReader.close();
                }
            }
        }
    }

    /**
     * Handles the processing of a DELE command during TRANSACTION.
     * <p>
     * Marks the specified message for deletion.
     *
     * @throws java.io.IOException
     */
    private void processDeleCommand( ConnectionHandler connectionHandler ) throws IOException
    {
        int messageNumber = getMessageNumber();

        // If the number is invalid, display an error message.
        if ( messageNumber < 0 )
        {
            String[] arguments = {getCommandArgument()};
            writeLine( connectionHandler,  getMessage( MESSAGE_INVALID_MESSAGE_NUMBER, arguments ) );
            return;
        }

        // Calcluate the index into the messageIds array.
        int index = messageNumber - 1;

        // Verify the message exists and is valid.
        String[] messageIds = mailbox.getMessageIds();
        if ( messageIds.length > messageNumber || mailbox.isMessageDeleted( messageIds[index] ) )
        {
            String[] arguments = {getCommandArgument()};
            writeLine( connectionHandler,  getMessage( MESSAGE_INVALID_MESSAGE_NUMBER, arguments ) );
        }
        else
        {
            mailbox.deleteMessage( messageIds[index] );
            writeLine( connectionHandler,  getMessage( MESSAGE_OK ) );
        }

    }

    /**
     * Handles the processing of a RSET command during TRANSACTION.
     * <p>
     * Unmarks all messages marked for deletion.
     *
     * @throws java.io.IOException
     */
    private void processRsetCommand( ConnectionHandler connectionHandler ) throws IOException
    {
        mailbox.resetDeleteFlags();
        writeLine( connectionHandler,  getMessage( MESSAGE_OK ) );
    }

    /**
     * Handles the processing of a TOP command during TRANSACTION.
     * <p>
     * Returns the message header and the top of the message.
     *
     * @throws java.io.IOException
     */
    private void processTopCommand( ConnectionHandler connectionHandler) throws IOException
    {
        String argument = getCommandArgument();

        // Parse the message number and lines to read from the command input
        int messageNumber = 0;
        int numberOfLines = 0;

        int delimiterIndex = argument.indexOf( " " );
        if ( delimiterIndex < -1 || delimiterIndex + 1 > argument.length() )
        {
            String[] arguments = {"TOP"};
            writeLine( connectionHandler,  getMessage( MESSAGE_MISSING_SECOND_ARGUMENT, arguments ) );
            return;
        }

        String messageNumberString = argument.substring( 0, delimiterIndex ).trim();
        String numberOfLinesString = argument.substring( delimiterIndex + 1 ).trim();

        try
        {
            messageNumber = Integer.parseInt( messageNumberString );
            numberOfLines = Integer.parseInt( numberOfLinesString );
        }
        catch ( NumberFormatException nfe )
        {
            String[] arguments = {argument};
            writeLine( connectionHandler,  getMessage( MESSAGE_INVALID_MESSAGE_NUMBER, arguments ) );
            return;
        }

        // Calcluate the index into the messageIds array.
        int index = messageNumber - 1;

        // Verify the message exists and is valid.
        String[] messageIds = mailbox.getMessageIds();
        if ( messageIds.length > messageNumber || mailbox.isMessageDeleted( messageIds[index] ) )
        {
            String[] arguments = {argument};
            writeLine( connectionHandler,  getMessage( MESSAGE_INVALID_MESSAGE_NUMBER, arguments ) );
            return;
        }
        else
        {
            writeLine( connectionHandler,  getMessage( MESSAGE_OK ) );

            Reader reader = mailbox.getMessage( messageIds[index] );
            BufferedReader messageReader = null;
            try
            {
                messageReader = new BufferedReader( reader );

                // Write the header
                String currentLine = messageReader.readLine();
                while ( currentLine != null && !currentLine.equals( "" ) )
                {
                    writeLine( connectionHandler,  currentLine );
                    currentLine = messageReader.readLine();
                }

                //Write an empty line to seperate header from body.
                writeLine( connectionHandler,  "" );

                // Write the body
                int lineNumber = 0;
                currentLine = messageReader.readLine();
                while ( currentLine != null && lineNumber < numberOfLines )
                {
                    writeLine( connectionHandler,  currentLine );
                    currentLine = messageReader.readLine();
                    lineNumber++;
                }

                writeLine( connectionHandler,  "." );
            }
            finally
            {
                if ( messageReader != null )
                {
                    messageReader.close();
                }
            }
        }
    }

    /**
     * Handles the verfication and parsing of a UIDL command during authentication.
     * <p>
     * Returns the unique ID of all the messages, or the unique ID of a specific
     * message if a message number is specified.
     *
     * @throws java.io.IOException
     */
    private void processUidlCommand( ConnectionHandler connectionHandler) throws IOException
    {

        String argument = getCommandArgument();
        String[] messageIds = mailbox.getMessageIds();

        // List all messages
        if ( argument.equals( "" ) )
        {
            writeLine( connectionHandler,  getMessage( MESSAGE_OK ) );

            for ( int index = 0; index < messageIds.length; index++ )
            {
                String[] arguments = {String.valueOf( index + 1 ), messageIds[index]};
                writeLine( connectionHandler,  getMessage( MESSAGE_UIDL_ALL, arguments ) );
            }

            writeLine( connectionHandler,  "." );
        }
        // List a specific message
        else
        {
            int messageNumber = getMessageNumber();

            // If the number is invalid, display an error message.
            if ( messageNumber < 0 )
            {
                String[] arguments = {argument};
                writeLine( connectionHandler,  getMessage( MESSAGE_INVALID_MESSAGE_NUMBER, arguments ) );
                return;
            }

            // Calcluate the index into the messageIds array.
            int index = messageNumber - 1;

            // Verify the message exists and is valid.
            if ( messageIds.length > messageNumber || mailbox.isMessageDeleted( messageIds[index] ) )
            {
                String[] arguments = {argument};
                writeLine( connectionHandler,  getMessage( MESSAGE_INVALID_MESSAGE_NUMBER, arguments ) );
            }
            else
            {
                String[] arguments = {String.valueOf( messageNumber ), messageIds[index]};
                writeLine( connectionHandler,  getMessage( MESSAGE_UIDL_ONE, arguments ) );
            }
        }
    }

    //***************************************************************
    // Private Utility Methods
    //***************************************************************

    /**
     * Checks to verify the specified command is valid for the current state.
     *
     * @param commandID
     * @return
     */
    private boolean isCommandValid( int commandID )
    {
        int allowedCommands = STATE_ALLOWED_COMMANDS[state];
        int commandFlag = COMMAND_FLAGS[commandID];
        return ( allowedCommands & commandFlag ) != 0;
    }

    /**
     * Returns the commandId for the specified command string.
     *
     * @param command
     * @return
     */
    private int getCommandId( String command )
    {
        command = command.trim();
        command = command.toUpperCase();

        // All the commands are at least 3 charecters.
        if ( command.length() < 3 )
        {
            return -1;
        }

        String fieldName;
        if ( command.length() == 3 )
        {
            fieldName = command.substring( 0, 3 );
        }
        else
        {
            fieldName = command.substring( 0, 4 );
        }
        fieldName = "COMMAND_" + fieldName;

        try
        {
            Field field = this.getClass().getField( fieldName );

            return field.getInt( this );
        }
        catch ( Exception exception )
        {
            // Command does not exist.
            return -1;
        }
    }

    /**
     * Parses the argument from the command input data.
     *
     * @return argument string, or empty string if no argument was specified.
     */
    private String getCommandArgument()
    {
        // Get the argument
        int index = inputLine.indexOf( " " );

        String argument;
        if ( index == -1 )
        {
            argument = "";
        }
        else
        {
            argument = inputLine.substring( index + 1 );
        }

        if ( argument.length() > 0 )
        {
            argument = argument.trim();
        }

        return argument;
    }

    /**
     * Returns the command argument as an integer, or -1 if the
     * argument is not a valid number
     *
     * @return the command argument as a number, or -1 if invalid.
     */
    private int getMessageNumber()
    {
        String numberString = getCommandArgument();
        try
        {
            return Integer.parseInt( numberString );
        }
        catch ( NumberFormatException numberFormatException )
        {
            return -1;
        }
    }

    /**
     * Returns the message retrieved from the ConfigurationManager.
     *
     * @param messageName name of the message to retrieve.
     * @return the message text.
     *
     * @todo this will require the i18n service.
     *
     */
    private String getMessage( String messageName )
    {
        return "we need a localized pop3 message here!" + messageName;
    }

    /**
     * Returns a message from the ConfigurationManager formatted using the
     * MessageFormat class and the specified arguments.
     *
     * @param messageName name of the message to retrieve.
     * @param arguments objects used to format the message.
     * @return the formatted message text.
     */
    private String getMessage( String messageName, String[] arguments )
    {
        return MessageFormat.format( getMessage( messageName ), arguments );
    }
}
