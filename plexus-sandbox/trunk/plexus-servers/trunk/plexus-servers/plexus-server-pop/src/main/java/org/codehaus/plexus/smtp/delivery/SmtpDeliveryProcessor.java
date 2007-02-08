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

package org.codehaus.plexus.smtp.delivery;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.smtp.Address;
import org.codehaus.plexus.smtp.SmtpMessage;
import org.codehaus.plexus.smtp.delivery.DeliveryProcessor;
import org.codehaus.plexus.dns.DNSResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SmtpDeliveryProcessor
    extends AbstractLogEnabled
    implements Serviceable, Configurable, DeliveryProcessor
{
    /**
     * The DNSResolver Plugin Implementation
     */
    private DNSResolver dnsResolver;

    /** Writer to sent data to the client */
    private PrintWriter out;
    /** Reader to read data from the client */
    private BufferedReader in;

    private String domain;

    //***************************************************************
    // Constructor
    //***************************************************************

    public SmtpDeliveryProcessor()
    {
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /** @see org.apache.avalon.framework.service.Serviceable#service */
    public void service( ServiceManager serviceManager )
        throws ServiceException
    {
        dnsResolver = (DNSResolver) serviceManager.lookup( DNSResolver.ROLE );
    }

    /** @see org.apache.avalon.framework.configuration.Configurable#configure */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        domain = configuration.getChild( "domain" ).getValue();
    }


    //***************************************************************
    // DeliveryProcessor Interface Methods
    //***************************************************************

    /**
     * Delivers the specified message to the addresses specified.
     *
     * Once a message has been successfully delivered (or a permanant error
     * occurs) to an address, the address
     * should be removed from the SMTPMessage.  Otherwise, delivery will
     * be reattempted for the addresses.
     *
     * @param message
     * @param addresses Map keyed by domain name, with a List of Address instances as values.
     */
    public void deliverMessage( SmtpMessage message, String domain, List addresses )
    {
        Collection serverAddresses = dnsResolver.getMXEntries( domain );

        Socket socket = null;

        Iterator serverAddressIterator = serverAddresses.iterator();
        String address;
        while ( serverAddressIterator.hasNext() )
        {
            address = (String) serverAddressIterator.next();
            try
            {
                socket = new Socket( address, 25 );
                if ( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Connected to: " + address + " for delivery of message: " +
                                       message.getId() + " to domain: " + domain );
                }
                break;
            }
            catch ( IOException e )
            {
                if ( getLogger().isDebugEnabled() ) getLogger().debug( "Unable to connect to remote SMPT Server: "
                                                                       + address + " from SMPT delivery." );
            }
        }

        if ( socket == null )
        {
            if ( getLogger().isInfoEnabled() )
            {
                getLogger().info( "Unable to make a connection any SMTP server for domain: " + domain );
            }

            return;
        }

        try
        {
            //Get the input and output streams.
            out = new PrintWriter( socket.getOutputStream(), true );
            in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

            //Perform initial commands
            List successfulAddresses = new ArrayList();
            List rejectedAddresses = new ArrayList();

            sendIntro( addresses, message, successfulAddresses, rejectedAddresses );

            if ( successfulAddresses.size() > 0 )
            {
                //Send message data
                sendData( message );
                if ( getLogger().isInfoEnabled() ) logDelivery( message, successfulAddresses, true );
            }

            //Close the connection.
            sendClose();

            if ( rejectedAddresses.size() > 0 )
            {
                // Send Rejection Emails.
                // TODO: Rejection Emails.
                if ( getLogger().isInfoEnabled() )
                {
                    logDelivery( message, rejectedAddresses, false );
                }
            }

            message.removeAddresses( successfulAddresses );
            message.removeAddresses( rejectedAddresses );

        }
        catch ( IOException ioe )
        {
            getLogger().warn( "IOException occured while talking to remote domain: " + domain );
        }
        catch ( Exception exception )
        {
            getLogger().warn( "Unexpected response from remote SMTP Server for domain: " + domain );
        }
        finally
        {
            if ( socket != null )
            {
                try
                {
                    socket.close();
                }
                catch ( IOException ioe )
                {
                    // Not a big deal.
                    getLogger().debug( "Error closing socket: " + ioe );
                }
            }
        }
    }

    /**
     * Sends all the commands neccessary to prepare the remote server
     * to receive the data command.
     */
    private void sendIntro( List addresses, SmtpMessage message, List successAddresses, List rejectedAddresses ) throws Exception
    {
        //Check to make sure remote server introduced itself with appropriate message.
        validateInput( read(), "220" );

        //Send HELO command to remote server.
        write( "HELO " + domain );
        validateInput( read(), "250" );

        //Send MAIL FROM: command
        write( "MAIL FROM:<" + message.getFromAddress().toString() + ">" );
        validateInput( read(), "250" );

        //Send RCTP TO: command
        Iterator addressIterator = addresses.iterator();
        Address address;
        while ( addressIterator.hasNext() )
        {
            address = (Address) addressIterator.next();
            write( "RCPT TO:<" + address.toString() + ">" );
            if ( read().startsWith( "250" ) )
            {
                if ( getLogger().isDebugEnabled() ) getLogger().debug( "Address " + address + " accepted." );
                successAddresses.add( address );
            }
            else
            {
                if ( getLogger().isDebugEnabled() ) getLogger().debug( "Address " + address + " rejected." );
                rejectedAddresses.add( address );
            }
        }
    }

    /**
     * This method sends the data command and all the message data to the
     * remote server.
     */
    private void sendData( SmtpMessage message ) throws Exception
    {
        //Send Data command
        write( "DATA" );
        validateInput( read(), "354" );

        writeData( message.getData() );

        //Send the command end data transmission.
        write( "." );

        validateInput( read(), "250" );
    }

    private void sendClose()
    {

        write( "QUIT" );
        // Who cares.. no reason to validate the input.
        read();
        //validateInput( read(), "221" );
    }

    /**
     * Returns the response code generated by the server.
     * This method will handle multi-line responses, but will
     * only log the responses, and discard the text, returning
     * only the 3 digit response code.
     *
     * @return 3 digit response string.
     */
    private String read()
    {
        try
        {
            String responseCode;

            //Read in the first line.  This is the only line
            //we really care about, since the response code
            //must be the same on all lines.
            String inputText = in.readLine();
            if ( inputText == null )
            {
                inputText = "";
            }
            else
            {
                inputText = inputText.trim();
            }

            if ( getLogger().isInfoEnabled() )
            {
                getLogger().info( "SMTPDeliveryProcessor Input: " + inputText );
            }
            if ( inputText.length() < 3 )
            {
                getLogger().info( "SMTP Response too short. Aborting Send. Response: " + inputText );
                return null;
            }

            //Strip of the response code.
            responseCode = inputText.substring( 0, 3 );

            //Handle Multi-Line Responses.
            while ( ( inputText.length() >= 4 ) && inputText.substring( 3, 4 ).equals( "-" ) )
            {
                inputText = in.readLine().trim();
                if ( getLogger().isInfoEnabled() )
                {
                    getLogger().info( "SMTPDeliveryProcessorInput: " + inputText );
                }
            }

            return responseCode;
        }
        catch ( IOException ioe )
        {
            getLogger().debug( "Error reading from socket, closing connection." );
            return null;
        }
    }

    /**
     * Writes the specified output message to the client.
     */
    private void write( String message )
    {
        if ( getLogger().isInfoEnabled() ) getLogger().info( "SMTPDeliveryProcessor Output: " + message );
        out.print( message + "\r\n" );
        out.flush();
    }

    /**
     * Same as write w/o the logging.
     * @param data
     */
    private void writeData( String data )
    {
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "SMTPDeliveryProcessor Output:\r\n" + data );
        }
        else if ( getLogger().isInfoEnabled() )
        {
            getLogger().debug( "SMTPDeliveryProcessor Output: <Message Body> " );
        }

        out.print( data + "\r\n" );
        out.flush();
    }

    protected void logDelivery( SmtpMessage message, List addresses, boolean success )
    {

        // Log the delivered addresses.
        StringBuffer logMessage = new StringBuffer();
        logMessage.append( "Message: " );
        logMessage.append( message.getId() );
        if ( success )
        {
            logMessage.append( " successfully delivered to: " );
        }
        else
        {
            logMessage.append( " rejected for delivery to: " );
        }
        Iterator addressIterator = addresses.iterator();
        while ( addressIterator.hasNext() )
        {
            logMessage.append( addressIterator.next() );
            logMessage.append( ", " );
        }
        int length = logMessage.length();
        logMessage.delete( length - 2, length );
        getLogger().info( logMessage.toString() );
    }

    /**
     * Validates the input from the remote server.
     *
     * @param input the entire line read.
     * @param expected the expected first 3 charecters
     * @throws java.lang.Exception thrown if the input does not match the expected.
     */
    private void validateInput( String input, String expected ) throws Exception
    {
        if ( input == null || !input.startsWith( expected ) )
        {
            getLogger().info( "Incorrect response read from remote server.  Expected response to begin with \"220\"." );
            throw new Exception( "Incorrect response read from remote server.  Expected response to begin with \"220\"." );
        }
    }
}
