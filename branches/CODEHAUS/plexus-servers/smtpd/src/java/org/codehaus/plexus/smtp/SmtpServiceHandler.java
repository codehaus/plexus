package org.codehaus.plexus.smtp;

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

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.smtp.queue.DeliveryQueueException;
import org.codehaus.plexus.smtp.queue.Queue;
import org.codehaus.plexus.synapse.handler.AbstractServiceHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Random;

/**
 * Manages the interaction between the server plugins and the
 * SMTP client.  This class provides the implementation
 * of rfc821.
 *
 * @author Eric Daugherty
 */
public class SmtpServiceHandler
    extends AbstractServiceHandler
    implements Serviceable, Initializable
{
    private static final int DATA_BUFFER_SIZE = 1024;

    // Message Names.  These constants are used to load messages from
    // the configuration data.
    public static final String MESSAGE_WELCOME = "welcome";
    public static final String MESSAGE_HELO = "helo";
    public static final String MESSAGE_DATA = "data";
    public static final String MESSAGE_HELP = "help";
    public static final String MESSAGE_QUIT = "quit";

    public static final String MESSAGE_OK = "ok";
    public static final String MESSAGE_CLIENT_INVALID = "client.invalid";
    public static final String MESSAGE_HELO_INVALID = "helo.invalid";
    public static final String MESSAGE_MAIL_ADDRESS_REJECTED = "mail.address.rejected";
    public static final String MESSAGE_RCPT_ADDRESS_REJECTED = "rcpt.address.rejected";
    public static final String MESSAGE_COMMAND_INVALID = "command.invalid";
    public static final String MESSAGE_COMMAND_NOT_IMPLEMENTED = "command.not.implemented";
    public static final String MESSAGE_COMMAND_ORDER_INVALID = "command.order.invalid";
    public static final String MESSAGE_ADDRESS_INVALID = "address.invalid";
    public static final String MESSAGE_TRANSACTION_FAILED = "transaction.failed";

    // These have been made public due to the use
    // of reflection.  Otherwise, they would be private.
    public static final int COMMAND_HELO = 0;
    public static final int COMMAND_MAIL = 1;
    public static final int COMMAND_RCPT = 2;
    public static final int COMMAND_DATA = 3;
    public static final int COMMAND_RSET = 4;
    public static final int COMMAND_HELP = 10;
    public static final int COMMAND_NOOP = 11;
    public static final int COMMAND_QUIT = 12;
    public static final int COMMAND_TURN = 13;

    private String heloDomain;

    private Queue queue;

    private SmtpMessage message;

    private String inputLine;

    private StringBuffer data;

    private Random random;

    private String domain;

    private boolean running = true;

    private boolean collectingMessage;

    public SmtpServiceHandler()
    {
    }

    // ----------------------------------------------------------------------
    // API
    // ----------------------------------------------------------------------

    public void handleEvents()
        throws Exception
    {
    }

    public void handleEvent( Socket socket )
        throws Exception
    {
        Client c = new Client( socket );

        c.start();
    }

    class Client
        extends Thread
    {
        private Socket socket;

        private BufferedReader reader;

        private PrintWriter writer;

        public Client( Socket socket )
            throws Exception
        {
            this.socket = socket;

            reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

            writer = new PrintWriter( new OutputStreamWriter( socket.getOutputStream() ) );
        }

        public void run()
        {
            InetAddress clientAddress = socket.getInetAddress();

            writer.write( getMessage( MESSAGE_WELCOME ) + "\r\n" );

            writer.flush();

            while ( running )
            {
                try
                {
                    inputLine = reader.readLine();

                    if ( collectingMessage )
                    {
                        if ( inputLine.trim().equals(".") )
                        {
                            processData( socket );
                        }
                        else
                        {
                            data.append( inputLine ).append("\r\n");
                        }
                    }
                    else
                    {
                        processCommand( socket );
                    }
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getHandleKey()
    {
        return "PROCESS";
    }

    /**
     * Process a SMTP Command.
     *
     * @throws java.io.IOException thrown if an error occurs writing a client response.
     */
    private void processCommand( Socket socket ) throws IOException
    {
        String command = inputLine;

        // Get the requested command.
        int commandId = getCommandId( command );

        if ( commandId == -1 )
        {
            String[] invalidCommandArguments = {command};
            writeLine( socket, getMessage( MESSAGE_COMMAND_INVALID, invalidCommandArguments ) );
            return;
        }

        // Handle the specific command.
        switch ( commandId )
        {
            case COMMAND_HELO:
                processHeloCommand( socket );
                break;
            case COMMAND_MAIL:
                processMailCommand( socket );
                break;
            case COMMAND_RCPT:
                processRcptCommand( socket );
                break;
            case COMMAND_DATA:
                collectingMessage = true;
                writeLine( socket, getMessage( MESSAGE_DATA ) );
                break;
            case COMMAND_RSET:
                reset();
                writeLine( socket, getMessage( MESSAGE_OK ) );
                break;
            case COMMAND_HELP:
                writeLine( socket, getMessage( MESSAGE_HELP ) );
                break;
            case COMMAND_NOOP:
                writeLine( socket, getMessage( MESSAGE_OK ) );
                break;
            case COMMAND_QUIT:
                String[] quitArguments = {domain};
                writeLine( socket, getMessage( MESSAGE_QUIT, quitArguments ) );
                socket.close();
                running = false;
                break;
            case COMMAND_TURN:
            default:
                String[] defaultArguments = {command};
                writeLine( socket, getMessage( MESSAGE_COMMAND_INVALID, defaultArguments ) );
                break;
        }
    }

    private void processData( Socket socket )
        throws IOException
    {
        //Prepend the Header information.
        StringBuffer messageData = new StringBuffer();
        messageData.append( "Received: from " );
        messageData.append( heloDomain );
        InetAddress clientAddress = socket.getInetAddress();
        messageData.append( " (" );
        messageData.append( clientAddress.getHostName() );
        messageData.append( " [" );
        messageData.append( clientAddress.getHostAddress() );
        messageData.append( "])\r\n" );
        messageData.append( "          by " );
        messageData.append( "Plexus SMTPD with SMTP ID " );
        messageData.append( message.getId() );
        messageData.append( "\r\n" );
        messageData.append( data.toString() );
        message.setData( messageData.toString() );

        try
        {
            queue.addMessage( message );

            writeLine( socket, getMessage( MESSAGE_OK ) );
        }
        catch ( DeliveryQueueException e )
        {
            writeLine( socket, getMessage( MESSAGE_TRANSACTION_FAILED ) );
        }
        finally
        {
            reset();
        }
    }

    private void processHeloCommand( Socket socket )
        throws IOException
    {
        InetAddress address = socket.getInetAddress();

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

        heloDomain = argument;
        String[] heloArguments = {domain, ( address.getHostName() + " [" + address.getHostAddress() + "]" )};
        writeLine( socket, getMessage( MESSAGE_HELO, heloArguments ) );
    }

    private void processMailCommand( Socket socket )
        throws IOException
    {
        String command = inputLine;

        // Verify the full command.
        if ( command.length() < 10 || !command.toUpperCase().startsWith( "MAIL FROM:" ) )
        {
            String[] invalidCommandArguments = {command};
            writeLine( socket, getMessage( MESSAGE_COMMAND_INVALID, invalidCommandArguments ) );
            return;
        }

        // Parse Address
        Address address = null;
        try
        {
            address = parseAddress( command.substring( 10 ).trim() );
        }
        catch ( InvalidAddressException e )
        {
            String[] invalidAddressArguments = {command.substring( 10 ).trim()};
            writeLine( socket, getMessage( MESSAGE_ADDRESS_INVALID, invalidAddressArguments ) );
            return;
        }

        message.setFromAddress( address );
        writeLine( socket, getMessage( MESSAGE_OK ) );
    }

    private void processRcptCommand( Socket socket )
        throws IOException
    {
        String command = inputLine;

        // Verify the full command.
        if ( command.length() < 8 || !command.toUpperCase().startsWith( "RCPT TO:" ) )
        {
            String[] invalidCommandArguments = {command};
            writeLine( socket, getMessage( MESSAGE_COMMAND_INVALID, invalidCommandArguments ) );
            return;
        }

        // Parse Address
        Address address = null;
        try
        {
            address = parseAddress( command.substring( 8 ).trim() );
        }
        catch ( InvalidAddressException e )
        {
            String[] invalidAddressArguments = {command.substring( 8 ).trim()};
            writeLine( socket, getMessage( MESSAGE_ADDRESS_INVALID, invalidAddressArguments ) );
            return;
        }

        message.addToAddress( address );
        writeLine( socket, getMessage( MESSAGE_OK ) );
    }

    /**
     * Resets the current state.
     */
    private void reset()
    {
        message = new SmtpMessage( generateSMTPId() );

        collectingMessage = false;
    }

    /**
     * Generates a unique String that is used to identify the
     * incoming message.
     * <p>
     * This method can be overridden by a subclass to provide an alternate
     * algorithm for generating IDs.
     *
     * @todo create a Plugin for SMTP ID Generation.
     *
     * @return a new unique SMTP Id.
     */
    protected String generateSMTPId()
    {
        StringBuffer messageId = new StringBuffer();
        messageId.append( random.nextInt( 1024 ) );
        messageId.append( (char) ( random.nextInt( 26 ) + 65 ) );
        messageId.append( (char) ( random.nextInt( 26 ) + 65 ) );
        messageId.append( random.nextInt( 1024 ) );
        messageId.append( (char) ( random.nextInt( 26 ) + 65 ) );
        messageId.append( (char) ( random.nextInt( 26 ) + 65 ) );
        messageId.append( random.nextInt( 1024 ) );
        messageId.append( (char) ( random.nextInt( 26 ) + 65 ) );
        messageId.append( (char) ( random.nextInt( 26 ) + 65 ) );
        messageId.append( random.nextInt( 1024 ) );
        messageId.append( (char) ( random.nextInt( 26 ) + 65 ) );
        messageId.append( (char) ( random.nextInt( 26 ) + 65 ) );

        return messageId.toString();
    }

    //***************************************************************
    // Private Utility Methods
    //***************************************************************

    /**
     * Returns the commandId for the specified command string.
     *
     * @param command
     * @return
     */
    private int getCommandId( String command )
    {
        command = command.toUpperCase();

        // All the commands are at least 4 charecters.
        if ( command.length() < 4 )
        {
            return -1;
        }

        String fieldName = "COMMAND_" + command.substring( 0, 4 );
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
     * Parses an address argument into a real email address.  This
     * method strips off any &gt; or &lt; symbols.
     */
    private Address parseAddress( String address ) throws InvalidAddressException
    {

        int index = address.indexOf( "<" );
        if ( index != -1 )
        {
            address = address.substring( index + 1 );
        }
        index = address.indexOf( ">" );
        if ( index != -1 )
        {
            address = address.substring( 0, index );
        }
        return new Address( address );
    }

    /**
     * Returns the message retrieved from the ConfigurationManager.
     *
     * @param messageName name of the message to retrieve.
     * @return the message text.
     *
     * @todo use the i18n component.
     */
    private String getMessage( String messageName )
    {
        return "We need i18n component for: " + messageName;
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

    public void writeLine( Socket c, String s )
        throws IOException
    {
        PrintWriter writer = new PrintWriter( c.getOutputStream() );

        writer.write( s + "\r\n" );

        writer.flush();
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /** @see org.apache.avalon.framework.service.Serviceable#service */
    public void service( ServiceManager serviceManager )
        throws ServiceException
    {
        queue = (Queue) serviceManager.lookup( Queue.ROLE );
    }

    /** @see org.apache.avalon.framework.configuration.Configurable#configure */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        domain = configuration.getChild( "domain" ).getValue();
    }

    /** @see org.apache.avalon.framework.activity.Initializable#initialize */
    public void initialize()
        throws Exception
    {
        data = new StringBuffer( DATA_BUFFER_SIZE );

        random = new Random();

        message = new SmtpMessage( generateSMTPId() );
    }

    public void dispose()
    {
    }
}
