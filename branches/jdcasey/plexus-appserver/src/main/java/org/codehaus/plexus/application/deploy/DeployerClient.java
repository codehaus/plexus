package org.codehaus.plexus.application.deploy;

/*
 * Copyright (c) 2004, Codehaus.org
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

import java.io.IOException;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

/**
 * DeployerClient
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DeployerClient
{
    private String serverUrl;
    private String applicationName;
    private String location;

    public String getApplicationName()
    {
        return applicationName;
    }

    public void setApplicationName( String applicationName )
    {
        this.applicationName = applicationName;
    }

    /**
     * @return Returns the directory location.
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * @param location The directory location to set.
     */
    public void setLocation( String location )
    {
        this.location = location;
    }

    public String getServerUrl()
    {
        return serverUrl;
    }

    public void setServerUrl( String serverUrl )
    {
        this.serverUrl = serverUrl;
    }

    public void deploy()
        throws XmlRpcException, IOException
    {
        XmlRpcClient xmlrpc = new XmlRpcClient( serverUrl );

        Vector params = new Vector();
        params.addElement( applicationName );
        params.addElement( location );

        xmlrpc.execute( "Deployer.deploy", params );
    }

    public void redeploy()
        throws XmlRpcException, IOException
    {
        XmlRpcClient xmlrpc = new XmlRpcClient( serverUrl );

        Vector params = new Vector();
        params.addElement( applicationName );
        params.addElement( location );

        xmlrpc.execute( "Deployer.redeploy", params );
    }

    public void undeploy()
        throws XmlRpcException, IOException
    {
        XmlRpcClient xmlrpc = new XmlRpcClient( serverUrl );

        Vector params = new Vector();
        params.addElement( applicationName );

        xmlrpc.execute( "Deployer.undeploy", params );
    }
}
