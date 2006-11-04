package org.codehaus.plexus.xmlrpc;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.net.URL;
import java.util.Vector;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultXmlRpcComponentTest
    extends PlexusTestCase
{
    public void testExecution()
        throws Exception
    {
        URL url = new URL( "http://localhost:40000/rpc2" );

        // ----------------------------------------------------------------------
        // Start the XML-RPC server
        // ----------------------------------------------------------------------

        XmlRpcComponent xmlRpc = (XmlRpcComponent) lookup( XmlRpcComponent.ROLE );

        // The XML-RPC webserver's start() method doesn't wait untill it's started
        // so we have to wait a bit here to make sure the listener thread has
        // started up properly
        Thread.sleep( 100 );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        Vector params = new Vector();

        params.add( "Travis" );

        Object obj = xmlRpc.executeRpc( url, "poker.poke", params);

        assertEquals( obj.getClass().getName(), String.class.getName() );

        String str = obj.toString();

        assertEquals( "Hello World Travis!", str );
    }
}
