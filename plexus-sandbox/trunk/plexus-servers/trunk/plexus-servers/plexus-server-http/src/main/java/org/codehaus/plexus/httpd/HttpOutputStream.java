/*
 * Copyright (C) MX4J.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package org.codehaus.plexus.httpd;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * HttpAdaptor sets the basic adaptor listening for HTTP requests
 *
 * @author <a href="mailto:tibu@users.sourceforge.org">Carlos Quiroz</a>
 * @version $Revision$
 */
public class HttpOutputStream
    extends BufferedOutputStream
{
    /** Answer code */
    protected int code;

    /** whether to send the headers*/
    protected boolean sendHeaders;

    /** Headers to be sent */
    protected Map headers = new HashMap( 7 );


    /**
     * Creates a new HttpOutputStream with a given OutputStream and an InputStream
     *
     * @param out  The OutputStream normally associated with the output socket
     *      stream of the incoming connection
     * @param in   HttpInputStream containing the incoming request
     */
    public HttpOutputStream( OutputStream out, HttpInputStream in )
    {
        super( out );
        code = HttpConstants.STATUS_OKAY;
        setHeader( "Server", HttpConstants.SERVER_INFO );
        sendHeaders = ( in.getVersion() >= 1.0 );
    }


    /**
     * Sets the answer code
     *
     * @param code  The new code value
     */
    public void setCode( int code )
    {
        this.code = code;
    }


    /**
     * Sets a given header code
     *
     * @param attr   The new header name
     * @param value  The new header value
     */
    public void setHeader( String attr, String value )
    {
        headers.put( attr, value );
    }


    /**
     * Sends the headers
     *
     * @return                 Description of the Returned Value
     * @exception IOException  Description of Exception
     */
    public boolean sendHeaders() throws IOException
    {
        if ( sendHeaders )
        {
            StringBuffer buffer = new StringBuffer( 512 );
            buffer.append( HttpConstants.HTTP_VERSION );
            buffer.append( code );
            buffer.append( " " );
            buffer.append( HttpUtil.getCodeMessage( code ) );
            buffer.append( "\r\n" );
            Iterator attrs = headers.keySet().iterator();
            int size = headers.size();

            for ( int i = 0; i < size; i++ )
            {
                String attr = (String) attrs.next();
                buffer.append( attr );
                buffer.append( ": " );
                buffer.append( headers.get( attr ) );
                buffer.append( "\r\n" );
            }
            buffer.append( "\n" );
            write( buffer.toString() );
        }
        return sendHeaders;
    }


    /**
     * Writes a given message line
     *
     * @param msg The message to be written
     * @exception IOException
     */
    public void write( String msg ) throws IOException
    {
        write( msg.getBytes( "latin1" ) );
    }


    /**
     * Writes the content of the input stream to the output stream
     *
     * @param in The input stream
     * @exception IOException
     */
    public void write( InputStream in ) throws IOException
    {
        int n;
        int length = buf.length;
        while ( ( n = in.read( buf, count, length - count ) ) >= 0 )
        {
            if ( ( count += n ) >= length )
            {
                out.write( buf, count = 0, length );
            }
        }
    }
}
