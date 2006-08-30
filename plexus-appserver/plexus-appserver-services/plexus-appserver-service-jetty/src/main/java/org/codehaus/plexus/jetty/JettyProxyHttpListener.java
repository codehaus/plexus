package org.codehaus.plexus.jetty;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.mortbay.http.HttpFields;
import org.mortbay.http.HttpMessage;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.SocketListener;
import org.mortbay.util.InetAddrPort;

import java.net.Socket;

/**
 * Forced Host Listener
 * this simple listener extention forces the host header to be set to a specific value.
 * It is useful when deployed behind old apache mod_proxy implementations that
 * lie about the real host used by the client.
 */
public class JettyProxyHttpListener
    extends SocketListener
{
    String forcedHost;

    public JettyProxyHttpListener()
    {
    }

    public JettyProxyHttpListener( InetAddrPort address )
    {
        super( address );
    }

    public String getForcedHost()
    {
        return forcedHost;
    }

    public void setForcedHost( String host )
    {
        forcedHost = host;
    }

    protected void customizeRequest( Socket socket, HttpRequest request )
    {
        request.setState( HttpMessage.__MSG_EDITABLE );

        if ( forcedHost == null )
        {
            request.removeField( HttpFields.__Host );
        }
        else
        {
            request.setField( HttpFields.__Host, forcedHost );
        }

        request.setState( HttpMessage.__MSG_RECEIVED );

        super.customizeRequest( socket, request );
    }
}
