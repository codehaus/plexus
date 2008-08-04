package org.codehaus.plexus.xmlrpc;

import org.apache.xmlrpc.XmlRpcException;

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

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface XmlRpcServer
{
    String ROLE = XmlRpcServer.class.getName();

    // ----------------------------------------------------------------------
    // Listener Management
    // ----------------------------------------------------------------------

    void addListener( String host, int port, boolean secure )
        throws XmlRpcException;

    void removeListener( String host, int port )
        throws XmlRpcException;

    void startListener( String host, int port )
        throws XmlRpcException;

    // ----------------------------------------------------------------------
    // Handler Management
    // ----------------------------------------------------------------------

    void addHandler( String name, Object handler )
        throws XmlRpcException;

    void addHandler( String host, String name, int port, Object handler )
        throws XmlRpcException;

    void removeHandler( String name )
        throws XmlRpcException;

    void removeHandler( String host, int port, String name )
        throws XmlRpcException;

    // ----------------------------------------------------------------------
    // Authorization Management
    // ----------------------------------------------------------------------

    void acceptClient( String clientHost );

    void acceptClient( String host, int port, String clientHost );

    void denyClient( String clientHost );

    void denyClient( String host, int port, String clientHost );

    void setParanoid( String host, int port, boolean state );
}
