//========================================================================
//$Id$
//Copyright 2004 Mort Bay Consulting Pty. Ltd.
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package org.codehaus.plexus.jetty.http;

import java.net.Socket;

import org.mortbay.util.InetAddrPort;
import org.mortbay.http.SocketListener;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpMessage;
import org.mortbay.http.HttpFields;

/**
 * Forced Host Listener
 * this simple listener extention forces the host header to be set to a specific value.
 * It is useful when deployed behind old apache mod_proxy implementations that
 * lie about the real host used by the client.
 * 
 */
public class ProxyListener extends SocketListener
{
    String _host;
    
    public ProxyListener()
    {
        super();
    }

    public ProxyListener(InetAddrPort address)
    {
        super(address);
    }

    /**
     * @return Returns the host.
     */
    public String getForcedHost()
    {
        return _host;
    }
    
    /**
     * @param host The host to set.
     */
    public void setForcedHost(String host)
    {
        _host = host;
    }
    
    /* 
     * @see org.mortbay.http.SocketListener#customizeRequest(java.net.Socket, org.mortbay.http.HttpRequest)
     */
    protected void customizeRequest(Socket socket, HttpRequest request)
    {
        request.setState(HttpMessage.__MSG_EDITABLE);
        if (_host==null)
            request.removeField(HttpFields.__Host);
        else
            request.setField(HttpFields.__Host, _host);
        request.setState(HttpMessage.__MSG_RECEIVED);
        super.customizeRequest(socket, request);
    }
}
